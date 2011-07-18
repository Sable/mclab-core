package natlab.Static.valueanalysis.value;

import java.util.List;

import natlab.Static.builtin.Builtin;
import natlab.Static.builtin.BuiltinVisitor;

/**
 * This helps propagating values, when propagating the value
 * if each component does not capture enough information.
 * 
 * The base case is that each component is propagated
 * 
 * @author ant6n
 */

public class ValuePropagator extends BuiltinVisitor<Args, Value>{
    @Override
    public Value caseBuiltin(Builtin builtin, Args args) {
        //propagate the class
        
        
        
        return null;
    }

}
