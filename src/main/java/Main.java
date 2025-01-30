import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine().trim();

            if (input.equals("exit 0")) {
                scanner.close();
                System.exit(0);
            }

                if (input.startsWith("echo ")) {
                System.out.println(input.substring(5));
                continue;
            }
            System.out.println(input + ": command not found");
        }
    }
}
