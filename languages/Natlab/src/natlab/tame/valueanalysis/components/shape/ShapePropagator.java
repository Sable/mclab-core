package natlab.tame.valueanalysis.components.shape;

import java.util.*;

import natlab.tame.builtin.*;
import natlab.tame.builtin.shapeprop.ShapePropTool;
import natlab.tame.builtin.shapeprop.HasShapePropagationInfo;
import natlab.tame.valueanalysis.value.*;
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
    	if (rhsArrayShape == null) return null;
    	List<DimValue> indexedDimensions = new ArrayList<DimValue>(indices.size());
    	List<DimValue> rhsArrayDimensions = rhsArrayShape.getDimensions();
    	// first check, array get cannot resize the accessed matrix.
    	if (indices.size() > rhsArrayDimensions.size()) {
    		System.err.println("indices exceed the array's dimensions, check you code.");
    		// TODO may need to mark the current flow set as nonviable.
    		return new ShapeFactory<V>().getOutOfBoundShape();
    	}
    	for (int i = 0; i < indices.size(); i++) {
    		int POS = i + 1;
    		/*
    		 * ColonValue extends from SpecialValue which extends from Value, 
    		 * but BasicMatrixValue extends from MatrixValue which extends from 
    		 * Value, so ColonValue is totally different from MatrixValue, and 
    		 * of course, we cannot convert ColonValue to HasShape. as a result, 
    		 * we should deal with ColonValue first.
    		 */
    		if (indices.get(i) instanceof ColonValue) {
    			if (POS == indices.size() && POS < rhsArrayDimensions.size()) {
        			/*
        			 * proceed linear indexing, won't resize the shape of the indexed 
        			 * array, but has to set the dimension of the return shape.
        			 */
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
					int howManyElementsRemain = rhsArrayShape.getHowManyElements(i);
					/*
					 * if only one index, the return shape will be a row, [1,X], so initialize 
					 * the first dimension as 1, then add the second dimension.
					 */
    				if (indices.size() == 1) indexedDimensions.add(new DimValue(1, null));
    				if (howManyElementsRemain == -1) 
    					indexedDimensions.add(new DimValue());
    				else 
    					indexedDimensions.add(new DimValue(howManyElementsRemain, null));
    			}
    			else {
    				/*
    				 * the ordinary case.
    				 */
    				indexedDimensions.add(rhsArrayDimensions.get(i).cloneThisValue());
    			}
    		}
    		else if (((HasShape<V>)indices.get(i)).getShape().isScalar()) {
    			if (POS == indices.size() && POS < rhsArrayDimensions.size()) {
        			/*
        			 * proceed linear indexing, won't resize the shape of the indexed 
        			 * array, but has to set the dimension of the return shape. 
        			 * besides, need static array bound check.
        			 */
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
    				if (rhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue() != null 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue().isBothBoundsKnown()) 
    				{
        				int howManyElementsRemain = rhsArrayShape.getHowManyElements(i);
    					if (!((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.isInBounds(0, howManyElementsRemain)) {
    						/* 
    						 * index out of bound.
    						 * 
    						 * TODO may need to mark the current flow set as nonviable.
    						 */
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    				}
    				/*
					 * if only one index, the return shape will be a row, [1,X], so initialize 
					 * the first dimension as 1, then add the second dimension.
					 */
    				if (indices.size() == 1) indexedDimensions.add(new DimValue(1, null));
   					indexedDimensions.add(new DimValue(1, null));
    			}
    			else {
    				/*
    				 * the ordinary case.
    				 */
    				// need insert static array bound check.
    				if (rhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue() != null 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue().isBothBoundsKnown())
    				{
    					if (!((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.isInBounds(0, rhsArrayDimensions.get(i).getIntValue())) {
    						/* 
    						 * index out of bound.
    						 * 
    						 * TODO may need to mark the current flow set as nonviable.
    						 */
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    				}
    				indexedDimensions.add(new DimValue(1, null));
    			}
    		}
    		else if (!((HasShape<V>)indices.get(i)).getShape().isScalar()) {
    			if (POS == indices.size() && POS < rhsArrayDimensions.size()) {
    				/*
        			 * proceed linear indexing, won't resize the shape of the indexed 
        			 * array, but has to set the dimension of the return shape. 
        			 * besides, need static array bound check.
        			 */
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
    				if (rhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue() != null 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue().isBothBoundsKnown()) 
    				{
        				int howManyElementsRemain = rhsArrayShape.getHowManyElements(i);
    					if (!((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.isInBounds(0, howManyElementsRemain)) {
    						/* 
    						 * index out of bound.
    						 * 
    						 * TODO may need to mark the current flow set as nonviable.
    						 */
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    				}
    				/*
					 * if only one index, the return shape will be a row, [1,X], so initialize 
					 * the first dimension as 1, then add the second dimension.
					 */
    				if (indices.size() == 1 && rhsArrayDimensions.get(0).hasIntValue() 
    						&& rhsArrayDimensions.get(0).getIntValue() == 1) {
    					indexedDimensions.add(new DimValue(1, null));
        				// add the value of the second dimension of the vector to the return shape.
        				indexedDimensions.add(((HasShape<V>)indices.get(i))
        						.getShape().getDimensions().get(1));
    				}
    				else if (indices.size() == 1 && rhsArrayDimensions.get(1).hasIntValue() 
    						&& rhsArrayDimensions.get(1).getIntValue() == 1) {
        				// add the value of the second dimension of the vector to the return shape.
        				indexedDimensions.add(((HasShape<V>)indices.get(i))
        						.getShape().getDimensions().get(1));
    					indexedDimensions.add(new DimValue(1, null));
    				}
    				else {
        				// add the value of the second dimension of the vector to the return shape.
        				indexedDimensions.add(((HasShape<V>)indices.get(i))
        						.getShape().getDimensions().get(1));    					
    				}
    			}
    			else {
    				/*
    				 * the ordinary case.
    				 */
    				// need insert static array bound check.
    				if (rhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue() != null 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue().isBothBoundsKnown()) 
    				{
    					if (!((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.isInBounds(0, rhsArrayShape.getDimensions().get(i).getIntValue())) {
    						/* 
    						 * index out of bound.
    						 * 
    						 * TODO may need to mark the current flow set as nonviable.
    						 */
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    				}
    				// add the value of the second dimension of the vector to the return shape.
    				indexedDimensions.add(((HasShape<V>)indices.get(i))
    						.getShape().getDimensions().get(1));
    			}
    		}
    	}
    	Shape<V> resultShape = new ShapeFactory<V>().newShapeFromDimValues(indexedDimensions);
    	return resultShape.eliminateTrailingOnes();
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
     * 
     * And when we cannot proceed static array bound check during the shape 
     * analysis of array set, we have to assume the indices may be out of array 
     * bound, mark array as allocatable array and leave the ABC to runtime.
     * 
     * Using range value analysis result in array bound check. If and only if 
     * both the upper bound of the index is smaller than the size of corresponding 
     * dimension and the lower bound of the index is larger than 0, we say it 
     * is not out of the array bound. If not, we assign null to the corresponding 
     * dimension, which means the array's shape is not exactly known, and will be 
     * declared as allocatable array in the generated code.
     * 
     * TODO what is the maximum size which an array can be or can be declared?
     * TODO in matlab array index, there is a keyword "end".
     * 
     * DONE: Since we cannot assume the input MATLAB code is 100% syntax correct 
     * and safe, we need to consider the situation that if the rhs value's shape 
     * doesn't conform with the lhs indexed array, i.e., we assign a shape of 
     * 1-by-2 to an indexed array whose indexed result is with the shape of 1-by-3. 
     * In MATLAB, this is an error. We should also throw something, error or 
     * warnings.
     */
    public Shape<V> arraySubsasgn(Shape<V> lhsArrayShape, Args<V> indices, V value) {
    	if (lhsArrayShape == null) return null;
    	List<DimValue> lhsArrayDimensions = lhsArrayShape.getDimensions();
    	List<DimValue> newDimensions = new ArrayList<DimValue>();
    	// need to copy a completely new dimension list from the old one.
    	for (DimValue dim : lhsArrayDimensions) {
    		newDimensions.add(dim.cloneThisValue());
    	}
    	
    	for (int i=0; i<indices.size(); i++) {
    		int POS = i + 1;
    		/*
    		 * ColonValue extends from SpecialValue which extends from Value, 
    		 * but BasicMatrixValue extends from MatrixValue which extends from 
    		 * Value, so ColonValue is totally different from MatrixValue, and 
    		 * of course, we cannot convert ColonValue to HasShape. as a result, 
    		 * we should deal with ColonValue first.
    		 */
    		if (indices.get(i) instanceof ColonValue) {
    			if (POS > lhsArrayDimensions.size()) {
    				/*
    				 * out-of-bound index with colon notation, matlab error.
    				 */
    				System.err.println("cannot grow the array with ':'.");
    				// TODO may need to mark the current flow set as nonviable.
    				return new ShapeFactory<V>().getOutOfBoundShape();
    			}
    			else if (POS == indices.size() && POS < lhsArrayDimensions.size()) {
    				/*
    				 * proceed linear indexing, won't resize the shape of the matrix.
    				 */
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
    			}
    			else {
    				/*
    				 * the ordinary case, won't resize the shape of the matrix.
    				 */
    			}
    		}
    		else if (((HasShape<V>)indices.get(i)).getShape().isScalar()) {
    			if (POS > lhsArrayDimensions.size()) {
    				/*
    				 * out-of-bound index with a scalar, matrix can be grew.
    				 * TODO using range value analysis result to improve accuracy.
    				 * 
    				 * quick fix, grow array with upper bound of index.
    				 */
    				if (((HasRangeValue<V>)indices.get(i)).getRangeValue() != null 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue().hasUpperBound()) {
    					newDimensions.add(new DimValue(
    							((HasRangeValue<V>)indices.get(i)).getRangeValue().getUpperBound().getIntValue()
    							, null));
    				}
    				else newDimensions.add(new DimValue());
    			}
    			else if (POS == indices.size() && POS < lhsArrayDimensions.size()) {
    				/*
    				 * proceed linear indexing, won't resize the 
    				 * shape of the matrix, but need array bound check.
    				 * 
    				 */
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
    				/*
    				 * using range value analysis result to proceed static abc.
    				 */
    				if (lhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue() != null 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue().isBothBoundsKnown()) 
    				{
        				int howManyElementsRemain = lhsArrayShape.getHowManyElements(i);
    					if (!((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.isInBounds(0, howManyElementsRemain)) {
    						/*
    						 * grow the original indexed array.
    						 */
    						if (lhsArrayShape.isScalar()) {
    							/*
    							 * for the case where there is no preallocation and 
    							 * using array assignment as array construction. 
    							 */
    							newDimensions.remove(0);
    							newDimensions.add(0, new DimValue(1, null));
    							newDimensions.remove(1);
    							// grow array with upper bound of index.
    							newDimensions.add(1, new DimValue(
    									((HasRangeValue<V>)indices.get(i)).getRangeValue()
    									.getUpperBound().getIntValue(), null));
    						}
    						else if (lhsArrayShape.isRowVector()) {
    							newDimensions.remove(1);
    							newDimensions.add(1, new DimValue(
    									((HasRangeValue<V>)indices.get(i)).getRangeValue()
    									.getUpperBound().getIntValue(), null));
    						}
    						else if (lhsArrayShape.isColVector()) {
    							newDimensions.remove(0);
    							newDimensions.add(0, new DimValue(
    									((HasRangeValue<V>)indices.get(i)).getRangeValue()
    									.getUpperBound().getIntValue(), null));
    						}
    						else {
        						/* 
        						 * index out of bound. Actually, when index larger 
        						 * than remaining elements, the error is 
        						 * "attempt to grow array along ambiguous dimension"
        						 * 
        						 * TODO may need to mark the current flow set as nonviable.
        						 */
        						return new ShapeFactory<V>().getOutOfBoundShape();    							
    						}
    					}
    				}
    				else if (indices.size() == 1 && lhsArrayShape.isRowVector()) {
    					// TODO linear indexing of row-vectors.
    					newDimensions.remove(1);
    					newDimensions.add(1, new DimValue());
    				}
    				else if (indices.size() == 1 && lhsArrayShape.isColVector()) {
    					// TODO linear indexing of column-vectors.
    					newDimensions.remove(0);
    					newDimensions.add(0, new DimValue());
    				}
    				else {
    					/*
    					 * the range value is not exactly unknown, cannot proceed 
    					 * static abc, have to mark that dimension as ? and leave 
    					 * it to the back end to generate runtime abc inlined code. 
    					 */
    					newDimensions.remove(POS);
						newDimensions.add(POS, new DimValue());    					
    				}
    			}
    			else {
    				/*
    				 * the ordinary case, out-of-bound index may grow the matrix.
    				 * using range value analysis result to proceed static abc.
    				 * 
    				 * since this is ordinary case, we don't need to require the 
    				 * whole shape is exactly known.
    				 */
    				if (lhsArrayDimensions.get(i).hasIntValue() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue() != null 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue().isBothBoundsKnown()) 
    				{
    					if (((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.getLowerBound().lessThanZero()) {
    						// TODO may need to mark the current flow set as nonviable.
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    					else if (!((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.isInBounds(0, lhsArrayDimensions.get(i).getIntValue())) {
    						/*
    						 * grow the original array.
    						 * TODO using range value analysis result to improve accuracy.
    						 * 
    						 * quick fix, grow array with upper bound of index.
    						 */
    						newDimensions.remove(i);
    						newDimensions.add(i, new DimValue(
    								((HasRangeValue<V>)indices.get(i)).getRangeValue().getUpperBound().getIntValue()
    								, null));
    					}
    					else {
    						// in-bound array indexing, do nothing.
    					}
    				}
    				else {
    					/*
    					 * the range value is not exactly unknown, cannot proceed 
    					 * static abc, have to mark that dimension as ? and leave 
    					 * it to the back end to generate runtime abc inlined code. 
    					 */
    					newDimensions.remove(i);
						newDimensions.add(i, new DimValue());
    				}
    			}
    		}
    		else if (!((HasShape<V>)indices.get(i)).getShape().isScalar()) {
    			/*
				 * proceed linear indexing, won't resize the shape 
				 * of the matrix, but need array bound check.
				 */
    			if (POS > lhsArrayDimensions.size()) {
    				/*
    				 * out-of-bound index with a scalar, matrix can be grew.
    				 * TODO using range value analysis result to improve accuracy.
    				 */
    				newDimensions.add(new DimValue());
    			}
    			if (POS == indices.size() && POS < lhsArrayDimensions.size()) {
    				/*
    				 * proceed linear indexing, won't resize the 
    				 * shape of the matrix, but need array bound check.
    				 */
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
    				/*
    				 * using range value analysis result to proceed static abc.
    				 */
    				if (lhsArrayShape.isConstant() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue() != null 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue().isBothBoundsKnown()) 
    				{
        				int howManyElementsRemain = lhsArrayShape.getHowManyElements(i);
    					if (!((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.isInBounds(0, howManyElementsRemain)) {
    						// TODO may need to mark the current flow set as nonviable.
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    				}
    				else {
    					/*
    					 * the range value is not exactly unknown, cannot proceed 
    					 * static abc, have to mark that dimension as ? and leave 
    					 * it to the back end to generate runtime abc inlined code. 
    					 */
    					newDimensions.remove(i);
						newDimensions.add(i, new DimValue());    					
    				}
    			}
    			else {
    				/*
    				 * the ordinary case, out-of-bound index may grow the matrix.
    				 * using range value analysis result to proceed static abc.
    				 * 
    				 * since this is ordinary case, we don't need to require the 
    				 * whole shape is exactly known.
    				 */
    				if (lhsArrayDimensions.get(i).hasIntValue() 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue() != null 
    						&& ((HasRangeValue<V>)indices.get(i)).getRangeValue().isBothBoundsKnown()) 
    				{
    					if (((HasRangeValue<V>)indices.get(i)).getRangeValue().getLowerBound().lessThanZero()) {
    						// TODO may need to mark the current flow set as nonviable.
    						return new ShapeFactory<V>().getOutOfBoundShape();
    					}
    					else if (!((HasRangeValue<V>)indices.get(i)).getRangeValue()
    							.isInBounds(0, lhsArrayDimensions.get(i).getIntValue())) {
    						/*
    						 * grow the original array.
    						 * TODO using range value analysis result to improve accuracy.
    						 */
    						newDimensions.remove(i);
    						newDimensions.add(i, new DimValue(
    								((HasRangeValue<V>)indices.get(i)).getRangeValue().getUpperBound().getIntValue()
    								, null));
    					}
    					else {
    						// in-bound array indexing, do nothing.
    					}
    				}
    				else {
    					/*
    					 * the range value is not exactly unknown, cannot proceed 
    					 * static abc, have to mark that dimension as ? and leave 
    					 * it to the back end to generate runtime abc inlined code. 
    					 */
    					newDimensions.remove(i);
						newDimensions.add(i, new DimValue());
    				}
    			}
    		}
    	}
    	Shape<V> resultShape = new ShapeFactory<V>().newShapeFromDimValues(newDimensions);
    	if (resultShape.isConstant()) return resultShape.eliminateTrailingOnes();
    	return resultShape;
    }
}


