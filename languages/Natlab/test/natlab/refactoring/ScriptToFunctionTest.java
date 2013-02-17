package natlab.refactoring;

import java.io.StringReader;
import java.util.ArrayList;

import junit.framework.TestCase;
import natlab.CompilationProblem;
import natlab.Parse;
import natlab.toolkits.filehandling.genericFile.BuiltinFile;
import ast.CompilationUnits;
import ast.Function;
import ast.Program;
import ast.Script;


public class ScriptToFunctionTest extends TestCase {
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

    public void testNoCallsError() {
        cu = new CompilationUnits();
        files = new String[]{"/s1.m", "/f1.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        Script s = (Script) addFile("/s1.m", "f = 1;");
        addFile("/f1.m", "function x() \n  TT();\n end");
        ScriptToFunction s2f = new ScriptToFunction(cu);
        Function f = new Function();
        assertEquals(s2f.replace(s, f).get(0).getClass(), Exceptions.NoCallsToScript.class);
    }

    public void testOneCallNoOutput() {
        cu = new CompilationUnits();
        files = new String[]{"/s1.m", "/f1.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        Script s = (Script) addFile("/s1.m", "f = 1;\n");
        addFile("/f1.m", "function x() \n  s1;\n end");
        ScriptToFunction s2f = new ScriptToFunction(cu);
        Function f = new Function();
        f.setName("s1f");
        assertEquals(s2f.replace(s, f).size(), 0);
        assertEquals(f.getPrettyPrinted(), "function [] = s1f()\nf = 1;\nend");
    }

   public void testOneCallWithOutput() {
        cu = new CompilationUnits();
        files = new String[]{"/s1.m", "/f1.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        Script s = (Script) addFile("/s1.m", "f = 1;\n");
        addFile("/f1.m", "function x() \n  s1;\n disp(f);\n end");
        ScriptToFunction s2f = new ScriptToFunction(cu);
        Function f = new Function();
        f.setName("s1f");
        assertEquals(s2f.replace(s, f).size(), 0);
        assertEquals(f.getPrettyPrinted(), "function [f] = s1f()\nf = 1;\nend");
    }

   public void testOneCallWithInput() {
        cu = new CompilationUnits();
        files = new String[]{"/s1.m", "/f1.m", "/f2.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        Script s = (Script) addFile("/s1.m", " b = f;\n f = 1;\n");
        addFile("/f1.m", "function x1() \n f = 2;\n  s1;\n end");
        ScriptToFunction s2f = new ScriptToFunction(cu);
        Function f = new Function();
        f.setName("s1f");
        assertEquals(s2f.replace(s, f).size(), 0);
        assertEquals(f.getPrettyPrinted(), "function [] = s1f(f)\nb = f;\nf = 1;\nend");
    }

   public void testOneCallWarnIDToFunction() {
        cu = new CompilationUnits();
        files = new String[]{"/s1.m", "/f1.m", "/f2.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        Script s = (Script) addFile("/s1.m", "disp(f);\n");
        addFile("/f1.m", "function x1() \n f = 2;\n  s1;\n end");
        ScriptToFunction s2f = new ScriptToFunction(cu);
        Function f = new Function();
        f.setName("s1f");
        assertTrue(s2f.replace(s, f).get(0) instanceof Exceptions.WarnIDToFunException);
        assertEquals(f.getPrettyPrinted(), "function [] = s1f(f)\ndisp(f);\nend");
    }
        
    public void testScriptWithMismatchingInputs() {
       cu = new CompilationUnits();
       files = new String[]{"/s1.m", "/f1.m", "/f2.m"};
       root = new BuiltinFile(files);
       cu.setRootFolder(root);
       Script s = (Script) addFile("/s1.m", "disp(f);\n");
       addFile("/f1.m", "function x1() \n f = 2;\n  s1;\n end");
       addFile("/f2.m", "function x1() \n s1;\n end");
       ScriptToFunction s2f = new ScriptToFunction(cu);
       Function f = new Function();
       f.setName("s1f");
       System.out.println(s2f.replace(s, f));
       assertTrue(s2f.replace(s, f).get(0) instanceof Exceptions.ScriptInputsNotMatching);
   }
}