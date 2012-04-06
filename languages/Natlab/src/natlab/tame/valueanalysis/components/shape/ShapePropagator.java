package natlab.tame.valueanalysis.components.shape;

import java.util.List;

import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.BuiltinVisitor;
import natlab.tame.valueanalysis.value.*;


public class ShapePropagator<V extends Value<V>> 
	extends BuiltinVisitor<Args<V>, List<Shape<V>>>{
	//this is a singleton class -- make it singleton, ignore all the generic stuff
    @SuppressWarnings("rawtypes")
	static ShapePropagator instance = null;
    private ShapePropagator(){} //hidden private constructor
    

    /**
     * return singleton instance of shape propagator
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	static public <V extends Value<V>> ShapePropagator<V> getInstance(){
        if (instance == null) instance = new ShapePropagator();
        return instance;
    }

    
	@Override
	public List<Shape<V>> caseBuiltin(Builtin builtin, Args<V> arg) {
		// TODO
		throw new UnsupportedOperationException();
	}

}


