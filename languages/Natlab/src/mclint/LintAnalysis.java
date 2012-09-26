package mclint;


/**
 * All analyses should implement this interface. They should also provide
 * a constructor taking an ASTNode.
 * @author ismail
 *
 */
public interface LintAnalysis {
  void analyze(Lint lint);
}