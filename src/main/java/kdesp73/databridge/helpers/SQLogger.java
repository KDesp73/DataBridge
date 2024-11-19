package kdesp73.databridge.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLogger is a logging utility class designed for logging SQL operations,
 * including INSERT, UPDATE, DELETE, SELECT, and others. It supports various
 * log levels (e.g., NONE, ERRO, WARN, INFO, ALL) and log types (e.g., FILE, STDERR, ALL).
 * It can log to standard error, log files, or both.
 * It also includes a method for formatting and printing SQL query result sets in a tabular format.
 *
 * @version 0.0.2
 * @author KDesp73
 */
public class SQLogger {

    /**
     * The current version of the SQLogger.
     */
    private static final String VERSION = "0.0.2";

    /**
     * The default log file name.
     */
    private static final String LOG_FILE = "sqlogger.log";

    /**
     * The singleton instance of SQLogger.
     */
    private static SQLogger instance;

    /**
     * The log level for filtering logs.
     */
    private LogLevel logLevel;

    /**
     * The log type for determining the output destinations.
     */
    private LogType logType;

    /**
     * Enum representing different log levels.
     */
    public enum LogLevel {
        NONE, ERRO, WARN, INFO, ALL
    }

    /**
     * Enum representing different log output types (e.g., file, stderr, both).
     */
    public enum LogType {
        FILE, STDERR, ALL
    }

    /**
     * Enum representing different SQL operations to log.
     */
    public enum SQLOperation {
        INSERT, UPDATE, DELETE, SELECT, CREATE, DROP, ALTER, TRUNCATE, RENAME,
        GRANT, REVOKE, MERGE, EXPLAIN, WITH, LOCK, SAVEPOINT, ROLLBACK, COMMIT
    }

    /**
     * Private constructor to initialize the SQLogger with specific log level and log type.
     *
     * @param logLevel the log level.
     * @param logType the log type.
     */
    private SQLogger(LogLevel logLevel, LogType logType) {
        this.logLevel = logLevel;
        this.logType = logType;
    }

    /**
     * Returns the singleton instance of SQLogger with specified log level and log type.
     *
     * @param logLevel the log level.
     * @param logType the log type.
     * @return the SQLogger instance.
     */
    public static SQLogger getLogger(LogLevel logLevel, LogType logType) {
        if (instance == null) {
            instance = new SQLogger(logLevel, logType);
        }
        return instance;
    }

    /**
     * Returns the singleton instance of SQLogger with specified log level, and default log type as ALL.
     *
     * @param logLevel the log level.
     * @return the SQLogger instance.
     */
    public static SQLogger getLogger(LogLevel logLevel) {
        if (instance == null) {
            instance = new SQLogger(logLevel, LogType.ALL);
        }
        return instance;
    }

    /**
     * Returns the singleton instance of SQLogger with default log level (ALL) and log type (ALL).
     *
     * @return the SQLogger instance.
     */
    public static SQLogger getLogger() {
        if (instance == null) {
            instance = new SQLogger(LogLevel.ALL, LogType.ALL);
        }
        return instance;
    }

    /**
     * Returns the log file path, either from the configuration or the default file.
     *
     * @return the log file path.
     */
    private static String logFile() {
        Config c = Config.getInstance();
        if (c == null || c.getLogFile() == null || c.getLogFile().isBlank()) {
            return LOG_FILE;
        }

        return c.getLogFile();
    }

    /**
     * Prints the current configuration of the SQLogger.
     */
    public static void printSelf() {
        SQLogger logger = getLogger();
        System.out.println("[SQLogger v" + VERSION + "]");
        System.out.println("  LogLevel: " + logger.logLevel);
        System.out.println("  LogType: " + logger.logType);
        System.out.println("  LogFile: " + logFile());
    }

    /**
     * Checks if a given log level should be logged based on the current log level.
     *
     * @param level the log level to check.
     * @return true if the log level should be logged, false otherwise.
     */
    private boolean shouldLog(LogLevel level) {
        return this.logLevel.ordinal() <= level.ordinal();
    }

    /**
     * Checks if logging to stderr is enabled.
     *
     * @return true if logging to stderr is enabled, false otherwise.
     */
    private boolean shouldLogToStderr() {
        return this.logType == LogType.STDERR || this.logType == LogType.ALL;
    }

    /**
     * Checks if logging to a file is enabled.
     *
     * @return true if logging to a file is enabled, false otherwise.
     */
    private boolean shouldLogToFile() {
        return this.logType == LogType.FILE || this.logType == LogType.ALL;
    }

    /**
     * Returns the current timestamp formatted as "yyyy-MM-dd HH:mm:ss".
     *
     * @return the current timestamp.
     */
    public static String getCurrentTimestamp() {
        Instant now = Instant.now();
        LocalDateTime dateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    /**
     * Appends a message to the log file.
     *
     * @param msg the message to append.
     * @return true if the message was successfully written, false otherwise.
     */
    private boolean appendToFile(String msg) {
        try {
            Files.write(Paths.get(logFile()),
                msg.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException ex) {
            System.err.println("Failed to write to log file: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Formats an error log entry with the exception details.
     *
     * @param msg the message to log.
     * @param ex the exception to log.
     * @return the formatted log entry.
     */
    private String formatEntry(String msg, Exception ex) {
        return String.format("[ERRO %s] %s ( %s )", getCurrentTimestamp(), msg, ex.getMessage());
    }

    /**
     * Formats a SQL log entry with operation details.
     *
     * @param msg the message to log.
     * @param op the SQL operation being logged.
     * @param obj the object related to the SQL operation.
     * @return the formatted SQL log entry.
     */
    private String formatEntry(String msg, SQLOperation op, Object obj) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[SQL  %s] %s", getCurrentTimestamp(), op));
        if (obj != null) {
            sb.append(" for ").append(obj);
        }
        if (msg != null) {
            sb.append(" ( ").append(msg).append(" )");
        }
        return sb.toString();
    }

    /**
     * Logs a message with the specified log level and formatted arguments.
     *
     * @param lvl the log level.
     * @param fmt the format string.
     * @param args the arguments for the format string.
     */
    public void log(LogLevel lvl, String fmt, Object... args) {
        if (!shouldLog(lvl)) {
            return;
        }

        String msg = String.format(fmt, args);

        if (!shouldLog(Config.getInstance().getLogLevel())) {
            return;
        }
        if (shouldLogToStderr()) {
            System.err.println(msg);
        }
        if (shouldLogToFile()) {
            appendToFile(msg + "\n\n");
        }
    }

    /**
     * Logs a message with the specified log level.
     *
     * @param lvl the log level.
     * @param msg the message to log.
     */
    public void log(LogLevel lvl, String msg) {
        if (!shouldLog(lvl)) {
            return;
        }
        if (shouldLogToStderr()) {
            System.err.println(msg);
        }
        if (shouldLogToFile()) {
            appendToFile(msg + "\n\n");
        }
    }

    /**
     * Logs a message and an exception with the specified log level.
     *
     * @param lvl the log level.
     * @param msg the message to log.
     * @param ex the exception to log.
     */
    public void log(LogLevel lvl, String msg, Exception ex) {
        if (!shouldLog(lvl)) {
            return;
        }
        log(lvl, formatEntry(msg, ex));
    }

    /**
     * Logs a SQL operation with the specified log level and operation details.
     *
     * @param lvl the log level.
     * @param msg the message to log.
     * @param op the SQL operation being logged.
     * @param obj the object related to the operation.
     */
    public void log(LogLevel lvl, String msg, SQLOperation op, Object obj) {
        if (!shouldLog(lvl)) {
            return;
        }
        log(lvl, formatEntry(msg, op, obj));
    }

    /**
     * Logs the result set of a SQL query, printing the results in a formatted table.
     *
     * @param resultSet the result set to log.
     */
    public void logResultSet(ResultSet resultSet) {
        try {
            ResultSetMetaData metadata = resultSet.getMetaData();
            int columnCount = metadata.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metadata.getColumnName(i));
            }

            String separator = "+" + "-".repeat(20) + "+";
            String header = "| " + String.join(" | ", columnNames) + " |";
            System.out.println(separator);
            System.out.println(header);
            System.out.println(separator);

            while (resultSet.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getString(i));
                }
                System.out.println("| " + String.join(" | ", row) + " |");
                System.out.println(separator);
            }
        } catch (SQLException e) {
            log(LogLevel.ERRO, "Failed to log ResultSet", e);
        }
    }
}
