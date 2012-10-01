package mclint;

import java.io.File;
import java.util.List;

import mclint.reports.ReportGenerators;
import mclint.util.Parsing;
import ast.CompilationUnits;

public class Main {
  private static final String usage = "mclint file1.m [file2.m ...]";

  private static void abortIf(boolean condition, String message) {
    if (condition) {
      System.err.println(message);
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    abortIf(args.length == 0, usage);
    for (String filename : args) {
      File mFile = new File(filename);
      abortIf(!mFile.exists(), "File not found: " + filename);
    }
    String pluginDirectoryName = System.getenv("MCLINT_PLUGIN_DIR");
    abortIf(pluginDirectoryName == null, "Make sure MCLINT_PLUGIN_DIR is set.");
    File pluginDirectory = new File(pluginDirectoryName);
    abortIf(!pluginDirectory.exists(),
        "Make sure MCLINT_PLUGIN_DIR points to an existing directory.");

    CompilationUnits AST = Parsing.files(args);
    AnalysisKit kit = AnalysisKit.forAST(AST);
    List<LintAnalysis> analyses = AnalysisLoader.loadAnalyses(kit, pluginDirectory);
    Lint lint = new Lint(analyses);
    lint.runAnalyses();
    lint.writeReport(ReportGenerators.plain(), System.out);
  }
}
