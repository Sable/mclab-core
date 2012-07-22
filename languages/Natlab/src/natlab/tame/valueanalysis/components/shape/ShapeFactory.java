package natlab.tame.valueanalysis.components.shape;

import java.util.*;

import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.value.*;

/**
 * allows construction of shapes
 */
public class ShapeFactory<V extends Value<V>> {
	ValueFactory<V> factory;
	
	public ShapeFactory(){
	}
	
	public ShapeFactory(ValueFactory<V> factory){
		this.factory = factory;
	}
	
	
    /**
     * returns a shape with the given dimensions.
     * The given constants should be scalar.
     * 
     */
    public Shape<V> newShapeFromConstants(List<Constant> dims){
    	ArrayList<V> list = new ArrayList<V>(dims.size());
    	for (Constant dim : dims){
    		list.add(factory.newMatrixValue(dim));
    	}
    	return null;
    	//return new Shape<V>(factory, list);
        //throw new UnsupportedOperationException();//return new Shape<D>(factory); XU modified
    }

    
    /**
     * returns a shape using the given values as dimensions
     */
    public Shape<V> newShapeFromValues(List<V> dims){
    	return null;
    	//return new Shape<V>(factory,dims);
    }
    
    /**
     * return a shape using the given input string, like 3x3...
     */
    public Shape<V> newShapeFromInputString(String s){
    	String[] array = s.split("[\\*]");
    	ArrayList<Integer> list = new ArrayList<Integer>();
    	for(String a : array){
    		if(a.matches("[\\?]")){
    			list.add(null);
    		}
    		else{
        		Integer i = Integer.parseInt(a);
        		list.add(i);
    		}
    	}
    	return new Shape<V>(factory,list);
    }
    
    /**
     * return a shape using the given integer list as dimensions
     */
    public Shape<V> newShapeFromIntegers(List<Integer> dims){
    	/*ArrayList<V> list = new ArrayList<V>(dims.size());
    	for (V dim : dims){
    		list.add(factory.newMatrixValue(dim)); //this line lead to infinite loop!!!
    	}*/
    	return new Shape<V>(factory,dims);
    }
    
    /**
     * returns a 0x0 shape
     */
    @SuppressWarnings("unchecked")
	public Shape<V> getEmptyShape(){
    	return null;
/*        return new Shape<V>(factory,Arrays.asList(
        		factory.newMatrixValue(0),
        		factory.newMatrixValue(0)        		
        		));*/
    }
    
    
    /**
     * returns 1x1 shape 
     */
    @SuppressWarnings("unchecked")
	public Shape<V> getScalarShape(){
    	return null;
/*        return new Shape<V>(factory,Arrays.asList(
        		factory.newMatrixValue(1),
        		factory.newMatrixValue(1)
        		));*/
    }
    
    public static void main(String[] args){
    	ShapeFactory sf = new ShapeFactory();
    	Shape<AggrValue<BasicMatrixValue>> testShape = sf.newShapeFromInputString("3*?*2");
    	System.out.println(testShape);
    }
	
}
