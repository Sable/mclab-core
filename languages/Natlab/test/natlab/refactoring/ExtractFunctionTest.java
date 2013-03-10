package natlab.refactoring;

import mclint.transform.Transformer;
import mclint.transform.Transformers;
import ast.Function;
import ast.FunctionList;


public class ExtractFunctionTest extends RefactoringTestCase {
    private ExtractFunction extract(FunctionList s, int from, int to) {
      Transformer transformer = Transformers.basic(s);
      ExtractFunction xf = new ExtractFunction(transformer,
          s.getFunction(0), from, to, "xf");
      xf.apply();
      return xf;
    }

    public void testExtractFunction() {
        FunctionList s = (FunctionList) addFile("f1.m", 
                                                "function f()\n" +
                                                "f = 1;\n" +
                                                "b = f+1;\n" +
                                                "disp(b);\n" +
                                                "end");
        ExtractFunction xf = extract(s, 1, 2);
        assertEquals("function [b] = xf(f)\n" +
                     "  b = (f + 1);\n" +
                     "end", xf.getExtractedFunction().getPrettyPrinted());
    } 

    public void testExtractFunctionMaybeUndefInput() {
        FunctionList s = (FunctionList) addFile("f1.m", 
                                                "function f()\n" +
                                                "if (1)\n" + 
                                                "  f = 1;\n" +
                                                "end\n" + 
                                                "b = f+1;\n" +
                                                "disp(b);\n" +
                                                "end");
        ExtractFunction xf = extract(s, 1, 2);
        assertEquals(Exceptions.FunctionInputCanBeUndefined.class, 
                     xf.getErrors().get(0).getClass());
    }

    public void testExtractFunctionMaybeUndefOutput() {
        FunctionList s = (FunctionList) addFile("f1.m", 
                                                "function f()\n" +
                                                "f = 1;\n" +
                                                "b = 1;\n" +
                                                "if (1)\n" +
                                                "  b = f+1;\n" +
                                                "end\n" +
                                                "disp(b);\n" +
                                                "end");
        ExtractFunction xf = extract(s, 2, 3);
        assertEquals(Exceptions.FunctionOutputCanBeUndefined.class, 
                     xf.getErrors().get(0).getClass());

        assertEquals("function [b] = xf(f, b)\n" +
                     "  if 1\n" +
                     "    b = (f + 1);\n" +
                     "  end\n" +
                     "end", xf.getExtractedFunction().getPrettyPrinted());
    }
}