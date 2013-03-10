package natlab.tame.valueanalysis.components.shape;

import java.util.*;

import natlab.tame.builtin.*;
import natlab.tame.builtin.shapeprop.ShapePropTool;
import natlab.tame.builtin.shapeprop.HasShapePropagationInfo;
import natlab.tame.valueanalysis.value.*;

/**
 * this is a singleton class -- make it singleton, ignore all the generic stuff.
 */
public class ShapePropagator<V extends Value<V>> 
	extends BuiltinVisitor<Args<V>, List<Shape<V>>> {
	
    static boolean Debug = false;
    @SuppressWarnings("rawtypes")
	static ShapePropagator instance = null;
    /**
     * return singleton instance of shape propagator
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	static public <V extends Value<V>> ShapePropagator<V> getInstance() {
        if (instance == null) instance = new ShapePropagator();
        return instance;
    }
    private ShapePropagator() {} //hidden private constructor

    @Override
	public List<Shape<V>> caseBuiltin(Builtin builtin, Args<V> arg) {
		if (Debug) System.out.println("inside ShapePropgator, builtin fn is "+builtin);
		if (Debug) System.out.println("the number of output variables is "+arg.getNargout());
		if(builtin instanceof HasShapePropagationInfo){
			//call shape prop tool
			ShapePropTool<V> shapePropTool = new ShapePropTool<V>();
		    @SuppressWarnings({ "unchecked" })
			List<Shape<V>> result = shapePropTool.matchByValues(((HasShapePropagationInfo<V>)builtin).getShapePropagationInfo(),arg);
			return result;
		}
		throw new UnsupportedOperationException();
	}
    
    /**
     * the shape propagation for loop variable.
     */
    public Shape<V> forRange(V lower, V upper, V inc) {
		List<Integer> scalarShape = new ArrayList<Integer>(2);
		scalarShape.add(1);
		scalarShape.add(1);
		return new ShapeFactory<V>().newShapeFromIntegers(scalarShape);
    }
    
    //FIXME rewrite the shape analysis for array get and array set statements!
    public Shape<V> arraySubsref(Shape<V> arrayShape, Args<V> indizes) {
    	return null;
    }

    public Shape<V> arraySubsasgn(Shape<V> arrayShape, Args<V> indizes, V value) {
    	return null;
    }
}


