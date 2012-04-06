package natlab.tame.valueanalysis.basicmatrix;

import java.util.*;
import natlab.tame.builtin.*;
import natlab.tame.builtin.classprop.*;
import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.*;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.constant.ConstantPropagator;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.value.*;

public class BasicMatrixValuePropagator extends AggrValuePropagator<BasicMatrixValue>{
    public static boolean DEBUG = false;
    ConstantPropagator<AggrValue<BasicMatrixValue>> constantProp = ConstantPropagator.getInstance();
    
    public BasicMatrixValuePropagator() {
        super(new BasicMatrixValueFactory());
    }

    @Override
    public Res<AggrValue<BasicMatrixValue>> caseBuiltin(Builtin builtin,
            Args<AggrValue<BasicMatrixValue>> arg) {
    	//try propagating constants
    	
    	//propagate classes
    	
    	//propagate shapes
    	
    	//propagate complex info
    	
    	throw new UnsupportedOperationException(); //TODO
    }
    
}



