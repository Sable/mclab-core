package natlab.tame.valueanalysis.components.shape;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.constant.DoubleConstant;
import natlab.tame.valueanalysis.components.constant.HasConstant;
import natlab.tame.valueanalysis.value.Value;

/**
 * allows construction of shapes
 */
public class ShapeFactory<V extends Value<V>> {
	
	public ShapeFactory() {}
    
    /**
     * returns a shape using the given DimValue list as dimensions.
     */
    public Shape<V> newShapeFromDimValues(List<DimValue> dims) {
    	return new Shape<V>(dims);
    }
    
	/**
     * returns a shape using the given values list as dimensions.
     */
    public Shape<V> newShapeFromValues(List<V> dims) {
    	List<DimValue> list = new ArrayList<DimValue>(dims.size());
    	for (V dim : dims) {
    		if (((HasConstant)dim).getConstant()!=null) {
    			DimValue dimValue = new DimValue(((DoubleConstant)((HasConstant)dim)
    					.getConstant()).getValue().intValue(), dim.getSymbolic());
    			list.add(dimValue);
    		}
    		else {
    			DimValue dimValue = new DimValue(null, dim.getSymbolic());
    			list.add(dimValue);
    		}
    	}
    	return new Shape<V>(list);
    }
	
    /**
     * returns a shape using the given constant list as dimensions.
     * 
     */
    public Shape<V> newShapeFromConstants(List<Constant> dims) {
    	List<DimValue> list = new ArrayList<DimValue>(dims.size());
    	for (Constant dim : dims) {
    		if (!(dim instanceof DoubleConstant)) throw new UnsupportedOperationException();
    		else {
    			DimValue dimValue = new DimValue(((DoubleConstant)dim).getValue().intValue(), null);
    			list.add(dimValue);
    		}
    	}
    	return new Shape<V>(list);
    }
    
    /**
     * returns a shape using the given integer list as dimensions.
     */
    public Shape<V> newShapeFromIntegers(List<Integer> dims) {
    	ArrayList<DimValue> list = new ArrayList<DimValue>(dims.size());
    	for (Integer dim : dims) {
    		DimValue dimValue = new DimValue(dim, null);
    		list.add(dimValue);
    	}
    	return new Shape<V>(list);
    }
    
    /**
     * returns a shape using the given input string, like 3x3, as dimensions.
     */
    public Shape<V> newShapeFromInputString(String s) {
    	String[] array = s.split("[\\*]");
    	ArrayList<DimValue> list = new ArrayList<DimValue>(array.length);
    	for (String a : array) {
    		if (a.matches("[\\?]")) {
    			DimValue dimValue = new DimValue(null, null);
    			list.add(dimValue);
    		}
    		else {
        		Integer i = Integer.parseInt(a);
        		DimValue dimValue = new DimValue(i, null);
        		list.add(dimValue);
    		}
    	}
    	return new Shape<V>(list);
    }    
    
    /**
     * returns a 1x1 shape 
     */
    public Shape<V> getScalarShape() {
    	List<DimValue> list = new ArrayList<DimValue>(2);
    	DimValue dim1 = new DimValue(1, null);
    	DimValue dim2 = new DimValue(1, null);
    	list.add(dim1);
    	list.add(dim2);
    	return new Shape<V>(list);
    }
    
    /**
     * returns a out of bound shape
     */
    public Shape<V> getOutOfBoundShape() {
    	List<DimValue> list = new ArrayList<DimValue>();
    	Shape<V> outOfBoundShape = new Shape<V>(list);
    	outOfBoundShape.flagOutOfBound();
    	return outOfBoundShape;
    }
    
    /**
     * returns an mismatch shape
     */
    public Shape<V> getMismatchShape() {
    	List<DimValue> list = new ArrayList<DimValue>();
    	Shape<V> mismatchShape = new Shape<V>(list);
    	mismatchShape.flagMismatch();
    	return mismatchShape;
    }
    
    /**
     * returns an empty shape
     */
    public Shape<V> getEmptyShape() {
    	List<DimValue> list = new ArrayList<DimValue>();
    	return new Shape<V>(list);
    }
}
