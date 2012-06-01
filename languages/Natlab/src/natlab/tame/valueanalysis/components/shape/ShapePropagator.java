package natlab.tame.valueanalysis.components.shape;

import java.util.*;

import natlab.tame.builtin.*;
import natlab.tame.builtin.shapeprop.ShapePropTool;
import natlab.tame.builtin.shapeprop.HasShapePropagationInfo;
import natlab.tame.valueanalysis.value.*;


public class ShapePropagator<V extends Value<V>> 
	extends BuiltinVisitor<Args<V>, List<Shape<V>>>{//XU modified at 4.28.12.55pm, very important!!!
	//this is a singleton class -- make it singleton, ignore all the generic stuff
    @SuppressWarnings("rawtypes")
	static ShapePropagator instance = null;
    static boolean Debug = false;
    /**
     * return singleton instance of shape propagator
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	static public <V extends Value<V>> ShapePropagator<V> getInstance(){
        if (instance == null) instance = new ShapePropagator();
        return instance;
    }
    private ShapePropagator(){} //hidden private constructor

    @Override
    //XU add this to support...
	public List<Shape<V>> caseBuiltin(Builtin builtin, Args<V> arg, int num) {
		// TODO
		if (Debug) System.out.println("inside ShapePropgator, builtin fn is "+builtin);
		if (Debug) System.out.println("the number of output variables is "+num);
		if(builtin instanceof HasShapePropagationInfo){
			//call shape prop tool
			List<Shape<?>> result = ShapePropTool.matchByValues(((HasShapePropagationInfo)builtin).getShapePropagationInfo(),arg,num);
			List<Shape<V>> vResult = new ArrayList<Shape<V>>();
			for(Shape<?> res: result){
				vResult.add((Shape<V>)res);
			}
			return vResult;
		}
		throw new UnsupportedOperationException();
	}
    
}


