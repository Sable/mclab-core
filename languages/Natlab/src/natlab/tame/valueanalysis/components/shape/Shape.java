package natlab.tame.valueanalysis.components.shape;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;
import natlab.toolkits.analysis.Mergable;

/**
 * represents a shape. it is represented using an array of values.
 * TODO - what to do about top?
 * 
 * Originally, our idea is to use List<V> to present shape's dimensions, 
 * for starters, we use List<Integer> instead, but as the project goes on, 
 * List<Integer> may not enough, at least when we want to support symbolic 
 * representation. But if we use List<V> to present shape's dimensions, 
 * whenever we want to know the int value of certain dimension, we need 
 * to write like this: 
 * ((DoubleConstant)(((HasConstant)dim).getConstant)).getValue().intValue(), 
 * to get the int value of that dimension. Another consideration is that, 
 * actually, for each dimension, it can only be an known integer or an 
 * unknown integer represented by a symbolic. We don't need to use MatrixValue 
 * to present it, because this will waste some fields in the MatrixValue, 
 * for example, isComplexInfo in AdvancedMatrixValue. Moreover, there is 
 * also another problem, when we generate a BasicMatrixValue containing a 
 * Constant value, we also need to assign shape info to this value, which is 
 * [1,1], and that 1 in [1,1] is also a BasicMatrixValue containing a Constant 
 * value whose shape is again [1,1], this will end up in an infinite loop...
 * of course, there is always some solution to resolve this problem, but 
 * based on other reasons above, I don't want to use List of V extends 
 * Value<V> to represent shape's dimensions.
 * 
 * @author XU
 * 
 */
public class Shape<V extends Value<V>> implements Mergable<Shape<V>> {
	
	static boolean Debug = false;
	List<DimValue> dimensions;
	boolean isTop = false;
	
	public Shape(List<DimValue> dimensions) {
		/* 
		 * new a new list, keep modifications on old dimensions from 
		 * affecting new shape. 
		 */
		List<DimValue> newDim = new ArrayList<DimValue>(dimensions);
		this.dimensions = newDim;
	}
	
    public List<DimValue> getDimensions() {
    	return dimensions;
    }

    /**
     * returns true if each dimension of the shape has exact integer value.
     */
    public boolean isConstant() {
    	for (int i=0; i<dimensions.size(); i++) {
    		if (!dimensions.get(i).hasIntValue()) return false;
    	}
    	return true;
    }
    
    /**
     * return true if each remaining dimension of the shape has exact integer 
     * value.
     * @param besidesDim
     * @return
     */
    public boolean isConstant(int besidesDim) {
    	for (int i=besidesDim; i<dimensions.size(); i++) {
    		if (!dimensions.get(i).hasIntValue()) return false;
    	}
    	return true;
    }
    
    /**
     * returns true if this shape is scalar or may be scalar
     * returns false if this shape is known to be non-scalar
     */
    public boolean maybeScalar() {
    	if (dimensions.size()!=2) return false;
    	else if (dimensions.get(0).equalsOne()&&(!dimensions.get(1).equalsOne())) 
    		return true;
    	else if (dimensions.get(1).equalsOne()&&(!dimensions.get(0).equalsOne())) 
    		return true;
    	else return false;
    }
    
    /**
     * returns true if this shape is known to be scalar. XU comment: scalar is [1,1]
     * returns false if this shape may or may not be scalar.
     */
    public boolean isScalar() {
    	if (dimensions.size()!=2) return false;
    	else if (dimensions.get(0).equalsOne()&&dimensions.get(1).equalsOne()) 
    		return true;
    	else return false;
    }
    
    public void flagIsTop() {
    	this.isTop=true;
    }
    
    /**
     * merge null with anything --> null
     * merge shapes with different dimensions --> top
     * should we replace top to growing the shape?
     */
    @Override
    public Shape<V> merge(Shape<V> o) {
    	if (Debug) System.out.println("inside shape merge!");
    	if (this.equals(o)) return this;
    	else if (o==null) return null;
    	else if (dimensions.size()!=o.getDimensions().size()) {
			/*
			 * currently, push to top.
			 * TODO, I kind of remember we decided to grow the shape, do it later.
			 */
			if (Debug) System.out.println("return a top shape!");
			Shape<V> topShape = new Shape<V>(null);
			topShape.flagIsTop();
			return topShape;
		}
		ArrayList<DimValue> list = new ArrayList<DimValue>(dimensions.size());
		for (int i=0; i<dimensions.size(); i++) {
			/* 
			 * this equals method is not an override method, it calls 
			 * the equals method in DimValue.
			 */
			if (dimensions.get(i).equals(o.getDimensions().get(i))) {
				list.add(dimensions.get(i));
			}
			else {
				list.add(new DimValue());
			}
		}
		return new Shape<V>(list);
    }
    
    @Override
    /**
     * this method is very important, since it will be used in loop 
     * fix point check.
     */
    public boolean equals(Object obj) {
    	if (obj == null) return false;
		if (obj instanceof Shape) {
	    	if (Debug) System.out.println("inside check whether shape equals!");
	    	Shape<V> o = (Shape)obj;
	    	if (isTop==true || o.isTop==true) return true;
	    	if (dimensions.size()!=o.getDimensions().size()) return false;
	    	for (int i=0; i<dimensions.size(); i++) {
	    		if (!dimensions.get(i).equals(o.getDimensions().get(i))) {
	    			return false;
	    		}
	    	}
	    	return true;			
		}
		return false;
    }
    
    /**
     * return how many elements in the remaining dimensions of this array. 
     * besidesDim means starting from besidesDim+1 dimension.
     * @param besidesDim
     * @return
     */
    public int getHowManyElements(int besidesDim) {
    	if (!isConstant(besidesDim)) return -1;
    	else {
    		int sum = 1;
    		for (int i=besidesDim; i<dimensions.size(); i++) {
    			sum *= dimensions.get(i).getIntValue();
    		}
    		return sum;
    	}
    }
    
    /**
     * this toString is only for there is a shape, in another word, the dimensions 
     * of this shape is not null, maybe some dimension's is unknown. if the 
     * shape is null, i.e. the shape propagation failed, when we call toString of 
     * BasicMatrixValue, it will return "[shape propagation fails]" there, so don't 
     * worry about dimensions may be null in this toString. 
     */
    public String toString() {
    	if (this.isTop==true) return "[is top]";
    	else if (isConstant()==false) {
    		List<String> dimension = new ArrayList<String>();
    		for (int i=0; i<dimensions.size(); i++) {
    			if (dimensions.get(i)==null) dimension.add("?");
				/*
				 *  since we extend dimension value to symbolic, this may return 
				 *  either integer value or symbolic value of this dimension.
				 */
    			else dimension.add(dimensions.get(i).toString());
    		}
    		return dimension.toString();
    	}
    	else return dimensions.toString();
    }

    /**
     * returns the single linear index that refers to the same element as
     * the given elements.
     */
    public DimValue getLinearIndex(Args<V> indizes) {
        throw new UnsupportedOperationException(); //TODO
    }
    
    /**
     * returns the first Non-Singleton dimension.
     * This may be known (i.e. a constant), or not.
     */
    public DimValue getFirstNonSingletonDimension() {
    	throw new UnsupportedOperationException(); //TODO
    }
    
    /**
     * returns a shape that is the result of growing this to the given shape.
     * This is different than a merge, for example
     * [2x3] merge [3x2] = [2 or 3 x 2 or 3]
     * whereas
     * [2x3] grow [3x2] = [3x3]
     */
    public Shape<V> grow(Shape<V> o){
        return new Shape<V>(null); //TODO
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
}
