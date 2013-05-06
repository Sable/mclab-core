package mclint.refactoring;

import mclint.MatlabProgram;
import mclint.Project;

public class RefactoringContext {
  private Project project;
  private MatlabProgram program;

  public static RefactoringContext create(MatlabProgram program) {
    return new RefactoringContext(program.getProject(), program);
  }

  private RefactoringContext(Project project, MatlabProgram program) {
    this.project = project;
    this.program = program;
  }

  public Project getProject() {
    return project;
  }

  public MatlabProgram getMatlabProgram() {
    return program;
  }
}
