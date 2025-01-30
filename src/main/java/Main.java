import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    // Définition des commandes internes (builtins)
    private static final Set<String> BUILTINS = Set.of("echo", "exit", "type");

    public static void main(String[] args) {
        // Création d'un scanner pour lire l'entrée de l'utilisateur
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Affiche le prompt de commande
            System.out.print("$ ");
            // Lecture de la commande de l'utilisateur
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;  // Ignore les lignes vides

            // Sépare la commande et ses arguments
            String[] tokens = input.split("\\s+");
            String command = tokens[0];
            String[] commandArgs = Arrays.copyOfRange(tokens, 1, tokens.length);

            // Vérifie si la commande fait partie des commandes internes
            if (BUILTINS.contains(command)) {
                handleBuiltin(command, commandArgs);
            } else {
                executeExternalCommand(command, commandArgs);  // Exécute une commande externe
            }
        }
    }

    // Gère l'exécution des commandes internes
    private static void handleBuiltin(String command, String[] args) {
        switch (command) {
            case "echo":
                // Affiche les arguments de la commande echo
                System.out.println(String.join(" ", args));
                break;
            case "exit":
                // Vérifie que l'argument est "0" pour quitter le programme
                if (args.length == 1 && args[0].equals("0")) {
                    System.exit(0);  // Quitte le programme avec un code de sortie 0
                } else {
                    System.out.println("Usage: exit 0");  // Affiche l'usage correct de la commande
                }
                break;
            case "type":
                // Vérifie la commande type et affiche si elle est interne ou une commande externe
                if (args.length == 1) {
                    String cmd = args[0];
                    if (BUILTINS.contains(cmd)) {
                        System.out.println(cmd + " is a shell builtin");  // Commande interne
                    } else {
                        String path = findExecutableInPath(cmd);  // Recherche la commande dans le PATH
                        if (path != null) {
                            System.out.println(cmd + " is " + path);  // Affiche le chemin de la commande
                        } else {
                            System.out.println(cmd + ": not found");  // Commande non trouvée
                        }
                    }
                } else {
                    System.out.println("Usage: type <command>");  // Affiche l'usage correct de la commande
                }
                break;
            default:
                // Cas où la commande n'est pas reconnue
                System.out.println(command + ": command not found");
        }
    }

    // Exécute une commande externe
    private static void executeExternalCommand(String command, String[] args) {
        // Recherche le chemin de la commande dans le PATH
        String executablePath = findExecutableInPath(command);
        if (executablePath == null) {
            System.out.println(command + ": command not found");
            return;
        }

        // Prépare la commande avec ses arguments
        List<String> commandWithArgs = new ArrayList<>();
        commandWithArgs.add(executablePath);
        commandWithArgs.addAll(Arrays.asList(args));

        // Utilisation de ProcessBuilder pour exécuter la commande
        ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);
        processBuilder.redirectErrorStream(true);  // Redirige la sortie d'erreur vers la sortie standard

        try {
            // Démarre le processus
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);  // Affiche la sortie du processus
                }
            }
            // Attend que le processus se termine et récupère le code de sortie
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Process exited with code " + exitCode);  // Affiche un message si le processus échoue
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error executing command: " + e.getMessage());  // Affiche une erreur si l'exécution échoue
        }
    }

    // Recherche une commande dans le PATH
    private static String findExecutableInPath(String command) {
        String pathEnv = System.getenv("PATH");  // Récupère la variable d'environnement PATH
        if (pathEnv == null) return null;

        // Sépare le PATH en différents répertoires
        String[] paths = pathEnv.split(File.pathSeparator);
        for (String path : paths) {
            Path fullPath = Paths.get(path, command);
            // Vérifie si le fichier est exécutable
            if (Files.isExecutable(fullPath)) {
                return fullPath.toString();  // Retourne le chemin complet de l'exécutable
            }
        }
        return null;  // Retourne null si la commande n'est pas trouvée
    }
}
