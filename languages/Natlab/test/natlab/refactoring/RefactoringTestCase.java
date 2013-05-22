package natlab.refactoring;

import java.io.StringReader;
import java.util.ArrayList;

import junit.framework.TestCase;
import natlab.CompilationProblem;
import natlab.Parse;
import natlab.toolkits.path.MockFile;
import ast.CompilationUnits;
import ast.Program;

public class RefactoringTestCase extends TestCase {
  protected CompilationUnits cu;
  protected MockFile root;
  
  protected void setUp() {
    cu = new CompilationUnits();
    root = MockFile.directory("");
    cu.setRootFolder(root);
  }

  protected Program addFile(String filename, String content) {
    ArrayList<CompilationProblem> errorList = new ArrayList<CompilationProblem>();
    Program p = Parse.parseNatlabFile(filename, new StringReader(content), errorList);
    assertTrue(errorList.isEmpty());
    root.with(filename, content);
    p.setFile(root.getChild(filename));
    cu.addProgram(p);
    return p;
  }

}
