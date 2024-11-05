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

/**
 *
 * @author kdesp73
 */
public class SQLogger {

	private static String VERSION = "0.0.1";

	public enum LogLevel {
		NONE, INFO, WARN, ERRO, ALL
	};

	public enum LogType {
		FILE, STDERR, ALL
	};

	public enum SQLOperation {
		INSERT, UPDATE, DELETE, SELECT, CREATE, DROP
	};

	private static String LOG_FILE = "sqlogger.log";
	private LogLevel logLevel;
	private LogType logType;
	private static SQLogger instance;

	public SQLogger(LogLevel logLevel, LogType logType) {
		this.logLevel = logLevel;
		this.logType = logType;
	}

	public SQLogger(LogLevel logLevel) {
		this.logLevel = logLevel;
		this.logType = LogType.ALL;
	}

	public static SQLogger getLogger(LogLevel logLevel, LogType logType) {
		SQLogger.instance = new SQLogger(logLevel, logType);

		return SQLogger.instance;
	}

	public static SQLogger getLogger(LogLevel logLevel) {
		SQLogger.instance = new SQLogger(
			logLevel,
			(SQLogger.instance == null)
				? SQLogger.LogType.ALL
				: SQLogger.instance.logType
		);

		return SQLogger.instance;
	}

	public static SQLogger getLogger() {
		if (SQLogger.instance == null) {
			SQLogger.instance = new SQLogger(LogLevel.ALL, LogType.ALL);
		}

		return SQLogger.instance;
	}

	public static void printSelf() {
		var logger = SQLogger.getLogger();
		System.out.println("[SQLogger v" + SQLogger.VERSION + "]");
		System.out.println("  LogLevel: " + logger.logLevel);
		System.out.println("  LogType: " + logger.logType);
		System.out.println("  LogFile: " + SQLogger.LOG_FILE);
	}

	private boolean logToStderr() {
		return this.logType == LogType.STDERR || this.logType == LogType.ALL;
	}

	private boolean logToFile() {
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
			File f = new File(SQLogger.LOG_FILE);
			if (!f.exists()) {
				Path newFilePath = Paths.get(SQLogger.LOG_FILE);
				Files.createFile(newFilePath);
			}

			Files.write(Paths.get(SQLogger.LOG_FILE),
				msg.getBytes(),
				StandardOpenOption.APPEND);
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	public void log(String msg) {
		if (this.logLevel == LogLevel.NONE) {
			return;
		}

		if (logToStderr()) {
			System.err.println(msg + "\n");
		}

		if (logToFile()) {
			appendToFile(msg + "\n\n");
		}
	}

	private String formatEntry(String msg, Exception ex) {
		return "[ERRO " + SQLogger.getCurrentTimestamp() + "] " + msg + " ( " + ex.getMessage() + " )";
	}

	public void log(String msg, Exception ex) {
		if (this.logLevel.ordinal() < LogLevel.ERRO.ordinal()) {
			return;
		}

		log(formatEntry(msg, ex));
	}

	private String formatEntry(String msg, SQLOperation op, Object obj) {
		StringBuilder sb = new StringBuilder();
		sb.append("[SQL  ");
		sb.append(SQLogger.getCurrentTimestamp());
		sb.append("] ");
		sb.append(op);
		if (obj != null) {
			sb.append(" for ");
			sb.append(obj.toString());
		}
		if (msg != null) {
			sb.append(" ( ");
			sb.append(msg);
			sb.append(" )");
		}
		return sb.toString();
	}

	public void logSQL(String msg, SQLOperation op, Object obj) {
		if (op == null) {
			return;
		}

		log(formatEntry(msg, op, obj));
	}
}
