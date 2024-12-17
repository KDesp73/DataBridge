package io.github.kdesp73.databridge.helpers;

import io.github.cdimascio.dotenv.Dotenv;

public class Environment {
	public static String get(String key) {
		Dotenv dotenv = Dotenv.load();
		return dotenv.get(key);
	}
}
