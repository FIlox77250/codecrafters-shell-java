import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Set of built-in commands
        Set<String> builtins = Set.of("echo", "exit", "type");

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine().trim();

            // Handle "exit 0" command
            if (input.equals("exit 0")) {
                scanner.close();
                System.exit(0);
            }

            // Handle "echo" command
            if (input.startsWith("echo ")) {
                System.out.println(input.substring(5));
                continue;
            }

            // Handle "type" command
            if (input.startsWith("type ")) {
                String command = input.substring(5).trim();
                if (builtins.contains(command)) {
                    System.out.println(command + " is a shell builtin");
                } else {
                    String commandPath = findExecutableInPath(command);
                    if (commandPath != null) {
                        System.out.println(command + " is " + commandPath);
                    } else {
                        System.out.println(command + ": not found");
                    }
                }
                continue;
            }

            // Default case: command not found
            System.out.println(input + ": command not found");
        }
    }

    private static String findExecutableInPath(String command) {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null) {
            return null;
        }

        String[] paths = pathEnv.split(System.getProperty("path.separator"));
        for (String path : paths) {
            Path fullPath = Paths.get(path, command);
            if (Files.isExecutable(fullPath)) {
                return fullPath.toString();
            }
        }
        return null;
    }
}
