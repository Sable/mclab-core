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

public abstract class McLintTestCase extends TestCase {
  protected Project project;
  protected MatlabProgram program;
  protected AnalysisKit kit;

  @Override
  protected void setUp() throws IOException {
    project = Project.at(Files.createTempDirectory(null));
  }

  protected void parse(String code) {
    Path file = project.getProjectRoot().resolve("f.m");
    try {
      Files.write(file, code.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
    }
    program = project.addMatlabProgram(file);
    program.parse();
    kit = program.analyze();
  }

  protected RefactoringContext createContext() {
    return RefactoringContext.create(program);
  }

  protected void assertEquivalent(String expectedCode, ASTNode<?> program) {
    Program expected = Parsing.string(expectedCode);
    assertEquals(expected.getPrettyPrinted(), program.getPrettyPrinted());
  }
}
