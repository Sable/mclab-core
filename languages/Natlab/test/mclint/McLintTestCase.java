package mclint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import junit.framework.TestCase;
import mclint.refactoring.RefactoringContext;
import mclint.util.Parsing;
import ast.ASTNode;
import ast.Program;

import com.google.common.base.Joiner;

public abstract class McLintTestCase extends TestCase {
  protected Project project;
  protected MatlabProgram program;
  protected AnalysisKit kit;
  
  @Override
  protected void setUp() throws IOException {
    project = Project.at(Files.createTempDirectory(null));
  }
  
  private String join(String... lines) {
    return Joiner.on('\n').join(lines);
  }
  
  protected void parse(String... lines) {
    Path file = project.getProjectRoot().resolve("f.m");
    try {
      Files.write(file, join(lines).getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
    }
    program = project.addMatlabProgram(file);
    program.parse();
    kit = program.analyze();
  }

  protected RefactoringContext createBasicTransformerContext() {
    return RefactoringContext.create(program, program.getBasicTransformer());
  }
  
  protected RefactoringContext createLayoutPreservingContext() {
    return RefactoringContext.create(program);
  }

  protected void assertEquivalent(ASTNode<?> program, String... lines) {
    Program expected = Parsing.string(join(lines));
    // Not sure why these calls to trim() are necessary; sometimes getPrettyPrinted() seems to
    // tack on a newline at the end of the output...
    assertEquals(expected.getPrettyPrinted().trim(), program.getPrettyPrinted().trim());
  }
}
