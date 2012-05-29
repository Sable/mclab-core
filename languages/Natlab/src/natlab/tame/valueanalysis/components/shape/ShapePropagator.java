package natlab.tame.valueanalysis.components.shape;

import java.util.List;

import natlab.tame.builtin.*;
import natlab.tame.builtin.shapeprop.ShapePropTool;
import natlab.tame.builtin.shapeprop.HasShapePropagationInfo;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.value.*;


public class ShapePropagator<V extends Value<V>> 
	extends BuiltinVisitor<Args<V>, List<Shape<AggrValue<BasicMatrixValue>>>>{//XU modified at 4.28.12.55pm, very important!!!
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
	public List<Shape<AggrValue<BasicMatrixValue>>> caseBuiltin(Builtin builtin, Args<V> arg) {
		// TODO
		if (Debug) System.out.println("inside ShapePropgator, builtin fn is "+builtin);
		if(builtin instanceof HasShapePropagationInfo){
			//call shape prop tool
			return ShapePropTool.matchByValues(((HasShapePropagationInfo)builtin).getShapePropagationInfo(),arg);
		}
		throw new UnsupportedOperationException();
	}

}


