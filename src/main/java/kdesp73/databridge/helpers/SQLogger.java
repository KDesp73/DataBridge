package kdesp73.databridge.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SQLogger {

	private static final String VERSION = "0.0.2";
	private static final String LOG_FILE = "sqlogger.log";
	private static SQLogger instance;

	private LogLevel logLevel;
	private LogType logType;

	public enum LogLevel {
		NONE, INFO, WARN, ERRO, ALL
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

	public static void printSelf() {
		SQLogger logger = getLogger();
		System.out.println("[SQLogger v" + VERSION + "]");
		System.out.println("  LogLevel: " + logger.logLevel);
		System.out.println("  LogType: " + logger.logType);
		System.out.println("  LogFile: " + LOG_FILE);
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

	private static String getCurrentTimestamp() {
		Instant now = Instant.now();
		LocalDateTime dateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return dateTime.format(formatter);
	}

	private boolean appendToFile(String msg) {
		try {
			Files.write(Paths.get(LOG_FILE),
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
}
