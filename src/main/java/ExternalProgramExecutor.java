package main.java;

import java.io.*;
import java.util.*;

public class ExternalProgramExecutor {
    public static void main(String[] args) {
        // Chemin vers le script shell
        String scriptPath = "C:\\Users\\UX3405\\codecrafters-shell-java\\your_program.sh";

        // Créer une liste pour la commande et ses arguments
        List<String> commandWithArgs = new ArrayList<>();
        commandWithArgs.add("cmd.exe");
        commandWithArgs.add("/c");
        commandWithArgs.add(scriptPath);

        // Créer un ProcessBuilder avec la commande et ses arguments
        ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);
        processBuilder.redirectErrorStream(true); // Rediriger les erreurs vers la sortie standard

        try {
            // Démarrer le processus
            Process process = processBuilder.start();

            // Lire la sortie du processus
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            // Attendre la fin du processus et obtenir le code de sortie
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Le processus s'est terminé avec le code de sortie " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Erreur lors de l'exécution du programme externe : " + e.getMessage());
        }
    }
}
