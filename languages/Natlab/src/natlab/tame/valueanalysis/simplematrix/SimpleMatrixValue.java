package natlab.tame.valueanalysis.simplematrix;

import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.aggrvalue.MatrixValue;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.constant.HasConstant;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Res;

/**
 * represents a MatrixValue that is instantiable. It stores a constant,
 * on top of the matlab class
 * 
 * @author ant6n
 * 
 * extended by XU @ 8:33pm March 9th 2013, since we modified the constructor in Value class, 
 * we should also modify the constructors in every subclass. This modification won't affect 
 * any previous work, because we only add a new filed in MatrixValue class.
 */
public class SimpleMatrixValue extends MatrixValue<SimpleMatrixValue> implements HasConstant{
    static SimpleMatrixValueFactory factory = new SimpleMatrixValueFactory();
    Constant constant;
    
    // TODO(xu): Decide whether this is necessary / makes sense. This constructor was added to make code build
    // after a merge.
    public SimpleMatrixValue(PrimitiveClassReference aClass) {
        this(null, aClass);
    }
    
    public SimpleMatrixValue(String symbolic, PrimitiveClassReference aClass) {
        super(symbolic, aClass);
    }
    public SimpleMatrixValue(String symbolic, Constant constant){
        super(symbolic, constant.getMatlabClass());
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
    public SimpleMatrixValue merge(AggrValue<SimpleMatrixValue> other) {
        if (!(other instanceof SimpleMatrixValue)) throw new UnsupportedOperationException(
                "can only merge a Matrix Value with another Matrix Value");
        if (!other.getMatlabClass().equals(classRef)) throw new UnsupportedOperationException(
                "only Values with the same class can be merged, trying to merge :"+this+", "+other);
        if (constant == null) return this;
        if (constant.equals(((SimpleMatrixValue)other).constant)) return this;
        return new SimpleMatrixValue(this.symbolic, this.classRef);
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
    
    
        

    public static final SimpleMatrixValueFactory FACTORY = new SimpleMatrixValueFactory();

    
    @Override
    public ValueSet<AggrValue<SimpleMatrixValue>> arraySubsref(Args<AggrValue<SimpleMatrixValue>> indizes) {
        return ValueSet.<AggrValue<SimpleMatrixValue>>newInstance(new SimpleMatrixValue(null, this.getMatlabClass()));
    }    
    @Override
    public AggrValue<SimpleMatrixValue> arraySubsasgn(
            Args<AggrValue<SimpleMatrixValue>> indizes,AggrValue<SimpleMatrixValue> value) {
        //TODO - check whether conversion is allowed
        return new SimpleMatrixValue(null, this.getMatlabClass());
    }    
    @Override
    public Res<AggrValue<SimpleMatrixValue>> cellSubsref(Args<AggrValue<SimpleMatrixValue>> indizes) {
        throw new UnsupportedOperationException();
    }
    @Override
    public AggrValue<SimpleMatrixValue> cellSubsasgn(Args<AggrValue<SimpleMatrixValue>> indizes, Args<AggrValue<SimpleMatrixValue>> values) {
        throw new UnsupportedOperationException();
    }    
    @Override
    public ValueSet<AggrValue<SimpleMatrixValue>> dotSubsref(String field) {
        throw new UnsupportedOperationException("cannot dot-access a matrix");
        //return ValueSet.newInstance(factory.newErrorValue("cannot dot-access a matrix"));
    }
    @Override
    public AggrValue<SimpleMatrixValue> dotSubsasgn(String field,
            AggrValue<SimpleMatrixValue> value) {
        throw new UnsupportedOperationException("cannot dot-assign a matrix");
    }    
    @Override
    public AggrValue<SimpleMatrixValue> toFunctionArgument(boolean recursive) {
        return isConstant()?new SimpleMatrixValue(null, this.classRef):this;
    }
}


