package W5;

import java.util.List;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.IOException;

public class HelloThere {
    public static void main(String[] args) throws IOException {
        List<String> programs = Arrays.asList("hello_world", "gday_mate", "well_sod_ya_then");
        for (String greet : programs) {
            generate("W5/meta/generated/", greet);
        }
    }

    public static void generate(String path, String greet) throws IOException {
        PrintWriter writer = new PrintWriter(path + "Hello" + greet + ".java", "UTF-8");
        writer.println("package W5.meta.generated;");
        writer.println();
        writer.println("public class Hello" + greet + " {");
        writer.println("    public static void main(String[] args) {");
        writer.println("        System.out.println(\"" + greet + "\");");
        writer.println("    }");
        writer.println("}");
        writer.close();
    }
}
