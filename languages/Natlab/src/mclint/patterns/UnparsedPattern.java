package mclint.patterns;

public class UnparsedPattern {
  private String pattern;
  private int index = 0;

  public static UnparsedPattern fromString(String pattern) {
    return new UnparsedPattern(pattern);
  }

  private UnparsedPattern(String pattern) {
    this.pattern = pattern;
  }
  
  private void advance() {
    if (index >= pattern.length()) {
      return;
    }
    do {
      ++index;
    } while (index < pattern.length() && Character.isWhitespace(pattern.charAt(index)));
  }
  
  private void advance(int by) {
    for (int i = 0; i < by; ++i) {
      advance();
    }
  }

  public boolean consume(String token) {
    if (pattern.regionMatches(index, token, 0, token.length())) {
      advance(token.length());
      return true;
    }
    return false;
  }

  public boolean startsWithMeta() {
    return pattern.length() - index >= 2 && pattern.charAt(index) == '%'
        && Character.isLetter(pattern.charAt(index + 1));
  }
  
  public boolean emptyAfterMeta() {
    return index + 2 >= pattern.length();
  }
  
  // Precondition: startsWithMeta()
  public char popMeta() {
    char meta = pattern.charAt(index + 1);
    advance(2);
    return meta;
  }
  
  public String asString() {
    return pattern;
  }
  
  @Override
  public String toString() {
    return String.format("<UnparsedPattern: %s>", pattern);
  }
}
