package tazyik;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Tazyik {
  static boolean hadError = false;
  static boolean hadRuntimeError = false;

  public static void main(String[] args) throws Exception {
    if (args.length == 1) {
        runFile(args[0]);
    } else {
        // Show usage information
        System.out.println("Tazyik - Water Flow Simulation Language");
        System.out.println("Assignment 2: Evaluation and Dams");
        System.out.println();
        System.out.println("Usage: java tazyik.Tazyik <source_file>");
        System.out.println();
        System.out.println("IMPORTANT: Run from the project directory:");
        System.out.println("  cd 47938234_IbrahimOmeroglu_COMP3000_A2");
        System.out.println();
        System.out.println("Example programs:");
        System.out.println("  Assignment 1 Examples:");
        System.out.println("    java -cp . tazyik.Tazyik Demo_Programs/example1.txt");
        System.out.println("    java -cp . tazyik.Tazyik Demo_Programs/example2.txt");
        System.out.println("    java -cp . tazyik.Tazyik Demo_Programs/example3.txt");
        System.out.println();
        System.out.println("  Assignment 2 Examples (with Dams):");
        System.out.println("    java -cp . tazyik.Tazyik Demo_Programs/example4_dams.txt");
        System.out.println();
        System.out.println("Features:");
        System.out.println("  - Multi-day water flow simulation");
        System.out.println("  - Dam control with 3-factor algorithm (level, inflow, rainfall)");
        System.out.println("  - Daily output showing system evolution");
        System.exit(64);
    }
  }

  private static void runFile(String path) throws Exception {
    String source = new String(Files.readAllBytes(Paths.get(path)));
    runStatements(source);

    if (hadError) System.exit(65);
}

  private static void runStatements(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    Parser parser = new Parser(tokens);
    List<Object> program = parser.parseProgram();

    // Use the interpreter to execute the program
    Interpreter interpreter = new Interpreter();
    interpreter.interpret(program);
}

  static void error(int line, String message) {
    report(line, "", message);
  }

  private static void report(int line, String where, String message) {
    System.err.println("[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }

  static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() +
        "\n[line " + error.token.line + "]");
    hadRuntimeError = true;
  }
}
