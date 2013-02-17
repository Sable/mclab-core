package natlab.refactoring;

import java.io.StringReader;
import java.util.ArrayList;

import junit.framework.TestCase;
import natlab.CompilationProblem;
import natlab.Parse;
import natlab.toolkits.filehandling.BuiltinFile;
import ast.CompilationUnits;
import ast.Program;


public class FEvalToCallTest extends TestCase {
    CompilationUnits cu = null;
    String[] files = null;
    BuiltinFile root = null;

    private Program addFile(String filename, String content) {
        ArrayList<CompilationProblem> errorList = new ArrayList<CompilationProblem>();
        Program p = Parse.parseNatlabFile(filename, new StringReader(content), errorList);
        assertTrue(errorList.isEmpty());
        p.setFile(root.getBuiltin(filename));
        cu.addProgram(p);
        return p;
    }

    public void testConvertSimple() {
        cu = new CompilationUnits();
        files = new String[]{"/f2.m", "/f1.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        addFile("/f1.m", "function f1() \n f = 1;\n end");
        Program p = addFile("/f2.m", "function f2() \n b = feval('f1'); \n end");
        FevalToCall ref = new FevalToCall(cu);
        ref.replaceAll(); 
        assertEquals(p.getPrettyPrinted(), "function [] = f2()\n  b = f1();\nend\n");      
    }

    public void testConvertVarRenameRequired() {
        cu = new CompilationUnits();
        files = new String[]{"/f2.m", "/f1.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        addFile("/f1.m", "function f1() \n f = 1;\n end");
        Program p = addFile("/f2.m", "function f2() \n f1 = feval('f1'); \n end");
        FevalToCall ref = new FevalToCall(cu);
        ref.replaceAll(); 
        assertEquals(p.getPrettyPrinted(), "function [] = f2()\n  f1 = feval('f1');\nend\n");      
    }

}