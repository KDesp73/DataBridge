package kdesp73.databridge.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import kdesp73.databridge.connections.DatabaseConnection;
import kdesp73.databridge.helpers.Adapter;
import kdesp73.databridge.helpers.Config;
import kdesp73.databridge.helpers.FileUtils;
import kdesp73.databridge.helpers.QueryBuilder;
import kdesp73.databridge.helpers.SQLogger;
import kdesp73.databridge.helpers.SQLogger.LogLevel;

public class Scheman {

	private DatabaseConnection connection;
	private static final String TABLE_NAME = "schema_changelog";
	private static final String VERSION_COL = "version_number";
	private static final String DESCRIPTION_COL = "migration_description";
	private static final String TIMESTAMP_COL = "applied_at";
	private static final String CHECKSUM_COL = "checksum";
	private static final String MIGRATIONS_PATH = System.getProperty("user.dir") + "/migrations";

	private List<Migration> migrations;
	private List<Migration> changedMigrations;

	public Scheman(DatabaseConnection connection) {
		this.connection = connection;
		this.changedMigrations = new ArrayList<>();
		initializeSchemaChangelog();
	}

	private void initializeSchemaChangelog() {
		try {
			connection.execute(String.format(
				"CREATE TABLE IF NOT EXISTS %s (%s INT PRIMARY KEY, %s VARCHAR(255), %s TIMESTAMP DEFAULT CURRENT_TIMESTAMP, %s CHAR(32));",
				TABLE_NAME, VERSION_COL, DESCRIPTION_COL, TIMESTAMP_COL, CHECKSUM_COL
			));
		} catch (SQLException ex) {
			SQLogger.getLogger(LogLevel.ERRO, SQLogger.LogType.FILE).log(Config.getInstance().getLogLevel(), "Failed creating schema_changelog table", ex);
		}
	}

	public int getCurrentVersion() throws SQLException {
		String query = String.format("SELECT %s FROM %s ORDER BY %s DESC LIMIT 1", VERSION_COL, TABLE_NAME, VERSION_COL);
		try (Statement statement = connection.get().createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			return resultSet.next() ? resultSet.getInt(VERSION_COL) : 0;
		}
	}

	public void applyMigration(Migration migration) throws SQLException {
		logMigration("Applying", migration);
		executeScript(migration.getUpScript());

		String insertQuery = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)", TABLE_NAME, VERSION_COL, DESCRIPTION_COL, CHECKSUM_COL);
		try (PreparedStatement preparedStatement = connection.get().prepareStatement(insertQuery)) {
			preparedStatement.setInt(1, migration.getVersion());
			preparedStatement.setString(2, migration.getDescription());
			preparedStatement.setString(3, Migration.generateChecksum(migration.getUpScript()));
			preparedStatement.executeUpdate();
		} catch (IOException | NoSuchAlgorithmException ex) {
			SQLogger.getLogger(LogLevel.ERRO, SQLogger.LogType.FILE).log(Config.getInstance().getLogLevel(), "Checksum generation failed", ex);
		}
	}

	public void rollbackMigration() throws SQLException {
		int currentVersion = getCurrentVersion();
		if (currentVersion <= 0) {
			return;
		}

		String downScript = getMigrationDownScript(currentVersion);

		logMigration("Rolling back", new Migration(currentVersion, "Rollback"));
		if (downScript != null) {
			executeScript(downScript);
		}

		String deleteQuery = String.format("DELETE FROM %s WHERE %s = ?", TABLE_NAME, VERSION_COL);
		try (PreparedStatement preparedStatement = connection.get().prepareStatement(deleteQuery)) {
			preparedStatement.setInt(1, currentVersion);
			preparedStatement.executeUpdate();
		}
	}

	public void runMigrations() throws SQLException {
		checkMigrationChecksums();
		int currentVersion = getCurrentVersion();

		for (Migration migration : migrations) {
			if (currentVersion < migration.getVersion()) {
				applyMigration(migration);
			}
		}
	}

	public void rerunMigrations() throws SQLException {
		if (changedMigrations.isEmpty()) {
			return;
		}

		for (Migration migration : changedMigrations) {
			reapplyMigration(migration);
		}
		changedMigrations.clear();
	}

	private void reapplyMigration(Migration migration) throws SQLException {
		logMigration("Reapplying", migration);
		executeScript(migration.getUpScript());

		String updateQuery = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ? WHERE %s = ?", TABLE_NAME, VERSION_COL, DESCRIPTION_COL, CHECKSUM_COL, VERSION_COL);
		try (PreparedStatement preparedStatement = connection.get().prepareStatement(updateQuery)) {
			preparedStatement.setInt(1, migration.getVersion());
			preparedStatement.setString(2, migration.getDescription());
			preparedStatement.setString(3, Migration.generateChecksum(migration.getUpScript()));
			preparedStatement.setInt(4, migration.getVersion());
			preparedStatement.executeUpdate();
		} catch (IOException | NoSuchAlgorithmException ex) {
			SQLogger.getLogger(LogLevel.ERRO, SQLogger.LogType.FILE).log(Config.getInstance().getLogLevel(), "Checksum generation failed", ex);
		}
	}

	private void loadMigrations() {
		migrations = new ArrayList<>();
		try {
			for (String path : FileUtils.getAbsoluteFilePaths(MIGRATIONS_PATH)) {
				Migration migration = new Migration(path);
				if (migration.isValid()) {
					migrations.add(migration);
				}
			}
			migrations.sort(null);
		} catch (IOException e) {
			SQLogger.getLogger(LogLevel.ERRO, SQLogger.LogType.FILE).log(Config.getInstance().getLogLevel(), "Error reading migrations directory", e);
		}
	}

	private void checkMigrationChecksums() {
		loadMigrations();
		for (Migration migration : migrations) {
			try {
				ResultSet resultSet = connection.executeQuery(
					new QueryBuilder().select(CHECKSUM_COL).from(TABLE_NAME).where(VERSION_COL + " = " + migration.getVersion()).build()
				);
				String currentChecksum = Adapter.load(resultSet, SchemaChangelog.class).stream()
					.map(SchemaChangelog::getChecksum)
					.findFirst()
					.orElse(null);

				if (currentChecksum != null && !currentChecksum.equals(migration.getChecksumUp())) {
					changedMigrations.add(migration);
				}
			} catch (SQLException ex) {
				SQLogger.getLogger(LogLevel.ERRO, SQLogger.LogType.FILE).log(Config.getInstance().getLogLevel(), "Failed to retrieve checksum for migration", ex);
			}
		}
	}

	private void logMigration(String action, Migration migration) {
		SQLogger.getLogger(LogLevel.INFO, SQLogger.LogType.ALL).log(Config.getInstance().getLogLevel(),
			"%s %s migration v%d (%s)", SQLogger.getCurrentTimestamp(), action, migration.getVersion(), migration.getDescription());
	}

	private void executeScript(String script) throws SQLException {
		try (Statement statement = connection.get().createStatement()) {
			statement.execute(script);
		}
	}

	private String getMigrationDownScript(int versionNumber) {
		loadMigrations();

		for (Migration m : this.migrations) {
			if (m.getVersion() == versionNumber) {
				return m.getDownScript();
			}
		}
		return null;
	}

	public void cli() {
		new CommandPrompt(this).start();
	}

	public ResultSet selectMigrations() throws SQLException {
		return this.connection.executeQuery("SELECT * FROM " + TABLE_NAME);
	}
}
