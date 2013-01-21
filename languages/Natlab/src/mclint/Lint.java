package mclint;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mclint.reports.ReportGenerator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * The analysis runner. This class calls the <tt>analyze</tt> method of each
 * analysis, passing itself as argument so it can gather the messages
 * generated.
 * @author ismail
 *
 */

public class Lint {
  private List<Message> messages = Lists.newArrayList();
  private List<LintAnalysis> analyses;
  private Multimap<String, MessageListener> messageListeners = LinkedHashMultimap.create();
  private AnalysisKit kit;
  
  private List<Message> currentMessages = Lists.newArrayList();

  public static Lint create(AnalysisKit kit, List<LintAnalysis> analyses) {
    return new Lint(kit, analyses);
  }

  private Lint(AnalysisKit kit, List<LintAnalysis> analyses) {
    this.kit = kit;
    this.analyses = analyses;
  }

  public AnalysisKit getKit() {
    return kit;
  }

  public void registerListenerForMessageCode(String code, MessageListener listener) {
    messageListeners.put(code, listener);
  }

  public void report(Message message) {
    currentMessages.add(message);
  }

  public void runAnalyses() {
    for (LintAnalysis analysis : analyses) {
      currentMessages.clear();
      analysis.analyze(this);
      for (final Message message : currentMessages) {
        Collection<MessageListener> listeners = messageListeners.get(message.getCode());
        if (!Iterables.any(listeners, addressesMessage(message))) {
          messages.add(message);
        }
      }
    }
  }

  public void writeReport(ReportGenerator reporter, OutputStream out) {
    Collections.sort(messages);
    try {
      reporter.write(Lists.newArrayList(messages), out);
    } catch (IOException e) {
      System.err.println("Could not write report: ");
      e.printStackTrace();
    }
  }
  
  private static Predicate<MessageListener> addressesMessage(final Message message) {
    return new Predicate<MessageListener>() {
      @Override public boolean apply(MessageListener listener) {
        return listener.messageReported(message);
      }
    };
  }
}
