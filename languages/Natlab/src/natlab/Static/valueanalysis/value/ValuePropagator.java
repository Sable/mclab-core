package natlab.Static.valueanalysis.value;

import java.util.List;

import natlab.Static.builtin.Builtin;
import natlab.Static.builtin.BuiltinVisitor;
import natlab.toolkits.path.FunctionReference;

/**
 * Propagate values for builtin functions.
 * 
 * Some of the cases where the argument types are not matrizes are
 * implemented in this class - this way, a ValueProagator for some
 * given MatrixValue type has to only implement the cases for functions
 * which operate on MatrixValues.
 * 
 * @author ant6n
 */

public abstract class ValuePropagator<D extends MatrixValue<D>> 
    extends BuiltinVisitor<Args<D>, Res<D>>{
    
    /**
     * produces the abstract interpretation result of calling the given builtin
     * This is the public interface that users of this class should use
     * @return
     */
    public Res<D> call(String builtin,Args<D> args){
        Builtin b = Builtin.getInstance(builtin);
        if (b == null){
            //throw error, or return error result or somethin'
            return null;
        }
        return b.visit(this, args);        
    }
    
    protected MatrixValueFactory<D> factory;
    /**
     * constructor takes in a MatrixValueFactory
     * TODO - do we need more args?
     */
    public ValuePropagator(MatrixValueFactory<D> factory){
        this.factory = factory;
    }
    
}


