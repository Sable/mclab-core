package natlab.tame.valueanalysis.basicmatrix;

import java.util.*;

import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.aggrvalue.MatrixValue;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

/**
 * represents a MatrixValue that is instantiable. It stores a constant,
 * on top of the matlab class
 */
public class BasicMatrixValue extends MatrixValue<BasicMatrixValue> implements HasConstant, HasShape<AggrValue<BasicMatrixValue>>{
    static boolean Debug = false;
	static BasicMatrixValueFactory factory = new BasicMatrixValueFactory();
    Constant constant;
    Shape<AggrValue<BasicMatrixValue>> shape;
    //TODO -- also need complex
    
    
    public BasicMatrixValue(PrimitiveClassReference aClass, Shape<AggrValue<BasicMatrixValue>> shape) {
        super(aClass);
        this.shape = shape;
    }
    
    public BasicMatrixValue(PrimitiveClassReference aClass) {
        super(aClass);
    }

    /**
     * return a BasicMatrixValue object by taking in a user typed input argument
     * add this method @25th,Jul,2012
     * @param onlyClassInfo
     * @param shapeInfo
     */
    public BasicMatrixValue(PrimitiveClassReference aClass, String shapeInfo){
    	super(aClass);
    	this.shape = (new ShapeFactory<AggrValue<BasicMatrixValue>>(factory).newShapeFromInputString(shapeInfo));
    }
    /*@Deprecated
    public BasicMatrixValue(BasicMatrixValue onlyClassInfo, Shape<AggrValue<BasicMatrixValue>> shape) {
    	super(onlyClassInfo.classRef);
    	this.shape = shape;
    	
    }*/
    
    /**
     * how to deal with a constant
     */
    public BasicMatrixValue(Constant constant){
        super(constant.getMatlabClass());
        if (Debug) System.out.println("inside basicmatrixvalue constant");
        if (Debug) System.out.println(constant.getShape());
        shape = (new ShapeFactory<AggrValue<BasicMatrixValue>>(factory)).newShapeFromIntegers(constant.getShape());//XU study this line!!! infinite loop!!!
        this.constant = constant;
    }
    
    /**
     * returns true if the represented data is a constant
     */
    public boolean isConstant(){
        return constant != null;
    }
    
    @Override
    /**
     * returns the constant represented by this data, or null if it is not constant
     */
    public Constant getConstant(){
        return constant;
    }
    
    public Shape<AggrValue<BasicMatrixValue>> getShape(){
    	return shape;
    }
    
    public void setConstantNull(){
    	this.constant = null;
    }
    
    @Override
    public BasicMatrixValue merge(AggrValue<BasicMatrixValue> other) {
        if (!(other instanceof BasicMatrixValue)) throw new UnsupportedOperationException(
                "can only merge a Matrix Value with another Matrix Value");
        if (!other.getMatlabClass().equals(classRef)) throw new UnsupportedOperationException(
                "only Values with the same class can be merged, trying to merge :"+this+", "+other);
        if (Debug) System.out.println("this constant is "+constant);
        if (constant == null){
        	if (Debug) System.out.println("this constant is null!");
        	if(((BasicMatrixValue)other).constant==null){
        		if (Debug) System.out.println("inside both constant null!");
        		if(shape==null){
        			return this;
        		}
        		if(shape.equals(((BasicMatrixValue)other).getShape())!=true){
        			return new BasicMatrixValue(this.classRef,this.shape.merge(((BasicMatrixValue)other).getShape()));
        		}
        	}
        	return this;	
        }
        if (constant.equals(((BasicMatrixValue)other).constant)){
        	if (Debug) System.out.println("this constant is equal to that one!");
        	return this;
        }
        BasicMatrixValue newMatrix = new BasicMatrixValue(this.classRef,this.shape.merge(((BasicMatrixValue)other).getShape()));
        if (Debug) System.out.println(newMatrix);
        return newMatrix;
       }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof BasicMatrixValue)) return false;
        BasicMatrixValue m = (BasicMatrixValue)obj;
        if (isConstant()) return constant.equals(m.constant);
        if (Debug) System.out.println(m.getMatlabClass());
        if (Debug) System.out.println(((HasShape<AggrValue<BasicMatrixValue>>)((BasicMatrixValue)obj)).getShape());
        if((shape==null)&&(((HasShape<AggrValue<BasicMatrixValue>>)((BasicMatrixValue)obj)).getShape()==null)){
        	return (classRef.equals(m.getMatlabClass()))&&true;
        }
        return (classRef.equals(m.getMatlabClass()) && shape.equals(((HasShape<AggrValue<BasicMatrixValue>>)((BasicMatrixValue)obj)).getShape()));
    }
    
    @Override
    public String toString() {
        return "("+classRef+(isConstant()?(","+constant):"")+","+shape+")";//XU added shape
    }
    
    public static final BasicMatrixValueFactory FACTORY = new BasicMatrixValueFactory();

    static ShapePropagator<AggrValue<BasicMatrixValue>> shapePropagator = ShapePropagator.getInstance();
    
    @Override
    public ValueSet<AggrValue<BasicMatrixValue>> arraySubsref(Args<AggrValue<BasicMatrixValue>> indizes){
    	
    	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
				new BasicMatrixValue(
						this.getMatlabClass(),shapePropagator.arraySubsref(this.getShape(), indizes)));
    }
    @Override
    public AggrValue<BasicMatrixValue> arraySubsasgn(
            Args<AggrValue<BasicMatrixValue>> indizes,AggrValue<BasicMatrixValue> value) {
    	return new BasicMatrixValue(
				this.getMatlabClass(),shapePropagator.arraySubsasgn(this.getShape(), indizes, value));
    }
    @Override
    public Res<AggrValue<BasicMatrixValue>> cellSubsref(Args<AggrValue<BasicMatrixValue>> indizes) {
        throw new UnsupportedOperationException(); //TODO
    }
    @Override
    public AggrValue<BasicMatrixValue> cellSubsasgn(Args<AggrValue<BasicMatrixValue>> indizes, Args<AggrValue<BasicMatrixValue>> values) {
        throw new UnsupportedOperationException(); //TODO
    }
    @Override
    public ValueSet<AggrValue<BasicMatrixValue>> dotSubsref(String field) {
        throw new UnsupportedOperationException("cannot dot-access a matrix!");
        //return ValueSet.newInstance(factory.newErrorValue("cannot dot-access a matrix"));
    }
    @Override
    public AggrValue<BasicMatrixValue> dotSubsasgn(String field,
            AggrValue<BasicMatrixValue> value) {
    	throw new UnsupportedOperationException(); //TODO
    }
    @Override
    public AggrValue<BasicMatrixValue> toFunctionArgument(boolean recursive) {
    	return this;
    	//throw new UnsupportedOperationException(); //TODO
    }
}


