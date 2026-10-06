package ui;

import java.util.Scanner;

/*
 * UI helper: InputValidator
 * Purpose: Centralizes reading and validating console input so ConsoleUI
 * doesn't scatter try/catch blocks everywhere. Never throws on bad input -
 * always reprompts.
 */
public class InputValidator {

    private final Scanner scanner;

    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readNonEmptyLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line != null && !line.trim().isEmpty()) {
                return line.trim();
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    public String readLineAllowEmpty(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine();
        return line == null ? "" : line.trim();
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    /** Reads an int constrained to [min, max] inclusive, reprompting until valid. */
    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) return value;
            System.out.println("Please enter a number between " + min + " and " + max + ".");
        }
    }

    public int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) return value;
            System.out.println("Please enter a positive number.");
        }
    }

    public String readDifficulty(String prompt) {
        while (true) {
            String value = readNonEmptyLine(prompt);
            String normalized = value.trim();
            if (normalized.equalsIgnoreCase("Easy") || normalized.equalsIgnoreCase("Medium")
                    || normalized.equalsIgnoreCase("Hard")) {
                return capitalize(normalized);
            }
            System.out.println("Please enter one of: Easy, Medium, Hard.");
        }
    }

    private String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
