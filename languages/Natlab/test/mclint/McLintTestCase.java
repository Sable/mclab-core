package mclint;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import mclint.refactoring.RefactoringContext;
import mclint.util.Parsing;
import ast.ASTNode;
import ast.Program;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public abstract class McLintTestCase extends TestCase {
  protected Project project;
  protected MatlabProgram program;
  protected AnalysisKit kit;

  @Override
  protected void setUp() {
    project = Project.at(Files.createTempDir());
  }

  protected void parse(String code) {
    File root = Files.createTempDir();
    File file = new File(root, "f.m");
    try {
      Files.write(code, file, Charsets.UTF_8);
    } catch (IOException e) {
    }
    project = Project.at(root);
    program = project.getMatlabPrograms().iterator().next();
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
