package mclint;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mclint.reports.ReportGenerator;


/**
 * The analysis runner. This class calls the <tt>analyze</tt> method of each
 * analysis, passing itself as argument so it can gather the messages
 * generated.
 * @author ismail
 *
 */

public class Lint {
  private List<Message> messages = new ArrayList<Message>();
  private List<LintAnalysis> analyses;

  public Lint(List<LintAnalysis> analyses) {
    this.analyses = analyses;
  }

  public void report(Message message) {
    messages.add(message);
  }

  public void runAnalyses() {
    for (LintAnalysis analysis : analyses)
      analysis.analyze(this);
  }

  public void writeReport(ReportGenerator reporter, OutputStream out) {
    Collections.sort(messages);
    try {
      reporter.write(new ArrayList<Message>(messages), out);
    } catch (IOException e) {
      System.err.println("Could not write report: ");
      e.printStackTrace();
    }
  }   
}
