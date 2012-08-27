package natlab.tame.valueanalysis.basicmatrix;

import java.util.*;

import natlab.tame.classes.reference.*;                 //class    component
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.aggrvalue.MatrixValue;
import natlab.tame.valueanalysis.components.constant.*; //constant component
import natlab.tame.valueanalysis.components.shape.*;    //shape    component
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.aggrvalue.*;

/**
 * represents a MatrixValue that is instantiable. It stores a constant,
 * on top of the matlab class
 */
public class BasicMatrixValue extends MatrixValue<BasicMatrixValue> implements HasConstant, HasShape<AggrValue<BasicMatrixValue>>{
    static boolean Debug = false;
	static BasicMatrixValueFactory factory = new BasicMatrixValueFactory();
    Constant constant;
    Shape<AggrValue<BasicMatrixValue>> shape;
    //TODO -- also need complex
    
    @Deprecated
    public BasicMatrixValue(PrimitiveClassReference aClass, Shape<AggrValue<BasicMatrixValue>> shape) {
        super(aClass);
        this.shape = shape;
    }
    
    public BasicMatrixValue(PrimitiveClassReference aClass) {
        super(aClass);
    }
    
    public BasicMatrixValue(BasicMatrixValue onlyClassInfo, Shape<AggrValue<BasicMatrixValue>> shape) {
    	super(onlyClassInfo.classRef);
    	this.shape = shape;
    	
    }
    
    /**
     * return a BasicMatrixValue object by taking in a user typed input argument
     * add this method @25th,Jul,2012
     * @param onlyClassInfo
     * @param shapeInfo
     */
    public BasicMatrixValue(BasicMatrixValue onlyClassInfo, String shapeInfo){
    	super(onlyClassInfo.classRef);
    	this.shape = (new ShapeFactory<AggrValue<BasicMatrixValue>>(factory).newShapeFromInputString(shapeInfo));
    }
    
    /**
     * how to deal with a constant
     */
    public BasicMatrixValue(Constant constant){
        super(constant.getMatlabClass());
        if (Debug) System.out.println("inside basicmatrixvalue constant");
        if (Debug) System.out.println(constant.getShape());
        shape = (new ShapeFactory<AggrValue<BasicMatrixValue>>(factory)).newShapeFromIntegers(constant.getShape());//XU study this line!!! infinite loop!!!
        this.constant = constant;
    }
    
    /**
     * returns true if the represented data is a constant
     */
    public boolean isConstant(){
        return constant != null;
    }
    
    @Override
    /**
     * returns the constant represented by this data, or null if it is not constant
     */
    public Constant getConstant(){
        return constant;
    }
    
    public Shape<AggrValue<BasicMatrixValue>> getShape(){
    	return shape;
    }
    
    public void setConstantNull(){
    	this.constant = null;
    }
    
    @Override
    public BasicMatrixValue merge(AggrValue<BasicMatrixValue> other) {
        if (!(other instanceof BasicMatrixValue)) throw new UnsupportedOperationException(
                "can only merge a Matrix Value with another Matrix Value");
        if (!other.getMatlabClass().equals(classRef)) throw new UnsupportedOperationException(
                "only Values with the same class can be merged, trying to merge :"+this+", "+other);
        if (Debug) System.out.println("this constant is "+constant);
        if (constant == null){
        	if (Debug) System.out.println("this constant is null!");
        	if(((BasicMatrixValue)other).constant==null){
        		if (Debug) System.out.println("inside both constant null!");
        		if(shape==null){
        			return this;
        		}
        		if(shape.equals(((BasicMatrixValue)other).getShape())!=true){
        			return new BasicMatrixValue(new BasicMatrixValue(this.classRef),this.shape.merge(((BasicMatrixValue)other).getShape()));
        		}
        	}
        	return this;	
        }
        if (constant.equals(((BasicMatrixValue)other).constant)){
        	if (Debug) System.out.println("this constant is equal to that one!");
        	return this;
        }
        BasicMatrixValue newMatrix = new BasicMatrixValue(new BasicMatrixValue(this.classRef),this.shape.merge(((BasicMatrixValue)other).getShape()));
        if (Debug) System.out.println(newMatrix);
        return newMatrix;
       }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof BasicMatrixValue)) return false;
        BasicMatrixValue m = (BasicMatrixValue)obj;
        if (isConstant()) return constant.equals(m.constant);
        if (Debug) System.out.println(m.getMatlabClass());
        if (Debug) System.out.println(((HasShape)((BasicMatrixValue)obj)).getShape());
        if((shape==null)&&(((HasShape)((BasicMatrixValue)obj)).getShape()==null)){
        	return (classRef.equals(m.getMatlabClass()))&&true;
        }
        return (classRef.equals(m.getMatlabClass()) && shape.equals(((HasShape)((BasicMatrixValue)obj)).getShape()));
    }
    
    @Override
    public String toString() {
        return "("+classRef+(isConstant()?(","+constant):"")+","+shape+")";//XU added shape
    }
    
    public static final BasicMatrixValueFactory FACTORY = new BasicMatrixValueFactory();

    @Override
    public ValueSet<AggrValue<BasicMatrixValue>> arraySubsref(Args<AggrValue<BasicMatrixValue>> indizes){
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
    	List<Integer> arrayDimensionList = new ArrayList<Integer>(this.getShape().getDimensions());
    	if(indizes.size()==2){
    		if (Debug) System.out.println("this array get is with two arguments!");
    		
    		/**
			 * to deal with array get whose first dimension is basicMatrixValue,
			 * in anther word, not colon.
			 */
    		if(indizes.get(0) instanceof BasicMatrixValue){
    			Shape<AggrValue<BasicMatrixValue>> indizesShape = ((BasicMatrixValue)(indizes.get(0))).getShape();
        		List<Integer> scalarShape = new ArrayList<Integer>(2);
        		scalarShape.add(1);
        		scalarShape.add(1);

    			/**
    			 * to deal with array get whose first index is scalar
    			 */
        		if(indizesShape.equals((new ShapeFactory()).newShapeFromIntegers(scalarShape))){
        			
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
        				if(indizes.get(1) instanceof BasicMatrixValue){
        					Shape<AggrValue<BasicMatrixValue>> indizesShape2 = ((BasicMatrixValue)(indizes.get(1))).getShape();
        	        		
        					/**
        					 * to deal with array get whose second index is scalar.
        					 */
        	        		if(indizesShape2.equals((new ShapeFactory()).newShapeFromIntegers(scalarShape))){
        	        			
        	        			/**
        	        			 * to deal with array get whose second index is scalar without exact value.
        	        			 */
        	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
        	        				return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
        	        						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(scalarShape)));
        	        			}
        	        			/**
        	        			 * second index is scalar with exact value.
        	        			 */
        	        			else{
        	        				return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
        	        						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(scalarShape)));
        	        			}
        	        		}
        	        		
        	        		/**
        	        		 * to deal with array set whose second index is not a scalar
        	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
        	        		 * instinctively, the result will be the shape of this index array
        	        		 * TODO more accurate!
        	        		 */
        	        		else{
        	        			return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
        	        					new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(
        	        							((BasicMatrixValue)indizes.get(1)).getShape().getDimensions())));
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
        	        		return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
	        						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newShape)));
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
        				if(indizes.get(1) instanceof BasicMatrixValue){
        					Shape<AggrValue<BasicMatrixValue>> indizesShape2 = ((BasicMatrixValue)(indizes.get(1))).getShape();
        	        		
        					/**
        					 * to deal with array get whose second index is scalar.
        					 */
        	        		if(indizesShape2.equals((new ShapeFactory()).newShapeFromIntegers(scalarShape))){
        	        			
        	        			/**
        	        			 * to deal with array get whose second index is scalar without exact value.
        	        			 */
        	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
        	        				return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
        	        						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(scalarShape)));
        	        			}
        	        			/**
        	        			 * second index is scalar with exact value.
        	        			 */
        	        			else{
        	        				return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
        	        						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(scalarShape)));
        	        			}
        	        		}
        	        		
        	        		/**
        	        		 * to deal with array set whose second index is not a scalar
        	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
        	        		 * instinctively, the result will be the shape of this index array
        	        		 * TODO more accurate!
        	        		 */
        	        		else{
        	        			return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
        	        					new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(
        	        							((BasicMatrixValue)indizes.get(1)).getShape().getDimensions())));
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
        	        		return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
	        						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newShape)));
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
    				if(indizes.get(1) instanceof BasicMatrixValue){
    					Shape<AggrValue<BasicMatrixValue>> indizesShape2 = ((BasicMatrixValue)(indizes.get(1))).getShape();
    	        		
    					/**
    					 * to deal with array get whose second index is scalar.
    					 */
    	        		if(indizesShape2.equals((new ShapeFactory()).newShapeFromIntegers(scalarShape))){
	        				List<Integer> newShape = new ArrayList<Integer>(2);
        	        		newShape.add(((BasicMatrixValue)indizes.get(0)).getShape().getDimensions().get(1));
        	        		newShape.add(1);
        	        		
    	        			/**
    	        			 * to deal with array get whose second index is scalar without exact value.
    	        			 */
    	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
    	        				return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
    	        						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newShape)));
    	        			}
    	        			/**
    	        			 * second index is scalar with exact value.
    	        			 */
    	        			else{
    	        				return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
    	        						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newShape)));
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
        	        		newShape.add(((BasicMatrixValue)indizes.get(0)).getShape().getDimensions().get(1));
        	        		newShape.add(((BasicMatrixValue)indizes.get(1)).getShape().getDimensions().get(1));
    	        			return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
    	        					new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newShape)));
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
    	        		newShape.add(((BasicMatrixValue)indizes.get(0)).getShape().getDimensions().get(1));
    	        		newShape.add(arrayDimensionList.get(1));
    	        		return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
        						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newShape)));
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
				if(indizes.get(1) instanceof BasicMatrixValue){
					Shape<AggrValue<BasicMatrixValue>> indizesShape2 = ((BasicMatrixValue)(indizes.get(1))).getShape();
	        		
					/**
					 * to deal with array get whose second index is scalar.
					 */
	        		if(indizesShape2.equals((new ShapeFactory()).newShapeFromIntegers(scalarShape))){
        				List<Integer> newShape = new ArrayList<Integer>(2);
    	        		newShape.add(arrayDimensionList.get(0));
    	        		newShape.add(1);
    	        		
	        			/**
	        			 * to deal with array get whose second index is scalar without exact value.
	        			 */
	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
	        				return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
	        						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newShape)));
	        			}
	        			/**
	        			 * second index is scalar with exact value.
	        			 */
	        			else{
	        				return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
	        						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newShape)));
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
    	        		newShape.add(((BasicMatrixValue)indizes.get(1)).getShape().getDimensions().get(1));
	        			return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
	        					new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newShape)));
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
	        		return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
    						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newShape)));
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
    		if(indizes.get(0) instanceof BasicMatrixValue){
    			Shape<AggrValue<BasicMatrixValue>> indizesShape = ((BasicMatrixValue)(indizes.get(0))).getShape();
    			
    			/**
    			 * to deal with array get whose only index is scalar, 
    			 * actually, we don't care whether or not the scalar is with an exact value.
    			 */
    			if(indizesShape.equals((new ShapeFactory()).newShapeFromIntegers(scalarShape))){
    				return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
    						new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(scalarShape)));
    			}
    			
    			/**
    			 * to deal with array get whose only index is array.
    			 */
    			else{
    				List<Integer> newShape = new ArrayList<Integer>(2);
	        		newShape.add(1);
	        		newShape.add(((BasicMatrixValue)indizes.get(0)).getShape().getDimensions().get(1));
        			return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
        					new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newShape)));
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
        			return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
        					new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newShape)));
    			}
    			catch(Exception e){
    				return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
        					new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(arrayDimensionList)));
    			}
    			
    		}
    	}
    }
        				

    @Override
    public AggrValue<BasicMatrixValue> arraySubsasgn(
            Args<AggrValue<BasicMatrixValue>> indizes,AggrValue<BasicMatrixValue> value) {
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
    	List<Integer> arrayDimensionList = new ArrayList<Integer>(this.getShape().getDimensions());
		List<Integer> scalarShape = new ArrayList<Integer>(2);
		scalarShape.add(1);
		scalarShape.add(1);
    	if(indizes.size()==2){
    		if (Debug) System.out.println("this array assign is with two arguments!");
    		
    		/**
			 * to deal with array assign whose first dimension is basicMatrixValue,
			 * in anther word, not colon.
			 */
    		if(indizes.get(0) instanceof BasicMatrixValue){
    			Shape<AggrValue<BasicMatrixValue>> indizesShape = ((BasicMatrixValue)(indizes.get(0))).getShape();

    			/**
    			 * to deal with array get whose first index is scalar
    			 */
        		if(indizesShape.equals((new ShapeFactory()).newShapeFromIntegers(scalarShape))){
        			
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
        				if(indizes.get(1) instanceof BasicMatrixValue){
        					Shape<AggrValue<BasicMatrixValue>> indizesShape2 = ((BasicMatrixValue)(indizes.get(1))).getShape();
        	        		
        					/**
        					 * to deal with array assign whose second index is scalar.
        					 */
        	        		if(indizesShape2.equals((new ShapeFactory()).newShapeFromIntegers(scalarShape))){
        	        			
        	        			/**
        	        			 * to deal with array assign whose second index is scalar without exact value.
        	        			 */
        	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
        	        				return this;
        	        			}
        	        			/**
        	        			 * second index is scalar with exact value.
        	        			 */
        	        			else{
        	        				return this;
        	        			}
        	        		}
        	        		
        	        		/**
        	        		 * to deal with array assign whose second index is not a scalar
        	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
        	        		 * TODO more accurate!
        	        		 */
        	        		else{
        	        			return this;
        	        		}
        				}
        				
        				/**
        				 * to deal with array assign whose first index is scalar without exact value,
        				 * the second index is colon.
        				 */
        				else{
        	        		return this;
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
        				if(indizes.get(1) instanceof BasicMatrixValue){
        					Shape<AggrValue<BasicMatrixValue>> indizesShape2 = ((BasicMatrixValue)(indizes.get(1))).getShape();
        	        		
        					/**
        					 * to deal with array assign whose second index is scalar.
        					 */
        	        		if(indizesShape2.equals((new ShapeFactory()).newShapeFromIntegers(scalarShape))){
        	        			
        	        			/**
        	        			 * to deal with array assign whose second index is scalar without exact value.
        	        			 */
        	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
        	        				return this;
        	        			}
        	        			/**
        	        			 * second index is scalar with exact value.
        	        			 */
        	        			else{
        	        				return this;
        	        			}
        	        		}
        	        		
        	        		/**
        	        		 * to deal with array assign whose second index is not a scalar
        	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
        	        		 * TODO more accurate!
        	        		 */
        	        		else{
        	        			return this;
        	        		}
        				}
        				
        				/**
        				 * to deal with array assign whose first index is scalar with exact value,
        				 * the second index is colon.
        				 */
        				else{
        	        		return this;
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
    				if(indizes.get(1) instanceof BasicMatrixValue){
    					Shape<AggrValue<BasicMatrixValue>> indizesShape2 = ((BasicMatrixValue)(indizes.get(1))).getShape();
    	        		
    					/**
    					 * to deal with array assign whose second index is scalar.
    					 */
    	        		if(indizesShape2.equals((new ShapeFactory()).newShapeFromIntegers(scalarShape))){
        	        		
    	        			/**
    	        			 * to deal with array get whose second index is scalar without exact value.
    	        			 */
    	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
    	        				return this;
    	        			}
    	        			/**
    	        			 * second index is scalar with exact value.
    	        			 */
    	        			else{
    	        				return this;
    	        			}
    	        		}
    	        		
    	        		/**
    	        		 * to deal with array assign whose second index is not a scalar
    	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
    	        		 * so the two index are both array.
    	        		 * TODO more accurate!
    	        		 */
    	        		else{
    	        			return this;
    	        		}
    				}
    				
    				/**
    				 * to deal with array assign whose first index is an array,
    				 * the second index is colon.
    				 */
    				else{
    	        		return this;
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
				if(indizes.get(1) instanceof BasicMatrixValue){
					Shape<AggrValue<BasicMatrixValue>> indizesShape2 = ((BasicMatrixValue)(indizes.get(1))).getShape();
	        		
					/**
					 * to deal with array assign whose second index is scalar.
					 */
	        		if(indizesShape2.equals((new ShapeFactory()).newShapeFromIntegers(scalarShape))){
    	        		
	        			/**
	        			 * to deal with array assign whose second index is scalar without exact value.
	        			 */
	        			if(((HasConstant)indizes.get(1)).getConstant()==null){
	        				return this;
	        			}
	        			/**
	        			 * second index is scalar with exact value.
	        			 */
	        			else{
	        				return this;
	        			}
	        		}
	        		
	        		/**
	        		 * to deal with array assign whose second index is not a scalar
	        		 * so, it will be an array, whose upper and lower boundary can both be either exact or unknown.
	        		 * so the two index are both array
	        		 * TODO more accurate!
	        		 */
	        		else{
	        			return this;
	        		}
				}
				
				/**
				 * to deal with array assign whose first index is colon,
				 * the second index is colon.
				 */
				else{
	        		return this;
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
    		if(indizes.get(0) instanceof BasicMatrixValue){
    			Shape<AggrValue<BasicMatrixValue>> indizesShape = ((BasicMatrixValue)(indizes.get(0))).getShape();
    			
    			/**
    			 * to deal with array assign whose only index is scalar, 
    			 * actually, we don't care whether or not the scalar is with an exact value.
    			 */
    			if(indizesShape.equals((new ShapeFactory()).newShapeFromIntegers(scalarShape))){
    				return this;
    			}
    			
    			/**
    			 * to deal with array assign whose only index is array.
    			 */
    			else{
        			return this;
    			}
    		}
    		
    		/**
    		 * to deal with array assign whose only index is colon.
    		 */
    		else{
    			return this;
    		}
    	}
    }
    @Override
    public ValueSet<AggrValue<BasicMatrixValue>> dotSubsref(String field) {
        throw new UnsupportedOperationException("cannot dot-access a matrix!");
        //return ValueSet.newInstance(factory.newErrorValue("cannot dot-access a matrix"));
    }
    @Override
    public AggrValue<BasicMatrixValue> dotSubsasgn(String field,
            AggrValue<BasicMatrixValue> value) {
    	throw new UnsupportedOperationException(); //TODO
    }
    @Override
    public Res<AggrValue<BasicMatrixValue>> cellSubsref(Args<AggrValue<BasicMatrixValue>> indizes) {
        throw new UnsupportedOperationException(); //TODO
    }
    @Override
    public AggrValue<BasicMatrixValue> cellSubsasgn(Args<AggrValue<BasicMatrixValue>> indizes, Args<AggrValue<BasicMatrixValue>> values) {
        throw new UnsupportedOperationException(); //TODO
    }
    @Override
    public AggrValue<BasicMatrixValue> toFunctionArgument(boolean recursive) {
    	return this;
    	//throw new UnsupportedOperationException(); //TODO
    }
}


