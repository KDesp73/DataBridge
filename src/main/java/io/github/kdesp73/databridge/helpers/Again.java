package io.github.kdesp73.databridge.helpers;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * The {@code Again} class provides utility methods for retrying tasks with specified
 * retry mechanisms, including simple retries with delays and retries with exponential backoff.
 * <p>
 * It retrieves configuration values such as retry times, retry delay, and retry activation
 * from the {@link Config} instance. If retrying is not enabled in the configuration, tasks
 * are executed without retries.
 * </p>
 *
 * <p>
 * The class supports retrying tasks up to a maximum number of attempts with a specified
 * delay between each attempt. It also supports exponential backoff, where the delay increases
 * after each failure.
 * </p>
 *
 * @author KDesp73
 */
public class Again {

    // Default delay between retries in milliseconds
    private static final long DEFAULT_DELAY_MS = 1000;

    // Default maximum number of retries
    private static final int DEFAULT_MAX_RETRIES = 3;

    /**
     * Checks if retries are active based on the configuration.
     *
     * @return {@code true} if retries are enabled in the configuration, otherwise {@code false}.
     */
    private static boolean active() {
        if (Config.getInstance() == null) return true;

        return Config.getInstance().getDbRetry();
    }

    /**
     * Gets the delay between retries from the configuration or uses the default value.
     *
     * @return The delay between retries in milliseconds.
     */
    public static long delay() {
        if (Config.getInstance() == null) return DEFAULT_DELAY_MS;

        return Config.getInstance().getDbRetryDelay();
    }

    /**
     * Gets the maximum number of retry attempts from the configuration or uses the default value.
     *
     * @return The maximum number of retry attempts.
     */
    public static int retries() {
        if (Config.getInstance() == null) return DEFAULT_MAX_RETRIES;

        return Config.getInstance().getDbRetryTimes();
    }

    /**
     * Retries a task a specified number of times with a fixed delay between retries.
     * <p>
     * If retries are not enabled in the configuration, the task is executed without retries.
     * </p>
     *
     * @param task The task to retry.
     * @param maxRetries The maximum number of retry attempts.
     * @param delay The delay between retry attempts in milliseconds.
     * @param <T> The return type of the task.
     * @return The result of the task, or {@code null} if the task failed after the maximum retries.
     * @throws SQLException If an error occurs during task execution.
     */
    public static <T> T retryWithDelay(RetryableTask<T> task, int maxRetries, long delay) throws SQLException {
        if (!active()) {
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
     * Retries a task a specified number of times with exponential backoff between retries.
     * <p>
     * Exponential backoff doubles the delay between each retry attempt.
     * </p>
     * If retries are not enabled in the configuration, the task is executed without retries.
     *
     * @param task The task to retry.
     * @param maxRetries The maximum number of retry attempts.
     * @param initialDelay The initial delay between retries in milliseconds.
     * @param <T> The return type of the task.
     * @return The result of the task, or {@code null} if the task failed after the maximum retries.
     * @throws SQLException If an error occurs during task execution.
     */
    public static <T> T retryWithExponentialBackoff(RetryableTask<T> task, int maxRetries, long initialDelay) throws SQLException {
        if (!active()) {
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

    /**
     * Pauses the current thread for the specified duration.
     *
     * @param milliseconds The duration to sleep in milliseconds.
     */
    private static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * A functional interface for tasks that can be retried.
     * <p>
     * Implementations of this interface must provide the {@code execute} method,
     * which defines the task to be retried.
     * </p>
     *
     * @param <T> The return type of the task.
     */
    @FunctionalInterface
    public interface RetryableTask<T> {
        /**
         * Executes the task.
         *
         * @return The result of the task.
         * @throws SQLException If an error occurs during task execution.
         */
        T execute() throws SQLException;
    }
}
