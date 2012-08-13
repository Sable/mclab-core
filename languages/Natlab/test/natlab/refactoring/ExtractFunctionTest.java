package natlab.refactoring;

import ast.*;
import natlab.*;
import natlab.toolkits.filehandling.genericFile.*;

import junit.framework.*;

import java.io.*;
import java.util.*;


public class ExtractFunctionTest extends TestCase {
    CompilationUnits cu = null;
    String[] files = null;
    BuiltinFile root = null;

    private Program addFile(String filename, String content) {
        ArrayList<CompilationProblem> errorList = new ArrayList<CompilationProblem>();
        Program p = Parse.parseFile(filename, new StringReader(content), errorList);
        assertTrue(errorList.isEmpty());
        p.setFile(root.getBuiltin(filename));
        cu.addProgram(p);
        return p;
    }

    public void testExtractFunction() {
        cu = new CompilationUnits();
        files = new String[]{"/f1.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        FunctionList s = (FunctionList) addFile("/f1.m", 
                                                "function f()\n" +
                                                "f = 1;\n" +
                                                "b = f+1;\n" +
                                                "disp(b);\n" +
                                                "end");
        ExtractFunction xf = new ExtractFunction(cu);
        Function f = new Function();
        f.setName("xf");
        xf.extract(s.getFunction(0).getStmtList(), 1, 2, f);
        assertEquals("function  [b] = xf(f)\n" +
                     "  b = (f + 1);\n" +
                     "end", f.getPrettyPrinted());
    } 

    public void testExtractFunctionMaybeUndefInput() {
        cu = new CompilationUnits();
        files = new String[]{"/f1.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        FunctionList s = (FunctionList) addFile("/f1.m", 
                                                "function f()\n" +
                                                "if (1)\n" + 
                                                "  f = 1;\n" +
                                                "end\n" + 
                                                "b = f+1;\n" +
                                                "disp(b);\n" +
                                                "end");
        ExtractFunction xf = new ExtractFunction(cu);
        Function f = new Function();
        assertEquals(Exceptions.FunctionInputCanBeUndefined.class, 
                     xf.extract(s.getFunction(0).getStmtList(), 1, 2, f).get(0).getClass());              
    }

    public void testExtractFunctionMaybeUndefOutput() {
        cu = new CompilationUnits();
        files = new String[]{"/f1.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        FunctionList s = (FunctionList) addFile("/f1.m", 
                                                "function f()\n" +
                                                "f = 1;\n" +
                                                "b = 1;\n" +
                                                "if (1)\n" +
                                                "  b = f+1;\n" +
                                                "end\n" +
                                                "disp(b);\n" +
                                                "end");
        ExtractFunction xf = new ExtractFunction(cu);
        Function f = new Function();
        assertEquals(Exceptions.FunctionOutputCanBeUndefined.class, 
                     xf.extract(s.getFunction(0).getStmtList(), 2, 3, f).get(0).getClass());

        assertEquals("function  [b] = (f, b)\n" +
                     "  if 1\n" +
                     "    b = (f + 1);\n" +
                     "  end\n" +
                     "end", f.getPrettyPrinted());
    }
}