package natlab.tame.valueanalysis.aggrvalue;

import java.util.*;

/**
 * we make the assumption that undefined values are undefined, rather than empty
 * double arrays; and that these are never accessed - that is, there should be
 * runtime checks to ensure cells are assigned.
 * TODO - do proper implementation when usesMap is true
 */

import natlab.tame.classes.reference.BuiltinCompoundClassReference;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.isComplex.*;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.value.*;

public class CellValue<D extends MatrixValue<D>> extends CompositeValue<D> {
    private Shape<AggrValue<D>> shape;
    private isComplexInfo<AggrValue<D>> isComplex;
    private HashMap<Integer,ValueSet<AggrValue<D>>> cellMap = new HashMap<Integer, ValueSet<AggrValue<D>>>();
    private boolean usesMap = true; //uses the map, if false, just uses an overall ValueSet
    private ValueSet<AggrValue<D>> values = null;
    
    /**
     * creates empty cell array
     */
    public CellValue(AggrValueFactory<D> factory){
        super(factory);
        shape = factory.getShapeFactory().getEmptyShape();
        isComplex = factory.getIsComplexInfoFactory().getNullinfo();
        
    }
    
        
    /**
     * creates cell array with given shape and internal values
     */
    public CellValue(AggrValueFactory<D> factory, Shape<AggrValue<D>> shape, ValueSet<AggrValue<D>> values){
        super(factory);
        usesMap = false;
        this.values = values;
        this.shape = shape;
    }
    
    
    public CellValue(AggrValueFactory<D> factory, Shape<AggrValue<D>> shape, isComplexInfo<AggrValue<D>> isComplex, ValueSet<AggrValue<D>> values){
        super(factory);
        usesMap = false;
        this.values = values;
        this.shape = shape;
        this.isComplex = isComplex;
    }
    
    /**
     * copy constructor
     */
    private CellValue(CellValue<D> other){
        super(other.factory);
        this.shape = other.shape;
        this.isComplex = other.isComplex;
        this.cellMap = other.cellMap;
        this.usesMap = other.usesMap;
        this.values = other.values;
    }
    

    @Override
    public AggrValue<D> arraySubsasgn(Args<AggrValue<D>> indizes, AggrValue<D> value) {
        if (value instanceof CellValue<?>){
            CellValue<D> cell = (CellValue<D>)value;
            CellValue<D> result = new CellValue<D>(this.factory);
            result.shape = this.shape.growByIndices(indizes);
            result.isComplex = this.isComplex;
            result.usesMap = false;
            result.values = cell.values.merge(toSingleValues().values);
            return result;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public ValueSet<AggrValue<D>> arraySubsref(Args<AggrValue<D>> indizes) {
        throw new UnsupportedOperationException("array indexing of cell values not supported");
    }

    @Override
    public AggrValue<D> dotSubsasgn(String field, AggrValue<D> value) {
        throw new UnsupportedOperationException("dot indexing of cell values not supported");
    }

    @Override
    public ValueSet<AggrValue<D>> dotSubsref(String field) {
        throw new UnsupportedOperationException("dot indexing of cell values not supported");
    }
    
    @Override
    public Res<AggrValue<D>> cellSubsref(Args<AggrValue<D>> indizes) {
        if (indizes.isAllConstant() && usesMap){
            throw new UnsupportedOperationException();
        } else {
            //FIXME use either nargout or indizes to determine how many values to return - just return one for now
            //TODO for now assume there's one valid return
            return Res.newInstance(this.values);
        }
    }
    @Override
    public CellValue<D> cellSubsasgn(Args<AggrValue<D>> indizes, Args<AggrValue<D>> values) {
        if (indizes.isAllConstant() && shape.isConstant()){
            throw new UnsupportedOperationException();
        } else {
            CellValue<D> result = new CellValue<D>(this.factory);
            result.shape = this.shape.growByIndices(indizes);
            result.usesMap = false;
            result.isComplex = this.isComplex;
            result.values = ValueSet.newInstance(values).merge(toSingleValues().values);
            return result;
        }
    }
    

    @Override
    public ClassReference getMatlabClass() {
        return BuiltinCompoundClassReference.CELL;
    }

    @Override
    public Shape<AggrValue<D>> getShape() {
        return shape;
    }

    @Override
    public isComplexInfo<AggrValue<D>> getisComplexInfo() {
        return isComplex;
    }
    
    public ValueSet<AggrValue<D>> getValues(){
    	return values;
    }
    
    @Override
    public CellValue<D> toFunctionArgument(boolean recursive) {
        CellValue<D> result = new CellValue<D>(this);
        if (usesMap){
            for (Integer i : cellMap.keySet()){
                result.cellMap.put(i,result.cellMap.get(i).toFunctionArgument(recursive));
            }
        } else {
            result.values = result.values.toFunctionArgument(recursive);          
        }
        if (recursive && result.usesMap){
            //todo merge valeus
        }
        return result;
    }

    @Override
    public AggrValue<D> merge(AggrValue<D> o) {
        if (!o.getMatlabClass().equals(this.getMatlabClass()))
            throw new UnsupportedOperationException("attempting to merge cell value with not a cell value");
        CellValue<D> other = (CellValue<D>)o;
        if (this.usesMap && other.usesMap && this.shape.equals(other.shape)){
            CellValue<D> result = new CellValue<D>(factory);
            result.shape = this.shape.merge(other.shape);
            Set<Integer> allKeys = new HashSet<Integer>(this.cellMap.keySet());
            allKeys.addAll(other.cellMap.keySet());
            for (int i : allKeys){
                if (this.cellMap.containsKey(i)&&other.cellMap.containsKey(i)){
                    result.cellMap.put(i,this.cellMap.get(i).merge(other.cellMap.get(i)));   
                } else if (this.cellMap.containsKey(i)){
                    result.cellMap.put(i,this.cellMap.get(i));
                } else {
                    result.cellMap.put(i,other.cellMap.get(i));                    
                }
            }
            return result;
        } else {
            return new CellValue<D>(factory,
                    this.shape.merge(other.shape),this.values.merge(other.values));
        }
    }
    
    /**
     * returns a cell value where all values are merged into one
     * returns this if this cell value arleady satisfies that condition.
     */
    public CellValue<D> toSingleValues(){
        if (!usesMap) return this;
        CellValue<D> result = new CellValue<D>(factory);
        result.usesMap = false;
        ValueSet<AggrValue<D>> values = ValueSet.newInstance();
        for (int i : cellMap.keySet()){
            values = values.merge(result.cellMap.get(i));
        }
        result.values = values;
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CellValue){
            CellValue<D> aCell = (CellValue<D>)obj;
            return usesMap == aCell.usesMap && shape.equals(aCell.shape) &&
                usesMap? (values.equals(aCell.values))
                        :(cellMap.equals(aCell.cellMap));
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        if (usesMap){
            throw new UnsupportedOperationException();
        } else {
            return "cell"+values+"";
        }
    }
}


