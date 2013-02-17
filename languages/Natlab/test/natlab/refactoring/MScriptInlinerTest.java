package natlab.refactoring;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;

import junit.framework.TestCase;
import natlab.CompilationProblem;
import natlab.Parse;
import natlab.toolkits.filehandling.BuiltinFile;
import ast.CompilationUnits;
import ast.Program;


public class MScriptInlinerTest extends TestCase {
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

    public void testNoInline() {
        cu = new CompilationUnits();
        files = new String[]{"/s1.m", "f1.m"};
        root = new BuiltinFile(files);
        addFile("/s1.m", "f = 1;");
        Program p = addFile("/f1.m", "function x() \n f = 2; \n end");
        MScriptInliner scriptInliner = new MScriptInliner(cu);
        assertTrue(scriptInliner.inlineAll().isEmpty());
        assertEquals(p.getPrettyPrinted(), "function [] = x()\n  f = 2;\nend\n");      
    }

    public void testInlineNormal() {
        cu = new CompilationUnits();
        files = new String[]{"/s1.m", "/f1.m"};
        root = new BuiltinFile(files);
        addFile("/s1.m", "f = 1;");
        cu.setRootFolder(root);
        Program p = addFile("/f1.m", "function x() \n s1; \n end");
        MScriptInliner scriptInliner = new MScriptInliner(cu);
        LinkedList<LinkedList<Exception>> errors = scriptInliner.inlineAll();
        assertEquals(errors.size(), 1);
        assertTrue(errors.get(0).isEmpty());
        assertEquals(p.getPrettyPrinted(), "function [] = x()\n  f = 1;\nend\n");      
    }

    public void testInlineFunctionVarConflict() {
        cu = new CompilationUnits();
        files = new String[]{"/s1.m", "/f1.m"};
        root = new BuiltinFile(files);
        addFile("/s1.m", "b = @size;");
        cu.setRootFolder(root);
        Program p = addFile("/f1.m", "function x() \n s1; size = 1; \n end");
        MScriptInliner scriptInliner = new MScriptInliner(cu);
        LinkedList<LinkedList<Exception>> errors = scriptInliner.inlineAll();
        assertEquals(errors.size(), 1);
        assertEquals(errors.get(0).get(0).getClass(), Exceptions.RenameRequired.class);
    }

    public void testUnassignedIDBecomesVariable() {
        cu = new CompilationUnits();
        files = new String[]{"/s1.m", "/f1.m", "/zeros.m"};
        root = new BuiltinFile(files);
        addFile("/s1.m", "b = zeros(1,2);");
        cu.setRootFolder(root);
        Program p = addFile("/f1.m", "function x() \n s1; zeros = 1; \n end");
        MScriptInliner scriptInliner = new MScriptInliner(cu);
        LinkedList<LinkedList<Exception>> errors = scriptInliner.inlineAll();
        assertEquals(errors.size(), 1);
        assertEquals(errors.get(0).get(0).getClass(), Exceptions.UnassignedVariableException.class);
    }

    public void testIDBecomesFunction() {
        cu = new CompilationUnits();
        files = new String[]{"/s1.m", "/f1.m", "/zeros.m"};
        root = new BuiltinFile(files);
        addFile("/s1.m", "b = zeros(1,2);");
        cu.setRootFolder(root);
        Program p = addFile("/f1.m", "function x() \n s1; \n end");
        MScriptInliner scriptInliner = new MScriptInliner(cu);
        LinkedList<LinkedList<Exception>> errors = scriptInliner.inlineAll();
        assertEquals(errors.size(), 1);
        assertEquals(errors.get(0).get(0).getClass(), Exceptions.WarnIDToFunException.class);
    }
}