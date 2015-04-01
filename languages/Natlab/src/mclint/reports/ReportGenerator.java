package mclint.reports;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import mclint.Message;

public interface ReportGenerator {
  void write(List<Message> messages, OutputStream out) throws IOException;
}
