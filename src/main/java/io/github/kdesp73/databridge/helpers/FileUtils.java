package kdesp73.databridge.helpers;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for working with file and directory operations. Provides methods to read,
 * write, and retrieve file paths from a directory.
 * <p>
 * This class offers static methods for accessing and manipulating files and directories
 * in a Java application, including functionality to read files line-by-line, write content
 * to files, and retrieve absolute paths of files in a specified directory.
 * </p>
 *
 * @author KDesp73
 */
public class FileUtils {

    /**
     * Retrieves a list of absolute file paths from a specified directory.
     * Only regular files are included in the list; subdirectories are skipped.
     *
     * @param directoryPath The path of the directory from which to retrieve files.
     * @return A list of absolute file paths as strings.
     * @throws IOException If the directory path does not exist or is not a directory.
     */
    public static List<String> getAbsoluteFilePaths(String directoryPath) throws IOException {
        List<String> filePaths = new ArrayList<>();
        Path dirPath = Paths.get(directoryPath);

        // Ensure the directory exists and is a directory
        if (Files.isDirectory(dirPath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
                for (Path entry : stream) {
                    if (Files.isRegularFile(entry)) {  // Only add files, skip subdirectories
                        filePaths.add(entry.toAbsolutePath().toString());
                    }
                }
            }
        } else {
            throw new IOException("Path is not a directory or does not exist: " + directoryPath);
        }

        return filePaths;
    }

    /**
     * Reads the content of a file and returns the lines as a list of strings.
     *
     * @param filePath The path of the file to read.
     * @return A list of strings representing the lines in the file, or {@code null} if an error occurs.
     */
    public static List<String> readFile(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return java.nio.file.Files.readAllLines(path);
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Writes a list of strings to a file, each string representing one line of content.
     *
     * @param filePath The path of the file to write to.
     * @param content A list of strings to be written to the file.
     * @throws IOException If an error occurs while writing the file.
     */
    public static void writeFile(String filePath, List<String> content) throws IOException {
        Path path = Paths.get(filePath);
        java.nio.file.Files.write(path, content);
    }

    /**
     * Writes a single string of content to a file.
     *
     * @param filePath The path of the file to write to.
     * @param content The string content to be written to the file.
     * @throws IOException If an error occurs while writing the file.
     */
    public static void writeFile(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);
        java.nio.file.Files.write(path, content.getBytes());
    }
}
