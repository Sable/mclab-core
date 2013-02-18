package natlab.refactoring;

import ast.Program;


public class FunctionInlinerTest extends RefactoringTestCase {
    public void testNoInline() {
        addFile("f2.m", "function f2() \n f = 1; \n end");
        Program p = addFile("f1.m", "function x() \n  TT();\n end");
        FunctionInliner inliner = new FunctionInliner(cu);
        assertTrue(inliner.inlineAll().isEmpty());
        assertEquals(p.getPrettyPrinted(), "function [] = x()\n  TT();\nend\n");      
    }

    public void testInlineSimple() {
        addFile("f2.m", "function out=f2() \n out = 2; \n end");
        Program p = addFile("f1.m", "function x() \n  in=f2();\n disp(in);\n end");
        FunctionInliner inliner = new FunctionInliner(cu);
        inliner.inlineAll();
        assertEquals(p.getPrettyPrinted(), "function [] = x()\n  out = 2;\n  disp(out);\nend\n"); 
    }

    public void testInlineWithExtraAssignments() {
        addFile("f2.m", "function out=f2() \n out = 2; \n end");
        Program p = addFile("f1.m", "function x() \n  in=f2();\nend");
        FunctionInliner inliner = new FunctionInliner(cu);
        inliner.inlineAll();
        assertEquals(p.getPrettyPrinted(), "function [] = x()\n  out = 2;\nend\n"); 
    }

    public void testInlineWithExtraAssignmentsAndInputs() {
        addFile("f2.m", "function out=f2(j) \n out = j; \n end");
        Program p = addFile("f1.m", "function x(y) \n  in=f2(y);\nend");
        FunctionInliner inliner = new FunctionInliner(cu);
        inliner.inlineAll();
        assertEquals(p.getPrettyPrinted(), "function [] = x(y)\n  out = y;\nend\n"); 
    }

    public void testInlineWithNoExtraAssignments() {
        addFile("f2.m", "function out=f2(j) \n if(1) \n disp(j);\n else\n j=1; \n end \n out = j;\n end");
        Program p = addFile("f1.m", "function x(y) \n  in=f2(y);\nend");
        FunctionInliner inliner = new FunctionInliner(cu);
        inliner.inlineAll();
        assertEquals(p.getPrettyPrinted(), "function [] = x(y)\n  j = y;\n  if 1\n    disp(j);\n  else \n    j = 1;\n  end\n  out = j;\nend\n"); 
    }
}
