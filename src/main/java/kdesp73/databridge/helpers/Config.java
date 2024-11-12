package kdesp73.databridge.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import kdesp73.databridge.connections.DatabaseConnection;
import kdesp73.databridge.connections.PostgresConnection;
import kdesp73.databridge.helpers.SQLogger.LogLevel;

public class Config {

	private static final String CONFIG_FILE = "config.properties";
	private Properties properties;
	private static Config instance;

	private Config() {
		properties = new Properties();
		loadConfig();
	}

	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

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

	public void saveConfig() {
		try (FileOutputStream output = new FileOutputStream(Config.CONFIG_FILE)) {
			properties.store(output, "Configuration File");
			System.out.println("Configuration saved successfully.");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// Get a property as a string
	public String getString(String key) {
		return properties.getProperty(key);
	}

	// Get a property as an integer
	public int getInt(String key) {
		String value = properties.getProperty(key);
		return value != null ? Integer.parseInt(value) : 0;
	}

	// Get a property as a boolean
	public boolean getBoolean(String key) {
		String value = properties.getProperty(key);
		return value != null ? Boolean.parseBoolean(value) : false;
	}

	// Set a property (optional, for saving config back to file)
	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	public void printConfig() {
		for (String key : properties.stringPropertyNames()) {
			System.out.println(key + "=" + properties.getProperty(key));
		}
	}

	public String getDbUrl() {
		return getString("db.url");
	}

	public String getDbUser() {
		return getString("db.user");
	}

	public String getDbPassword() {
		return getString("db.password");
	}
	
	public String getDbType() {
		return getString("db.type");
	}


	public boolean getDbRetry() {
		return getBoolean("db.retry");
	}

	public int getDbRetryTimes() {
		return getInt("db.retry.times");
	}

	public int getDbRetryDelay() {
		return getInt("db.retry.delay");
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

	public static void generate() {
		Properties defaultProperties = new Properties();
		defaultProperties.setProperty("db.url", "jdbc:postgresql://localhost:5432/mydatabase");
		defaultProperties.setProperty("db.user", "myuser");
		defaultProperties.setProperty("db.password", "mypassword");
		defaultProperties.setProperty("db.retry", "true");
		defaultProperties.setProperty("db.retry.times", "3");
		defaultProperties.setProperty("db.retry.delay", "1000");
		defaultProperties.setProperty("db.type", "sqlite3");

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
