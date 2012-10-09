package mclint.refactoring;

import mclint.util.Parsing;
import ast.Program;

public class Main {
  public static void main(String[] args) {
    Program program = Parsing.string(new StringBuilder()
        .append("X = repmat(0, 4, 4);\n")
        .append("Y = repmat(1, 4, 4);\n")
        .append("Z = X + Y;\n")
        .append("if length(X) == 0\n")
        .append("  disp(sprintf('%f', Z));\n")
        .append("end\n").toString());
    System.out.println("Original program:");
    System.out.println(program.getPrettyPrinted());
    Refactorings.repmatToZeros().apply(program);
    System.out.println("After repmatToZeros:");
    System.out.println(program.getPrettyPrinted());
    Refactorings.repmatToOnes().apply(program);
    System.out.println("After repmatToOnes:");
    System.out.println(program.getPrettyPrinted());
    Refactorings.dispSprintfToFprintf().apply(program);
    System.out.println("After dispSprintfToFprintf:");
    System.out.println(program.getPrettyPrinted());
    Refactorings.lengthEqZeroToIsempty().apply(program);
    System.out.println("After lengthEqZeroToIsempty");
    System.out.println(program.getPrettyPrinted());
  }
}
