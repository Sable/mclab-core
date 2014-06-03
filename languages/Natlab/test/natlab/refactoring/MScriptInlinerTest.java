package natlab.refactoring;

import mclint.MatlabProgram;
import mclint.McLintTestCase;
import mclint.refactoring.Refactorings;
import ast.Script;

public class MScriptInlinerTest extends McLintTestCase {
    public void testNoInline() {
        MatlabProgram s1 = parse("s1.m", "f = 1;");
        MatlabProgram f1 = parse("f1.m",
            "function x()",
            "  f = 2;",
            "end");
        
        MScriptInliner inliner = Refactorings.inlineScript(basicContext(), (Script) s1.parse());
        inliner.apply();

        assertTrue(inliner.getErrors().isEmpty());
        assertEquivalent(f1.parse(), "function [] = x()\n  f = 2;\nend\n");
    }

    
    public void testInlineNormal() {
        MatlabProgram s1 = parse("s1.m", "f = 1;");
        MatlabProgram f1 = parse("f1.m", "function x() \n s1; \n end");
        MScriptInliner inliner = Refactorings.inlineScript(basicContext(), (Script) s1.parse());
        inliner.apply();

        assertTrue(inliner.getErrors().isEmpty());
        assertEquivalent(f1.parse(), "function [] = x()\n  f = 1;\nend\n");
    }

    public void testInlineFunctionVarConflict() {
        MatlabProgram s1 = parse("s1.m", "b = @size;");
        parse("f1.m", "function x() \n s1; size = 1; \n end");
        MScriptInliner inliner = Refactorings.inlineScript(basicContext(), (Script) s1.parse());
        inliner.apply();

        assertEquals(inliner.getErrors().size(), 1);
        assertEquals(inliner.getErrors().get(0).getClass(), Exceptions.RenameRequired.class);
    }

    public void testUnassignedIDBecomesVariable() {
        MatlabProgram s1 = parse("s1.m", "b = zeros(1,2);");
        parse("f1.m", "function x() \n s1; zeros = 1; \n end");
        MScriptInliner inliner = Refactorings.inlineScript(basicContext(), (Script) s1.parse());
        inliner.apply();

        assertEquals(inliner.getErrors().size(), 1);
        assertEquals(inliner.getErrors().get(0).getClass(), Exceptions.UnassignedVariableException.class);
    }

    public void testIDBecomesFunction() {;
        MatlabProgram s1 = parse("s1.m", "b = zeros(1,2);");
        parse("f1.m", "function x() \n s1; \n end");
        MScriptInliner inliner = Refactorings.inlineScript(basicContext(), (Script) s1.parse());
        inliner.apply();

        assertEquals(inliner.getErrors().size(), 1);
        assertEquals(inliner.getErrors().get(0).getClass(), Exceptions.WarnIDToFunException.class);
    }
}