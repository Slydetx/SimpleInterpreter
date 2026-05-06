
import com.interpreterNodes.TreePrinter;
import com.tokenizer.Tokenizer;
import parser.Parser;

public class Main {
    public static void main(String[] args) {
        TerminalScanner terminalScanner = new TerminalScanner();
        terminalScanner.scanConsole();

        Tokenizer tokenizer = new Tokenizer();

        tokenizer.tokenize(terminalScanner.input);

        Parser parser = new Parser(tokenizer.tokenList);
        parser.parse();


    }

}
