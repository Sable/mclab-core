package natlab.tame.builtin.shapeprop.mathmode;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.builtin.shapeprop.ast.SPAbstractScalarExpr;
import natlab.tame.builtin.shapeprop.mathmode.ast.MathModeExprEvaluator;
import natlab.tame.builtin.shapeprop.mathmode.ast.SPMathModeAbstractExpr;
import natlab.tame.valueanalysis.components.shape.DimValue;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

import java.io.StringReader;
import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * @author dherre3
 * This node is the parent language for the mathmode in the shape propagation equations.
 * The math mode is simply a language of math operations and built-ins that where the
 * terminals are numbers, and lowercase identifiers, the result of the language is a value integer
 * The idea of this is to allow for very expressive math equations when propagating shapes.
 */
public class SPMathMode<V extends Value<V>> extends SPAbstractScalarExpr<V> {
    static boolean Debug = true;
    String mathString;
    SPMathModeAbstractExpr sp;
    public SPMathMode(String mathString){
        if(mathString != null){
            this.mathString = mathString;
            if (Debug) System.out.println("parsing: "+mathString);
            ShapePropMathModeParser parser = new ShapePropMathModeParser();
            ShapePropMathModeScanner scanner = new ShapePropMathModeScanner(new StringReader(mathString));
            try {
                this.sp = (SPMathModeAbstractExpr) parser.parse(scanner);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }
    private static SPMathModeAbstractExpr parse(String s) throws Exception{
        ShapePropMathModeParser parser = new ShapePropMathModeParser();
        ShapePropMathModeScanner scanner = new ShapePropMathModeScanner(new StringReader(s));
        return  (SPMathModeAbstractExpr) parser.parse(scanner);
    }

    @Override
    public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
        HashMap<String, DimValue> lowercase = new HashMap<>();
        if(sp != null){
            MathModeExprEvaluator<V> mathModeExprEvaluator = new MathModeExprEvaluator<>(previousMatchResult, argValues,Nargout);
            Integer val = sp.apply(mathModeExprEvaluator);
            if(!mathModeExprEvaluator.hasError()
                    && previousMatchResult.getIsInsideAssign()){
                lowercase.put(previousMatchResult.getLatestMatchedLowercase(),
                        new DimValue(val, null));
                return new ShapePropMatch<>(previousMatchResult, lowercase, null);
            }else {
                previousMatchResult.setIsError(true);
                return previousMatchResult;
            }
        }else {
            previousMatchResult.setIsError(true);
            return previousMatchResult;
        }
    }
    public static void main(String[] args){
        try {
            SPMathModeAbstractExpr expr  = SPMathMode.parse("{ -3 -3 - -2 +2-2 }");
            MathModeExprEvaluator eva = new MathModeExprEvaluator<>();
            System.out.println(expr.apply(eva));
        }catch (Exception e){
           e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return sp.toString();
    }
}
