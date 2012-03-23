package natlab.tame.valueanalysis.value;

import java.util.Arrays;

import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.constant.*;
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

public abstract class ValueFactory<D extends MatrixValue<D>> {
    /**
     * constructs a new Primitive Value from a constant
     * @param constant
     */
    abstract public D newMatrixValue(Constant constant);
    
    
    /**
     * returns a ValuePropagator
     */
    abstract public ValuePropagator<D> getValuePropagator();
    
    
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
     * creates a function handle value
     */
    public FunctionHandleValue<D> newFunctionHandleValue(FunctionReference f){
        return new FunctionHandleValue<D>(this, f);
    }
    
    /**
     * creates a function handle value, but already supplies some arguments (partial application)
     */
    public FunctionHandleValue<D> newFunctionHandlevalue(FunctionReference f,java.util.List<ValueSet<D>> partialValues){
        return new FunctionHandleValue<D>(this,f,partialValues);
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
    
    /**
     * creates a scalar shape
     */
    public Shape<D> newScalarShape(){
        return Shape.scalar(this);
    }
    
    /**
     * creates a 0x0 shape
     */
    public Shape<D> newEmptyShape(){
        return Shape.empty(this);
    }
    
    /**
     * creates a shape from the given collection of constants
     */
    public Shape<D> newShape(Constant... constants){
        return Shape.newInstance(this,Arrays.asList(constants));
    }
    
    /**
     * returns a shape which would be the result when accessing an array with the given
     * indizes (i.e., the shape [max(a1),max(a2),max(a3),...,max(an)])
     */
    public Shape<D> newShapeFromIndizes(Args<D> indizes){
        return Shape.fromIndizes(this, indizes);
    }
    
    public ColonValue<D> newColonValue(){
        return new ColonValue<D>();
    }
    
    /**
     * creates an empty struct
     */
    public StructValue<D> newStruct(){
        return new StructValue<D>(this);
    }
    
    /**
     * creates an empty cell array
     */
    public CellValue<D> newCell(){
        return new CellValue<D>(this);
    }
}
