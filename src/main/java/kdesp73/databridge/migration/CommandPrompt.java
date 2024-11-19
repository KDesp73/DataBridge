package kdesp73.databridge.migration;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import kdesp73.databridge.helpers.SQLogger;

/**
 * The {@code CommandPrompt} class provides a command-line interface (CLI) for interacting with the database migration system.
 * It allows users to execute commands for managing database migrations, such as running, rolling back, and listing migrations,
 * as well as generating migration templates and clearing the console.
 *
 * <p>This class continuously prompts the user for input and executes the corresponding command until the user exits.</p>
 *
 * @author KDesp73
 */
public class CommandPrompt {

    /**
     * The {@code Scheman} object responsible for managing database migrations.
     */
    private Scheman scheman;

    /**
     * Constructs a new {@code CommandPrompt} with the specified {@code Scheman}.
     *
     * @param scheman the {@code Scheman} object responsible for handling migrations.
     */
    public CommandPrompt(Scheman scheman) {
        this.scheman = scheman;
    }

    /**
     * Displays a prompt and reads the user input.
     *
     * @param prompt the prompt message to display to the user.
     * @return the user's input as a string.
     */
    private String prompt(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Displays a help message listing all available commands and their descriptions.
     */
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

    /**
     * Clears the console screen. The method detects the operating system
     * and executes the appropriate command to clear the screen (e.g., "cls" on Windows,
     * "clear" on Unix-based systems).
     *
     * <p>If an error occurs while attempting to clear the console, an error message is printed.</p>
     */
    private static void clear() {
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

    /**
     * Starts the command-line interface, continuously prompting the user for commands and executing the corresponding actions.
     *
     * <p>The following commands are available:</p>
     * <ul>
     *   <li><b>exit</b>, <b>quit</b>, <b>q</b>: Exit the CLI</li>
     *   <li><b>up</b>, <b>run</b>, <b>r</b>: Run all migrations</li>
     *   <li><b>down</b>, <b>rollback</b>: Rollback the last migration</li>
     *   <li><b>list</b>, <b>ls</b>: List completed migrations</li>
     *   <li><b>generate</b>, <b>gen</b>: Generate a template migration file</li>
     *   <li><b>clear</b>: Clear the screen</li>
     *   <li><b>rerun</b>, <b>rr</b>: Re-run changed migrations</li>
     * </ul>
     * <p>If the user enters an unknown command, a help message is displayed.</p>
     */
    public void start() {
        while (true) {
            String command = prompt("scheman > ");

            switch (command.toLowerCase()) {
                case "exit", "quit", "q" -> {
                    return;
                }
                case "up", "run", "r" -> {
                    try {
                        this.scheman.runMigrations();
                    } catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
                case "down", "rollback" -> {
                    try {
                        this.scheman.rollbackMigration();
                    } catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
                case "list", "ls" -> {
                    try {
                        ResultSet rs = scheman.selectMigrations();
                        SQLogger.getLogger().logResultSet(rs);
                    } catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
                case "help", "?" -> help();
                case "generate", "gen" -> {
                    try {
                        Migration.templateMigration();
                        System.out.println("File generated");
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
                case "clear" -> clear();
                case "rerun", "rr" -> {
                    try {
                        this.scheman.rerunMigrations();
                    } catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
                default -> {
                    System.err.println("Unknown command: " + command);
                    help();
                }
            }
            System.out.println("");
        }
    }
}
