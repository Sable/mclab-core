package natlab.refactoring;

import ast.Program;

public class FEvalToCallTest extends RefactoringTestCase {
    public void testConvertSimple() {
        addFile("f1.m", "function f1() \n f = 1;\n end");
        Program p = addFile("f2.m", "function f2() \n b = feval('f1'); \n end");
        FevalToCall ref = new FevalToCall(cu);
        ref.replaceAll(); 
        assertEquals(p.getPrettyPrinted(), "function [] = f2()\n  b = f1();\nend\n");      
    }

    public void testConvertVarRenameRequired() {
        addFile("f1.m", "function f1() \n f = 1;\n end");
        Program p = addFile("f2.m", "function f2() \n f1 = feval('f1'); \n end");
        FevalToCall ref = new FevalToCall(cu);
        ref.replaceAll(); 
        assertEquals(p.getPrettyPrinted(), "function [] = f2()\n  f1 = feval('f1');\nend\n");      
    }
}