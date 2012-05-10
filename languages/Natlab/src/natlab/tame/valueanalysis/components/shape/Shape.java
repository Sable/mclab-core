package natlab.tame.valueanalysis.components.shape;

import java.util.*;

import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.constant.HasConstant;
import natlab.tame.valueanalysis.value.*;
import natlab.toolkits.analysis.Mergable;

/**
 * represents a shape. it is represented using an array of values.
 * TODO - what to do about top?                 XU: two options...just don't know nothing.
 */


public class Shape<V extends Value<V>> implements Mergable<Shape<V>>{
	private ValueFactory<V> factory;
	List<Integer> dimensions;                       //change V to Integer
	boolean isTop = false;
	boolean isError = false;
    //FIXME -- actually put dimensions,         XU comment: List<V> dimensions?
	
	public Shape(ValueFactory<V> factory, List<Integer> dimensions){ //change V to Integer
		this.factory = factory;
		this.dimensions = dimensions;
	}
	
    public List<Integer> getDimensions(){
    	return dimensions;
    }
    
    public int getSize(){                     //XU made
    	return dimensions.size();
    }
    
    public Integer getCertainDimensionSize(int i){  //change V to Integer
    	Integer value = dimensions.get(i);          //change V to Integer
    	if (value==null){
    		return null;
    	}
    	else
    		return value;
    }
    
    public String toString(){
    	if(this.isTop==true){
    		return "[is top]";
    	}
    	else if(this.isError==true){
    		return "[MATLAB syntax error, check your code]";
    	}
    	else{
    		return this.dimensions.toString();
    	}
    }
    
    public void printShapeInfo(){
    	System.out.println(dimensions);
    }
    
    
    /**
     * returns true if this shape is scalar or may be scalar
     * returns false if this shape is known to be non-scalar
     */
    public boolean maybeScalar(){
        return true;//TODO
    }
    
    /**
     * returns true if this shape is known to be scalar. XU comment: scalar is [1,1]
     * returns false if this shape may or may not be scalar.
     */
    public boolean isScalar(){
    	return false;//TODO
    }

    /**
     * returns the single linear index that refers to the same element as
     * the given elements.
     */
    public V getLinearIndex(Args<V> indizes){
        throw new UnsupportedOperationException(); //TODO
    }
    
    
    /**
     * returns the first Non-Singleton dimension.
     * This may be known (i.e. a constant), or not.
     */
    public V getFirstNonSingletonDimension(){
    	throw new UnsupportedOperationException(); //TODO
    }
    
    public void FlagItsTop(){
    	this.isTop=true;
    }
    
    public void FlagItsError(){
    	this.isError=true;
    }
    
    @Override
    public Shape<V> merge(Shape<V> o){
    	System.out.println("inside shape merge!");
    	if(this.equals(o)){
    		return this;
    	}
    	else{
    		if(this.getSize()!=o.getSize()){
    			System.out.println("return a top shape!");//currently, just from toString to show it's a top shape
    			Shape<V> topShape = new Shape<V>(this.factory, null);
    			topShape.FlagItsTop();
    			return topShape;
    		}
    		else{
    			int j=0;
    			ArrayList<Integer> newDimensions = new ArrayList<Integer>(this.getSize());
    			for(Integer i : this.dimensions){
        			System.out.println(this.dimensions.size());    				
    				if(i==o.getCertainDimensionSize(j)){
    					newDimensions.add(j, i);
    				}
    				else{
    					newDimensions.add(j, null);
    				}
    				j = j+1;
    			}
    			return new Shape<V>(this.factory, newDimensions);
    		}
    	}
    }
    

    public boolean equals(Shape<V> o){
    	if(this.getSize()==o.getSize()){
    		int j=0;
    		for(Integer i : this.dimensions){
    			System.out.println("testing weather or not shape equals!");
    			//System.out.println("i is "+i+", j is "+o.getCertainDimensionSize(j));
    			if(i==o.getCertainDimensionSize(j)){
    				j=j+1;
    			}
    			else{
    				System.out.println("inside shape equals false!");
    				return false;
    			}
    		}
    		return true;
    	}
    	return false;//FIXME
    }
    
    /**
     * returns a shape that is the result of growing this to the given shape.
     * This is different than a merge, for example
     * [2x3] merge [3x2] = [2 or 3 x 2 or 3]
     * whereas
     * [2x3] grow [3x2] = [3x3]
     */
    public Shape<V> grow(Shape<V> o){
        return new Shape<V>(factory, null); //TODO
    }
    
    /**
     * returns a shape that is the result of taking a matlab value with
     * a shape of this, and then index-assigning it with the given indizes.
     * i.e. if matlab variable A has the shape represented by this object,
     * and indizes i,j are represented by the arguments indizes, then
     * the Matlab assignment
     * A(i,j) = ...
     * will result in a shape for A that is the result of this method.
     */
    public Shape<V> growByIndices(Args<V> indizes){
    	return this; //FIXME -- do something here. For now, assume matrizes don't grow.
    }

    /**
     * returns true if the shape is known  XU modified
     * @return
     */
    public boolean isConstant() {
/*    	for (V valueOfDimension: dimensions){
    		if (!(valueOfDimension instanceof HasConstant) || (null == ((HasConstant)valueOfDimension).getConstant())) 
        		return false;
    	}*/
        return true; //TODO
    }    
}
