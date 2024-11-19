package io.github.kdesp73.databridge.migration;

import java.sql.Timestamp;

/**
 * The SchemaChangelog class represents an entry in the schema changelog table
 * for tracking the migrations that have been applied to the database.
 * <p>
 * Each changelog entry includes the version number of the migration, a description
 * of the migration, the timestamp when the migration was applied, and a checksum
 * to verify the integrity of the migration script.
 * </p>
 *
 * @author kdesp73
 */
public class SchemaChangelog {

    private int versionNumber;
    private String migrationDescription;
    private String appliedAt;
    private String checksum;

    /**
     * Returns the version number of the migration.
     *
     * @return The version number of the migration.
     */
    public int getVersionNumber() {
        return versionNumber;
    }

    /**
     * Returns the description of the migration.
     *
     * @return The description of the migration.
     */
    public String getMigrationDescription() {
        return migrationDescription;
    }

    /**
     * Returns the timestamp of when the migration was applied.
     *
     * @return The timestamp of when the migration was applied.
     */
    public String getAppliedAt() {
        return appliedAt;
    }

    /**
     * Returns the checksum associated with the migration to verify its integrity.
     *
     * @return The checksum of the migration.
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Returns a string representation of the SchemaChangelog object, including all its properties.
     *
     * @return A string representing the SchemaChangelog object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SchemaChangelog{");
        sb.append("versionNumber=").append(versionNumber);
        sb.append(", migrationDescription=").append(migrationDescription);
        sb.append(", appliedAt=").append(appliedAt);
        sb.append(", checksum=").append(checksum);
        sb.append('}');
        return sb.toString();
    }
}
