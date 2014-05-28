package mclint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import junit.framework.TestCase;
import mclint.refactoring.RefactoringContext;
import mclint.util.Parsing;
import ast.ASTNode;
import ast.Program;

public abstract class McLintTestCase extends TestCase {
  protected Project project;
  
  @Override
  protected void setUp() throws IOException {
    project = Project.at(Files.createTempDirectory(null));
  }
  
  protected String join(String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }
  
  protected MatlabProgram parse(String path, String... lines) {
    Path file = project.getProjectRoot().resolve(path);
    try {
      Files.write(file, join(lines).getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
    }
    return project.addMatlabProgram(file);
  }
  
  protected RefactoringContext basicContext() {
    return RefactoringContext.create(project, RefactoringContext.Transformations.BASIC);
  }

  protected RefactoringContext layoutPreservingContext() {
    return RefactoringContext.create(project, RefactoringContext.Transformations.LAYOUT_PRESERVING);
  }

  protected void assertEquivalent(ASTNode<?> program, String... lines) {
    Program expected = Parsing.string(join(lines));
    // Not sure why these calls to trim() are necessary; sometimes getPrettyPrinted() seems to
    // tack on a newline at the end of the output...
    assertEquals(expected.getPrettyPrinted().trim(), program.getPrettyPrinted().trim());
  }
}
