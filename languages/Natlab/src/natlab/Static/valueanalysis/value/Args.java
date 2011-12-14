package natlab.Static.valueanalysis.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
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
 * - execution environment
 *   - reference to call graph?
 *      - reference to class graph?
 * - execute enviornment ??
 *   - need a way to execute functions (i.e. when passing in function handles?!)
 *     - but what's the callsite?!
 * 
 * TODO
 * - generate an overall (abstract?) parent class containing arg-info?
 * - create a child class called Indizes?
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
    public static <D extends MatrixValue<D>> Args<D> newInstance(Value<D> arg){
        LinkedList<Value<D>> values = new LinkedList<Value<D>>();
        values.add(arg);
        return new Args<D>(values);
    }
    public static <D extends MatrixValue<D>> Args<D> newInstance(Value<D> arg1,Value<D> arg2){
        LinkedList<Value<D>> values = new LinkedList<Value<D>>();
        values.add(arg1);
        values.add(arg2);
        return new Args<D>(values);
    }
    public static <D extends MatrixValue<D>> Args<D> newInstance(Value<D>... args){
        return new Args<D>(Arrays.asList((Value<D>[])args));
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
     * returns true if all values are constant
     */
    public boolean isAllConstant(){
        for (Value<?> v : this){
            if (!v.isConstant()) return false;
        }
        return true;        
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
    
    
    @Override
    public boolean equals(Object arg0) {
        return super.equals(arg0);
    }
}


