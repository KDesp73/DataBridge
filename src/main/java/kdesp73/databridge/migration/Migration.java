/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kdesp73.databridge.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kdesp73.databridge.helpers.FileUtils;
import kdesp73.databridge.helpers.SQLogger;

/**
 *
 * @author kdesp73
 */
public class Migration implements Comparable {

	private static String upTag = "@up";
	private static String downTag = "@down";
	private static String descriptionTag = "@desc";
	private static String versionTag = "@version";

	private String file;
	private String script;
	private String upScript;
	private String downScript;
	private int version;
	private String description;
	private String checksum;

	/**
	 * Read Migration constructor
	 *
	 * @param path
	 */
	public Migration(String path) {
		this.file = path;
		this.loadScript(path);
	}

	private static int extractVersion(String line) {
		String pattern = String.format("--\\s*%s\\s*(.*)", versionTag);
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(line);

		if (matcher.find()) {
			return Integer.parseInt(matcher.group(1).trim());
		} else {
			return -1;
		}
	}

	private static String extractDescription(String line) {
		String pattern = String.format("--\\s*%s\\s*(.*)", descriptionTag);
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(line);

		if (matcher.find()) {
			return matcher.group(1).trim();
		} else {
			return null;
		}
	}

	private void loadScript(String path) {
		List<String> lines = FileUtils.readFile(path);
		if (lines == null) {
			return;
		}

		boolean upFound = false, downFound = false;

		for (String line : lines) {
			if (line.startsWith("-- " + descriptionTag)) {
				this.description = extractDescription(line);
				continue;
			}
			if (line.startsWith("-- " + versionTag)) {
				this.version = extractVersion(line);
				continue;
			}
			if (line.equals("-- " + upTag)) {
				upFound = true;
				continue;
			}
			if (line.equals("-- " + downTag)) {
				downFound = true;
				continue;
			}

			if (upFound && !downFound && !line.isBlank()) {
				if (this.upScript == null) {
					this.upScript = line + "\n";
				} else {
					this.upScript += line + "\n";
				}
			}

			if (downFound && upFound && !line.isBlank()) {
				if (this.downScript == null) {
					this.downScript = line + "\n";
				} else {
					this.downScript += line + "\n";
				}
			}
		}

		this.script = String.format(
			"-- %s %d\n-- %s %s\n\n-- %s\n%s\n-- %s\n%s",
			versionTag, version,
			descriptionTag, description,
			upTag, upScript,
			downTag, downScript
		);
		
		try {
			this.checksum = Migration.generateChecksum(this.script);
		} catch (IOException | NoSuchAlgorithmException ex) {
			Logger.getLogger(Migration.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Write migration constructor
	 *
	 * @param path
	 * @param version
	 * @param description
	 * @param upScript
	 * @param downScript
	 */
	public Migration(String path, int version, String description, String upScript, String downScript) {
		this.file = path;
		this.upScript = upScript;
		this.downScript = downScript;
		this.script = String.format(
			"-- %s %d\n-- %s %s\n\n-- %s\n%s\n-- %s\n%s",
			versionTag, version,
			descriptionTag, description,
			upTag, upScript,
			downTag, downScript
		);
		try {
			this.checksum = Migration.generateChecksum(this.script);
		} catch (IOException | NoSuchAlgorithmException ex) {
			Logger.getLogger(Migration.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Migration{");
		sb.append("file=").append(file);
		sb.append(", script=").append(script);
		sb.append(", upScript=").append(upScript);
		sb.append(", downScript=").append(downScript);
		sb.append(", version=").append(version);
		sb.append(", description=").append(description);
		sb.append('}');
		return sb.toString();
	}

	public String getFile() {
		return file;
	}

	public String getScript() {
		return script;
	}

	public String getUpScript() {
		return upScript;
	}

	public String getDownScript() {
		return downScript;
	}

	public int getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public static String generateChecksum(String script) throws IOException, NoSuchAlgorithmException {
		byte[] fileContent = script.getBytes();

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] checksumBytes = md.digest(fileContent);

		// Convert the byte array to a hexadecimal string
		StringBuilder checksum = new StringBuilder();
		for (byte b : checksumBytes) {
			checksum.append(String.format("%02x", b));
		}

		return checksum.toString();
	}
	
	@Override
	public int compareTo(Object o) {
		if (!(o instanceof Migration)) {
			throw new ClassCastException("Cannot compare Migration with non-Migration object.");
		}

		return this.version - ((Migration) o).version;
	}
}
