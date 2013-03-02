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
import mclint.refactoring.Refactoring;
import mclint.refactoring.Refactorings;
import mclint.refactoring.RemoveUnusedVar;
import mclint.reports.ReportGenerators;
import mclint.transform.Transformer;
import mclint.transform.Transformers;
import mclint.util.Parsing;
import natlab.options.Options;
import natlab.utils.NodeFinder;
import ast.CompilationUnits;
import ast.Name;
import ast.Program;

import com.google.common.collect.ImmutableList;

public class McLint {
  
  private static List<LintAnalysis> getBuiltinAnalyses(AnalysisKit kit) {
    return ImmutableList.<LintAnalysis>builder()
        .add(new ChangedLoopVar(kit))
        .add(new LoopInvariantComputation(kit))
        .add(new OutputSuppression(kit))
        .add(new UnusedVar(kit))
        .add(new Shadowing(kit))
        .add(new UnreachableCode(kit))
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
  
  private static boolean prompt(Message message, String prompt) {
    String decision = System.console().readLine("%s: %s %s (Y/n) ",
            message.getLocation(), message.getDescription(), prompt);
    return decision.isEmpty() || decision.startsWith("y") || decision.startsWith("Y");
  }
  
  private static void registerBuiltinListeners(final Lint lint) {
    lint.registerListenerForMessageCode("UNUSED_VAR", new MessageListener() {
      @Override public boolean messageReported(Message message) {
        if (prompt(message, "Remove definition?")) {
          RemoveUnusedVar.exec((Name) message.getAstNode());
          return true;
        }
        return false;
      }
    });
    
    lint.registerListenerForMessageCode("SHADOW_BUILTIN", new MessageListener() {
      @Override public boolean messageReported(Message message) {
        Name node = (Name) message.getAstNode();
        if (prompt(message, "Rename?")) {
          String newName = System.console().readLine("    rename %s to: ", node.getID());
          Program parent = NodeFinder.findParent(Program.class, node);
          Transformer transformer = Transformers.basic(parent);
          Refactoring rename = Refactorings.renameVariable(transformer, node, newName,
              lint.getKit().getUseDefDefUseChain());
          rename.apply();
          return true;
        }
        return false;
      }
    });
  }

  public static void main(Options options) {
    CompilationUnits AST = Parsing.files(options.getFiles());
    AnalysisKit kit = AnalysisKit.forAST(AST);
    List<LintAnalysis> analyses = getAllAnalyses(kit);
    Lint lint = Lint.create(kit, analyses);
    registerBuiltinListeners(lint);
    lint.runAnalyses();
    lint.writeReport(ReportGenerators.plain(), System.out);
    System.out.println(AST.getPrettyPrinted());
  }
}
