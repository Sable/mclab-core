package mclint.refactoring;

import mclint.MatlabProgram;
import mclint.Project;
import mclint.transform.Transformer;
import ast.ASTNode;

public class RefactoringContext {
  public static enum Transformations {
    BASIC,
    LAYOUT_PRESERVING
  };

  private Project project;
  private Transformations transformations;

  public static RefactoringContext create(Project project, Transformations transformations) {
    return new RefactoringContext(project, transformations);
  }
  
  public static RefactoringContext create(Project project) {
    return new RefactoringContext(project, Transformations.LAYOUT_PRESERVING);
  }

  private RefactoringContext(Project project, Transformations transformations) {
    this.project = project;
    this.transformations = transformations;
  }

  public Project getProject() {
    return project;
  }

  public Transformer getTransformer(MatlabProgram program) {
    if (transformations == Transformations.BASIC) {
      return program.getBasicTransformer();
    } else {
      return program.getLayoutPreservingTransformer();
    }
  }
  
  public Transformer getTransformer(ASTNode<?> node) {
    return getTransformer(node.getMatlabProgram());
  }
}
