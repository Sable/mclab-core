package natlab.tame.valueanalysis.aggrvalue;

import java.util.Arrays;
import natlab.tame.valueanalysis.*;
import natlab.tame.valueanalysis.constant.*;
import natlab.tame.valueanalysis.value.*;
import natlab.toolkits.path.FunctionReference;


public abstract class AggrValueFactory<D extends MatrixValue<D>> extends ValueFactory<AggrValue<D>> {
    /**
     * constructs a new Primitive Value from a constant
     * @param constant
     */
    abstract public D newMatrixValue(Constant constant);
    
    
    /**
     * returns a ValuePropagator
     */
    abstract public AggrValuePropagator<D> getValuePropagator();
    

    /**
     * creates a function handle value
     */
    public FunctionHandleValue<D> newFunctionHandleValue(FunctionReference f){
        return new FunctionHandleValue<D>(this, f);
    }
    
    /**
     * creates a function handle value, but already supplies some arguments (partial application)
     */
    public FunctionHandleValue<D> newFunctionHandlevalue(FunctionReference f,java.util.List<ValueSet<AggrValue<D>>> partialValues){
        return new FunctionHandleValue<D>(this,f,partialValues);
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
    public Shape<D> newShapeFromIndizes(Args<AggrValue<D>> indizes){
        return Shape.fromIndizes(this, indizes);
    }

}
