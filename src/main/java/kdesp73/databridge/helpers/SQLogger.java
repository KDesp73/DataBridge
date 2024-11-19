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

public class SQLogger {

	private static final String VERSION = "0.0.2";
	private static final String LOG_FILE = "sqlogger.log";
	private static SQLogger instance;

	private LogLevel logLevel;
	private LogType logType;

	public enum LogLevel {
		NONE, ERRO, WARN, INFO, ALL
	}

	public enum LogType {
		FILE, STDERR, ALL
	}

	public enum SQLOperation {
		INSERT, UPDATE, DELETE, SELECT, CREATE, DROP, ALTER, TRUNCATE, RENAME,
		GRANT, REVOKE, MERGE, EXPLAIN, WITH, LOCK, SAVEPOINT, ROLLBACK, COMMIT
	}

	private SQLogger(LogLevel logLevel, LogType logType) {
		this.logLevel = logLevel;
		this.logType = logType;
	}

	public static SQLogger getLogger(LogLevel logLevel, LogType logType) {
		if (instance == null) {
			instance = new SQLogger(logLevel, logType);
		}
		return instance;
	}

	public static SQLogger getLogger(LogLevel logLevel) {
		if (instance == null) {
			instance = new SQLogger(logLevel, LogType.ALL);
		}
		return instance;
	}

	public static SQLogger getLogger() {
		if (instance == null) {
			instance = new SQLogger(LogLevel.ALL, LogType.ALL);
		}
		return instance;
	}

	private static String logFile() {
		Config c = Config.getInstance();
		if (c == null || c.getLogFile() == null || c.getLogFile().isBlank()) {
			return LOG_FILE;
		}

		return c.getLogFile();
	}

	public static void printSelf() {
		SQLogger logger = getLogger();
		System.out.println("[SQLogger v" + VERSION + "]");
		System.out.println("  LogLevel: " + logger.logLevel);
		System.out.println("  LogType: " + logger.logType);
		System.out.println("  LogFile: " + logFile());
	}

	private boolean shouldLog(LogLevel level) {
		return this.logLevel.ordinal() <= level.ordinal();
	}

	private boolean shouldLogToStderr() {
		return this.logType == LogType.STDERR || this.logType == LogType.ALL;
	}

	private boolean shouldLogToFile() {
		return this.logType == LogType.FILE || this.logType == LogType.ALL;
	}

	public static String getCurrentTimestamp() {
		Instant now = Instant.now();
		LocalDateTime dateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return dateTime.format(formatter);
	}

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

	private String formatEntry(String msg, Exception ex) {
		return String.format("[ERRO %s] %s ( %s )", getCurrentTimestamp(), msg, ex.getMessage());
	}

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

	public void log(LogLevel lvl, String msg, Exception ex) {
		if (!shouldLog(lvl)) {
			return;
		}
		log(lvl, formatEntry(msg, ex));
	}

	public void logSQL(LogLevel lvl, String msg, SQLOperation op, Object obj) {
		if (!shouldLog(lvl)) {
			return;
		}
		if (op == null) {
			return;
		}
		log(lvl, formatEntry(msg, op, obj));
	}

	public void log(String fmt, Object... args) {
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

	public void log(String msg) {
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

	public void log(String msg, Exception ex) {
		LogLevel lvl = Config.getInstance().getLogLevel();
		if (!shouldLog(lvl)) {
			return;
		}
		log(lvl, formatEntry(msg, ex));
	}

	public void logSQL(String msg, SQLOperation op, Object obj) {
		LogLevel lvl = Config.getInstance().getLogLevel();
		if (!shouldLog(lvl)) {
			return;
		}
		if (op == null) {
			return;
		}
		log(lvl, formatEntry(msg, op, obj));
	}

	public void logResultSet(ResultSet resultSet) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();

		List<String> columnNames = new ArrayList<>();
		List<Integer> columnWidths = new ArrayList<>();

		for (int i = 1; i <= columnCount; i++) {
			String columnName = metaData.getColumnName(i);
			columnNames.add(columnName);
			columnWidths.add(columnName.length());
		}

		List<List<String>> rows = new ArrayList<>();
		while (resultSet.next()) {
			List<String> row = new ArrayList<>();
			for (int i = 1; i <= columnCount; i++) {
				String value = resultSet.getString(i);
				if (value == null) {
					value = "NULL";
				}
				row.add(value);

				columnWidths.set(i - 1, Math.max(columnWidths.get(i - 1), value.length()));
			}
			rows.add(row);
		}

		printTableBorderTop(columnWidths);
		printRow(columnNames, columnWidths);
		printTableBorderMiddle(columnWidths);
		for (List<String> row : rows) {
			printRow(row, columnWidths);
		}
		printTableBorderBottom(columnWidths);
	}

	private static void printTableBorderTop(List<Integer> columnWidths) {
		System.out.print("┌");
		for (int i = 0; i < columnWidths.size(); i++) {
			int width = columnWidths.get(i);
			System.out.print("─".repeat(width + 2) + ((i == columnWidths.size()-1) ? "┐" : "┬"));
		}
		System.out.println();
	}

	private static void printTableBorderBottom(List<Integer> columnWidths) {
		System.out.print("└");
		for (int i = 0; i < columnWidths.size(); i++) {
			int width = columnWidths.get(i);
			System.out.print("─".repeat(width + 2) + ((i == columnWidths.size()-1) ? "┘" : "┴"));
		}
		System.out.println();
	}

	private static void printTableBorderMiddle(List<Integer> columnWidths) {
		System.out.print("├");
		for (int i = 0; i < columnWidths.size(); i++) {
			int width = columnWidths.get(i);
			System.out.print("─".repeat(width + 2) + ((i == columnWidths.size()-1) ? "┤" : "┼"));
		}
		System.out.println();
	}

	private static void printRow(List<String> row, List<Integer> columnWidths) {
		System.out.print("│");
		for (int i = 0; i < row.size(); i++) {
			String value = row.get(i);
			System.out.printf(" %-" + columnWidths.get(i) + "s │", value);
		}
		System.out.println();
	}
}
