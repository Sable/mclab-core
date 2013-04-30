package natlab.tame.valueanalysis.aggrvalue;

import java.util.HashMap;

import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.*;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfo;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.value.*;

/**
 * implements a value for structs.
 * Structs are assumed to be scalar.
 * 
 * Structs are a map from 
 * name -> Value
 * 
 * TODO - implement struct-arrays at some point.
 * 
 * @author ant6n
 */
public class StructValue<D extends MatrixValue<D>> extends CompositeValue<D>{
    HashMap<String,ValueSet<AggrValue<D>>> structMap = 
    		new HashMap<String,ValueSet<AggrValue<D>>>();
    
    public StructValue(AggrValueFactory<D> factory) {
        super(factory);
    }
    
    
    @Override
    public AggrValue<D> arraySubsasgn(Args<AggrValue<D>> indizes, AggrValue<D> value) {
        return this; //TODO - error?
    }

    @Override
    public StructValue<D> dotSubsasgn(String field, AggrValue<D> value) {
        StructValue<D> result = new StructValue<D>(factory);
        result.structMap = new HashMap<String,ValueSet<AggrValue<D>>>(structMap);
        result.structMap.put(field, ValueSet.newInstance(value));
        return result;
    }
    
    @Override
    public ValueSet<AggrValue<D>> arraySubsref(Args<AggrValue<D>> indizes) {
        return ValueSet.<AggrValue<D>>newInstance(this); //TODO - error?
    }
    
    @Override
    public ValueSet<AggrValue<D>> dotSubsref(String field) {
        if (structMap.containsKey(field)){
            return structMap.get(field);
        } else {
            throw new UnsupportedOperationException(
                    "attempted to access struct with non-existing field "+field);
        }
    }
    
    @Override
    public Res<AggrValue<D>> cellSubsref(Args<AggrValue<D>> indizes) {
        throw new UnsupportedOperationException();
    }
    @Override
    public AggrValue<D> cellSubsasgn(Args<AggrValue<D>> indizes, Args<AggrValue<D>> values) {
        throw new UnsupportedOperationException();
    }
    

    @Override
    public ClassReference getMatlabClass() {
        return BuiltinCompoundClassReference.STRUCT;
    }

    @Override
    public Shape<AggrValue<D>> getShape() {
        return factory.getShapeFactory().getScalarShape();
    }


    @Override
    public StructValue<D> toFunctionArgument(boolean recursive) {
        StructValue<D> result = new StructValue<D>(factory);
        for (String id : structMap.keySet()){
            result.structMap.put(id,
                    structMap.get(id).toFunctionArgument(recursive));
        }
        return result;
    }
    
    @Override
    public StructValue<D> merge(AggrValue<D> o) {
        if (!o.getMatlabClass().equals(this.getMatlabClass()))
            throw new UnsupportedOperationException("attempting to merge struct value with not a struct value");
        StructValue<D> aStruct = (StructValue<D>)o;
        if (!this.structMap.keySet().equals(aStruct.structMap.keySet())){
            throw new UnsupportedOperationException(
                    "merging structs with different keys not implemented:\n"+this.structMap+"\n"+aStruct.structMap);
        }
        StructValue<D> result = new StructValue<D>(factory);
        for (String id : structMap.keySet()){
            result.structMap.put(id,structMap.get(id).merge(aStruct.structMap.get(id)));
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "struct"+new ValueFlowMap<AggrValue<D>>(structMap);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StructValue<?>) {
            return this.structMap.equals(((StructValue<?>)obj).structMap);           
        } else {
            return false;
        }
    }


    @Override
	public isComplexInfo<AggrValue<D>> getisComplexInfo() {
		return factory.getIsComplexInfoFactory().newisComplexInfoFromStr("REAL");
	}
}

