package mclint.reports;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import mclint.Message;

abstract class AbstractReportGenerator implements ReportGenerator {

  @Override
  public void write(List<Message> messages, OutputStream out) throws IOException {
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
    writer.write(header());
    for (Message message : messages) {
      writer.write(message(message));
      writer.newLine();
    }
    writer.write(footer());
    writer.flush();
  }

  protected String message(Message message) { return ""; }
  protected String header() { return ""; }
  protected String footer() { return ""; }
}
