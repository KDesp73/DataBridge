/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kdesp73.databridge.helpers;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kdesp73
 */
public class FileUtils {
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
	
	public static List<String> readFile(String filePath) {
		Path path = Paths.get(filePath);
		try {
			return java.nio.file.Files.readAllLines(path);
		} catch (IOException ex) {
			return null;
		}
	}

	public static void writeFile(String filePath, List<String> content) throws IOException {
		Path path = Paths.get(filePath);
		java.nio.file.Files.write(path, content);
	}
	
	public static void writeFile(String filePath, String content) throws IOException {
		Path path = Paths.get(filePath);
		java.nio.file.Files.write(path, content.getBytes());
	}
}
