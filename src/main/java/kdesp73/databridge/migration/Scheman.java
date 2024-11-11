package kdesp73.databridge.migration;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kdesp73.databridge.connections.DatabaseConnection;
import kdesp73.databridge.helpers.Config;
import kdesp73.databridge.helpers.FileUtils;
import kdesp73.databridge.helpers.SQLogger;
import kdesp73.databridge.helpers.SQLogger.LogLevel;

// WARNING: Not ready. Should not be used
public class Scheman {

	private DatabaseConnection connection;
	private static String table = "schema_changelog";
	private static String versionCol = "version_number";
	private static String descriptionCol = "migration_description";
	private static String timestampCol = "applied_at";
	private static String checksumCol = "checksum";

	private static String migrationsPath = System.getProperty("user.dir") + "/migrations";
	private List<Migration> migrations;

	public Scheman(DatabaseConnection connection) {
		this.connection = connection;
	}

	public void setup() throws SQLException {
		try {
			connection.execute(String.format("CREATE TABLE IF NOT EXISTS %s (%s INT PRIMARY KEY, %s VARCHAR(255), %s TIMESTAMP DEFAULT CURRENT_TIMESTAMP, %s CHAR(32));", table, versionCol, descriptionCol, timestampCol, checksumCol));
		} catch (SQLException ex) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE).log("Failed creating schema_changelog table", ex);
			throw new SQLException(ex);
		}
	}

	/**
	 * Gets the current schema version from the database.
	 *
	 * @return
	 * @throws java.sql.SQLException
	 */
	public int getCurrentVersion() throws SQLException {
		String query = String.format("SELECT %s FROM %s ORDER BY %s DESC LIMIT 1", versionCol, table, versionCol);
		try (Statement statement = connection.get().createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			if (resultSet.next()) {
				return resultSet.getInt("version_number");
			}
			return 0; // If no version is found, assume it's version 0 (initial state)
		}
	}

	/**
	 * Apply a migration to the database.
	 *
	 * @param versionNumber
	 * @param migrationDescription
	 * @param migrationScript
	 * @throws java.sql.SQLException
	 */
	public void applyMigration(int versionNumber, String migrationDescription, String migrationScript) throws SQLException {
		SQLogger
			.getLogger(LogLevel.INFO, SQLogger.LogType.ALL)
			.log(Config.getInstance().getLogLevel(), "Running migration v%d (%s)", versionNumber, migrationDescription);

		try (Statement statement = connection.get().createStatement()) {
			statement.execute(migrationScript);
		}

		String insertMigration = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)", table, versionCol, descriptionCol);
		try (PreparedStatement preparedStatement = connection.get().prepareStatement(insertMigration)) {
			preparedStatement.setInt(1, versionNumber);
			preparedStatement.setString(2, migrationDescription);
			preparedStatement.executeUpdate();
		}
	}

	/**
	 * Rollback the most recent migration.
	 *
	 * @throws SQLException if rollback fails
	 */
	public void rollbackMigration() throws SQLException {
		int currentVersion = getCurrentVersion();
		if (currentVersion > 0) {
			String downScript = getMigrationDownScript(currentVersion);
			if (downScript == null) {
				return;
			}

			SQLogger
				.getLogger(LogLevel.INFO, SQLogger.LogType.ALL)
				.log(Config.getInstance().getLogLevel(), "Rolling back migration v%d", currentVersion);

			try (Statement statement = connection.get().createStatement()) {
				statement.execute(downScript);
			}

			String deleteMigration = String.format("DELETE FROM %s WHERE %s = ?", table, versionCol);
			try (PreparedStatement preparedStatement = connection.get().prepareStatement(deleteMigration)) {
				preparedStatement.setInt(1, currentVersion);
				preparedStatement.executeUpdate();
			}
		}
	}

	/**
	 * Run all pending migrations.
	 *
	 * @throws java.sql.SQLException
	 */
	public void runMigrations() throws SQLException {
		loadMigrations();
		int currentVersion = getCurrentVersion();

		for (Migration m : this.migrations) {
			if (currentVersion < m.getVersion()) {
				int nextVersion = currentVersion + 1;
				applyMigration(nextVersion, m.getDescription(), m.getUpScript());
				currentVersion = nextVersion;
			}
		}
	}

	/**
	 * Load migration scripts from files.
	 */
	private void loadMigrations() {
		this.migrations = new ArrayList<>();
		try {
			List<String> filePaths = FileUtils.getAbsoluteFilePaths(migrationsPath);

			for (String path : filePaths) {
				var migration = new Migration(path);

				if (migration.getVersion() < 0 || migration.getDownScript() == null || migration.getUpScript() == null) {
					continue;
				}

				this.migrations.add(migration);
			}
		} catch (IOException e) {
			System.err.println("Error reading directory: " + e.getMessage());
		}

		this.migrations.sort(null);
	}

	/**
	 * Get the script to rollback a specific migration (this is a simplified
	 * placeholder). In a real-world scenario, you would store rollback scripts
	 * separately.
	 */
	private String getMigrationDownScript(int versionNumber) {
		loadMigrations();

		for (Migration m : this.migrations) {
			if (m.getVersion() == versionNumber) {
				return m.getDownScript();
			}
		}
		return null;
	}
}
