package io.github.kdesp73.databridge.helpers;

import java.io.*;
import java.util.Properties;
import io.github.kdesp73.databridge.helpers.SQLogger.LogLevel;

/**
 * The {@code Config} class is a singleton utility class that loads and saves application
 * configuration from and to a properties file located in src/resources.
 */
public class Config {

	// Path to the configuration file in src/main/resources
	private static final String CONFIG_FILE = "src/main/resources/config.properties";
	private Properties properties;
	private static Config instance;

	/**
	 * Private constructor for the singleton pattern. Initializes the properties and loads
	 * the configuration from the config file.
	 */
	private Config() {
		properties = new Properties();
		loadConfig();
	}

	/**
	 * Gets the singleton instance of the {@code Config} class.
	 *
	 * @return The singleton instance of the {@code Config} class.
	 */
	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	/**
	 * Loads the configuration from the {@code config.properties} file located in resources.
	 * If the file is not found or there is an error loading it, an error message is displayed.
	 */
	private void loadConfig() {
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
			if (inputStream == null) {
				System.err.println("Config file not found in classpath. Generating default config.");
				generate();
			} else {
				properties.load(inputStream);
			}
		} catch (IOException e) {
			System.err.println("Error loading config file: " + e.getMessage());
		}
	}

	/**
	 * Saves the current configuration properties back to the {@code config.properties} file.
	 */
	public void saveConfig() {
		try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
			properties.store(output, "Updated Configuration File");
			System.out.println("Configuration saved successfully to: " + CONFIG_FILE);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Generates a default configuration file in the {@code src/resources} directory.
	 */
	public static void generate() {
		Properties defaultProperties = new Properties();
		defaultProperties.setProperty("db.url", "jdbc:postgresql://localhost:5432/mydatabase");
		defaultProperties.setProperty("db.user", "DB_USERNAME");
		defaultProperties.setProperty("db.password", "DB_PASSWORD");
		defaultProperties.setProperty("db.retry", "true");
		defaultProperties.setProperty("db.retry.times", "3");
		defaultProperties.setProperty("db.retry.delay", "1000");

		defaultProperties.setProperty("log.level", "INFO");
		defaultProperties.setProperty("log.file", "logs/application.log");

		defaultProperties.setProperty("app.name", "MyApplication");
		defaultProperties.setProperty("app.version", "1.0.0");

		try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
			defaultProperties.store(output, "Default Configuration");
			System.out.println("Default config file created at: " + CONFIG_FILE);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// Other existing methods remain unchanged...
	public String getString(String key) {
		return properties.getProperty(key);
	}

	public int getInt(String key) {
		String value = properties.getProperty(key);
		return value != null ? Integer.parseInt(value) : 0;
	}

	public boolean getBoolean(String key) {
		String value = properties.getProperty(key);
		return value != null ? Boolean.parseBoolean(value) : false;
	}

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	public void printConfig() {
		for (String key : properties.stringPropertyNames()) {
			System.out.println(key + "=" + properties.getProperty(key));
		}
	}

	public LogLevel getLogLevel() {
		String level = getString("log.level");
		if(level == null) return LogLevel.ALL;
		try {
			return LogLevel.valueOf(level);
		} catch (IllegalArgumentException e) {
			return LogLevel.ALL; // Default value
		}
	}

	public String getLogFile() {
		return getString("log.file");
	}
	/**
	 * Gets the database URL from the configuration.
	 *
	 * @return The database URL.
	 */
	public String getDbUrl() {
		return getString("db.url");
	}

	/**
	 * Gets the database user from the configuration.
	 *
	 * @return The database user.
	 */
	public String getDbUser() {
		return Environment.get(getString("db.user"));
	}

	/**
	 * Gets the database password from the configuration.
	 *
	 * @return The database password.
	 */
	public String getDbPassword() {
		return Environment.get(getString("db.password"));
	}

	/**
	 * Gets the database retry setting from the configuration.
	 *
	 * @return {@code true} if retries are enabled, otherwise {@code false}.
	 */
	public boolean getDbRetry() {
		return getBoolean("db.retry");
	}

	/**
	 * Gets the number of retry attempts for the database connection.
	 *
	 * @return The number of retry attempts.
	 */
	public int getDbRetryTimes() {
		return getInt("db.retry.times");
	}

	/**
	 * Gets the delay between retry attempts for the database connection.
	 *
	 * @return The delay between retry attempts in milliseconds.
	 */
	public int getDbRetryDelay() {
		return getInt("db.retry.delay");
	}
}
