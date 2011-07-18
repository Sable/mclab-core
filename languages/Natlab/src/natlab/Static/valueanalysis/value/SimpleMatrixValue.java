package natlab.Static.valueanalysis.value;

import java.util.List;

import natlab.Static.classes.reference.*;
import natlab.Static.valueanalysis.constant.Constant;
import natlab.toolkits.analysis.Mergable;

/**
 * represents a MatrixValue that is instantiable. It stores a constant,
 * on top of the matlab class
 */
public class SimpleMatrixValue extends MatrixValue<SimpleMatrixValue> {
    Constant constant;
    
    
    public SimpleMatrixValue(PrimitiveClassReference aClass) {
        super(aClass);
    }
    public SimpleMatrixValue(Constant constant){
        super(constant.getMatlabClass());
        this.constant = constant;
    }
    
    
    /**
     * returns true if the represented data is a constant
     */
    public boolean isConstant(){
        return constant != null;
    }
    
    /**
     * returns the constant represented by this data, or null if it is not constant
     */
    public Constant getConstant(){
        return constant;
    }
    
    /**
     * returns the MatlabClass for this object
     * @author ant6n
     */
    public PrimitiveClassReference getMatlabClass(){
        return classRef;
    }
    
    

    @Override
    public SimpleMatrixValue merge(Value<SimpleMatrixValue> other) {
        if (!(other instanceof SimpleMatrixValue)) throw new UnsupportedOperationException(
                "can only merge a Matrix Value with another Matrix Value");
        if (!other.getMatlabClass().equals(classRef)) throw new UnsupportedOperationException(
                "only Values with the same class can be merged, trying to merge :"+this+", "+other);
        if (constant == null) return this;
        if (constant.equals(((SimpleMatrixValue)other).constant)) return this;
        return new SimpleMatrixValue(this.classRef);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof SimpleMatrixValue)) return false;
        SimpleMatrixValue m = (SimpleMatrixValue)obj;
        if (isConstant()) return constant.equals(m.constant);
        return classRef.equals(m.classRef);
    }
    
    @Override
    public String toString() {
        return "("+classRef+(isConstant()?(","+constant):"")+")";
    }
    
    
    @Override
    public SimpleMatrixValue forRange(SimpleMatrixValue upper,
            SimpleMatrixValue inc) {
        //TODO - do something better here, maybe
        //call COLON
        return new SimpleMatrixValue(this.classRef);
    }
    
    @Override
    public SimpleMatrixValue subsref(List<Value<SimpleMatrixValue>> indizes) {
        return new SimpleMatrixValue(this.getMatlabClass());
        //TODO
    }
    
    
    
    //*** factory *************************************************************
    public static class SimpleMatrixValueFactory extends MatrixValueFactory<SimpleMatrixValue>{
        @Override
        public SimpleMatrixValue newMatrixValue(Constant constant) {
            return new SimpleMatrixValue(constant);
        }
    }
    public static final SimpleMatrixValueFactory FACTORY = new SimpleMatrixValueFactory();
}


