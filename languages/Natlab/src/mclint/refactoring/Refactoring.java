package mclint.refactoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import natlab.refactoring.Exceptions.RefactorException;

public abstract class Refactoring {
  protected RefactoringContext context;
  private List<RefactorException> errors = new ArrayList<>();

  protected Refactoring(RefactoringContext context) {
    this.context = context;
  }

  public abstract boolean checkPreconditions();
  public abstract void apply();

  protected void addError(RefactorException error) {
    errors.add(error);
  }

  protected void addErrors(List<RefactorException> otherErrors) {
    errors.addAll(otherErrors);
  }
  
  public RefactoringContext getContext() {
    return context;
  }

  public List<RefactorException> getErrors() {
    return Collections.unmodifiableList(errors);
  }
}
