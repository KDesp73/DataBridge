package kdesp73.databridge.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kdesp73.databridge.connections.DatabaseConnection;
import kdesp73.databridge.helpers.Adapter;
import kdesp73.databridge.helpers.Config;
import kdesp73.databridge.helpers.FileUtils;
import kdesp73.databridge.helpers.QueryBuilder;
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
	private List<Migration> changed;

	public Scheman(DatabaseConnection connection) {
		this.connection = connection;
		try {
			setup();
		} catch (SQLException ex) {
			Logger.getLogger(Scheman.class.getName()).log(Level.SEVERE, null, ex);
		}
		this.changed = new ArrayList<>();
	}

	private void setup() throws SQLException {
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
	 * @param migration
	 * @throws java.sql.SQLException
	 */
	public void applyMigration(Migration migration) throws SQLException {
		SQLogger
			.getLogger(LogLevel.INFO, SQLogger.LogType.ALL)
			.log(Config.getInstance().getLogLevel(), "%s Running migration v%d (%s)", SQLogger.getCurrentTimestamp(), migration.getVersion(), migration.getDescription());

		try (Statement statement = connection.get().createStatement()) {
			statement.execute(migration.getUpScript());
		}

		String insertMigration = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)", table, versionCol, descriptionCol, checksumCol);
		try (PreparedStatement preparedStatement = connection.get().prepareStatement(insertMigration)) {
			preparedStatement.setInt(1, migration.getVersion());
			preparedStatement.setString(2, migration.getDescription());
			try {
				preparedStatement.setString(3, Migration.generateChecksum(migration.getUpScript()));
			} catch (IOException | NoSuchAlgorithmException ex) {
				Logger.getLogger(Scheman.class.getName()).log(Level.SEVERE, null, ex);
			}
			preparedStatement.executeUpdate();
		}
	}

	public void reapplyMigration(Migration migration) throws SQLException {
		SQLogger
			.getLogger(LogLevel.INFO, SQLogger.LogType.ALL)
			.log(Config.getInstance().getLogLevel(), "%s Running migration v%d (%s)", SQLogger.getCurrentTimestamp(), migration.getVersion(), migration.getDescription());

		try (Statement statement = connection.get().createStatement()) {
			statement.execute(migration.getUpScript());
		}

		String insertMigration = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ? WHERE %s = ?", table, versionCol, descriptionCol, checksumCol, versionCol);
		try (PreparedStatement preparedStatement = connection.get().prepareStatement(insertMigration)) {
			preparedStatement.setInt(1, migration.getVersion());
			preparedStatement.setString(2, migration.getDescription());
			try {
				preparedStatement.setString(3, Migration.generateChecksum(migration.getUpScript()));
			} catch (IOException | NoSuchAlgorithmException ex) {
				Logger.getLogger(Scheman.class.getName()).log(Level.SEVERE, null, ex);
			}
			preparedStatement.setInt(4, migration.getVersion());
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
		if (currentVersion <= 0) {
			return;
		}

		String downScript = getMigrationDownScript(currentVersion);

		SQLogger
			.getLogger(LogLevel.INFO, SQLogger.LogType.ALL)
			.log(Config.getInstance().getLogLevel(), "%s Rolling back migration v%d", SQLogger.getCurrentTimestamp(), currentVersion);

		if (downScript != null) {
			try (Statement statement = connection.get().createStatement()) {
				statement.execute(downScript);
			}
		}

		String deleteMigration = String.format("DELETE FROM %s WHERE %s = ?", table, versionCol);
		try (PreparedStatement preparedStatement = connection.get().prepareStatement(deleteMigration)) {
			preparedStatement.setInt(1, currentVersion);
			preparedStatement.executeUpdate();
		}
	}

	/**
	 * Run all pending migrations.
	 *
	 * @throws java.sql.SQLException
	 */
	public void runMigrations() throws SQLException {
		checkSums();
		int currentVersion = getCurrentVersion();

		for (Migration m : this.migrations) {
			if (currentVersion < m.getVersion()) {
				applyMigration(m);
			}
		}
	}

	/**
	 * Run all changed migrations.
	 *
	 * @throws java.sql.SQLException
	 */
	public void rerunMigrations() throws SQLException {
		if(this.changed.isEmpty()) return;
		
		for (Migration m : this.changed) {
			reapplyMigration(m);
		}
		this.changed = new ArrayList<>();
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

				if (migration.getVersion() < 0 || migration.getUpScript() == null) {
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

	public List<Migration> getMigrations() {
		return migrations;
	}

	public ResultSet selectMigrations() throws SQLException {
		return this.connection.executeQuery("SELECT * FROM " + table);
	}

	public void checkSums() {
		loadMigrations();
		for (Migration m : this.migrations) {
			String sum = null;
			try {
				ResultSet rs = this.connection.executeQuery(new QueryBuilder().select(checksumCol).from(table).where(versionCol + " = " + m.getVersion()).build());
				List<SchemaChangelog> result = Adapter.load(rs, SchemaChangelog.class);
				sum = (result.isEmpty()) ? null : result.get(0).getChecksum();
			} catch (SQLException ex) {
				SQLogger.getLogger(LogLevel.ERRO, SQLogger.LogType.FILE).log(Config.getInstance().getLogLevel(), "Failed getting checksum for migration v" + m.getVersion(), ex);
				continue;
			}
			if(sum == null) continue;

			if (!sum.equals(m.getChecksumUp())) {
				System.out.println("Migration v" + m.getVersion() + " has changed");
				changed.add(m);
			}
		}
	}

	public void cli() {
		new CommandPrompt(this).start();
	}
}
