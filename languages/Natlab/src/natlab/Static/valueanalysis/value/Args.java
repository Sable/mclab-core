package natlab.Static.valueanalysis.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import natlab.Static.classes.reference.ClassReference;

/**
 * A way to combine multiple argument abstract values together to make an
 * argument Set, i.e. the information that flows into a call.
 * 
 * Also provides extra information into the function, like
 * - nargin=number of arguments
 * - nargout default=1
 * 
 * TODO
 * - globals?
 * TODO
 * - generate an overall (abstract?) parent class containing arg-info
 * 
 * @author ant6n
 */
public class Args<D extends MatrixValue<D>> extends ArrayList<Value<D>>{
    private static final long serialVersionUID = 1L;
    
    private Args(Collection<Value<D>> list){
        super(list);
    }
    public static <D extends MatrixValue<D>> Args<D> newInstance(Collection<Value<D>> list){
        return new Args<D>(list);
    }
    
    /**
     * returns true if all values are primitive
     */
    boolean isAllPrimitive(){
        for (Value<?> v : this){
            if (!(v instanceof MatrixValue<?>)){
                return false;
            }
        }
        return true;
    }
    
    /**
     * returns as a list of all primitive values, if the values are all primitive
     */
    public List<MatrixValue<D>> asAllPrimitive(){
        ArrayList<MatrixValue<D>> list = new ArrayList<MatrixValue<D>>(size());
        for (Value<D> v : this){
            list.add((MatrixValue<D>)v);
        }
        return list;
    }
    
    
    /**
     * returns the list of the class of each value in this list
     */
    public List<ClassReference> getClassList(){
        List<ClassReference> list = new ArrayList<ClassReference>(this.size());
        for (Value<?> v : this){
            list.add(v.getMatlabClass());
        }
        return list;
    }
    
    
}


