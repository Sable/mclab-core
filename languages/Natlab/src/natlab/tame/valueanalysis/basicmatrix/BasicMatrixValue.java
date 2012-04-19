package natlab.tame.valueanalysis.basicmatrix;

import natlab.tame.classes.reference.*;                 //class    component
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.MatrixValue;
import natlab.tame.valueanalysis.components.constant.*; //constant component
import natlab.tame.valueanalysis.components.shape.*;    //shape    component
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.aggrvalue.*;

/**
 * represents a MatrixValue that is instantiable. It stores a constant,
 * on top of the matlab class
 */
public class BasicMatrixValue extends MatrixValue<BasicMatrixValue> implements HasConstant{
    static BasicMatrixValueFactory factory = new BasicMatrixValueFactory();
    Constant constant;
    Shape<AggrValue<BasicMatrixValue>> shape;
    //TODO -- also need complex
    
    @Deprecated
    public BasicMatrixValue(PrimitiveClassReference aClass, Shape<AggrValue<BasicMatrixValue>> shape) {
        super(aClass);
        this.shape = shape;
    }
    
    public BasicMatrixValue(PrimitiveClassReference aClass) {
        super(aClass);
    }
    /**
     * how to deal with a constant
     */
    public BasicMatrixValue(Constant constant){
        super(constant.getMatlabClass());
        shape = (new ShapeFactory<AggrValue<BasicMatrixValue>>(factory)).newShapeFromIntegers(constant.getShape());
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
    
    
    

    @Override
    public BasicMatrixValue merge(AggrValue<BasicMatrixValue> other) {
        if (!(other instanceof BasicMatrixValue)) throw new UnsupportedOperationException(
                "can only merge a Matrix Value with another Matrix Value");
        if (!other.getMatlabClass().equals(classRef)) throw new UnsupportedOperationException(
                "only Values with the same class can be merged, trying to merge :"+this+", "+other);
        if (constant == null) return this;
        if (constant.equals(((BasicMatrixValue)other).constant)) return this;
        return new BasicMatrixValue(this.classRef,this.shape.merge(((BasicMatrixValue)other).shape));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof BasicMatrixValue)) return false;
        BasicMatrixValue m = (BasicMatrixValue)obj;
        if (isConstant()) return constant.equals(m.constant);
        return classRef.equals(m.classRef) && shape.equals(((BasicMatrixValue)obj).shape);
    }
    
    @Override
    public String toString() {
        return "("+classRef+(isConstant()?(","+constant):"")+shape.toString()+")";//XU added shape.toString
    }
    
    
        

    public static final BasicMatrixValueFactory FACTORY = new BasicMatrixValueFactory();


    @Override
    public ValueSet<AggrValue<BasicMatrixValue>> arraySubsref(Args<AggrValue<BasicMatrixValue>> indizes) {
    	throw new UnsupportedOperationException(); //TODO
    }
    
    @Override
    public ValueSet<AggrValue<BasicMatrixValue>> dotSubsref(String field) {
        throw new UnsupportedOperationException("cannot dot-access a matrix");
        //return ValueSet.newInstance(factory.newErrorValue("cannot dot-access a matrix"));
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
    public AggrValue<BasicMatrixValue> arraySubsasgn(
            Args<AggrValue<BasicMatrixValue>> indizes,AggrValue<BasicMatrixValue> value) {
    	throw new UnsupportedOperationException(); //TODO
    }
    
    @Override
    public AggrValue<BasicMatrixValue> toFunctionArgument(boolean recursive) {
    	throw new UnsupportedOperationException(); //TODO
    }
    @Override
    public AggrValue<BasicMatrixValue> dotSubsasgn(String field,
            AggrValue<BasicMatrixValue> value) {
    	throw new UnsupportedOperationException(); //TODO
    }
}


