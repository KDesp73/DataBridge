/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kdesp73.databridge.migration;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import kdesp73.databridge.helpers.SQLogger;

/**
 *
 * @author kdesp73
 */
public class CommandPrompt {

	private Scheman scheman;

	public CommandPrompt(Scheman scheman) {
		this.scheman = scheman;
	}

	public String prompt(String prompt) {
		Scanner scanner = new Scanner(System.in);
		System.out.print(prompt);
		String str = scanner.nextLine();
		return str;
	}

	private void help() {
		System.out.println("Commands");
		System.out.println("  help           Prints this message");
		System.out.println("  exit           Exit the cli");
		System.out.println("  down           Rollback last migration");
		System.out.println("  up             Run all migrations");
		System.out.println("  rerun          Re-run changed migrations");
		System.out.println("  list           List completed migrations");
		System.out.println("  generate       Generate template migration file");
		System.out.println("  clear          Clear screen");

	}

	public static void clear() {
		try {
			if (System.getProperty("os.name").contains("Windows")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				new ProcessBuilder("clear").inheritIO().start().waitFor();
			}
		} catch (Exception e) {
			System.err.println("Error clearing console: " + e.getMessage());
		}
	}

	public void start() {
		while (true) {
			String command = prompt("scheman > ");

			switch (command.toLowerCase()) {
				case "exit":
					return;
				case "up", "run":
					try {
						this.scheman.runMigrations();
					} catch (SQLException ex) {
						System.err.println(ex.getMessage());
					}
					break;
				case "down", "rollback":
					try {
						this.scheman.rollbackMigration();
					} catch (SQLException ex) {
						System.err.println(ex.getMessage());
					}
					break;
				case "list", "ls":
					try {
						ResultSet rs = scheman.selectMigrations();
						SQLogger.getLogger().logResultSet(rs);
					} catch (SQLException ex) {
						System.err.println(ex.getMessage());
					}
					break;
				case "help", "?":
					help();
					break;
				case "generate", "gen":
					try {
						Migration.templateMigration();
						System.out.println("File generated");
					} catch (IOException ex) {
						System.err.println(ex.getMessage());
					}
					break;
				case "clear":
					clear();
					break;
				case "rerun", "rr":
					try {
						this.scheman.rerunMigrations();
					} catch (SQLException ex) {
						System.err.println(ex.getMessage());
					}
					break;
				default:
					break;
			}
			System.out.println("");
		}
	}

}
