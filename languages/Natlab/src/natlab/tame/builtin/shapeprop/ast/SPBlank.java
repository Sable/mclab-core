package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

/**
 * This class is meant to replace the [] as void type, since these are two different things in Matlab.
 * i.e. ones([]) returns an error, while ones() returns a scalar. In terms of output, disp(), for instance, does
 * not return [], it returns nothing. On the other hand, a built-in such as  vertcat([]) actually returns [].
 * @param <V> Value from the ValueAnalysis class, these values carry shapes which are use as basic unit to propagate
 *          Shape information
 */
public class SPBlank<V extends Value<V>> extends SPAbstractVectorExpr<V> {

    @Override
    public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int Nargout) {
        if(isPatternSide){
            if(!previousMatchResult.getIsInsideAssign()&&argValues.size() == 0){
                return previousMatchResult;
            }else{
                previousMatchResult.setIsError(true);
                return previousMatchResult;
            }
        }else{
            // Blank identifier should only represent void output, therefore if there are more arguments on the
            // output side or the number of targets is larger than 0, then is an error
            if(previousMatchResult.gethowManyEmitted() > 0 || Nargout>0){
                previousMatchResult.setIsError(true);
                return previousMatchResult;
            }
            // The default should be to return null.
            return previousMatchResult;
        }
    }

    @Override
    public String toString() {
        return "_";
    }
}
