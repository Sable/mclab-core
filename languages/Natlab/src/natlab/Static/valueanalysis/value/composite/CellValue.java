package natlab.Static.valueanalysis.value.composite;

import java.util.*;

/**
 * we make the assumption that undefined values are undefined, rather than empty
 * double arrays; and that these are never accessed - that is, there should be
 * runtime checks to ensure cells are assigned.
 * TODO - do proper implementation when usesMap is true
 */

import natlab.Static.classes.reference.BuiltinCompoundClassReference;
import natlab.Static.classes.reference.ClassReference;
import natlab.Static.valueanalysis.ValueSet;
import natlab.Static.valueanalysis.constant.Constant;
import natlab.Static.valueanalysis.value.*;

public class CellValue<D extends MatrixValue<D>> extends CompositeValue<D> {
    private Shape<D> shape;
    private HashMap<Integer,ValueSet<D>> cellMap = new HashMap<Integer, ValueSet<D>>();
    private boolean usesMap = true; //uses the map, if false, just uses an overall ValueSet
    private ValueSet<D> values = null;
    
    /**
     * creates empty cell array
     */
    public CellValue(ValueFactory<D> factory){
        super(factory);
        shape = factory.newEmptyShape();
        
    }
    
        
    /**
     * creates cell array with given shape and internal values
     */
    public CellValue(ValueFactory<D> factory, Shape<D> shape, ValueSet<D> values){
        super(factory);
        usesMap = false;
        this.values = values;
        this.shape = shape;
    }
    
    /**
     * copy constructor
     */
    private CellValue(CellValue<D> other){
        super(other.factory);
        this.shape = other.shape;
        this.cellMap = other.cellMap;
        this.usesMap = other.usesMap;
        this.values = other.values;
    }
    

    @Override
    public Value<D> arraySubsasgn(Args<D> indizes, Value<D> value) {
        if (value instanceof CellValue<?>){
            CellValue<D> cell = (CellValue<D>)value;
            CellValue<D> result = new CellValue<D>(this.factory);
            result.shape = this.shape.grow(factory.newShapeFromIndizes(indizes));
            result.usesMap = false;
            result.values = cell.values.merge(toSingleValues().values);
            return result;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public ValueSet<D> arraySubsref(Args<D> indizes) {
        throw new UnsupportedOperationException("array indexing of cell values not supported");
    }

    @Override
    public Value<D> dotSubsasgn(String field, Value<D> value) {
        throw new UnsupportedOperationException("dot indexing of cell values not supported");
    }

    @Override
    public ValueSet<D> dotSubsref(String field) {
        throw new UnsupportedOperationException("dot indexing of cell values not supported");
    }
    
    @Override
    public Res<D> cellSubsref(Args<D> indizes) {
        if (indizes.isAllConstant() && usesMap){
            throw new UnsupportedOperationException();
        } else {
            //FIXME use either nargout or indizes to determine how many values to return - just return one for now
            //TODO for now assume there's one valid return
            return Res.newInstance(this.values);
        }
    }
    @Override
    public CellValue<D> cellSubsasgn(Args<D> indizes, Args<D> values) {
        if (indizes.isAllConstant() && shape.isConstant()){
            throw new UnsupportedOperationException();
        } else {
            CellValue<D> result = new CellValue<D>(this.factory);
            result.shape = this.shape.grow(factory.newShapeFromIndizes(indizes));
            result.usesMap = false;
            result.values = ValueSet.newInstance(values).merge(toSingleValues().values);
            return result;
        }
    }
    

    @Override
    public Constant getConstant() {
        return null;
    }

    @Override
    public ClassReference getMatlabClass() {
        return BuiltinCompoundClassReference.CELL;
    }

    @Override
    public Shape<D> getShape() {
        return shape;
    }

    @Override
    public boolean hasShape() {
        return true;
    }

    @Override
    public boolean isConstant() {
        return false; //TODO - maybe we need a cell const?
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
    public Value<D> merge(Value<D> o) {
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
        ValueSet<D> values = ValueSet.newInstance();
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


