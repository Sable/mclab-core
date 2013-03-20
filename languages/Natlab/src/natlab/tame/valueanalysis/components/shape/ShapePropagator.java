package natlab.tame.valueanalysis.components.shape;

import java.util.*;

import natlab.tame.builtin.*;
import natlab.tame.builtin.shapeprop.ShapePropTool;
import natlab.tame.builtin.shapeprop.HasShapePropagationInfo;
import natlab.tame.valueanalysis.value.*;

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
    
    //FIXME rewrite the shape analysis for array get and array set statements!
    
    /**
     * shape analysis for array get statement:
     * first, the indices of array in our IR can be divided into three categories:
     * 1. scalar;
     * 2. vector, which should have a range value;
     * 3. colon value, which is ":".
     * second, in MATLAB syntax, we don't need the same number of indices as the 
     * number of array's dimensions, i.e. shape(a)=[2,3,4], which is a three 
     * dimensions array, in MATLAB, we can use only one, two or three indices 
     * to index this array, i.e. b=a(5), which is a linear indexing, b=a(2,:) or 
     * b=a(2,:,2).
     * @param arrayShape
     * @param indices
     * @return
     */
    public Shape<V> arraySubsref(Shape<V> arrayShape, Args<V> indices) {
    	if (arrayShape==null) return null;
    	List<DimValue> newDimensions = new ArrayList<DimValue>(indices.size());
    	List<DimValue> arrayDimensions = arrayShape.getDimensions();
    	if (indices.size() > arrayDimensions.size()) {
    		System.err.println("index exceed the array bound, check you code.");
    		return null;
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
    			if (indices.size()==i+1 && arrayDimensions.size()>i+1) {
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
					int howManyElementsRemain = arrayShape.getHowManyElements(i);
    				if (indices.size()==1) newDimensions.add(new DimValue(1, null));
    				if (howManyElementsRemain==-1) newDimensions.add(new DimValue());
    				else newDimensions.add(new DimValue(howManyElementsRemain, null));
    			}
    			else newDimensions.add(arrayDimensions.get(i));
    		}
    		else if (((HasShape<V>)indices.get(i)).getShape().isScalar()) {
    			if (indices.size()==i+1 && arrayDimensions.size()>i+1) {
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
    				if (indices.size()==1) newDimensions.add(new DimValue(1, null));
    				newDimensions.add(new DimValue(1, null));
    			}
    			else newDimensions.add(new DimValue(1, null));
    		}
    		else if (!((HasShape<V>)indices.get(i)).getShape().isScalar()) {
    			if (indices.size()==i+1 && arrayDimensions.size()>i+1) {
    				if (Debug) System.out.println("need to collapse the remaining dimensions");
    				if (indices.size()==1) newDimensions.add(new DimValue(1, null));
    			}
    			if (((HasShape<V>)indices.get(i)).getShape().isConstant()) 
    				newDimensions.add(((HasShape<V>)indices.get(i)).getShape()
    						.getDimensions().get(1));
    			else
    				newDimensions.add(new DimValue());
    		}
    		else {
    			System.err.println("this may not happen...");
    		}
    	}
    	return new ShapeFactory<V>().newShapeFromDimValues(newDimensions);
    }

    /**
     * shape analysis for array set statement:
     * the only problem for this shape analysis is that we need to determine 
     * whether the indices may grow the original array. when we do bound check, 
     * we may need the range value analysis result, i.e. if shape(a)=[2,b,5] 
     * and range(b)=<3,5>, then a(2,4,4)=rhs will not grow array a, because 
     * range(b)=<3,5>, by the way, do we need to say range(b)=<4,5> after this?
     * TODO think about this range value analysis problem.
     * TODO is the input argument V value useful for this analysis, if the 
     * value's shape doesn't conform with the lhs indexed array, MATLAB will 
     * throw exception, should we also throw exception, in another word, 
     * can we assume the input MATLAB code's syntax is always correct?
     * @param arrayShape
     * @param indices
     * @param value
     * @return
     * 
     * TODO add array bound check.
     * 
     */
    public Shape<V> arraySubsasgn(Shape<V> arrayShape, Args<V> indices, V value) {
    	List<DimValue> newDimensions = new ArrayList<DimValue>();
    	List<DimValue> arrayDimensions = arrayShape.getDimensions();
    	if (indices.size() > arrayDimensions.size()) {
    		System.err.println("index exceed the array bound, check you code.");
    		return null;
    	}
    	return arrayShape;
    }
}


