package mclint.patterns;

public class UnparsedPattern {
  private String originalPattern;
  private String pattern;

  public static UnparsedPattern fromString(String pattern) {
    return new UnparsedPattern(pattern);
  }

  private UnparsedPattern(String pattern) {
    this.originalPattern = pattern;
    this.pattern = pattern.replaceAll("\\s+", "");
  }

  public boolean consume(String token) {
    if (pattern.startsWith(token)) {
      pattern = pattern.substring(token.length());
      return true;
    }
    return false;
  }

  public boolean startsWithMeta() {
    return pattern.length() >= 2 && pattern.charAt(0) == '%'
        && Character.isLetter(pattern.charAt(1));
  }
  
  public boolean emptyAfterMeta() {
    return pattern.substring(2).isEmpty();
  }
  
  // Precondition: startsWithMeta()
  public char popMeta() {
    char meta = pattern.charAt(1);
    pattern = pattern.substring(2);
    return meta;
  }
  
  public String asString() {
    return originalPattern;
  }
  
  @Override
  public String toString() {
    return String.format("<UnparsedPattern: %s>", originalPattern);
  }
}
