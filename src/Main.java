import com.Program;

public class Main {
    public static void main(String[] args) {
        TerminalScanner terminalScanner = new TerminalScanner();
        terminalScanner.scanConsole();

        if (!terminalScanner.input.isEmpty()) {
            Program program = new Program();
            program.execute(terminalScanner.input);
        }
    }
}
