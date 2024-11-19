package io.github.kdesp73.databridge.connections;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.github.kdesp73.databridge.helpers.Config;

/**
 * The {@code AvailableConnections} enum defines the supported database connections
 * (SQLITE, POSTGRES, MSACCESS) and provides a method to retrieve a database
 * connection instance based on the selected type.
 * <p>
 * Each enum constant corresponds to a specific database connection implementation,
 * and upon initialization, it tries to instantiate the associated connection class
 * using reflection.
 * <p>
 * The {@code getConnection()} method retrieves a configured connection instance,
 * ensuring that the necessary configuration properties are available before
 * establishing a connection.
 * <p>
 * The supported connection types are:
 * <ul>
 *   <li>{@code SQLITE} - SQLite database connection.</li>
 *   <li>{@code POSTGRES} - PostgreSQL database connection.</li>
 *   <li>{@code MSACCESS} - MS Access database connection.</li>
 * </ul>
 *
 * @author KDesp73
 */
public enum AvailableConnections {

    /**
     * SQLite connection type.
     */
    SQLITE(SQLiteConnection.class),

    /**
     * PostgreSQL connection type.
     */
    POSTGRES(PostgresConnection.class),

    /**
     * MS Access connection type.
     */
    MSACCESS(MSAccessConnection.class);

    private DatabaseConnection connection;

    /**
     * Constructs an {@code AvailableConnections} enum constant and tries to instantiate
     * the corresponding connection class via reflection.
     *
     * @param clazz The class of the database connection to instantiate.
     */
    AvailableConnections(Class<? extends DatabaseConnection> clazz) {
        try {
            this.connection = clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException ex) {
            Logger.getLogger(AvailableConnections.class.getName()).log(Level.SEVERE, "Failed to instantiate connection", ex);
        }
    }

    /**
     * Returns a configured database connection instance based on the configuration
     * properties.
     * <p>
     * This method will attempt to connect using the URL, user, and password from
     * the {@code Config} class. If no valid configuration is found, an exception is thrown.
     *
     * @return A {@link DatabaseConnection} instance configured and connected to the database.
     * @throws SQLException If there is an error establishing the database connection.
     * @throws UnsupportedOperationException If the configuration properties are not properly set up.
     */
    public DatabaseConnection getConnection() throws SQLException {
        Config c = Config.getInstance();
        if (c == null) {
            throw new UnsupportedOperationException("You need to have config.properties set up. Run `Config.generate()`");
        }
        this.connection.connect(c.getDbUrl(), c.getDbUser(), c.getDbPassword());
        return connection;
    }

    /**
     * Returns the name of the enum constant as a string.
     *
     * @return The name of the enum constant.
     */
    @Override
    public String toString() {
        return this.name();
    }
}
