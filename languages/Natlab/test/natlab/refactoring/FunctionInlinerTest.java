package natlab.refactoring;

import ast.*;
import natlab.*;
import natlab.toolkits.filehandling.genericFile.*;

import junit.framework.*;

import java.io.*;
import java.util.*;


public class FunctionInlinerTest extends TestCase {
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
        files = new String[]{"/f1.m", "/f2.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        addFile("/f2.m", "function f2() \n f = 1; \n end");
        Program p = addFile("/f1.m", "function x() \n  TT();\n end");
        FunctionInliner inliner = new FunctionInliner(cu);
        assertTrue(inliner.inlineAll().isEmpty());
        assertEquals(p.getPrettyPrinted(), "function [] = x()\n  TT();\nend\n");      
    }

    public void testInlineSimple() {
        cu = new CompilationUnits();
        files = new String[]{"/f2.m", "/f1.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        addFile("/f2.m", "function out=f2() \n out = 2; \n end");
        Program p = addFile("/f1.m", "function x() \n  in=f2();\n disp(in);\n end");
        FunctionInliner inliner = new FunctionInliner(cu);
        inliner.inlineAll();
        assertEquals(p.getPrettyPrinted(), "function [] = x()\n  out = 2;\n  disp(out);\nend\n"); 
    }

    public void testInlineWithExtraAssignments() {
        cu = new CompilationUnits();
        files = new String[]{"/f2.m", "/f1.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        addFile("/f2.m", "function out=f2() \n out = 2; \n end");
        Program p = addFile("/f1.m", "function x() \n  in=f2();\nend");
        FunctionInliner inliner = new FunctionInliner(cu);
        inliner.inlineAll();
        assertEquals(p.getPrettyPrinted(), "function [] = x()\n  out = 2;\nend\n"); 
    }

    public void testInlineWithExtraAssignmentsAndInputs() {
        cu = new CompilationUnits();
        files = new String[]{"/f2.m", "/f1.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        addFile("/f2.m", "function out=f2(j) \n out = j; \n end");
        Program p = addFile("/f1.m", "function x(y) \n  in=f2(y);\nend");
        FunctionInliner inliner = new FunctionInliner(cu);
        inliner.inlineAll();
        assertEquals(p.getPrettyPrinted(), "function [] = x(y)\n  out = y;\nend\n"); 
    }

    public void testInlineWithNoExtraAssignments() {
        cu = new CompilationUnits();
        files = new String[]{"/f2.m", "/f1.m"};
        root = new BuiltinFile(files);
        cu.setRootFolder(root);
        addFile("/f2.m", "function out=f2(j) \n if(1) \n disp(j);\n else\n j=1; \n end \n out = j;\n end");
        Program p = addFile("/f1.m", "function x(y) \n  in=f2(y);\nend");
        FunctionInliner inliner = new FunctionInliner(cu);
        inliner.inlineAll();
        assertEquals(p.getPrettyPrinted(), "function [] = x(y)\n  j = y;\n  if 1\n    disp(j);\n  else \n    j = 1;\n  end\n  out = j;\nend\n"); 
    }

}
