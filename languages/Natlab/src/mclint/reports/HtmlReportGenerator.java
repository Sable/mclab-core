package mclint.reports;

import mclint.Message;

class HtmlReportGenerator extends AbstractReportGenerator {
  @Override
  protected String header() {
    return "<table id=\"mclint-messages\">\n" +
        "<tr><td>Location</td><td>Code</td><td>Message</td></tr>\n";
  }

  @Override
  protected String message(Message message) {
    return String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>",
        message.getLocation(), message.getCode(), message.getDescription());
  }

  @Override
  protected String footer() {
    return "</table>";
  }
}
