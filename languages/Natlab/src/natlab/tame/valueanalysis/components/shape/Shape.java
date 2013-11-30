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
	boolean outOfBound = false; // for array bound check.
	boolean mismatch = false; // for array bound check.
	
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
    
    public void setDimensions(List<DimValue> newDimensions) {
    	dimensions = newDimensions;
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
     * returns true if this shape may be scalar;
     * returns false if this shape is known to be non-scalar.
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
    
    /**
     * returns treu if this shape is row vector.
     */
    public boolean isRowVector() {
    	if (dimensions.size() != 2) return false;
    	else if (dimensions.get(0).equalsOne() && !dimensions.get(1).equalsOne()) {
    		return true;
    	}
    	else return false;
    }
    
    /**
     * returns true if this shape is column vector.
     */
    public boolean isColVector() {
    	if (dimensions.size() != 2) return false;
    	else if (!dimensions.get(0).equalsOne() && dimensions.get(1).equalsOne()) {
    		return true;
    	}
    	else return false;
    }
    
    /**
     * returns true if this shape may be vector.
     */
    public boolean maybeVector() {
    	if (dimensions.size() != 2) return false;
    	else if (dimensions.get(0).equalsOne() && !dimensions.get(1).equalsOne()) {
    		return true;
    	}
    	else if (!dimensions.get(0).equalsOne() && dimensions.get(1).equalsOne()) {
    		return true;
    	}
    	else return false;
    }
    
    public void flagOutOfBound() {
    	outOfBound = true;
    }
    
    public void flagMismatch() {
    	mismatch = true;
    }
    
    @Override
    public Shape<V> merge(Shape<V> o) {
    	if (Debug) System.out.println("inside shape merge!");
    	if (this.equals(o)) return this;
    	else if (o == null) return null;
    	/*
    	 * if the size of dimensions of two shapes are different, first, 
    	 * we make them have the same size of dimensions by adding 
    	 * trailing 1s at the end of the dimension with smaller size.
    	 */
    	else if (dimensions.size() > o.getDimensions().size()) {
			List<DimValue> tempList = new ArrayList<DimValue>(dimensions.size());
			for (int i=0; i<dimensions.size(); i++) {
				if (i<o.getDimensions().size()) tempList.add(o.getDimensions().get(i));
				else tempList.add(new DimValue(1, null));
			}
			o.setDimensions(tempList);
		}
    	else if (dimensions.size() < o.getDimensions().size()) {
    		List<DimValue> tempList = new ArrayList<DimValue>(o.getDimensions().size());
    		for (int i=0; i<o.getDimensions().size(); i++) {
    			if (i<dimensions.size()) tempList.add(dimensions.get(i));
    			else tempList.add(new DimValue(1, null));
    		}
    		this.setDimensions(tempList);
    	}
    	/*
    	 * after we make the shapes have the same size of dimensions, 
    	 * we start to merge them.
    	 */
		List<DimValue> list = new ArrayList<DimValue>(dimensions.size());
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
	    	if (dimensions.size() != o.getDimensions().size()) return false;
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
    	if (outOfBound) return "[index out of bound]";
    	else if (mismatch) return "[mismatched shape]";
    	else if (!isConstant()) {
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
     * In MATLAB, shape(a)=[3,3,1] -> shape(a)=[3,3], but 
     * shape(a)=[1,1,3] -> shape(a)=[1,1,3]. So it only collapses 
     * or eliminates the extra trailing 1s, not leading 1s. But 
     * you can assign shape(b)=[1,3] to shape(a)=[1,1,3], so in 
     * this case, we should eliminate the extra leading 1s, then 
     * compare them.
     */
    public Shape<V> eliminateTrailingOnes() {
    	/* 
    	 * since if a shape has trailing 1s which can be eliminated, 
    	 * its isTop, mismatch and outOfBound must be false. 
    	 */
    	if (dimensions.size() == 0) return this;
    	List<DimValue> newDim = new ArrayList<DimValue>();
    	int pos = 0; 
    	/*
    	 * used to know from which position there starts with trailing 1s, 
    	 * and since every variable in MATLAB is at least 2 dimensions. 
    	 * So pos is at least 2.
    	 */
    	for (int i = dimensions.size() - 1; i >= 0; i--) {
    		if (dimensions.get(i).hasIntValue() 
    				&& dimensions.get(i).getIntValue() != 1) {
    			pos = i + 1;
    			break;
    		}
    	}
    	if (pos < 2) pos = 2;
    	for (int i = 0; i < pos; i++) {
    		newDim.add(dimensions.get(i));
    	}
    	Shape<V> newShape = new Shape<V>(newDim);
    	return newShape;
    }
    
    public Shape<V> eliminateLeadingOnes() {
    	if (dimensions.size() == 0) return this;
    	List<DimValue> newDim = new ArrayList<DimValue>();
    	int pos = 0; 
    	/*
    	 * used to know from which position there starts with non-extra 
    	 * leading 1s, and since every variable in MATLAB is at least 2 
    	 * dimensions. So pos is at least oldDim.size()-2.
    	 */
    	for (int i = 0; i < dimensions.size(); i++) {
    		if (dimensions.get(i).hasIntValue() 
    				&& dimensions.get(i).getIntValue() != 1) {
    			pos = i + 1;
    			break;
    		}
    	}
    	if (dimensions.size() - pos < 2) 
    		pos = dimensions.size() - 2;
    	for (int i = pos; i < dimensions.size(); i++) {
    		newDim.add(dimensions.get(i));
    	}
    	Shape<V> newShape = new Shape<V>(newDim);
    	return newShape;
    }
    
    /**
     * returns the first Non-Singleton dimension.
     * This may be known (i.e. a constant), or not.
     */
    public DimValue getFirstNonSingletonDimension() {
    	throw new UnsupportedOperationException(); //TODO
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
