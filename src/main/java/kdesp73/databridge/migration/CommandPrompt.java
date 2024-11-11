/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kdesp73.databridge.migration;

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

	public void start() {
		while (true) {
			String command = prompt("scheman > ");

			switch (command.toLowerCase()) {
				case "exit":
					return;
				case "up":
					try {
						this.scheman.runMigrations();
					} catch (SQLException ex) {
						System.err.println(ex.getMessage());
					}	break;
				case "down":
					try {
						this.scheman.rollbackMigration();
					} catch (SQLException ex) {
						System.err.println(ex.getMessage());
					}	break;
				case "list", "ls":
				{
					try {
						ResultSet rs = scheman.selectMigrations();
						SQLogger.getLogger().logResultSet(rs);
					} catch (SQLException ex) {
						System.err.println(ex.getMessage());
					}
				}

				default:
					break;
			}
			System.out.println("");
		}
	}

}
