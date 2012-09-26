package mclint.reports;

import mclint.Location;
import mclint.Message;

class PlainTextReportGenerator extends AbstractReportGenerator {
  @Override
  protected String message(Message message) {
    Location location = message.getLocation();
    String code = message.getCode();
    String description = message.getDescription();
    return String.format("%s: (%s) %s", location, code, description);
  }
}
