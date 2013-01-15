package mclint;

import java.io.File;
import java.util.Collections;
import java.util.List;

import mclint.analyses.ChangedLoopVar;
import mclint.analyses.LoopInvariantComputation;
import mclint.analyses.OutputSuppression;
import mclint.analyses.Shadowing;
import mclint.analyses.UnreachableCode;
import mclint.analyses.UnusedVar;
import mclint.reports.ReportGenerators;
import mclint.util.Parsing;
import natlab.options.Options;
import ast.CompilationUnits;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class McLint {
  private static final String usage = "mclint file1.m [file2.m ...]";

  private static void abortIf(boolean condition, String message) {
    if (condition) {
      System.err.println(message);
      System.exit(1);
    }
  }
  
  private static List<LintAnalysis> getBuiltinAnalyses(AnalysisKit kit) {
    return ImmutableList.<LintAnalysis>builder()
        .add(new ChangedLoopVar(kit))
        .add(new LoopInvariantComputation(kit))
        .add(new OutputSuppression(kit))
        .add(new Shadowing(kit))
        .add(new UnreachableCode(kit))
        .add(new UnusedVar(kit))
        .build();
  }
  
  private static List<LintAnalysis> getPluginAnalyses(AnalysisKit kit) {
    String pluginDirectoryName = System.getenv("MCLINT_PLUGIN_DIR");
    if (pluginDirectoryName == null) {
      return Collections.emptyList();
    }
    File pluginDirectory = new File(pluginDirectoryName);
    if (!pluginDirectory.exists()) {
      return Collections.emptyList();
    }
    return AnalysisLoader.loadAnalyses(kit, pluginDirectory);
  }
  
  private static List<LintAnalysis> getAllAnalyses(AnalysisKit kit) {
    return ImmutableList.<LintAnalysis>builder()
        .addAll(getBuiltinAnalyses(kit))
        .addAll(getPluginAnalyses(kit))
        .build();
  }

  public static void main(Options options) {
    CompilationUnits AST = Parsing.files(options.getFiles());
    AnalysisKit kit = AnalysisKit.forAST(AST);
    List<LintAnalysis> analyses = getAllAnalyses(kit);
    Lint lint = new Lint(analyses);
    lint.runAnalyses();
    lint.writeReport(ReportGenerators.plain(), System.out);
  }
}
