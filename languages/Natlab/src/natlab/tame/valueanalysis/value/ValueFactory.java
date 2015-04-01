package natlab.tame.valueanalysis.value;

import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.toolkits.path.FunctionReference;

/**
 * Since PrimitiveValues are generic in the data they store, we need a 
 * factory to create Objects of that type (i.e. for constants). This 
 * class provides an interface for these factories, and a couple of extra
 * methods that are independent of the PrimitiveData.
 * 
 * This class also provides a Factory for non-extended PrimitiveData
 * (i.e. an implementation of PrimitiveValueFactory<PrimitiveData>)
 * 
 * This class also has to provide a BuiltinVisitor that propagates values
 * through builtins.
 * 
 * @author ant6n
 *
 * TODO - should some of the methods be in different internal factories?
 * 
 * extended by XU to support symbolic info. @ 6:26pm March 9th 2013 
 */

public abstract class ValueFactory<V extends Value<V>> {
    /**
     * returns a ValuePropagator
     */
    abstract public ValuePropagator<V> getValuePropagator();
    
    
	/**
     * constructs a new Primitive Value from a constant, extended to support symbolic.
     * @param constant
     */
    abstract public V newMatrixValue(String symbolic, Constant constant);
        
    
    /**
     * Creates a primitive scalar constant value
     */
    public V newMatrixValue(String symbolic, double value){
        return newMatrixValue(symbolic, Constant.get(value));
    }
    /**
     * Creates a primitive scalar constant value
     */
    public V newMatrixValue(String symbolic, boolean value){
        return newMatrixValue(symbolic, Constant.get(value));
    }
    /**
     * Creates a primitive constant value
     */
    public V newMatrixValue(String symbolic, String value){
        return newMatrixValue(symbolic, Constant.get(value));
    }

    /**
     * creates a function handle value
     */
    abstract public V newFunctionHandleValue(FunctionReference f);
    
    /**
     * creates a function handle value, but already supplies some arguments (partial application)
     */
    abstract public V newFunctionHandlevalue(FunctionReference f,java.util.List<ValueSet<V>> partialValues);

    
    /**
     * creates a value set with one constant primitive value
     */
    public ValueSet<V> newValueSet(String symbolic, Constant constant){
        return ValueSet.newInstance(newMatrixValue(symbolic, constant));
    }
    /**
     * creates a value set with one constant primitive value
     */
    public ValueSet<V> newValueSet(String symbolic, double value){
        return ValueSet.newInstance((newMatrixValue(symbolic, Constant.get(value))));
    }
    /**
     * creates a value set with one constant primitive value
     */
    public ValueSet<V> newValueSet(String symbolic, boolean value){
        return ValueSet.newInstance((newMatrixValue(symbolic, Constant.get(value))));
    }
    /**
     * creates a value set with one constant primitive value
     */
    public ValueSet<V> newValueSet(String symbolic, String value){
        return ValueSet.newInstance((newMatrixValue(symbolic, Constant.get(value))));
    }
        
    public V newColonValue(){
        return (V)new ColonValue<V>();
    }

    
    /**
     * creates an empty struct
     */
    abstract public V newStruct();
    
    /**
     * creates an empty cell array
     */
    abstract public V newCell();

    
    /**
     * returns a value representing the i in 
     *   i = lower:inc:upper 
     * when used as the range expression in a for loop. The value for i has to be a valid
     * representation for all iterations.
     * 
     * inc is optional, and may be null.
     * lower, upper, inc should be values with matrix mclasses.
     */
    abstract public V forRange(V lower,	V upper, V inc);

    	
}



