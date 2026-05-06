import java.util.Scanner;

public class TerminalScanner {
    String input;

    public void scanConsole() {

        StringBuilder fullTerminalInput = new StringBuilder();
        Scanner sc = new Scanner(System.in);

        String currentTerminalInput = sc.nextLine();
        while (currentTerminalInput != "") {

            fullTerminalInput.append(currentTerminalInput + "\n");
            currentTerminalInput = sc.nextLine();
        }

        sc.close();
        this.input = fullTerminalInput.toString();
    }
}
