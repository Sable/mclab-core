package mclint;

import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.CompilationUnits;
import ast.Program;

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
  private String path;

  public static Location of(ASTNode<?> node) {
    int line = node.getStartLine();
    int col = node.getStartColumn();
    // Some ASTNodes don't have position information...
    if (line == 0 && col == 0)
      return Location.of(node.getParent());
    return new Location(getPathOf(node), line, col);
  }

  // TODO change this use Project / MatlabProgram
  private static String getPathOf(ASTNode<?> node) {
    Program program = null;
    if (node instanceof CompilationUnits)
      program = ((CompilationUnits)node).getProgram(0);
    else if (node.getParent() instanceof CompilationUnits)
      program = (Program)(node.getChild(0));
    else
      program = NodeFinder.findParent(Program.class, node);
    return program.getFile().getPath();
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public String getPath() {
    return path;
  }

  @Override
  public String toString() {
    return String.format("%s [%d, %d]", path, line, column);
  }

  private Location(String path, int line, int column) {
    this.line = line;
    this.column = column;
    this.path = path;
  }

  @Override
  public int compareTo(Location location) {
    return ComparisonChain.start()
        .compare(getPath(), location.getPath())
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
