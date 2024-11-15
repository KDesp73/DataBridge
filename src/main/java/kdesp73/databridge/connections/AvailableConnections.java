package kdesp73.databridge.connections;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kdesp73.databridge.helpers.Config;

/**
 *
 * @author kdesp73
 */
public enum AvailableConnections {
	SQLITE(SQLiteConnection.class),
	POSTGRES(PostgresConnection.class),
	MSACCESS(MSAccessConnection.class);

	private DatabaseConnection connection;

	AvailableConnections(Class<? extends DatabaseConnection> clazz) {
		try {
			this.connection = clazz.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException ex) {
			Logger.getLogger(AvailableConnections.class.getName()).log(Level.SEVERE, "Failed to instantiate connection", ex);
		}
	}

	public DatabaseConnection getConnection() throws SQLException {
		Config c = Config.getInstance();
		if (c == null) {
			throw new UnsupportedOperationException("You need to have config.properties set up. Run `Config.generate()`");
		}
		this.connection.connect(c.getDbUrl(), c.getDbUser(), c.getDbPassword());

		return connection;
	}

	@Override
	public String toString() {
		return this.name();
	}
}
