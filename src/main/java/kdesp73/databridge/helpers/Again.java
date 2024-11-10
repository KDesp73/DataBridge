package kdesp73.databridge.helpers;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Again {

    // Default delay between retries in milliseconds
    private static final long DEFAULT_DELAY_MS = 1000;
    
    // Default maximum number of retries
    private static final int DEFAULT_MAX_RETRIES = 3;
	
	private static boolean active() {
		if(Config.getInstance() == null) return true;
		
		return Config.getInstance().getDbRetry();
	}
	
	public static long delay() {
		if(Config.getInstance() == null) return DEFAULT_DELAY_MS;
		
		return Config.getInstance().getDbRetryDelay();
	}
	
	public static int retries() {
		if(Config.getInstance() == null) return DEFAULT_MAX_RETRIES;
		
		return Config.getInstance().getDbRetryTimes();
	}

    /**
     * Retries a task a specified number of times with a delay between retries.
     * 
     * @param task The task to retry.
     * @param maxRetries The maximum number of retry attempts.
     * @param delay The delay between retry attempts in milliseconds.
     * @param <T> The return type of the task.
     * @return The result of the task, or null if the task failed after the maximum retries.
	 * @throws java.sql.SQLException
     */
    public static <T> T retryWithDelay(RetryableTask<T> task, int maxRetries, long delay) throws SQLException {
		if(!active()) {
			return task.execute();
		}
		
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                return task.execute();
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxRetries) {
                    System.err.println("Task failed after " + maxRetries + " attempts.");
                    return null;
                }
                sleep(delay);
            }
        }
        return null;
    }

    /**
     * Retries a task with exponential backoff.
     * 
     * @param task The task to retry.
     * @param maxRetries The maximum number of retry attempts.
     * @param initialDelay The initial delay between retries in milliseconds.
     * @param <T> The return type of the task.
     * @return The result of the task, or null if the task failed after the maximum retries.
	 * @throws java.sql.SQLException
     */
    public static <T> T retryWithExponentialBackoff(RetryableTask<T> task, int maxRetries, long initialDelay) throws SQLException {
		if(!active()) {
			return task.execute();
		}
		
        int attempt = 0;
        long delay = initialDelay;
        while (attempt < maxRetries) {
            try {
                return task.execute();
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxRetries) {
                    System.err.println("Task failed after " + maxRetries + " attempts.");
                    return null;
                }
                sleep(delay);
                delay *= 2; // Exponential backoff: doubling the delay each time
            }
        }
        return null;
    }

    private static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @FunctionalInterface
    public interface RetryableTask<T> {
        T execute() throws SQLException;
    }
}
