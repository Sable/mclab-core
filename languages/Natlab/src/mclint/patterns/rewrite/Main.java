package mclint.patterns.rewrite;

import java.io.IOException;
import java.net.URL;

import mclint.util.Parsing;
import ast.Program;

public class Main {
  public static void main(String[] args) throws IOException {
    Program program = Parsing.string(new StringBuilder()
        .append("X = repmat(0, 4, 4);\n")
        .append("Y = repmat(1, 4, 4);\n")
        .append("Z = X + Y;\n")
        .append("if length(X) == 0\n")
        .append("  disp(sprintf('%f', Z));\n")
        .append("end\n").toString());
    System.out.println("Original program:");
    System.out.println(program.getPrettyPrinted());
    URL resource = Main.class.getResource("rewrites.txt");
    for (Rewrite refactoring : Rewrites.fromResource(resource)) {
      refactoring.apply(program);
    }
    System.out.println("After applying all refactorings:");
    System.out.println(program.getPrettyPrinted());
  }
}
