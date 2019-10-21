package natlab.tame.builtin.shapeprop.mathmode.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

import java.util.ArrayList;
import java.util.List;

public class MathModeExprEvaluator<V extends Value<V>> implements AbstractMathModeVisitor<Integer>{
    public static boolean Debug = false;
    private ShapePropMatch<V> previousMatchResult;
    private Args<V> argValues;
    private int argout;
    private boolean error = false;

    public MathModeExprEvaluator(){

    }
    public MathModeExprEvaluator(ShapePropMatch<V> previousMatchResult, Args<V> argValues, int argout) {
        this.previousMatchResult = previousMatchResult;
        this.argValues = argValues;
        this.argout = argout;
    }

    @Override
    public Integer visitBinOp(SPMathModeBinOpExpr binOp) {
        Integer left = binOp.left.apply(this);
        Integer right = binOp.right.apply(this);
        switch (binOp.op){
            case POW:
                if(Debug) System.out.print("Math.pow("+left+","+right+")");
                return (int) Math.pow(left,right);
            case PLUS:
                if(Debug) System.out.print("("+left+"+"+right+")");

                return left + right;
            case MINUS:
                if(Debug) System.out.print("("+left+"-"+right+")");

                return left - right;
            case TIMES:
                if(Debug) System.out.print("("+left+"*"+right+")");

                return left*right;
            case DIV:
                if(Debug) System.out.print("("+left+"/"+right+")");
                return left/right;
            case LT:
                if(Debug) System.out.print("("+left+"<"+right+")");

                return (left<right)?1:0;
            case GT:
                if(Debug) System.out.print("("+left+">"+right+")");

                return (left>right)?1:0;
            case LE:
                if(Debug) System.out.print("("+left+"<="+right+")");

                return (left<=right)?1:0;
            case GE:
                if(Debug) System.out.print("("+left+">="+right+")");
                return (left>=right)?1:0;
            case EQ:
                if(Debug) System.out.print("("+left+"=="+right+")");
                return (left.equals(right))?1:0;
            case NE:
                if(Debug) System.out.print("("+left+"!="+right+")");
                return (!left.equals(right))?1:0;
            case AND:
                if(Debug) System.out.print("("+left+"&&"+right+")");
                return ((left!=0)&&(right!=0))?1:0;
            default:
                throw new UnsupportedOperationException("Operation not supported in Math Mode");
        }
    }

    @Override
    public Integer visitUnOp(SPMathModeUnOpExpr unOp) {
        Integer val = unOp.expr.apply(this);
        if(Debug) System.out.print("-"+val);
        return -val;
    }

    @Override
    public Integer visitLowerCase(SPMathModeLowercaseExpr lowerCaseExpr) {
        if(previousMatchResult.hasValue(lowerCaseExpr.lowercase) &&
                previousMatchResult.getValueOfVariable(lowerCaseExpr.lowercase).hasIntValue()){
            if(Debug) System.out.print(previousMatchResult.getValueOfVariable(lowerCaseExpr.lowercase).getIntValue());
            return previousMatchResult.getValueOfVariable(lowerCaseExpr.lowercase).getIntValue();
        }else{
            error = true;
        }
        return 0;
    }

    @Override
    public Integer visitNumber(SPMathModeNumberExpr numberExpr) {
        if(Debug) System.out.print(numberExpr.number.intValue());
        return numberExpr.number.intValue();
    }

    @Override
    public Integer visitFuncCall(SPMathModeFuncCallExpr funcCallExpr) {
        List<SPMathModeAbstractExpr> argIntegerList = new ArrayList<>();
        SPMathModeArglist argList = funcCallExpr.arguments;
        while(argList!=null){
            SPMathModeAbstractExpr expr =  argList.expr;
            argIntegerList.add(expr);
            argList = argList.next;
        }
        String funcName = funcCallExpr.funcName;
        if(funcName!= null){
            if(funcName.equals("abs")){
                if(argIntegerList.size() == 1){
                    Integer val = argIntegerList.get(0).apply(this);
                    if(Debug)System.out.print("abs("+val+")");
                    return Math.abs(val);
                }else{
                    System.err.print("abs() function in math mode only accepts 1 argument");
                }
            }else if(funcName.equals("select")){
                if(argIntegerList.size() == 3){
                    Integer condition = argIntegerList.get(0).apply(this);
                    if(Debug)System.out.print("select(cond:"+condition+","+
                            argIntegerList.get(1).apply(this)+","+
                            argIntegerList.get(2).apply(this)+")");

                    if(condition == 1 ) return argIntegerList.get(1).apply(this);
                    if(condition == 0 ) return argIntegerList.get(2).apply(this);
                }
            }
        }
        error = true;
        return 0;
    }

    public boolean hasError() {
        return error;
    }
}
