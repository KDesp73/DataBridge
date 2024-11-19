package io.github.kdesp73.databridge.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import io.github.kdesp73.databridge.connections.DatabaseConnection;
import io.github.kdesp73.databridge.helpers.Adapter;
import io.github.kdesp73.databridge.helpers.Config;
import io.github.kdesp73.databridge.helpers.FileUtils;
import io.github.kdesp73.databridge.helpers.QueryBuilder;
import io.github.kdesp73.databridge.helpers.SQLogger;
import io.github.kdesp73.databridge.helpers.SQLogger.LogLevel;

/**
 * The Scheman class handles the database schema migrations. It provides methods for applying,
 * rolling back, and verifying migrations. The class also manages the schema changelog to track
 * the versions and checksums of the migrations applied to the database.
 * <p>
 * It interacts with the database through a {@link DatabaseConnection} instance and uses
 * migration scripts stored in a designated migrations directory.
 * </p>
 *
 * @author kdesp73
 */
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

    /**
     * Constructs a new Scheman instance with the provided database connection.
     * Initializes the schema changelog and verifies the existence of the changelog table.
     *
     * @param connection The {@link DatabaseConnection} instance used to interact with the database.
     */
    public Scheman(DatabaseConnection connection) {
        this.connection = connection;
        this.changedMigrations = new ArrayList<>();
        initializeSchemaChangelog();
    }

    /**
     * Creates the schema changelog table if it does not already exist in the database.
     * The table is used to track applied migrations and their details.
     */
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

    /**
     * Returns the current version of the schema by querying the version from the schema changelog table.
     *
     * @return The current schema version.
     * @throws SQLException If there is an error querying the database.
     */
    public int getCurrentVersion() throws SQLException {
        String query = String.format("SELECT %s FROM %s ORDER BY %s DESC LIMIT 1", VERSION_COL, TABLE_NAME, VERSION_COL);
        try (Statement statement = connection.get().createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.next() ? resultSet.getInt(VERSION_COL) : 0;
        }
    }

    /**
     * Applies the given migration to the database by executing its up script and
     * inserting a record into the schema changelog table.
     *
     * @param migration The migration to be applied.
     * @throws SQLException If there is an error applying the migration or updating the changelog.
     */
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

    /**
     * Rolls back the most recent migration by executing its down script and removing
     * its record from the schema changelog table.
     *
     * @throws SQLException If there is an error during the rollback process.
     */
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

    /**
     * Runs all unapplied migrations, checking for any checksum mismatches and applying migrations as needed.
     *
     * @throws SQLException If there is an error running the migrations.
     */
    public void runMigrations() throws SQLException {
        checkMigrationChecksums();
        int currentVersion = getCurrentVersion();

        for (Migration migration : migrations) {
            if (currentVersion < migration.getVersion()) {
                applyMigration(migration);
            }
        }
    }

    /**
     * Reapplies any migrations that have been modified after they were first applied,
     * based on checksum mismatches.
     *
     * @throws SQLException If there is an error reapplying migrations.
     */
    public void rerunMigrations() throws SQLException {
        if (changedMigrations.isEmpty()) {
            return;
        }

        for (Migration migration : changedMigrations) {
            reapplyMigration(migration);
        }
        changedMigrations.clear();
    }

    /**
     * Reapplies a modified migration by executing its up script again and updating its record in the schema changelog table.
     *
     * @param migration The migration to be reapplied.
     * @throws SQLException If there is an error reapplying the migration.
     */
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

    /**
     * Loads all migration files from the migrations directory and sorts them by version.
     *
     * @throws IOException If there is an error reading the migration files.
     */
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

    /**
     * Checks if any migrations have modified scripts by comparing their current checksum
     * with the checksum stored in the schema changelog table.
     */
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

    /**
     * Logs the details of a migration operation (e.g., applying, rolling back, or reapplying).
     *
     * @param action The action being performed on the migration (e.g., "Applying", "Rolling back").
     * @param migration The migration being operated on.
     */
    private void logMigration(String action, Migration migration) {
        SQLogger.getLogger(LogLevel.INFO, SQLogger.LogType.FILE).log(
            Config.getInstance().getLogLevel(), action + " migration version " + migration.getVersion() + ": " + migration.getDescription()
        );
    }

    /**
     * Executes a SQL script, logging the operation and handling any exceptions that occur.
     *
     * @param script The SQL script to execute.
     */
    private void executeScript(String script) {
        try {
            connection.execute(script);
        } catch (SQLException ex) {
            SQLogger.getLogger(LogLevel.ERRO, SQLogger.LogType.FILE).log(Config.getInstance().getLogLevel(), "Error executing script", ex);
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
