package natlab.refactoring;

import ast.FunctionList;
import mclint.MatlabProgram;
import mclint.McLintTestCase;


public class FunctionInlinerTest extends McLintTestCase {
    private void assertInliningOutput(String... expectedCode) {
      MatlabProgram f1 = project.getMatlabProgram("f1.m");
      FunctionInliner inliner = new FunctionInliner(basicContext(), ((FunctionList) f1.parse()).getFunction(0));
      inliner.apply();
      assertEquals(inliner.getErrors().size(), 0);
      assertEquivalent(f1.parse(), expectedCode);
    }
    public void testNoInline() {
        parse("f2.m",
            "function f2()",
            "  f = 1;",
            "end");
        parse("f1.m",
            "function x()",
            "  TT();",
            "end");
        assertInliningOutput(
            "function [] = x()",
            "  TT();",
            "end");
    }

    public void testInlineSimple() {
        parse("f2.m",
            "function out=f2()",
            "  out = 2;",
            "end");
        parse("f1.m",
            "function x()",
            "  in=f2();",
            "  disp(in);",
            "end");
        assertInliningOutput(
            "function [] = x()",
            "  out = 2;",
            "  disp(out);",
            "end");
    }

    public void testInlineWithExtraAssignments() {
        parse("f2.m",
            "function out=f2()",
            "  out = 2;",
             "end");
        parse("f1.m",
            "function x()",
             "  in=f2();",
             "end");
        assertInliningOutput(
            "function [] = x()",
            "  out = 2;",
            "end");
    }

    public void testInlineWithExtraAssignmentsAndInputs() {
        parse("f2.m",
            "function out=f2(j)",
             " out = j;",
             "end");
        parse("f1.m",
            "function x(y)",
            "  in=f2(y);",
            "end");
        assertInliningOutput(
            "function [] = x(y)",
            "  out = y;",
            "end");
    }

    public void testInlineWithNoExtraAssignments() {
        parse("f2.m",
            "function out=f2(j)",
            "  if(1)",
            "    disp(j);",
            "  else",
            "    j=1;",
            "  end",
            "  out = j;",
            "end");
        parse("f1.m",
            "function x(y)",
            "  in=f2(y);",
            "end");
        assertInliningOutput(
            "function [] = x(y)",
            "  j = y;",
            "  if 1",
            "    disp(j);",
            "  else",
            "    j = 1;",
            "  end",
            "  out = j;",
            "end");
    }
}
