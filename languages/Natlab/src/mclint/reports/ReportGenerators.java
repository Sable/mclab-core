package mclint.reports;

public class ReportGenerators {
  public static ReportGenerator plain() {
    return new PlainTextReportGenerator();
  }

  public static ReportGenerator html() {
    return new HtmlReportGenerator();
  }

  private ReportGenerators() {}
}
