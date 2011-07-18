package natlab.Static.valueanalysis.value;

import natlab.Static.valueanalysis.ValueSet;
import natlab.Static.valueanalysis.constant.*;

/**
 * Since PrimitiveValues are generic in the data they store, we need a 
 * factory to create Objects of that type (i.e. for constants). This 
 * class provides an interface for these factories, and a couple of extra
 * methods that are independent of the PrimitiveData.
 * 
 * This class also provides a Factory for non-extended PrimitiveData
 * (i.e. an implementation of PrimitiveValueFactory<PrimitiveData>)
 * 
 * @author ant6n
 *
 * @param <D>
 */

public abstract class MatrixValueFactory<D extends MatrixValue<D>> {
    /**
     * constructs a new Primitive Value from a constant
     * @param constant
     */
    abstract public D newMatrixValue(Constant constant);
    
    
    /**
     * Creates a primitive scalar constant value
     */
    public MatrixValue<D> newMatrixValue(double value){
        return newMatrixValue(Constant.get(value));
    }
    /**
     * Creates a primitive scalar constant value
     */
    public MatrixValue<D> newMatrixValue(boolean value){
        return newMatrixValue(Constant.get(value));
    }
    /**
     * Creates a primitive constant value
     */
    public MatrixValue<D> newMatrixValue(String value){
        return newMatrixValue(Constant.get(value));
    }


    /**
     * creates a value set with one constant primitive value
     */
    public ValueSet<D> newValueSet(Constant constant){
        return ValueSet.newInstance(newMatrixValue(constant));
    }
    /**
     * creates a value set with one constant primitive value
     */
    public ValueSet<D> newValueSet(double value){
        return ValueSet.newInstance((newMatrixValue(Constant.get(value))));
    }
    /**
     * creates a value set with one constant primitive value
     */
    public ValueSet<D> newValueSet(boolean value){
        return ValueSet.newInstance((newMatrixValue(Constant.get(value))));
    }
    /**
     * creates a value set with one constant primitive value
     */
    public ValueSet<D> newValueSet(String value){
        return ValueSet.newInstance((newMatrixValue(Constant.get(value))));
    }
    
    
}
