package mclint;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mclint.reports.ReportGenerators;
import natlab.CompilationProblem;
import natlab.Parse;
import natlab.toolkits.filehandling.genericFile.FileFile;
import ast.CompilationUnits;

public class Main {
  private static final String usage = "mclint file1.m [file2.m ...]";

  private static CompilationUnits getAST(String[] mfiles) {
    List<CompilationProblem> errors = new ArrayList<CompilationProblem>();
    CompilationUnits code = Parse.parseFiles(Arrays.asList(mfiles), errors);
    if (!errors.isEmpty()) {
      System.err.println("Could not run analyses; the following errors occured during parsing:");
      for (CompilationProblem error : errors)
        System.err.println(error);
      System.exit(1);
    }
    for (int i = 0; i < mfiles.length; i++)
      code.getProgram(i).setFile(new FileFile(mfiles[i]));
    return code;
  }

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

    CompilationUnits AST = getAST(args);
    AnalysisKit kit = AnalysisKit.forAST(AST);
    List<LintAnalysis> analyses = AnalysisLoader.loadAnalyses(kit, pluginDirectory);
    Lint lint = new Lint(analyses);
    lint.runAnalyses();
    lint.writeReport(ReportGenerators.plain(), System.out);
  }
}
