package io.github.kdesp73.databridge.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import io.github.kdesp73.databridge.connections.DatabaseConnection;
import io.github.kdesp73.databridge.connections.PostgresConnection;
import io.github.kdesp73.databridge.helpers.SQLogger.LogLevel;

/**
 * The {@code Config} class is a singleton utility class that loads and saves application
 * configuration from and to a properties file. It provides methods to retrieve configuration
 * properties like database connection details, retry options, and logging settings.
 * <p>
 * The configuration is loaded from a file named {@code config.properties} and can be saved
 * back to the file. The class also provides default values and methods to generate a default
 * configuration file if the config file is missing.
 * </p>
 *
 * @author KDesp73
 */
public class Config {

    // The path to the configuration file
    private static final String CONFIG_FILE = "config.properties";
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
     * Loads the configuration from the {@code config.properties} file.
     * If the file is not found or there is an error loading it, an error message is displayed.
     */
    private void loadConfig() {
        try (InputStream inputStream = new FileInputStream(CONFIG_FILE)) {
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            System.err.println("Config file not found: " + CONFIG_FILE);
            System.out.println("Call Config.generate() to create a template config file");
        } catch (IOException e) {
            System.err.println("Error loading config file: " + e.getMessage());
        }
    }

    /**
     * Saves the current configuration properties back to the {@code config.properties} file.
     */
    public void saveConfig() {
        try (FileOutputStream output = new FileOutputStream(Config.CONFIG_FILE)) {
            properties.store(output, "Configuration File");
            System.out.println("Configuration saved successfully.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets a property as a string.
     *
     * @param key The property key.
     * @return The value of the property, or {@code null} if not found.
     */
    public String getString(String key) {
        return properties.getProperty(key);
    }

    /**
     * Gets a property as an integer.
     *
     * @param key The property key.
     * @return The integer value of the property, or 0 if not found.
     */
    public int getInt(String key) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : 0;
    }

    /**
     * Gets a property as a boolean.
     *
     * @param key The property key.
     * @return The boolean value of the property, or {@code false} if not found.
     */
    public boolean getBoolean(String key) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : false;
    }

    /**
     * Sets a property to a new value. This method does not automatically save the configuration
     * to the file, but allows for setting new properties in memory.
     *
     * @param key The property key.
     * @param value The new value for the property.
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * Prints all the properties in the configuration file to the console.
     */
    public void printConfig() {
        for (String key : properties.stringPropertyNames()) {
            System.out.println(key + "=" + properties.getProperty(key));
        }
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
        return getString("db.user");
    }

    /**
     * Gets the database password from the configuration.
     *
     * @return The database password.
     */
    public String getDbPassword() {
        return getString("db.password");
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

    /**
     * Gets the log level from the configuration.
     *
     * @return The log level.
     */
    public LogLevel getLogLevel() {
        String level = getString("log.level");
        if(level == null) return LogLevel.ALL;
        try {
            return LogLevel.valueOf(level);
        } catch (IllegalArgumentException e) {
            return LogLevel.ALL; // Default value
        }
    }

    /**
     * Gets the log file path from the configuration.
     *
     * @return The log file path.
     */
    public String getLogFile() {
        return getString("log.file");
    }

    /**
     * Generates a default configuration file with recommended settings.
     * This method creates a template {@code config.properties} file if one does not exist.
     */
    public static void generate() {
        Properties defaultProperties = new Properties();
        defaultProperties.setProperty("db.url", "jdbc:postgresql://localhost:5432/mydatabase");
        defaultProperties.setProperty("db.user", "myuser");
        defaultProperties.setProperty("db.password", "mypassword");
        defaultProperties.setProperty("db.retry", "true");
        defaultProperties.setProperty("db.retry.times", "3");
        defaultProperties.setProperty("db.retry.delay", "1000");

        defaultProperties.setProperty("log.level", "INFO");
        defaultProperties.setProperty("log.file", "logs/application.log");

        defaultProperties.setProperty("app.name", "MyApplication");
        defaultProperties.setProperty("app.version", "1.0.0");

        try (FileOutputStream output = new FileOutputStream(Config.CONFIG_FILE)) {
            defaultProperties.store(output, "Default Configuration");
            System.out.println("Default config file created at: " + Config.CONFIG_FILE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
