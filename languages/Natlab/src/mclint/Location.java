package mclint;

import ast.ASTNode;

import com.google.common.collect.ComparisonChain;

/**
 * A code location; a file path together with line and column numbers.
 * Essentially just a value object, together with a handy method to
 * extract the necessary information from an AST node.
 * @author ismail
 *
 */
public class Location implements Comparable<Location> {
  private int line;
  private int column;
  private MatlabProgram program;

  public static Location of(ASTNode<?> node) {
    int line = node.getStartLine();
    int col = node.getStartColumn();
    // Some ASTNodes don't have position information...
    if (line == 0 && col == 0)
      return Location.of(node.getParent());
    return new Location(node.getMatlabProgram(), line, col);
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public MatlabProgram getMatlabProgram() {
    return program;
  }

  @Override
  public String toString() {
    return String.format("%s [%d, %d]", program.getPath(), line, column);
  }

  private Location(MatlabProgram program, int line, int column) {
    this.line = line;
    this.column = column;
    this.program = program;
  }

  @Override
  public int compareTo(Location location) {
    return ComparisonChain.start()
        .compare(getMatlabProgram().getPath(), location.getMatlabProgram().getPath())
        .compare(getLine(), location.getLine())
        .compare(getColumn(), location.getColumn())
        .result();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof Location))
      return false;
    return compareTo((Location)o) == 0;
  }
}
