package natlab.Static.valueanalysis.value;

import java.util.ArrayList;

import natlab.Static.valueanalysis.ValueSet;

/**
 * combining multiple Values into a list, as used by Results
 * 
 */

public class Res<D extends MatrixValue<D>> extends ArrayList<ValueSet<D>> {
    public static <D extends MatrixValue<D>> Res<D> newInstance(Value<D> aValue){
        Res<D> result = new Res<D>();
        result.add(ValueSet.newInstance(aValue));
        return result;
    }
    
    public static <D extends MatrixValue<D>> Res<D> newInstance(){
        return new Res<D>();
    }
    
}


