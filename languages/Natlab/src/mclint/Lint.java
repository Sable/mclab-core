package mclint;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import mclint.refactoring.RefactoringContext;
import mclint.reports.ReportGenerator;
import mclint.util.AstUtil;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * The analysis runner. This class calls the <tt>analyze</tt> method of each
 * analysis, passing itself as argument so it can gather the messages
 * generated.
 * @author ismail
 *
 */

public class Lint {
  private List<Message> messages = new ArrayList<>();
  private List<LintAnalysis> analyses;
  private Multimap<String, MessageListener> messageListeners = LinkedHashMultimap.create();
  private Project project;

  private List<Message> currentMessages = new ArrayList<>();

  public static Lint create(Project project, List<LintAnalysis> analyses) {
    return new Lint(project, analyses);
  }

  private Lint(Project project, List<LintAnalysis> analyses) {
    this.project = project;
    this.analyses = analyses;
  }

  public AnalysisKit getKit() {
    // TODO fix this, this is just to make it compile
    return AnalysisKit.forAST(project.asCompilationUnits());
  }
  
  public RefactoringContext getBasicRefactoringContext() {
    return RefactoringContext.create(project, RefactoringContext.Transformations.BASIC);
  }

  public RefactoringContext getLayoutPreservingRefactoringContext() {
    return RefactoringContext.create(project, RefactoringContext.Transformations.LAYOUT_PRESERVING);
  }

  public void registerListenerForMessageCode(String code, MessageListener listener) {
    messageListeners.put(code, listener);
  }

  public void report(Message message) {
    currentMessages.add(message);
  }

  public void runAnalyses() {
    boolean changed = true;
    while (changed) {
      changed = false;
      for (LintAnalysis analysis : analyses) {
        currentMessages.clear();
        analysis.analyze(this);
        for (final Message message : currentMessages) {
          Collection<MessageListener> listeners = messageListeners.get(message.getCode());
          if (!listeners.stream().anyMatch(listener -> listener.messageReported(message))) {
            messages.add(message);
          } else {
            changed = true;
            // TODO figure out what to do with this line
            // kit.notifyTreeChanged();
          }
        }
      }
    }
    messages = messages.stream()
        .filter(message -> !AstUtil.removed(message.getAstNode()))
        .collect(Collectors.toList());
  }

  public void writeReport(ReportGenerator reporter, OutputStream out) {
    Collections.sort(messages);
    try {
      reporter.write(new ArrayList<>(messages), out);
    } catch (IOException e) {
      System.err.println("Could not write report: ");
      e.printStackTrace();
    }
  }
}
