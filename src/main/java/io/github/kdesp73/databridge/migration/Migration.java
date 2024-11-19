package io.github.kdesp73.databridge.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.github.kdesp73.databridge.helpers.FileUtils;
import io.github.kdesp73.databridge.helpers.SQLogger;

/**
 * The Migration class represents a database migration script, which includes
 * versioning, descriptions, and the SQL scripts required for upgrading and
 * downgrading a database schema.
 * <p>
 * A migration consists of an "up" script, which applies changes to the database,
 * and a "down" script, which reverts those changes. Migrations are identified by
 * a version number and a description.
 * </p>
 * <p>
 * The Migration class provides methods for reading migration files, validating
 * migration data, generating checksums, and comparing migrations by version.
 * </p>
 *
 * @author KDesp73
 */
public class Migration implements Comparable {

    private static String upTag = "@up";
    private static String downTag = "@down";
    private static String descriptionTag = "@desc";
    private static String versionTag = "@version";

    private String file;
    private String script;
    private String upScript;
    private String downScript;
    private int version;
    private String description;
    private String checksum;

    /**
     * Constructs a Migration object by reading the migration script from the specified path.
     *
     * @param path The path to the migration script file.
     */
    public Migration(String path) {
        this.file = path;
        this.loadScript(path);
    }

    /**
     * Constructs a Migration object with the specified version and description.
     *
     * @param version The version of the migration.
     * @param description A description of the migration.
     */
    public Migration(int version, String description) {
        this.version = version;
        this.description = description;
    }

    /**
     * Validates the migration by ensuring it has a valid version and an "up" script.
     *
     * @return true if the migration is valid, otherwise false.
     */
    public boolean isValid() {
        return this.version > 0 && this.upScript != null;
    }

    /**
     * Extracts the version number from a line of the migration script.
     *
     * @param line The line of the script.
     * @return The extracted version number, or -1 if not found.
     */
    private static int extractVersion(String line) {
        String pattern = String.format("--\\s*%s\\s*(.*)", versionTag);
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(line);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1).trim());
        } else {
            return -1;
        }
    }

    /**
     * Extracts the description from a line of the migration script.
     *
     * @param line The line of the script.
     * @return The extracted description, or null if not found.
     */
    private static String extractDescription(String line) {
        String pattern = String.format("--\\s*%s\\s*(.*)", descriptionTag);
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(line);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return null;
        }
    }

    /**
     * Loads the migration script from a file and parses its contents into the
     * migration's properties (upScript, downScript, version, description, etc.).
     *
     * @param path The path to the migration file.
     */
    private void loadScript(String path) {
        List<String> lines = FileUtils.readFile(path);
        if (lines == null) {
            return;
        }

        boolean upFound = false, downFound = false;

        for (String line : lines) {
            if (line.startsWith("-- " + descriptionTag)) {
                this.description = extractDescription(line);
                continue;
            }
            if (line.startsWith("-- " + versionTag)) {
                this.version = extractVersion(line);
                continue;
            }
            if (line.equals("-- " + upTag)) {
                upFound = true;
                continue;
            }
            if (line.equals("-- " + downTag)) {
                downFound = true;
                continue;
            }

            if (upFound && !downFound && !line.isBlank()) {
                if (this.upScript == null) {
                    this.upScript = line + "\n";
                } else {
                    this.upScript += line + "\n";
                }
            }

            if (downFound && upFound && !line.isBlank()) {
                if (this.downScript == null) {
                    this.downScript = line + "\n";
                } else {
                    this.downScript += line + "\n";
                }
            }
        }

        this.script = String.format(
            "-- %s %d\n-- %s %s\n\n-- %s\n%s\n-- %s\n%s",
            versionTag, version,
            descriptionTag, description,
            upTag, upScript,
            downTag, downScript
        );

        try {
            this.checksum = Migration.generateChecksum(this.script);
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(Migration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Constructs a Migration object with the specified path, version, description,
     * up script, and down script.
     *
     * @param path The path to the migration script file.
     * @param version The version of the migration.
     * @param description A description of the migration.
     * @param upScript The SQL script for applying the migration.
     * @param downScript The SQL script for reverting the migration.
     */
    public Migration(String path, int version, String description, String upScript, String downScript) {
        this.file = path;
        this.upScript = upScript;
        this.downScript = downScript;
        this.script = String.format(
            "-- %s %d\n-- %s %s\n\n-- %s\n%s\n-- %s\n%s",
            versionTag, version,
            descriptionTag, description,
            upTag, upScript,
            downTag, downScript
        );
        try {
            this.checksum = Migration.generateChecksum(this.script);
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(Migration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a string representation of the Migration object, including all its properties.
     *
     * @return A string representing the Migration object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Migration{");
        sb.append("file=").append(file);
        sb.append(", script=").append(script);
        sb.append(", upScript=").append(upScript);
        sb.append(", downScript=").append(downScript);
        sb.append(", version=").append(version);
        sb.append(", description=").append(description);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Returns the file path of the migration script.
     *
     * @return The file path of the migration script.
     */
    public String getFile() {
        return file;
    }

    /**
     * Returns the full migration script, including version, description, up and down scripts.
     *
     * @return The full migration script.
     */
    public String getScript() {
        return script;
    }

    /**
     * Returns the "up" script of the migration.
     *
     * @return The "up" script.
     */
    public String getUpScript() {
        return upScript;
    }

    /**
     * Returns the "down" script of the migration.
     *
     * @return The "down" script.
     */
    public String getDownScript() {
        return downScript;
    }

    /**
     * Returns the version of the migration.
     *
     * @return The version of the migration.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Returns the description of the migration.
     *
     * @return The description of the migration.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the checksum of the entire migration script.
     *
     * @return The checksum of the migration script.
     */
    public String getChecksum() {
        try {
            return generateChecksum(this.script);
        } catch (IOException | NoSuchAlgorithmException ex) {
            return null;
        }
    }

    /**
     * Returns the checksum of the "up" script.
     *
     * @return The checksum of the "up" script.
     */
    public String getChecksumUp() {
        try {
            return generateChecksum(this.upScript);
        } catch (IOException | NoSuchAlgorithmException ex) {
            return null;
        }
    }

    /**
     * Generates the checksum of the provided script using MD5 hashing.
     *
     * @param script The script to generate the checksum for.
     * @return The checksum of the script.
     * @throws IOException If an I/O error occurs while processing the script.
     * @throws NoSuchAlgorithmException If the MD5 algorithm is unavailable.
     */
    public static String generateChecksum(String script) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(script.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Compares this migration to another based on their version numbers.
     *
     * @param other The other migration to compare to.
     * @return A negative integer, zero, or a positive integer as this migration
     *         is less than, equal to, or greater than the other migration.
     */
    @Override
    public int compareTo(Object other) {
        if (other == null || !(other instanceof Migration)) {
            return 1;
        }
        Migration otherMigration = (Migration) other;
        return Integer.compare(this.version, otherMigration.version);
    }

    /**
     * Returns a string representing the path and version of this migration.
     *
     * @return A string representing the migration path and version.
     */
    public String toShortString() {
        return String.format("%s (%d)", file, version);
    }

	/**
	 * Generates a template migration file in the current working directory
	 *
	 * @throws IOException
	 */
	public static void templateMigration() throws IOException {
		String content = """
                   -- @version <version>
                   -- @desc <description>

                   -- @up
                   <sql script>

                   -- @down
                   <sql script>
                   """;

		FileUtils.writeFile(System.getProperty("user.dir") + "/migration.sql", content);
	}
}
