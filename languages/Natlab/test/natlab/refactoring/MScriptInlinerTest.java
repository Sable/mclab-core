package natlab.refactoring;

import java.util.LinkedList;

import ast.Program;

public class MScriptInlinerTest extends RefactoringTestCase {
    public void testNoInline() {
        addFile("s1.m", "f = 1;");
        Program p = addFile("f1.m", "function x() \n f = 2; \n end");
        MScriptInliner scriptInliner = new MScriptInliner(cu);
        assertTrue(scriptInliner.inlineAll().isEmpty());
        assertEquals(p.getPrettyPrinted(), "function [] = x()\n  f = 2;\nend\n");      
    }

    public void testInlineNormal() {
        addFile("s1.m", "f = 1;");
        Program p = addFile("f1.m", "function x() \n s1; \n end");
        MScriptInliner scriptInliner = new MScriptInliner(cu);
        LinkedList<LinkedList<Exception>> errors = scriptInliner.inlineAll();
        assertEquals(errors.size(), 1);
        assertTrue(errors.get(0).isEmpty());
        assertEquals(p.getPrettyPrinted(), "function [] = x()\n  f = 1;\nend\n");      
    }

    public void testInlineFunctionVarConflict() {
        addFile("s1.m", "b = @size;");
        Program p = addFile("f1.m", "function x() \n s1; size = 1; \n end");
        MScriptInliner scriptInliner = new MScriptInliner(cu);
        LinkedList<LinkedList<Exception>> errors = scriptInliner.inlineAll();
        assertEquals(errors.size(), 1);
        assertEquals(errors.get(0).get(0).getClass(), Exceptions.RenameRequired.class);
    }

    public void testUnassignedIDBecomesVariable() {
        addFile("s1.m", "b = zeros(1,2);");
        Program p = addFile("f1.m", "function x() \n s1; zeros = 1; \n end");
        MScriptInliner scriptInliner = new MScriptInliner(cu);
        LinkedList<LinkedList<Exception>> errors = scriptInliner.inlineAll();
        assertEquals(errors.size(), 1);
        assertEquals(errors.get(0).get(0).getClass(), Exceptions.UnassignedVariableException.class);
    }

    public void testIDBecomesFunction() {;
        addFile("s1.m", "b = zeros(1,2);");
        addFile("f1.m", "function x() \n s1; \n end");
        MScriptInliner scriptInliner = new MScriptInliner(cu);
        LinkedList<LinkedList<Exception>> errors = scriptInliner.inlineAll();
        assertEquals(errors.size(), 1);
        assertEquals(errors.get(0).get(0).getClass(), Exceptions.WarnIDToFunException.class);
    }
}