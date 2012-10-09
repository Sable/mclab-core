package natlab.tame.valueanalysis.components.shape;

import java.util.*;

import natlab.tame.builtin.*;
import natlab.tame.builtin.shapeprop.ShapePropTool;
import natlab.tame.builtin.shapeprop.HasShapePropagationInfo;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.value.*;


public class ShapePropagator<V extends Value<V>> 
	extends BuiltinVisitor<Args<V>, List<Shape<V>>>{//XU modified at 4.28.12.55pm, very important!!!
	//this is a singleton class -- make it singleton, ignore all the generic stuff
    @SuppressWarnings("rawtypes")
	static ShapePropagator instance = null;
    static boolean Debug = false;
    /**
     * return singleton instance of shape propagator
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	static public <V extends Value<V>> ShapePropagator<V> getInstance(){
        if (instance == null) instance = new ShapePropagator();
        return instance;
    }
    private ShapePropagator(){} //hidden private constructor

    @Override
	public List<Shape<V>> caseBuiltin(Builtin builtin, Args<V> arg) {
		// TODO
		if (Debug) System.out.println("inside ShapePropgator, builtin fn is "+builtin);
		if (Debug) System.out.println("the number of output variables is "+arg.getNargout());
		if(builtin instanceof HasShapePropagationInfo){
			//call shape prop tool
			List<Shape<?>> result = ShapePropTool.matchByValues(((HasShapePropagationInfo)builtin).getShapePropagationInfo(),arg);
			List<Shape<V>> vResult = new ArrayList<Shape<V>>();
			for(Shape<?> res: result){
				vResult.add((Shape<V>)res);
			}
			return vResult;
		}
		throw new UnsupportedOperationException();
	}
    
    public Shape<V> forRange(V lower,	V upper, V inc){
		//FIXME do something proper here
		List<Integer> scalarShape = new ArrayList<Integer>(2);
		scalarShape.add(1);
		scalarShape.add(1);
		if (inc != null){
			return (new ShapeFactory<V>()).newShapeFromIntegers(scalarShape);
		} else {
			return (new ShapeFactory<V>()).newShapeFromIntegers(scalarShape);
		}
    }
    
    public Shape<V> arraySubsref(Shape<V> arrayShape, Args<V> indizes){
    	/**
    	 * Consider array get, like b=a(1,2), index are 1 and 2.
    	 * If the index doesn't exceed matrix dimension,
    	 * the array get statement doesn't change the array's shape;
    	 * if the index does exceed matrix dimension,
    	 * the array's shape will be changed.
    	 * 
    	 * each dimension can be scalar with exact value, scalar without exact value, 
    	 * array with exact upper and lower boundary, array without exact upper and lower boundary,
    	 * or just a colon, ":", i.e. arr(1,:).
    	 * btw, only colon is not basicMatrixValue class.
    	 */
    	List<Integer> arrayDimensionList = new ArrayList<Integer>(arrayShape.getDimensions());
    	if(indizes.size()==2){
    		if (Debug) System.out.println("this array get is with two arguments!");
    		
    		/**
			 * to deal with array get whose first dimension is basicMatrixValue,
			 * in anther word, not colon.
			 */
    		if((indizes.get(0) instanceof Value<?>)&&(!(indizes.get(0) instanceof ColonValue))){
    			Shape<V> indizesShape = ((HasShape<V>)(indizes.get(0))).getShape();
        		List<Integer> scalarShape = new ArrayList<Integer>(2);
        		scalarShape.add(1);
        		scalarShape.add(1);

    			/**
    			 * to deal with array get whose first index is scalar
    			 */
        		if(indizesShape.equals((new ShapeFactory<V>()).newShapeFromIntegers(scalarShape))){
        			
        			/**
        			 * to deal with array get whose first index is scalar without exact value
        			 */
        			if(((HasConstant)indizes.get(0)).getConstant()==null){
        				if (Debug) System.out.println("first index's constant component is null!");
        				if (Debug) System.out.println("first index's constant value is unknown, but it's definitely a scalar!");
        				
        				/**
        				 * to deal with array get whose first index is scalar without exact value,
        				 * the second index is basicMatrixValue, in another word, not colon.
        				 */
        				if((indizes.get(1) instanceof Value<?>)&&(!(indizes.get(1) instanceof ColonValue))){
        					Shape<V> indizesShape2 = ((HasShape<V>)(indizes.get(1))).getShape();
        	        		
        					/**
        					 * to deal with array get whose second index is scalar.
        					 */
        	        		if(indizesShape2.equals((new ShapeFactory<V>()).newShapeFromIntegers(scalarShape))){
        	        			
        	        			/**
        	        			 * to deal with array get whose second index is scalar without exact value.
        	        			 */
        	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
        	        				return new ShapeFactory<V>().newShapeFromIntegers(scalarShape);
        	        			}
        	        			/**
        	        			 * second index is scalar with exact value.
        	        			 */
        	        			else{
        	        				return new ShapeFactory<V>().newShapeFromIntegers(scalarShape);
        	        			}
        	        		}
        	        		
        	        		/**
        	        		 * to deal with array set whose second index is not a scalar
        	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
        	        		 * instinctively, the result will be the shape of this index array
        	        		 * TODO more accurate!
        	        		 */
        	        		else{
        	        			return new ShapeFactory<V>().newShapeFromIntegers(
        	        							((HasShape<V>)indizes.get(1)).getShape().getDimensions());
        	        		}
        				}
        				
        				/**
        				 * to deal with array get whose first index is scalar without exact value,
        				 * the second index is colon.
        				 * instinctively, the result will be [1,dim], where dim is the second dimension of this array,
        				 * if this array is only one dimension, then, arr(1,:) will be equal to arr itself.
        				 */
        				else{
        					List<Integer> newShape = new ArrayList<Integer>(2);
        	        		newShape.add(1);
        	        		newShape.add(arrayDimensionList.get(1));
        	        		return new ShapeFactory<V>().newShapeFromIntegers(newShape);
        				}
        			}
        			
        			/**
        			 * to deal with array get whose first index is scalar with exact value
        			 */
        			else{
        				if (Debug) System.out.println("first index's constant value is an exact scalar!");
        				
        				/**
        				 * to deal with array get whose first index is scalar with exact value,
        				 * the second index is basicMatrixValue, in another word, not colon.
        				 */
        				if((indizes.get(1) instanceof Value<?>)&&(!(indizes.get(1) instanceof ColonValue))){
        					Shape<V> indizesShape2 = ((HasShape<V>)(indizes.get(1))).getShape();
        	        		
        					/**
        					 * to deal with array get whose second index is scalar.
        					 */
        	        		if(indizesShape2.equals((new ShapeFactory<V>()).newShapeFromIntegers(scalarShape))){
        	        			
        	        			/**
        	        			 * to deal with array get whose second index is scalar without exact value.
        	        			 */
        	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
        	        				return new ShapeFactory<V>().newShapeFromIntegers(scalarShape);
        	        			}
        	        			/**
        	        			 * second index is scalar with exact value.
        	        			 */
        	        			else{
        	        				return new ShapeFactory<V>().newShapeFromIntegers(scalarShape);
        	        			}
        	        		}
        	        		
        	        		/**
        	        		 * to deal with array set whose second index is not a scalar
        	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
        	        		 * instinctively, the result will be the shape of this index array
        	        		 * TODO more accurate!
        	        		 */
        	        		else{
        	        			return new ShapeFactory<V>().newShapeFromIntegers(
        	        							((HasShape<V>)indizes.get(1)).getShape().getDimensions());
        	        		}
        				}
        				
        				/**
        				 * to deal with array get whose first index is scalar with exact value,
        				 * the second index is colon.
        				 * instinctively, the result will be [1,dim], where dim is the second dimension of this array,
        				 * if this array is only one dimension, then, arr(1,:) will be equal to arr itself.
        				 */
        				else{
        					List<Integer> newShape = new ArrayList<Integer>(2);
        	        		newShape.add(1);
        	        		newShape.add(arrayDimensionList.get(1));
        	        		return new ShapeFactory<V>().newShapeFromIntegers(newShape);
        				}
        			}
        		}
        		
        		/**
    			 * to deal with array get whose first index is basicMatrixValue, but not scalar,
    			 * in another word, first index is an array,
    			 * also this array's upper and lower boundary can both be either known or unknown.
    			 * instinctively, the first dimension of result will be the size of this array,
    			 * for example, brr = arr(2:3,don'tCare), the first dimension of brr will be 2.
    			 */
        		else{
        			
        			/**
    				 * to deal with array get whose first index is an array,
    				 * the second index is basicMatrixValue, in another word, not colon.
    				 */
    				if((indizes.get(1) instanceof Value<?>)&&(!(indizes.get(1) instanceof ColonValue))){
    					Shape<V> indizesShape2 = ((HasShape<V>)(indizes.get(1))).getShape();
    	        		
    					/**
    					 * to deal with array get whose second index is scalar.
    					 */
    	        		if(indizesShape2.equals((new ShapeFactory<V>()).newShapeFromIntegers(scalarShape))){
	        				List<Integer> newShape = new ArrayList<Integer>(2);
        	        		newShape.add(((HasShape<V>)(indizes.get(1))).getShape().getDimensions().get(1));
        	        		newShape.add(1);
        	        		
    	        			/**
    	        			 * to deal with array get whose second index is scalar without exact value.
    	        			 */
    	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
    	        				return new ShapeFactory<V>().newShapeFromIntegers(newShape);
    	        			}
    	        			/**
    	        			 * second index is scalar with exact value.
    	        			 */
    	        			else{
    	        				return new ShapeFactory<V>().newShapeFromIntegers(newShape);
    	        			}
    	        		}
    	        		
    	        		/**
    	        		 * to deal with array set whose second index is not a scalar
    	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
    	        		 * so the two index are both array
    	        		 * instinctively, the result's each dimension will be the size of each index array.
    	        		 * TODO more accurate!
    	        		 */
    	        		else{
    	        			List<Integer> newShape = new ArrayList<Integer>(2);
        	        		newShape.add(((HasShape<V>)indizes.get(0)).getShape().getDimensions().get(1));
        	        		newShape.add(((HasShape<V>)indizes.get(1)).getShape().getDimensions().get(1));
    	        			return new ShapeFactory<V>().newShapeFromIntegers(newShape);
    	        		}
    				}
    				
    				/**
    				 * to deal with array get whose first index is an array,
    				 * the second index is colon.
    				 * instinctively, the result will be [size,dim], 
    				 * where size is the size of first index array,
    				 *        dim is the second dimension of this array,
    				 */
    				else{
    					List<Integer> newShape = new ArrayList<Integer>(2);
    	        		newShape.add(((HasShape<V>)indizes.get(0)).getShape().getDimensions().get(1));
    	        		newShape.add(arrayDimensionList.get(1));
    	        		return new ShapeFactory<V>().newShapeFromIntegers(newShape);
    				}
        		}
    		}
    		
    		/**
			 * to deal with array get whose first dimension is colon,
			 * so the result's first dimension will be the same size of this array's first dimension
			 */
    		else{
        		List<Integer> scalarShape = new ArrayList<Integer>(2);
        		scalarShape.add(1);
        		scalarShape.add(1);
        		
    			/**
				 * to deal with array get whose first index is colon,
				 * the second index is basicMatrixValue, in another word, not colon.
				 */
				if((indizes.get(1) instanceof Value<?>)&&(!(indizes.get(1) instanceof ColonValue))){
					Shape<V> indizesShape2 = ((HasShape<V>)(indizes.get(1))).getShape();
	        		
					/**
					 * to deal with array get whose second index is scalar.
					 */
	        		if(indizesShape2.equals((new ShapeFactory<V>()).newShapeFromIntegers(scalarShape))){
        				List<Integer> newShape = new ArrayList<Integer>(2);
    	        		newShape.add(arrayDimensionList.get(0));
    	        		newShape.add(1);
    	        		
	        			/**
	        			 * to deal with array get whose second index is scalar without exact value.
	        			 */
	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
	        				return new ShapeFactory<V>().newShapeFromIntegers(newShape);
	        			}
	        			/**
	        			 * second index is scalar with exact value.
	        			 */
	        			else{
	        				return new ShapeFactory<V>().newShapeFromIntegers(newShape);
	        			}
	        		}
	        		
	        		/**
	        		 * to deal with array set whose second index is not a scalar
	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
	        		 * so the two index are both array
	        		 * instinctively, the result's each dimension will be the size of each index array.
	        		 * TODO more accurate!
	        		 */
	        		else{
	        			List<Integer> newShape = new ArrayList<Integer>(2);
    	        		newShape.add(arrayDimensionList.get(0));
    	        		newShape.add(((HasShape<V>)indizes.get(1)).getShape().getDimensions().get(1));
	        			return new ShapeFactory<V>().newShapeFromIntegers(newShape);
	        		}
				}
				
				/**
				 * to deal with array get whose first index is colon,
				 * the second index is colon.
				 * obviously, the result is the same as this array.
				 */
				else{
					List<Integer> newShape = new ArrayList<Integer>(2);
	        		newShape.add(arrayDimensionList.get(0));
	        		newShape.add(arrayDimensionList.get(1));
	        		return new ShapeFactory<V>().newShapeFromIntegers(newShape);
				}
    		}
    	}
    	
    	/**
    	 * only one index, TODO find all the situations.
    	 * the index can be scalar, array and colon
    	 */
    	else{
    		if (Debug) System.out.println("this array get is with one argument!");
    		List<Integer> scalarShape = new ArrayList<Integer>(2);
    		scalarShape.add(1);
    		scalarShape.add(1);
    		
    		/**
    		 * to deal with array get whose only index is basicMatrixValue.
    		 */
    		if((indizes.get(0) instanceof Value<?>)&&(!(indizes.get(0) instanceof ColonValue))){
    			Shape<V> indizesShape = ((HasShape<V>)(indizes.get(0))).getShape();
    			
    			/**
    			 * to deal with array get whose only index is scalar, 
    			 * actually, we don't care whether or not the scalar is with an exact value.
    			 */
    			if(indizesShape.equals((new ShapeFactory<V>()).newShapeFromIntegers(scalarShape))){
    				return new ShapeFactory<V>().newShapeFromIntegers(scalarShape);
    			}
    			
    			/**
    			 * to deal with array get whose only index is array.
    			 */
    			else{
    				List<Integer> newShape = new ArrayList<Integer>(2);
	        		newShape.add(1);
	        		newShape.add(((HasShape<V>)indizes.get(0)).getShape().getDimensions().get(1));
        			return new ShapeFactory<V>().newShapeFromIntegers(newShape);
    			}
    		}
    		
    		/**
    		 * to deal with array get whose only index is colon.
    		 * in Matlab, if array1's shape is [2,3], after array2=array1(:), array2's shape will be [6,1].
    		 */
    		else{
    			int result = 1;
    			try{
    				for(int dim : arrayDimensionList){
        				result = result*dim;
        			}
        			List<Integer> newShape = new ArrayList<Integer>(2);
        			newShape.add(result);
        			newShape.add(1);
        			return new ShapeFactory<V>().newShapeFromIntegers(newShape);
    			}
    			catch(Exception e){
    				return new ShapeFactory<V>().newShapeFromIntegers(arrayDimensionList);
    			}
    			
    		}
    	}
    }

    public Shape<V> arraySubsasgn(Shape<V> arrayShape, Args<V> indizes, V value){
    	/**
    	 * Consider array assign, like a(1,2)=b, index are 1 and 2, b is value.
    	 * one intresting thing is that, for array set, it doesn't care about value!!!
    	 * If the index doesn't exceed matrix dimension,
    	 * the array get statement doesn't change the array's shape;
    	 * if the index does exceed matrix dimension,
    	 * the array's shape will be changed.
    	 * 
    	 * each dimension can be scalar with exact value, scalar without exact value, 
    	 * array with exact upper and lower boundary, array without exact upper and lower boundary,
    	 * or just a colon, ":", i.e. arr(1,:).
    	 * btw, only colon is not basicMatrixValue class.
    	 */
    	List<Integer> scalarShape = new ArrayList<Integer>(2);
		scalarShape.add(1);
		scalarShape.add(1);
    	if(indizes.size()==2){
    		if (Debug) System.out.println("this array assign is with two arguments!");
    		
    		/**
			 * to deal with array assign whose first dimension is basicMatrixValue,
			 * in anther word, not colon.
			 */
    		if((indizes.get(0) instanceof Value<?>)&&(!(indizes.get(0) instanceof ColonValue))){
    			Shape<V> indizesShape = ((HasShape<V>)(indizes.get(0))).getShape();

    			/**
    			 * to deal with array get whose first index is scalar
    			 */
        		if(indizesShape.equals((new ShapeFactory<V>()).newShapeFromIntegers(scalarShape))){
        			
        			/**
        			 * to deal with array assign whose first index is scalar without exact value
        			 */
        			if(((HasConstant)indizes.get(0)).getConstant()==null){
        				if (Debug) System.out.println("first index's constant component is null!");
        				if (Debug) System.out.println("first index's constant value is unknown, but it's definitely a scalar!");
        				
        				/**
        				 * to deal with array assign whose first index is scalar without exact value,
        				 * the second index is basicMatrixValue, in another word, not colon.
        				 */
        				if((indizes.get(1) instanceof Value<?>)&&(!(indizes.get(1) instanceof ColonValue))){
        					Shape<V> indizesShape2 = ((HasShape<V>)(indizes.get(1))).getShape();
        	        		
        					/**
        					 * to deal with array assign whose second index is scalar.
        					 */
        	        		if(indizesShape2.equals((new ShapeFactory<V>()).newShapeFromIntegers(scalarShape))){
        	        			
        	        			/**
        	        			 * to deal with array assign whose second index is scalar without exact value.
        	        			 */
        	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
        	        				return arrayShape;
        	        			}
        	        			/**
        	        			 * second index is scalar with exact value.
        	        			 */
        	        			else{
        	        				return arrayShape;
        	        			}
        	        		}
        	        		
        	        		/**
        	        		 * to deal with array assign whose second index is not a scalar
        	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
        	        		 * TODO more accurate!
        	        		 */
        	        		else{
        	        			return arrayShape;
        	        		}
        				}
        				
        				/**
        				 * to deal with array assign whose first index is scalar without exact value,
        				 * the second index is colon.
        				 */
        				else{
        	        		return arrayShape;
        				}
        			}
        			
        			/**
        			 * to deal with array assign whose first index is scalar with exact value
        			 */
        			else{
        				if (Debug) System.out.println("first index's constant value is an exact scalar!");
        				
        				/**
        				 * to deal with array assign whose first index is scalar with exact value,
        				 * the second index is basicMatrixValue, in another word, not colon.
        				 */
        				if((indizes.get(1) instanceof Value<?>)&&(!(indizes.get(1) instanceof ColonValue))){
        					Shape<V> indizesShape2 = ((HasShape<V>)(indizes.get(1))).getShape();
        	        		
        					/**
        					 * to deal with array assign whose second index is scalar.
        					 */
        	        		if(indizesShape2.equals((new ShapeFactory<V>()).newShapeFromIntegers(scalarShape))){
        	        			
        	        			/**
        	        			 * to deal with array assign whose second index is scalar without exact value.
        	        			 */
        	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
        	        				return arrayShape;
        	        			}
        	        			/**
        	        			 * second index is scalar with exact value.
        	        			 */
        	        			else{
        	        				return arrayShape;
        	        			}
        	        		}
        	        		
        	        		/**
        	        		 * to deal with array assign whose second index is not a scalar
        	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
        	        		 * TODO more accurate!
        	        		 */
        	        		else{
        	        			return arrayShape;
        	        		}
        				}
        				
        				/**
        				 * to deal with array assign whose first index is scalar with exact value,
        				 * the second index is colon.
        				 */
        				else{
        	        		return arrayShape;
        				}
        			}
        		}
        		
        		/**
    			 * to deal with array assign whose first index is basicMatrixValue, but not scalar,
    			 * in another word, first index is an array,
    			 * also this array's upper and lower boundary can both be either known or unknown.
    			 */
        		else{
        			
        			/**
    				 * to deal with array assign whose first index is an array,
    				 * the second index is basicMatrixValue, in another word, not colon.
    				 */
    				if((indizes.get(1) instanceof Value<?>)&&(!(indizes.get(1) instanceof ColonValue))){
    					Shape<V> indizesShape2 = ((HasShape<V>)(indizes.get(1))).getShape();
    	        		
    					/**
    					 * to deal with array assign whose second index is scalar.
    					 */
    	        		if(indizesShape2.equals((new ShapeFactory<V>()).newShapeFromIntegers(scalarShape))){
        	        		
    	        			/**
    	        			 * to deal with array get whose second index is scalar without exact value.
    	        			 */
    	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
    	        				return arrayShape;
    	        			}
    	        			/**
    	        			 * second index is scalar with exact value.
    	        			 */
    	        			else{
    	        				return arrayShape;
    	        			}
    	        		}
    	        		
    	        		/**
    	        		 * to deal with array assign whose second index is not a scalar
    	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
    	        		 * so the two index are both array.
    	        		 * TODO more accurate!
    	        		 */
    	        		else{
    	        			return arrayShape;
    	        		}
    				}
    				
    				/**
    				 * to deal with array assign whose first index is an array,
    				 * the second index is colon.
    				 */
    				else{
    	        		return arrayShape;
    				}
        		}
    		}
    		
    		/**
			 * to deal with array assign whose first dimension is colon.
			 */
    		else{
        		
    			/**
				 * to deal with array assign whose first index is colon,
				 * the second index is basicMatrixValue, in another word, not colon.
				 */
				if((indizes.get(1) instanceof Value<?>)&&(!(indizes.get(1) instanceof ColonValue))){
					Shape<V> indizesShape2 = ((HasShape<V>)(indizes.get(1))).getShape();
	        		
					/**
					 * to deal with array assign whose second index is scalar.
					 */
	        		if(indizesShape2.equals((new ShapeFactory<V>()).newShapeFromIntegers(scalarShape))){
    	        		
	        			/**
	        			 * to deal with array assign whose second index is scalar without exact value.
	        			 */
	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
	        				return arrayShape;
	        			}
	        			/**
	        			 * second index is scalar with exact value.
	        			 */
	        			else{
	        				return arrayShape;
	        			}
	        		}
	        		
	        		/**
	        		 * to deal with array assign whose second index is not a scalar
	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
	        		 * so the two index are both array
	        		 * TODO more accurate!
	        		 */
	        		else{
	        			return arrayShape;
	        		}
				}
				
				/**
				 * to deal with array assign whose first index is colon,
				 * the second index is colon.
				 */
				else{
	        		return arrayShape;
				}
    		}
    	}
    	
    	/**
    	 * only one index, TODO find all the situations.
    	 * the index can be scalar, array and colon
    	 */
    	else{
    		if (Debug) System.out.println("this array get is with one argument!");
    		
    		/**
    		 * to deal with array assign whose only index is basicMatrixValue.
    		 */
    		if(!arrayShape.isShapeExactlyKnown()){
    			return arrayShape;
    		}
    		else{
    			if((indizes.get(0) instanceof Value<?>)&&(!(indizes.get(0) instanceof ColonValue))){
        			Shape<V> indizesShape = ((HasShape<V>)(indizes.get(0))).getShape();
        			
        			/**
        			 * to deal with array assign whose only index is scalar, 
        			 * actually, we don't care whether or not the scalar is with an exact value.
        			 */
        			if(indizesShape.equals((new ShapeFactory<V>()).newShapeFromIntegers(scalarShape))){
        				/**
        				 * here, we need to check the bound, in case the array may be expanded.
        				 */
        				if(((HasConstant)indizes.get(0)).getConstant()==null){
        					return arrayShape;
        				}
        				else{
        					double dbIndize = (Double) ((HasConstant)indizes.get(0)).getConstant().getValue();
        					int intIndize = (int) dbIndize;
        					int arraySize = 1;
        			    	for(Integer i : arrayShape.getDimensions()){
        			    		arraySize = arraySize*i;
        			    	}
        			    	if(intIndize>arraySize){
        			    		arrayShape.getDimensions().set(1, intIndize/arrayShape.getDimensions().get(0));
        			    	}
        					return arrayShape;
        				}
        			}
        			
        			/**
        			 * to deal with array assign whose only index is array.
        			 * here, we need to think about maybe the original array will be expanded by the index.
        			 */
        			else{
        				//TODO
        				if(arrayShape.bigger(indizesShape)){
        					return arrayShape;
        				}
        				else{
        					return new ShapeFactory<V>().newShapeFromIntegers(indizesShape.getDimensions());
        				}
        			}
        		}
        		
        		/**
        		 * to deal with array assign whose only index is colon.
        		 */
        		else{
        			return arrayShape;
        		}
    		}
    	}
    }
}


