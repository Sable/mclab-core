package mclint.refactoring;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mclint.MatlabProgram;
import mclint.Project;
import mclint.transform.Transformer;
import ast.ASTNode;

public class RefactoringContext {
  private Set<MatlabProgram> modifiedPrograms = new HashSet<>();

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

  public Transformer getTransformer(ASTNode<?> node) {
    return getTransformer(node.getMatlabProgram());
  }

  public Transformer getTransformer(MatlabProgram program) {
    // Assuming that if you get a transformer then you're modifying the program.
    // This is simpler than hooking into the Transformer somehow.
    modifiedPrograms.add(program);

    if (transformations == Transformations.BASIC) {
      return program.getBasicTransformer();
    } else {
      return program.getLayoutPreservingTransformer();
    }
  }

  public Set<MatlabProgram> getModifiedPrograms() {
    return Collections.unmodifiableSet(modifiedPrograms);
  }
}
