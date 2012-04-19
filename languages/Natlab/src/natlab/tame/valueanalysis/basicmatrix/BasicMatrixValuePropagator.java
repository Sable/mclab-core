package natlab.tame.valueanalysis.basicmatrix;

import java.util.*;
import natlab.tame.builtin.*;
import natlab.tame.builtin.classprop.*;
import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.*;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.constant.ConstantPropagator;
import natlab.tame.valueanalysis.components.mclass.ClassPropagator;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.value.*;

public class BasicMatrixValuePropagator extends MatrixPropagator<BasicMatrixValue>{
    public static boolean DEBUG = false;
    ConstantPropagator<AggrValue<BasicMatrixValue>> constantProp = ConstantPropagator.getInstance();
    
    public BasicMatrixValuePropagator() {
        super(new BasicMatrixValueFactory());
    }
    
    /**
     * base case
     */
    @Override
    public Res<AggrValue<BasicMatrixValue>> caseBuiltin(Builtin builtin,
            Args<AggrValue<BasicMatrixValue>> arg) {
        //deal with constants
        if (arg.isAllConstant()){ //FIXME
            Constant cResult = builtin.visit(constantProp, arg);
            if (cResult != null){
                return Res.<AggrValue<BasicMatrixValue>>newInstance(new BasicMatrixValue(cResult));
            }
        }
                
        //if it's not constant check whether the Builtin implements class propagation
        if (builtin instanceof HasClassPropagationInfo){
            if (DEBUG) System.out.println("case builtin: "+builtin
                    +" prop: "+((HasClassPropagationInfo)builtin).getMatlabClassPropagationInfo()
                    +" args: "+arg);
            List<HashSet<ClassReference>> matchResult =                                          //XU changed here
            		builtin.visit(ClassPropagator.<AggrValue<BasicMatrixValue>>getInstance(),arg);
            if (matchResult == null){
                return Res.newErrorResult(builtin.getName()+" is not defined for arguments "+arg);
            }
            return matchResultToRes(matchResult);
        }
        //else throw an error
        throw new UnsupportedOperationException("No class propagation defined for builtin "+builtin);
        
        //deal with shape
        
        //deal with complex
    }
    
    private Res<AggrValue<BasicMatrixValue>> matchResultToRes(List<HashSet<ClassReference>> matchResult){
        //go through and fill in result
        Res<AggrValue<BasicMatrixValue>> result = Res.newInstance();
        for (HashSet<ClassReference> values: matchResult){
            HashMap<ClassReference,AggrValue<BasicMatrixValue>> map = new HashMap<ClassReference,AggrValue<BasicMatrixValue>>();
            if (values.size() == 0){
                throw new UnsupportedOperationException("encountered match result with 0 values");
            }
            for (ClassReference classRef : values){
                map.put(classRef,new BasicMatrixValue((PrimitiveClassReference)classRef));
            }
            result.add(ValueSet.newInstance(map));
        }
        return result;
    }
}


