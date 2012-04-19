package natlab.tame.valueanalysis.components.shape;

import java.util.*;

import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.value.*;

/**
 * allows construction of shapes
 */
public class ShapeFactory<V extends Value<V>> {
	ValueFactory<V> factory;
	
	public ShapeFactory(ValueFactory<V> factory){
		this.factory = factory;
	}
	
	
    /**
     * returns a shape with the given dimensions.
     * The given constants should be scalar.
     */
    public Shape<V> newShapeFromConstants(List<Constant> dims){
    	ArrayList<V> list = new ArrayList<V>(dims.size());
    	for (Constant dim : dims){
    		list.add(factory.newMatrixValue(dim));
    	}
    	return new Shape<V>(factory, list);
        //throw new UnsupportedOperationException();//return new Shape<D>(factory); XU modified
    }

    
    /**
     * returns a shape using the given values as dimensions
     */
    public Shape<V> newShapeFromValues(List<V> dims){
    	return new Shape<V>(factory,dims);
    }
    
    
    /**
     * return a shape using the given integer list as dimensions
     */
    public Shape<V> newShapeFromIntegers(List<Integer> dims){
    	ArrayList<V> list = new ArrayList<V>(dims.size());
    	for (int dim : dims){
    		list.add(factory.newMatrixValue(dim));
    	}
    	return new Shape<V>(factory,list);
    }
    
    
    /**
     * returns a 0x0 shape
     */
    @SuppressWarnings("unchecked")
	public Shape<V> getEmptyShape(){
        return new Shape<V>(factory,Arrays.asList(
        		factory.newMatrixValue(0),
        		factory.newMatrixValue(0)        		
        		));
    }
    
    
    /**
     * returns 1x1 shape 
     */
    @SuppressWarnings("unchecked")
	public Shape<V> getScalarShape(){
        return new Shape<V>(factory,Arrays.asList(
        		factory.newMatrixValue(1),
        		factory.newMatrixValue(1)
        		));
    }
    
	
}
