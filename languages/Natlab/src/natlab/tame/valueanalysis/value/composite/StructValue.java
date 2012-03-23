package natlab.tame.valueanalysis.value.composite;

import java.util.HashMap;

import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.*;
import natlab.tame.valueanalysis.constant.*;
import natlab.tame.valueanalysis.value.*;

/**
 * implements a value for structs.
 * Structs are assumed to be scalar.
 * 
 * Structs are a map from 
 * name -> Value
 * 
 * @author ant6n
 */

public class StructValue<D extends MatrixValue<D>> extends CompositeValue<D>{
    HashMap<String,ValueSet<D>> structMap = new HashMap<String,ValueSet<D>>();
    
    public StructValue(ValueFactory<D> factory) {
        super(factory);
    }
    
    
    @Override
    public Value<D> arraySubsasgn(Args<D> indizes, Value<D> value) {
        return this; //TODO - error?
    }

    @Override
    public StructValue<D> dotSubsasgn(String field, Value<D> value) {
        StructValue<D> result = new StructValue<D>(factory);
        result.structMap = new HashMap<String,ValueSet<D>>(structMap);
        result.structMap.put(field, ValueSet.newInstance(value));
        return result;
    }
    
    @Override
    public ValueSet<D> arraySubsref(Args<D> indizes) {
        return ValueSet.newInstance(this); //TODO - error?
    }
    
    @Override
    public ValueSet<D> dotSubsref(String field) {
        if (structMap.containsKey(field)){
            return structMap.get(field);
        } else {
            throw new UnsupportedOperationException(
                    "attempted to access struct with non-existing field "+field);
        }
    }
    
    @Override
    public Res<D> cellSubsref(Args<D> indizes) {
        throw new UnsupportedOperationException();
    }
    @Override
    public Value<D> cellSubsasgn(Args<D> indizes, Args<D> values) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Constant getConstant() {
        return null;
    }

    @Override
    public ClassReference getMatlabClass() {
        return BuiltinCompoundClassReference.STRUCT;
    }

    @Override
    public Shape<D> getShape() {
        return factory.newScalarShape();
    }

    @Override
    public boolean hasShape() {
        return true;
    }

    @Override
    public boolean isConstant() {
        return false;
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
    public StructValue<D> merge(Value<D> o) {
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
        return "struct"+new ValueFlowMap<D>(structMap);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StructValue<?>) {
            return this.structMap.equals(((StructValue)obj).structMap);           
        } else {
            return false;
        }
    }
}

