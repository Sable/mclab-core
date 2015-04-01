package mclint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import mclint.analyses.ChangedLoopVar;
import mclint.analyses.LoopInvariantComputation;
import mclint.analyses.OutputSuppression;
import mclint.analyses.Shadowing;
import mclint.analyses.UnreachableCode;
import mclint.analyses.UnusedVar;
import mclint.refactoring.Refactoring;
import mclint.refactoring.RefactoringContext;
import mclint.refactoring.Refactorings;
import mclint.refactoring.RemoveUnusedVar;
import mclint.reports.ReportGenerators;
import natlab.options.Options;
import natlab.refactoring.Exceptions.RefactorException;
import ast.Name;

import com.google.common.collect.ImmutableList;

public class McLint {

  private static List<LintAnalysis> getBuiltinAnalyses(Project project) {
    return ImmutableList.<LintAnalysis>builder()
        .add(new ChangedLoopVar(project))
        .add(new LoopInvariantComputation(project))
        .add(new OutputSuppression(project))
        .add(new UnusedVar(project))
        .add(new Shadowing(project))
        .add(new UnreachableCode(project))
        .build();
  }

  private static List<LintAnalysis> getPluginAnalyses(Project project) {
    String pluginDirectoryName = System.getenv("MCLINT_PLUGIN_DIR");
    if (pluginDirectoryName == null) {
      return Collections.emptyList();
    }
    File pluginDirectory = new File(pluginDirectoryName);
    if (!pluginDirectory.exists()) {
      return Collections.emptyList();
    }
    return AnalysisLoader.loadAnalyses(project, pluginDirectory);
  }

  private static List<LintAnalysis> getAllAnalyses(Project project) {
    return ImmutableList.<LintAnalysis>builder()
        .addAll(getBuiltinAnalyses(project))
        .addAll(getPluginAnalyses(project))
        .build();
  }

  private static boolean prompt(Message message, String prompt) {
    String decision = System.console().readLine("%s: %s %s (Y/n) ",
        message.getLocation(), message.getDescription(), prompt);
    return decision.isEmpty() || decision.startsWith("y") || decision.startsWith("Y");
  }

  private static void registerBuiltinListeners(final Lint lint) {
    lint.registerListenerForMessageCode("UNUSED_VAR", message -> {
      if (prompt(message, "Remove definition?")) {
        RemoveUnusedVar.exec((Name) message.getAstNode());
        return true;
      }
      return false;
    });

    lint.registerListenerForMessageCode("SHADOW_BUILTIN", message -> {
      Name node = (Name) message.getAstNode();
      if (!prompt(message, "Rename?")) {
        return false;
      }
      String newName = System.console().readLine("    rename %s to: ", node.getID());
      RefactoringContext context = lint.getLayoutPreservingRefactoringContext();
      Refactoring rename = Refactorings.renameVariable(context, node, newName);
      if (!rename.checkPreconditions()) {
        for (RefactorException error : rename.getErrors()) {
          System.err.println(error);
        }
        return false;
      }
      rename.apply();
      return true;
    });
  }

  public static void main(Options options) throws IOException {
    Project project = Project.at(Paths.get(options.getFiles().getFirst()));
    List<LintAnalysis> analyses = getAllAnalyses(project);
    Lint lint = Lint.create(project, analyses);
    registerBuiltinListeners(lint);
    lint.runAnalyses();
    lint.writeReport(ReportGenerators.plain(), System.out);
    project.write();
  }
}