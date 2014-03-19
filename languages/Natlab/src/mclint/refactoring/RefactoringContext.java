package mclint.refactoring;

import mclint.MatlabProgram;
import mclint.Project;
import mclint.transform.Transformer;

public class RefactoringContext {
  private Project project;
  private MatlabProgram program;
  private Transformer transformer;

  public static RefactoringContext create(MatlabProgram program) {
    return new RefactoringContext(program.getProject(), program,
        program.getLayoutPreservingTransformer());
  }

  private RefactoringContext(Project project, MatlabProgram program, Transformer transformer) {
    this.project = project;
    this.program = program;
    this.transformer = transformer;
  }

  public Project getProject() {
    return project;
  }

  public MatlabProgram getMatlabProgram() {
    return program;
  }
  
  public Transformer getTransformer() {
    return transformer;
  }
}
