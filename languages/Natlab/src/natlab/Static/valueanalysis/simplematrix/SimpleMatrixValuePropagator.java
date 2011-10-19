package natlab.Static.valueanalysis.simplematrix;

import java.util.*;
import natlab.Static.builtin.*;
import natlab.Static.classes.reference.*;
import natlab.Static.valueanalysis.*;
import natlab.Static.valueanalysis.value.*;

public class SimpleMatrixValuePropagator extends ValuePropagator<SimpleMatrixValue>{
    public SimpleMatrixValuePropagator() {
        super(SimpleMatrixValue.FACTORY);
    }

    /**
     * base case
     */
    @Override
    public Res<SimpleMatrixValue> caseBuiltin(Builtin builtin,
            Args<SimpleMatrixValue> arg) {
        //TODO - deal with constants
        
        //if it's not constant check whether the Builtin implements class propagation
        if (builtin instanceof ClassPropagationDefined){
            LinkedList<HashSet<ClassReference>> matchResult = 
                ClassPropTools.matchByValues(((ClassPropagationDefined)builtin).getMatlabClassPropagationInfo(),arg);
            if (matchResult == null){
                throw new UnsupportedOperationException("error"); //TODO give better error
            }
            return matchResultToRes(matchResult);
        }
        //else throw an error
        throw new UnsupportedOperationException("No class propagation defined for builtin "+builtin);
    }
    
    private Res<SimpleMatrixValue> matchResultToRes(LinkedList<HashSet<ClassReference>> matchResult){
        //go through and fill in result
        Res<SimpleMatrixValue> result = Res.newInstance();
        for (HashSet<ClassReference> values: matchResult){
            HashMap<ClassReference,SimpleMatrixValue> map = new HashMap<ClassReference,SimpleMatrixValue>();
            for (ClassReference classRef : values){
                map.put(classRef,new SimpleMatrixValue((PrimitiveClassReference)classRef));
            }
            result.add(ValueSet.newInstance(map));
        }
        return result;
    }

    
}
