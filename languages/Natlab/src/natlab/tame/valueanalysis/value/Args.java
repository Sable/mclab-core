package natlab.tame.valueanalysis.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.aggrvalue.MatrixValue;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.constant.HasConstant;

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
public class Args<V extends Value<V>> extends ArrayList<V>{
    private static final long serialVersionUID = 1L;
    private int nargout = 1;
    
    public Args(Collection<V> list){
        super(list);
    }
    
    private Args(int nargout,Collection<V> list){
        super(list);
        this.nargout = nargout;
    }
    
    public static <V extends Value<V>> Args<V> newInstance(Collection<V> list){
        return new Args<V>(list);
    }
    
    public static <V extends Value<V>> Args<V> newInstance(int nargout,Collection<V> list){
        return new Args<V>(nargout,list);
    }
    
    public static <V extends Value<V>> Args<V> newInstance(V arg){
        LinkedList<V> values = new LinkedList<V>();
        values.add(arg);
        return new Args<V>(values);
    }
    
    public static <V extends Value<V>> Args<V> newInstance(V arg1,V arg2){
        LinkedList<V> values = new LinkedList<V>();
        values.add(arg1);
        values.add(arg2);
        return new Args<V>(values);
    }
    
    public static <V extends Value<V>> Args<V> newInstance(V... args){
        return new Args<V>(Arrays.asList((V[])args));
    }
    
    /**
     * returns true if all values are primitive
     */
    boolean isAllPrimitive(){
        for (V v : this){
            if (!(v instanceof MatrixValue)){
                return false;
            }
        }
        return true;
    }
    
    /**
     * returns as a list of all primitive values, if the values are all primitive
     */
    @SuppressWarnings("rawtypes")
	public List<MatrixValue<?>> asAllPrimitive(){
        ArrayList<MatrixValue<?>> list = new ArrayList<MatrixValue<?>>(size());
        for (V v : this){
            list.add((MatrixValue)v);
        }
        return list;
    }
    
    /**
     * returns true if all values are constant
     */
    public boolean isAllConstant(){
        for (V v : this){
            if (!(v instanceof HasConstant) || (null == ((HasConstant)v).getConstant())) 
            		return false;
        }
        return true;        
    }
    
    /**
     * returns a list of constants, if all values are constant
     * returns null if any value is not a constant
     */
    public List<Constant> getConstants(){
    	List<Constant> list = new ArrayList<Constant>(this.size());
        for (V v : this){
            if (!(v instanceof HasConstant) || (null == ((HasConstant)v).getConstant())){
            	return null;
            } else {
            	list.add(((HasConstant)v).getConstant());
            }
        }
        return list;
    }
    
    
    /**
     * returns the list of the class of each value in this list
     */
    public List<ClassReference> getClassList(){
        List<ClassReference> list = new ArrayList<ClassReference>(this.size());
        for (V v : this){
            list.add(v.getMatlabClass());
        }
        return list;
    }      
    
    @Override
    public boolean equals(Object arg0) {
    	if (arg0 instanceof Args){
    		if (((Args)arg0).nargout != nargout) return false; 
    	}
        return super.equals(arg0);
    }
    
    public int getNargout() {
    	return nargout;
    }
}


