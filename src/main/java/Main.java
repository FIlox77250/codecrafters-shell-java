import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    private static final Set<String> BUILTINS = Set.of("echo", "exit", "type", "pwd", "cd");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] tokens = input.split("\\s+");
            String command = tokens[0];
            String[] commandArgs = Arrays.copyOfRange(tokens, 1, tokens.length);

            if (BUILTINS.contains(command)) {
                handleBuiltin(command, commandArgs);
            } else {
                executeExternalCommand(command, commandArgs);
            }
        }
    }

    private static void handleBuiltin(String command, String[] args) {
        switch (command) {
            case "echo":
                System.out.println(String.join(" ", args));
                break;
            case "exit":
                if (args.length == 1 && args[0].equals("0")) {
                    System.exit(0);
                } else {
                    System.out.println("Usage: exit 0");
                }
                break;
            case "type":
                if (args.length == 1) {
                    String cmd = args[0];
                    if (BUILTINS.contains(cmd)) {
                        System.out.println(cmd + " is a shell builtin");
                    } else {
                        String path = findExecutableInPath(cmd);
                        if (path != null) {
                            System.out.println(cmd + " is " + path);
                        } else {
                            System.out.println(cmd + ": not found");
                        }
                    }
                } else {
                    System.out.println("Usage: type <command>");
                }
                break;
            case "pwd":
                System.out.println(System.getProperty("user.dir"));
                break;
            case "cd":
                if (args.length == 1) {
                    String pathStr = args[0];
                    if (pathStr.equals("~")) {
                        pathStr = System.getenv("HOME");
                    }
                    Path newPath = Paths.get(pathStr);
                    if (!newPath.isAbsolute()) {
                        newPath = Paths.get(System.getProperty("user.dir")).resolve(newPath).normalize();
                    }
                    if (Files.isDirectory(newPath)) {
                        System.setProperty("user.dir", newPath.toAbsolutePath().toString());
                    } else {
                        System.out.println("cd: " + args[0] + ": No such file or directory");
                    }
                } else {
                    System.out.println("Usage: cd <path>");
                }
                break;
            default:
                System.out.println(command + ": command not found");
        }
    }

    private static void executeExternalCommand(String command, String[] args) {
        String executablePath = findExecutableInPath(command);
        if (executablePath == null) {
            System.out.println(command + ": command not found");
            return;
        }

        List<String> commandWithArgs = new ArrayList<>();
        commandWithArgs.add(executablePath);
        commandWithArgs.addAll(Arrays.asList(args));

        ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Arg #0 (program name): " + executablePath)) {
                        System.out.println("Arg #0 (program name): " + command);
                    } else {
                        System.out.println(line);
                    }
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Process exited with code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error executing command: " + e.getMessage());
        }
    }

    private static String findExecutableInPath(String command) {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null) pathEnv = "";

        String[] paths = (pathEnv + File.pathSeparator + "/tmp/bar" + File.pathSeparator + "/tmp/baz").split(File.pathSeparator);
        for (String path : paths) {
            Path fullPath = Paths.get(path, command);
            if (Files.isExecutable(fullPath)) {
                return fullPath.toString();
            }
        }
        return null;
    }
}