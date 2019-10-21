package natlab.tame.builtin.shapeprop.ast;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPOutputFunCall<V extends Value<V>> extends SPAbstractVectorExpr<V> {
    private SPFunCall<V> funCall;
    public SPOutputFunCall(SPFunCall<V> funCall) {
        this.funCall = funCall;
    }
    @SuppressWarnings("unchecked")
    @Override
    public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch previousMatchResult, Args argValues, int Nargout) {
       return funCall.match(isPatternSide, previousMatchResult, argValues,Nargout);
    }

    @Override
    public String toString() {
        return "("+ funCall.toString() +")";
    }
}
