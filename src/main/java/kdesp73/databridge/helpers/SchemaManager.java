package kdesp73.databridge.helpers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kdesp73.databridge.connections.DatabaseConnection;
import kdesp73.databridge.helpers.SQLogger.LogLevel;

// WARNING: Not ready. Should not be used
public class SchemaManager {
	private DatabaseConnection connection;

	public SchemaManager(DatabaseConnection connection) {
		this.connection = connection;
	}

	public void setup() throws SQLException {
		try {
			connection.execute("CREATE TABLE IF NOT EXISTS schema_changelog (version_number INT PRIMARY KEY,migration_description VARCHAR(255), applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,checksum CHAR(32));");
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
		String query = "SELECT version_number FROM schema_version ORDER BY version_number DESC LIMIT 1";
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
		try (Statement statement = connection.get().createStatement()) {
			statement.execute(migrationScript);
		}

		String insertMigration = "INSERT INTO schema_version (version_number, migration_description) VALUES (?, ?)";
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
			// Get the migration description and script for the current version
			String getMigrationQuery = "SELECT migration_description FROM schema_version WHERE version_number = ?";
			String migrationScript = getMigrationScript(currentVersion); // You would load the script to rollback the migration

			// Undo the migration script (you can store reverse migration scripts in your project)
			try (Statement statement = connection.get().createStatement()) {
				statement.execute(migrationScript); // Assuming rollback script exists
			}

			// Remove the version record
			String deleteMigration = "DELETE FROM schema_version WHERE version_number = ?";
			try (PreparedStatement preparedStatement = connection.get().prepareStatement(deleteMigration)) {
				preparedStatement.setInt(1, currentVersion);
				preparedStatement.executeUpdate();
			}
		}
	}

	/**
	 * Run all pending migrations.
	 *
	 * @param migrationScripts
	 * @throws java.sql.SQLException
	 */
	public void runMigrations(List<String> migrationScripts) throws SQLException {
		int currentVersion = getCurrentVersion();

		for (String script : migrationScripts) {
			// Assume that scripts are in version order; migrate to the next version
			int nextVersion = currentVersion + 1;
			applyMigration(nextVersion, "Migration " + nextVersion, script);
			currentVersion = nextVersion;
		}
	}

	/**
	 * Load migration scripts from files/resources or a predefined list.
	 */
	private List<String> loadMigrationScripts() {
		// This could be extended to load migration scripts from files or resources
		List<String> migrationScripts = new ArrayList<>();
		migrationScripts.add("CREATE TABLE IF NOT EXISTS ..."); // Example migration script
		migrationScripts.add("ALTER TABLE ..."); // Another example
		return migrationScripts;
	}

	/**
	 * Get the script to rollback a specific migration (this is a simplified
	 * placeholder). In a real-world scenario, you would store rollback scripts
	 * separately.
	 */
	private String getMigrationScript(int versionNumber) {
		// Return the rollback script for the given version
		return "DROP TABLE IF EXISTS ..."; // Example rollback script
	}
}
