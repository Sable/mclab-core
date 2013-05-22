package mclint.refactoring;

import java.util.Collections;
import java.util.List;

import mclint.transform.Transformer;
import natlab.refactoring.Exceptions.RefactorException;

public abstract class Refactoring {
  protected Transformer transformer;
  
  protected Refactoring(Transformer transformer) {
    this.transformer = transformer;
  }
  
  public abstract void apply();
  
  public List<RefactorException> getErrors() {
    return Collections.emptyList();
  }
}
