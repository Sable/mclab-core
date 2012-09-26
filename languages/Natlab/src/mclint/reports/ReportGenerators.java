package mclint.reports;

public class ReportGenerators {
  public static ReportGenerator plainText() {
    return new PlainTextReportGenerator();
  }

  public static ReportGenerator html() {
    return new HtmlTableReportGenerator();
  }

  private ReportGenerators() {}
}
