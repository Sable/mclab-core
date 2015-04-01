package natlab.refactoring;

import ast.Function;
import ast.Name;
import ast.Script;

public class ScriptToFunctionTest extends RefactoringTestCase {

    public void testNoCallsError() {
        Script s = (Script) addFile("s1.m", "f = 1;");
        addFile("f1.m", "function x() \n  TT();\n end");
        ScriptToFunction s2f = new ScriptToFunction(cu);
        Function f = new Function();
        assertEquals(s2f.replace(s, f).get(0).getClass(), Exceptions.NoCallsToScript.class);
    }

    public void testOneCallNoOutput() {
        Script s = (Script) addFile("s1.m", "f = 1;\n");
        addFile("f1.m", "function x() \n  s1;\n end");
        ScriptToFunction s2f = new ScriptToFunction(cu);
        Function f = new Function();
        f.setName(new Name("s1f"));
        assertEquals(s2f.replace(s, f).size(), 0);
        assertEquals(f.getPrettyPrinted(), "function [] = s1f()\nf = 1;\nend");
    }

   public void testOneCallWithOutput() {
        Script s = (Script) addFile("s1.m", "f = 1;\n");
        addFile("f1.m", "function x() \n  s1;\n disp(f);\n end");
        ScriptToFunction s2f = new ScriptToFunction(cu);
        Function f = new Function();
        f.setName(new Name("s1f"));
        assertEquals(s2f.replace(s, f).size(), 0);
        assertEquals(f.getPrettyPrinted(), "function [f] = s1f()\nf = 1;\nend");
    }

   public void testOneCallWithInput() {
        Script s = (Script) addFile("s1.m", " b = f;\n f = 1;\n");
        addFile("f1.m", "function x1() \n f = 2;\n  s1;\n end");
        ScriptToFunction s2f = new ScriptToFunction(cu);
        Function f = new Function();
        f.setName(new Name("s1f"));
        assertEquals(s2f.replace(s, f).size(), 0);
        assertEquals(f.getPrettyPrinted(), "function [] = s1f(f)\nb = f;\nf = 1;\nend");
    }

   public void testOneCallWarnIDToFunction() {
        Script s = (Script) addFile("s1.m", "disp(f);\n");
        addFile("f1.m", "function x1() \n f = 2;\n  s1;\n end");
        ScriptToFunction s2f = new ScriptToFunction(cu);
        Function f = new Function();
        f.setName(new Name("s1f"));
        assertTrue(s2f.replace(s, f).get(0) instanceof Exceptions.WarnIDToFunException);
        assertEquals(f.getPrettyPrinted(), "function [] = s1f(f)\ndisp(f);\nend");
    }
        
    public void testScriptWithMismatchingInputs() {
       Script s = (Script) addFile("s1.m", "disp(f);\n");
       addFile("f1.m", "function x1() \n f = 2;\n  s1;\n end");
       addFile("f2.m", "function x1() \n s1;\n end");
       ScriptToFunction s2f = new ScriptToFunction(cu);
       Function f = new Function();
       f.setName(new Name("s1f"));
       System.out.println(s2f.replace(s, f));
       assertTrue(s2f.replace(s, f).get(0) instanceof Exceptions.ScriptInputsNotMatching);
   }
}
