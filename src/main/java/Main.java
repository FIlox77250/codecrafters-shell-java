import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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

            // Default case: command not found
            System.out.println(input + ": command not found");
        }
    }
}
