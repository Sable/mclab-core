package natlab.tame.valueanalysis.components.shape;

import java.util.*;

import natlab.tame.builtin.*;
import natlab.tame.builtin.shapeprop.ShapePropTool;
import natlab.tame.builtin.shapeprop.HasShapePropagationInfo;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.rangeValue.*;

/**
 * this is a singleton class -- make it singleton, ignore all the generic stuff.
 */
public class ShapePropagator<V extends Value<V>> 
	extends BuiltinVisitor<Args<V>, List<Shape<V>>> {
	
    static boolean Debug = false;
    @SuppressWarnings("rawtypes")
	static ShapePropagator instance = null;
    /**
     * return singleton instance of shape propagator
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	static public <V extends Value<V>> ShapePropagator<V> getInstance() {
        if (instance == null) instance = new ShapePropagator();
        return instance;
    }
    private ShapePropagator() {} //hidden private constructor

    @Override
	public List<Shape<V>> caseBuiltin(Builtin builtin, Args<V> arg) {
		if (Debug) System.out.println("inside ShapePropgator, builtin fn is " 
	+ builtin);
		if (Debug) System.out.println("the number of output variables is " 
	+ arg.getNargout());
		if(builtin instanceof HasShapePropagationInfo){
			//call shape prop tool
			ShapePropTool<V> shapePropTool = new ShapePropTool<V>();
		    @SuppressWarnings({ "unchecked" })
			List<Shape<V>> result = shapePropTool.matchByValues(
					((HasShapePropagationInfo<V>)builtin).getShapePropagationInfo()
					, arg);
			return result;
		}
		throw new UnsupportedOperationException();
	}
    
    /**
     * the shape propagation for loop variable.
     */
    public Shape<V> forRange(V lower, V upper, V inc) {
		List<Integer> scalarShape = new ArrayList<Integer>(2);
		scalarShape.add(1);
		scalarShape.add(1);
		return new ShapeFactory<V>().newShapeFromIntegers(scalarShape);
    }
    
    /*
     * The indices of array in our IR can be divided into three categories:
     * 1. scalar;
     * 2. vector, which should have a shape and maybe a range value;
     * TODO currently, we only have range value for the vectors which are 
     * created by colon function, try to extend it to horzcat and vertcat.
     * 3. colon value, which is ":".
     * 
     * In MATLAB syntax, we don't need the same number of indices as the 
     * number of array's dimensions, i.e. shape(a)=[2,3,4], which is a three 
     * dimensions array, in MATLAB, we can use only one, two or three indices 
     * to index this array, i.e. a(5), which is a linear indexing, (2,:) or 
     * a(2,:,2).
     * 
     * Currently, we divide array bound check in two phases: statically(compile 
     * time) and dynamically(runtime). And we decided to integrate static array 
     * bound check with shape analysis.
     * 
     * example:
     * 	a = ones(2,2);
     * 	a(3,1) = 5; // this line will grow the original array.
     * 	b = a(3,3); // this line will throw error, since index out of bound.
     * 	a(1:2,3) = zeros(2,3); // subscripted assignment dimension mismatch.
     * 
     * So, in the following two statements handler, arraySubsref (for array-get) 
     * and arraySubsasgn (for array-get), besides doing regular shape analysis, 
     * we also need to do the array bound check.
     */
    
    /**
     * shape analysis for array get statement:
     * 
     * Currently, if the shape is exactly known, we will proceed static array 
     * bound check here, if not, we leave the array bound check to the runtime.
     * 
     * By the way, we use range value of scalar indices to do the check instead 
     * of using its constant value, because a scalar may not have constant value, 
     * but in most cases, it do have range value. And if a scalar doesn't have a 
     * range value, leave the array bound check to the runtime.
     * 
     * According to our discussion, we don't want to terminate the analysis if 
     * there is some "out of bound" problem, so the solution is that we mark 
     * the "out of bound" as a warning in the program where there may be index 
     * out of bound, and keep doing the value analysis.
     * 
     * TODO if shape(a)=[2,b,4], although b is not a constant, b may have a range 
     * value, and if the corresponding index is smaller than the lower bound of 
     * b, it will also be considered as safe, which is not out of bound.
     */
    public Shape<V> arraySubsref(Shape<V> rhsArrayShape, Args<V> indices) {
    	if (rhsArrayShape==null) return null;
    	List<DimValue> indexedDimensions = new ArrayList<DimValue>(indices.size());
    	List<DimValue> rhsArrayDimensions = rhsArrayShape.getDimensions();
    	if (indices.size() > rhsArrayDimensions.size()) {
    		System.err.println("indices exceed the array's dimensions, check you code.");
    		return new ShapeFactory<V>().getOutOfBoundShape();
    	}
    	for (int i=0; i<indices.size(); i++) {
    		/*
    		 * ColonValue extends from SpecialValue which extends from Value, 
    		 * but BasicMatrixValue extends from MatrixValue which extends from 
    		 * Value, so ColonValue is totally different from MatrixValue, and 
    		 * of course, we cannot convert ColonValue to HasShape. as a result, 
    		 * we should deal with ColonValue first.
    		 */
    		if (indices.get(i) instanceof ColonValue) {
    			// don't need to insert array bound check for ":" index.
    			if (indices.size()==i+1 && rhsArrayDimensions.size()>i+1) {
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
					int howManyElementsRemain = rhsArrayShape.getHowManyElements(i);
					/*
					 * if only one index, the return shape will be a row, [1,X], so initialize 
					 * the first dimension as 1, then add the second dimension.
					 */
    				if (indices.size()==1) indexedDimensions.add(new DimValue(1, null));
    				if (howManyElementsRemain==-1) indexedDimensions.add(new DimValue());
    				else indexedDimensions.add(new DimValue(howManyElementsRemain, null));
    			}
    			else indexedDimensions.add(rhsArrayDimensions.get(i));
    		}
    		else if (((HasShape<V>)indices.get(i)).getShape().isScalar()) {
    			if (indices.size()==i+1 && rhsArrayDimensions.size()>i+1) {
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
    				// need insert static array bound check.
    				if (rhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue()!=null 
    						&& !((HasRangeValue<V>)indices.get(i)).getRangeValue().hasTop()) {
        				int howManyElementsRemain = rhsArrayShape.getHowManyElements(i);
    					if (((HasRangeValue<V>)indices.get(i)).getRangeValue().getLowerBound()<=0 
    							|| ((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.getUpperBound()>howManyElementsRemain) {
    						// index out of bound.
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    				}
    				/*
					 * if only one index, the return shape will be a row, [1,X], so initialize 
					 * the first dimension as 1, then add the second dimension.
					 */
    				if (indices.size()==1) indexedDimensions.add(new DimValue(1, null));
   					indexedDimensions.add(new DimValue(1, null));
    			}
    			else {
    				// need insert static array bound check.
    				if (rhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue()!=null 
    						&& !((HasRangeValue<V>)indices.get(i)).getRangeValue().hasTop()) {
    					if (((HasRangeValue<V>)indices.get(i)).getRangeValue().getLowerBound()<=0 
    							|| ((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.getUpperBound()>rhsArrayDimensions.get(i).getIntValue()) {
    						// index out of bound.
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    				}
    				indexedDimensions.add(new DimValue(1, null));
    			}
    		}
    		else if (!((HasShape<V>)indices.get(i)).getShape().isScalar()) {
    			if (indices.size()==i+1 && rhsArrayDimensions.size()>i+1) {
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
    				// need insert static array bound check.
    				if (rhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue()!=null 
    						&& !((HasRangeValue<V>)indices.get(i)).getRangeValue().hasTop()) {
        				int howManyElementsRemain = rhsArrayShape.getHowManyElements(i);
    					if (((HasRangeValue<V>)indices.get(i)).getRangeValue().getLowerBound()<=0 
    							|| ((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.getUpperBound()>howManyElementsRemain) {
    						// index out of bound.
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    				}
    				/*
					 * if only one index, the return shape will be a row, [1,X], so initialize 
					 * the first dimension as 1, then add the second dimension.
					 */
    				if (indices.size()==1) indexedDimensions.add(new DimValue(1, null));
    				indexedDimensions.add(((HasShape<V>)indices.get(i))
    						.getShape().getDimensions().get(1));
    			}
    			else {
    				// need insert static array bound check.
    				if (rhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue()!=null 
    						&& !((HasRangeValue<V>)indices.get(i)).getRangeValue().hasTop()) {
    					if (((HasRangeValue<V>)indices.get(i)).getRangeValue().getLowerBound()<=0 
    							|| ((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.getUpperBound()>rhsArrayShape.getDimensions().get(i).getIntValue()) {
    						// index out of bound.
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    				}
    				indexedDimensions.add(((HasShape<V>)indices.get(i))
    						.getShape().getDimensions().get(1));
    			}
    		}
    	}
    	Shape<V> resultShape = new ShapeFactory<V>().newShapeFromDimValues(indexedDimensions);
    	if (resultShape.isConstant()) return resultShape.eliminateTrailingOnes();
    	return resultShape;
    }

    /**
     * shape analysis for array set statement:
     * 
     * The array bound check is inseparable for shape analysis of array set, 
     * since the indices may be out of array bound and hence grow the original 
     * array, which will lead to a different shape result.
     * 
     * Currently, if the shape is exactly known, we will proceed static array 
     * bound check here, if not, we leave the array bound check to the runtime. 
     * And when we cannot proceed static array bound check during the shape 
     * analysis of array set, we have to assume the indices are not out of array 
     * bound, and return the original array's shape.
     * 
     * 1st, whether the indices may be out of array bound? If the upper bound 
     * of the index is still smaller than the size of certain dimension, then 
     * this will be definitely not out of array bound. And if the upper bound 
     * of the index is bigger than the size of certain dimension, even the 
     * lower bound may be smaller than the size of that dimension, we should 
     * still grow the array to the upper bound of the index, this is safe. But 
     * this is not always precise, since maybe on some branch, the index is 
     * smaller than the corresponding dimension's size at runtime, in this 
     * situation, we don't need to grow the array. But anyway, our analysis is 
     * static analysis...
     * 
     * TODO advanced version of this array bound check: when we do bound check, 
     * we can use the range value analysis result, i.e. if shape(a)=[2,b,5] 
     * and range(b)=<3,5>, then a(2,4,4)=rhs will not grow array a, because 
     * range(b)=<3,5>, by the way, do we need to say range(b)=<4,5> after this?
     * wait a minute, this is totally unsafe, since in the runtime, if on one 
     * branch shape(a)=[2,3,5], a(2,4,4)=rhs will be an error! So if the sizes 
     * of some certain dimensions are symbolic and maybe have range values, this 
     * cannot be done by static array bound check, should insert runtime array 
     * bound check in the generated code. So, it is safe only when all the upper 
     * bound of indices are smaller than the lower bound of all the dimensions 
     * and of course bigger than 0. And it is definitely an index out of bound 
     * error when any index' lower bound is bigger than the upper bound of the 
     * corresponding dimension.
     * 
     * TODO what is the maximum size which an array can be or can be declared?
     * 
     * DONE: Since we cannot assume the input MATLAB code is 100% syntax correct 
     * and safe, we need to consider the situation that if the rhs value's shape 
     * doesn't conform with the lhs indexed array, i.e., we assign a shape of 
     * 1-by-2 to an indexed array whose indexed result is with the shape of 1-by-3. 
     * In MATLAB, this is an error. We should also throw something, error or 
     * warnings.
     */
    public Shape<V> arraySubsasgn(Shape<V> lhsArrayShape, Args<V> indices, V value) {
    	if (lhsArrayShape==null) return null;
    	List<DimValue> indexedDimensions = new ArrayList<DimValue>();
    	List<DimValue> lhsArrayDimensions = lhsArrayShape.getDimensions();
    	List<DimValue> newDimensions = lhsArrayDimensions; // if array need to be grew.
    	// indices.size() can be bigger than lhsArrayDimensions.size(), grow it.
    	for (int i=0; i<indices.size(); i++) {
    		/*
    		 * ColonValue extends from SpecialValue which extends from Value, 
    		 * but BasicMatrixValue extends from MatrixValue which extends from 
    		 * Value, so ColonValue is totally different from MatrixValue, and 
    		 * of course, we cannot convert ColonValue to HasShape. as a result, 
    		 * we should deal with ColonValue first.
    		 */
    		if (indices.get(i) instanceof ColonValue) {
    			// insert array growth check.
    			if (i+1 > lhsArrayDimensions.size()) {
    				System.err.println("cannot grow the array with ':'.");
    				return new ShapeFactory<V>().getOutOfBoundShape();
    			}
    			// don't need to insert array bound check for ":" index.
    			else if (indices.size()==i+1 && lhsArrayDimensions.size()>i+1) {
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
					int howManyElementsRemain = lhsArrayShape.getHowManyElements(i);
					/*
					 * if only one index, the return shape will be a row, [1,X], so initialize 
					 * the first dimension as 1, then add the second dimension.
					 */
    				if (indices.size()==1) indexedDimensions.add(new DimValue(1, null));
    				if (howManyElementsRemain==-1) indexedDimensions.add(new DimValue());
    				else indexedDimensions.add(new DimValue(howManyElementsRemain, null));
    			}
    			else indexedDimensions.add(lhsArrayDimensions.get(i));
    		}
    		else if (((HasShape<V>)indices.get(i)).getShape().isScalar()) {
    			// insert array growth check.
    			if (i+1 > lhsArrayDimensions.size()) {
    				newDimensions.add(new DimValue(((HasRangeValue<V>)indices.get(i))
    						.getRangeValue().getUpperBound().intValue(), null));
    			}
    			else if (indices.size()==i+1 && lhsArrayDimensions.size()>i+1) {
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
    				// need insert static array bound check.
    				if (lhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue()!=null 
    						&& !((HasRangeValue<V>)indices.get(i)).getRangeValue().hasTop()) {
        				int howManyElementsRemain = lhsArrayShape.getHowManyElements(i);
    					if (((HasRangeValue<V>)indices.get(i)).getRangeValue().getLowerBound()<=0 
    							|| ((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.getUpperBound()>howManyElementsRemain) {
    						/* 
    						 * index out of bound. Actually, when index larger 
    						 * than remaining elements, the error is 
    						 * "attempt to grow array along ambiguous dimension"
    						 */
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    				}
    				/*
					 * if only one index, the return shape will be a row, [1,X], so initialize 
					 * the first dimension as 1, then add the second dimension.
					 */
    				if (indices.size()==1) indexedDimensions.add(new DimValue(1, null));
   					indexedDimensions.add(new DimValue(1, null));
    			}
    			else {
    				// need insert static array bound check.
    				if (lhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue()!=null 
    						&& !((HasRangeValue<V>)indices.get(i)).getRangeValue().hasTop()) {
    					if (((HasRangeValue<V>)indices.get(i)).getRangeValue().getLowerBound()<=0) {
    						// index out of bound.
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    					else if (((HasRangeValue<V>)indices.get(i)).getRangeValue().getUpperBound() 
    							> lhsArrayDimensions.get(i).getIntValue()) {
    						// grow the original array.
    						newDimensions.remove(i);
    						newDimensions.add(i, new DimValue(((HasRangeValue<V>)indices.get(i))
    								.getRangeValue().getUpperBound().intValue(), null));
    					}
    				}
    				indexedDimensions.add(new DimValue(1, null));
    			}
    		}
    		else if (!((HasShape<V>)indices.get(i)).getShape().isScalar()) {
    			// insert array growth check.
    			if (i+1 > lhsArrayDimensions.size()) {
    				newDimensions.add(new DimValue(((HasRangeValue<V>)indices.get(i))
    						.getRangeValue().getUpperBound().intValue(), null));
    			}
    			if (indices.size()==i+1 && lhsArrayDimensions.size()>i+1) {
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
    				// need insert static array bound check.
    				if (lhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue()!=null 
    						&& !((HasRangeValue<V>)indices.get(i)).getRangeValue().hasTop()) {
        				int howManyElementsRemain = lhsArrayShape.getHowManyElements(i);
    					if (((HasRangeValue<V>)indices.get(i)).getRangeValue().getLowerBound()<=0 
    							|| ((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.getUpperBound()>howManyElementsRemain) {
    						// index out of bound.
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    				}
    				/*
					 * if only one index, the return shape will be a row, [1,X], so initialize 
					 * the first dimension as 1, then add the second dimension.
					 */
    				if (indices.size()==1) indexedDimensions.add(new DimValue(1, null));
    				indexedDimensions.add(((HasShape<V>)indices.get(i))
    						.getShape().getDimensions().get(1));
    			}
    			else {
    				// need insert static array bound check.
    				if (lhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue()!=null 
    						&& !((HasRangeValue<V>)indices.get(i)).getRangeValue().hasTop()) {
    					if (((HasRangeValue<V>)indices.get(i)).getRangeValue().getLowerBound()<=0) {
    						// index out of bound.
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    					else if (((HasRangeValue<V>)indices.get(i)).getRangeValue().getUpperBound() 
    							> lhsArrayShape.getDimensions().get(i).getIntValue()) {
    						// grow the original array.
    						newDimensions.remove(i);
    						newDimensions.add(i, new DimValue(((HasRangeValue<V>)indices.get(i))
    								.getRangeValue().getUpperBound().intValue(), null));
    					}
    				}
    				indexedDimensions.add(((HasShape<V>)indices.get(i))
    						.getShape().getDimensions().get(1));
    			}
    		}
    	}
    	Shape<V> indexedShape = new ShapeFactory<V>().newShapeFromDimValues(indexedDimensions);
    	/*
    	 * test whether lhs indexed shape and the rhs value's shape matched.
    	 */
    	if (indexedShape.isConstant()) {
			indexedShape.eliminateTrailingOnes().eliminateLeadingOnes();
    		if(!indexedShape.equals(((HasShape<V>)value).getShape())) {
    			return new ShapeFactory<V>().getMismatchShape();
    		}
    	}
    	Shape<V> resultShape = new ShapeFactory<V>().newShapeFromDimValues(newDimensions);
    	if (resultShape.isConstant()) return resultShape.eliminateTrailingOnes();
    	return resultShape;
    }
}


