package natlab.Static.valueanalysis.constant;

import java.util.List;

import natlab.Static.builtin.Builtin;
import natlab.Static.builtin.BuiltinVisitor;

/**
 * propagates constants.
 * For any case, takes the arguments as a list, and returns the result as a constant.
 * If the result is not a constant, returns null.
 * This is a singleton class, whose only instance is returned via 'getInstance()'.
 * 
 * TODO: how to deal with error cases?
 * 
 * 
 * @author adubra
 */

public class ConstantPropagator extends BuiltinVisitor<List<Constant>, Constant>{
    static ConstantPropagator instance = null;
    static ConstantPropagator getInstance(){
        if (instance == null) instance = new ConstantPropagator();
        return instance;
    }
    
    private ConstantPropagator(){}
     
    @Override
    public Constant caseBuiltin(Builtin builtin, List<Constant> arg) {
        return null;
    }
    
    /* the constants 
     * TODO - check whether there are no arguments */
    @Override
    public Constant casePi(Builtin builtin, List<Constant> arg) {
        return Constant.get(Math.PI);
    }
   
    @Override
    public Constant caseTrue(Builtin builtin, List<Constant> arg) {
        return Constant.get(true);
    }
    @Override
    public Constant caseFalse(Builtin builtin, List<Constant> arg) {
        return Constant.get(false);
    }
}



