package natlab.tame.valueanalysis.value;

import java.util.Arrays;

import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.aggrvalue.CellValue;
import natlab.tame.valueanalysis.aggrvalue.FunctionHandleValue;
import natlab.tame.valueanalysis.aggrvalue.MatrixValue;
import natlab.tame.valueanalysis.aggrvalue.StructValue;
import natlab.tame.valueanalysis.constant.*;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.value.composite.*;
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
 */

public abstract class ValueFactory<V extends Value<V>> {
    /**
     * returns a ValuePropagator
     */
    abstract public ValuePropagator<V> getValuePropagator();
    
    
	/**
     * constructs a new Primitive Value from a constant
     * @param constant
     */
    abstract public V newMatrixValue(Constant constant);
        
    
    /**
     * Creates a primitive scalar constant value
     */
    public V newMatrixValue(double value){
        return newMatrixValue(Constant.get(value));
    }
    /**
     * Creates a primitive scalar constant value
     */
    public V newMatrixValue(boolean value){
        return newMatrixValue(Constant.get(value));
    }
    /**
     * Creates a primitive constant value
     */
    public V newMatrixValue(String value){
        return newMatrixValue(Constant.get(value));
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
    public ValueSet<V> newValueSet(Constant constant){
        return ValueSet.newInstance(newMatrixValue(constant));
    }
    /**
     * creates a value set with one constant primitive value
     */
    public ValueSet<V> newValueSet(double value){
        return ValueSet.newInstance((newMatrixValue(Constant.get(value))));
    }
    /**
     * creates a value set with one constant primitive value
     */
    public ValueSet<V> newValueSet(boolean value){
        return ValueSet.newInstance((newMatrixValue(Constant.get(value))));
    }
    /**
     * creates a value set with one constant primitive value
     */
    public ValueSet<V> newValueSet(String value){
        return ValueSet.newInstance((newMatrixValue(Constant.get(value))));
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
     * returns a value representing the 
     * lower:inc:upper used as the range expression in a for loop.
     * inc is optional, and may be null.
     * lower, upper, inc should be values with matrix mclasses.
     * TODO - move this somewhere else.
     */
    abstract public V forRange(V lower,	V upper, V inc);

    	
}



