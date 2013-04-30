package natlab.tame.valueanalysis.aggrvalue;

import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfoFactory;
import natlab.tame.valueanalysis.components.shape.ShapeFactory;
import natlab.tame.valueanalysis.value.ValueFactory;
import natlab.toolkits.path.FunctionReference;


public abstract class AggrValueFactory<D extends MatrixValue<D>> extends ValueFactory<AggrValue<D>> {
	/**
	 * constructor builds shape factor
	 */
	ShapeFactory<AggrValue<D>> shapeFactory;
	isComplexInfoFactory<AggrValue<D>> isComplexFactory;
	public AggrValueFactory(){
		this.shapeFactory = new ShapeFactory<AggrValue<D>>(this);
		this.isComplexFactory = new isComplexInfoFactory<AggrValue<D>>(this); //added by Vineet
	}
	
	
    /**
     * constructs a new Primitive Value from a constant
     * @param constant
     */
    abstract public D newMatrixValue(Constant constant);
    
    
    /**
     * returns a ValuePropagator
     * This should always be an AggrValuePropagator, containing a matrix value propagator
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
     * returns the shape factory
     */
    public ShapeFactory<AggrValue<D>> getShapeFactory(){
    	return shapeFactory;
    }
    
    public isComplexInfoFactory<AggrValue<D>> getIsComplexInfoFactory(){
    	return isComplexFactory;
    }
}
