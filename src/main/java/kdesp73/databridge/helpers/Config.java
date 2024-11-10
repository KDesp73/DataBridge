package kdesp73.databridge.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
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

    // Print all the loaded properties for debugging
    public void printConfig() {
        for (String key : properties.stringPropertyNames()) {
            System.out.println(key + "=" + properties.getProperty(key));
        }
    }

    // Example method to load specific configuration like DB credentials
    public String getDbUrl() {
        return getString("db.url");
    }

    public String getDbUser() {
        return getString("db.user");
    }

    public String getDbPassword() {
        return getString("db.password");
    }

    public LogLevel getLogLevel() {
        return switch(getString("log.level")) {
			case "NONE" -> {
				yield LogLevel.NONE;
			}
			case "INFO" -> {
				yield LogLevel.INFO;
			}
			case "WARN" -> {
				yield LogLevel.WARN;
			}
			case "ERRO" -> {
				yield LogLevel.ERRO;
			}
			case "ALL" -> {
				yield LogLevel.ALL;
			}
			default -> {
				yield LogLevel.ALL;
			}
		};
    }

    public String getLogFile() {
        return getString("log.file");
    }
	
	public static void generate() {
		String templateConfig = """
								# Database Configuration
								db.url=jdbc:postgresql://localhost:5432/mydatabase
								db.user=myuser
								db.password=mypassword
								
								# Logging Configuration
								log.level=INFO
								log.file=logs/application.log
								
								# Additional Configuration
								app.name=MyApplication
								app.version=1.0.0
								""";

        try (FileWriter writer = new FileWriter(new File(CONFIG_FILE))) {
            writer.write(templateConfig);
            System.out.println("Template config file generated successfully at " + CONFIG_FILE);
        } catch (IOException e) {
            System.err.println("Error generating template config file: " + e.getMessage());
        }
	}
}
