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
  
  private int nextAfter(int index) {
    if (index >= pattern.length()) {
      return index;
    }
    do {
      ++index;
    } while (index < pattern.length() && Character.isWhitespace(pattern.charAt(index)));
    return index;
  }
  
  private void advance() {
    index = nextAfter(index);
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
  
  private boolean validMeta(char x) {
    return Character.isLetter(x) || x == '_';
  }

  public boolean startsWithMeta() {
    return pattern.length() - index >= 2 && pattern.charAt(index) == '%'
        && validMeta(pattern.charAt(index + 1));
  }
  
  public UnparsedPattern afterMeta() {
    return UnparsedPattern.fromString(pattern.substring(nextAfter(index + 1)));
  }

  public boolean emptyAfterMeta() {
    return nextAfter(index + 1) >= pattern.length();
  }

  // Precondition: startsWithMeta()
  public char popMeta() {
    char meta = pattern.charAt(index + 1);
    advance(2);
    return meta;
  }

  public boolean finished() {
    return nextAfter(index) >= pattern.length();
  }

  public String asString() {
    return pattern;
  }

  @Override
  public String toString() {
    return String.format("<UnparsedPattern: %s, index %d, char %c>",
        pattern, index, pattern.charAt(index));
  }
}
