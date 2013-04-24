package mclint;


/**
 * All analyses should implement this interface. They should also provide
 * a constructor taking a Project.
 * @author ismail
 *
 */
public interface LintAnalysis {
  void analyze(Lint lint);
}