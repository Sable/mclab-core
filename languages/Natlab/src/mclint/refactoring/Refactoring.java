package mclint.refactoring;

import mclint.transform.Transformer;

public abstract class Refactoring {
  protected Transformer transformer;
  
  protected Refactoring(Transformer transformer) {
    this.transformer = transformer;
  }
  
  public abstract void apply();

}
