package mclint.util;

import natlab.toolkits.analysis.Merger;

/**
 * Provides some useful/common Mergers to be used for McSAF structural analyses.
 * @author ismail
 *
 */
public class Mergers {
  public static Merger<Boolean> OR = new Merger<Boolean>() {
    public Boolean merge(Boolean b1, Boolean b2) {
      return b1 || b2;
    }
  };

  public static Merger<Boolean> AND = new Merger<Boolean>() {
    public Boolean merge(Boolean b1, Boolean b2) {
      return b1 && b2;
    }
  };

  private Mergers() {}
}
