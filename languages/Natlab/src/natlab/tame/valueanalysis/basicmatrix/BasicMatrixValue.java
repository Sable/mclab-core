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
    	 */
    	List<Integer> ls = new ArrayList<Integer>(this.getShape().getDimensions());
    	if(indizes.size()==2){
    		if (Debug) System.out.println("this array get is with two arguments!");
    		/**
			 * this situation is for array assign whose first dimension is basicMatrixValue.
			 */
    		if(indizes.get(0) instanceof BasicMatrixValue){
    			if(((HasConstant)indizes.get(0)).getConstant()==null){
            		if (Debug) System.out.println("constant component is null!");
            		Shape<AggrValue<BasicMatrixValue>> indizesShape = ((BasicMatrixValue)(indizes.get(0))).getShape();
            		List<Integer> dims = new ArrayList<Integer>(2);
            		dims.add(1);
            		dims.add(1);
            		if(indizesShape.equals((new ShapeFactory()).newShapeFromIntegers(dims))){
            			if (Debug) System.out.println("constant value is unknown, but it's definitely a scalar!");
            			List<Integer> newLs = new ArrayList<Integer>(ls.size());
            	    	newLs.add(1);
            	    	ls.remove(0);
            	    	int result = 1;
            	    	for(Integer dimNum : ls){
            	    		result = result*dimNum;
            	    	}
            	    	newLs.add(result);
            	    	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
            	    			new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
            		}
            		/**
            		 * deal with constant value is empty and the shape is not scalar, like arr2 = arr1(1:2,2).
            		 */
            		//FIXME
            		return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
            				new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),this.getShape()));
            	}
    			/**
				 * this situation is for array assign whose first and second dimension are both basicMatrixValue.
				 */
    			if(indizes.get(1) instanceof BasicMatrixValue){
    				if(indizes.size()==ls.size()){
    					List<Integer> newLs = new ArrayList<Integer>(ls.size());
    					newLs.add(1);
    					newLs.add(1);
    					return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
    							new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
    				}
    				//TODO throw error, warning or exception?
    				throw new UnsupportedOperationException();
    			}
				/**
				 * this situation is for array assign like arr(1,:), the second dimension is a colon.
				 */
    			else{
    				Double indexDouble = (Double)((HasConstant)indizes.get(0)).getConstant().getValue();
                	int index = indexDouble.intValue();
                	if(index>ls.get(0)){
                		if (Debug) System.out.println("index exceeds matrix dimensions!");
                		throw new UnsupportedOperationException();//FIXME
                	}
                	List<Integer> newLs = new ArrayList<Integer>(ls.size());
                	newLs.add(1);
                	ls.remove(0);
                	int result = 1;
                	for(Integer dimNum : ls){
                		result = result*dimNum;
                	}
                	newLs.add(result);
                	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
                			new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
    			}	
    		}
    		/**
			 * this situation is for array assign whose first dimension is not a basicMatrixValue, which is a colon, like array(:,1) or arr(:,:)
			 */
    		else{
    			/**
				 * this situation is for array assign whose second dimension is a basicMatrixValue.
				 */
    			if(indizes.get(1) instanceof BasicMatrixValue){
    				if(((HasConstant)indizes.get(1)).getConstant()==null){
                		if (Debug) System.out.println("constant component is null!");
                		Shape<AggrValue<BasicMatrixValue>> indizesShape = ((BasicMatrixValue)(indizes.get(1))).getShape();
                		List<Integer> dims = new ArrayList<Integer>(2);
                		dims.add(1);
                		dims.add(1);
                		if(indizesShape.equals((new ShapeFactory()).newShapeFromIntegers(dims))){
                			if (Debug) System.out.println("constant value is unknown, but it's definitely a scalar!");
                			List<Integer> newLs = new ArrayList<Integer>(ls.size());
                	    	newLs.add(1);
                	    	ls.remove(0);
                	    	int result = 1;
                	    	for(Integer dimNum : ls){
                	    		result = result*dimNum;
                	    	}
                	    	newLs.add(result);
                	    	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
                	    			new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
                		}
                		//deal with constant value is empty and the shape is not scalar
                		//FIXME
                		return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
                				new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),this.getShape()));
                	}
                	Double indexDouble = (Double)((HasConstant)indizes.get(1)).getConstant().getValue();
                	int index = indexDouble.intValue();
                	if(index>ls.get(1)){
                		if (Debug) System.out.println("index exceeds matrix dimensions!");
                		throw new UnsupportedOperationException();//FIXME
                	}
                	List<Integer> newLs = new ArrayList<Integer>(ls.size());
                	newLs.add(ls.get(0));
                	newLs.add(1);
                	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
                			new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
    			}

    			/**
    			 * this situation is for array assign whose first and second dimension are both not basicMatrixvalue, like arr(:,:).
    			 */
    			else{
    				return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(this);
    			}
    		}
    		
    	}
    	else{
    		if (Debug) System.out.println("this array get is with one argument!");
    		if(((HasConstant)indizes.get(0)).getConstant()==null){
        		if (Debug) System.out.println("constant component is null!");
        		Shape<AggrValue<BasicMatrixValue>> indizesShape = ((BasicMatrixValue)(indizes.get(0))).getShape();
        		List<Integer> dims = new ArrayList<Integer>(2);
        		dims.add(1);
        		dims.add(1);
        		if(indizesShape.equals((new ShapeFactory()).newShapeFromIntegers(dims))){
        			if (Debug) System.out.println("constant value is unknown, but it's definitely a scalar!");
        			List<Integer> newLs = new ArrayList<Integer>(ls.size());
        	    	newLs.add(1);
        	    	newLs.add(1);
        	    	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
        	    			new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
        		}
        	}
        	Double indexDouble = (Double)((HasConstant)indizes.get(0)).getConstant().getValue();
        	int index = indexDouble.intValue();
        	int size = 1;
        	for(Integer dimNum : ls){
        		size = size*dimNum;
        	}
        	if (Debug) System.out.println(size);
        	if(index>size){
        		if (Debug) System.out.println("index exceeds matrix dimensions!");
        		throw new UnsupportedOperationException();//FIXME
        	}
        	List<Integer> newLs = new ArrayList<Integer>(ls.size());
        	newLs.add(1);
        	newLs.add(1);
        	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(
        			new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
    	}	
    }
    @Override
    public AggrValue<BasicMatrixValue> arraySubsasgn(
            Args<AggrValue<BasicMatrixValue>> indizes,AggrValue<BasicMatrixValue> value) {
    	/**
    	 * Consider array set, like a(1,2)=3, indizes are 1,2, and value is 3.
    	 * If the index doesn't exceed matrix dimension,
    	 * the array set statement doesn't change the array's shape;
    	 * if the index does exceed matrix dimension,
    	 * the array's shape will be changed.
    	 */
    	if(indizes.size()==2){
    		if (Debug) System.out.println("this array get is with two arguments!");
    		/**
			 * this situation is for array assign whose first dimension is basicMatrixValue.
			 */
    		if(indizes.get(0) instanceof BasicMatrixValue){
				/**
				 * this situation is for array assign whose first and second dimension are both basicMatrixValue.
				 */
    			if(indizes.get(1) instanceof BasicMatrixValue){
    				List<Integer> ls = new ArrayList<Integer>(this.getShape().getDimensions());
    		    	int size = ls.size();
    		    	for(int i=0; i<size; i++){
    		    		Double indexDouble = (Double)((HasConstant)indizes.get(i)).getConstant().getValue();
    		        	int index = indexDouble.intValue();
    		    		if(index>ls.get(i)){
    		    			if (Debug) System.out.println("index exceeds matrix dimensions, the matrix shape expands.");
    		    			ls.remove(i);
    		    			ls.add(i, index);
    		    		}
    		    	}
    		    	return new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(ls));
    			}
				/**
				 * this situation is for array assign like arr(1,:), the second dimension is a colon.
				 */
    			else{
    				return this;
    			}
    		}
			/**
			 * this situation is for array assign whose first dimension is not a basicMatrixValue, which is a colon, like array(:,1) or arr(:,:)
			 */
    		else{
				/**
				 * this situation is for array assign whose second dimension is a basicMatrixValue.
				 */
    			if(indizes.get(1) instanceof BasicMatrixValue){
    				return this;
    			}
    			/**
    			 * this situation if for array assign whose first and second dimension are both not basicMatrixvalue.
    			 */
    			else{
    				return this;
    			}
    		}
    	}
    	else{
    		if (Debug) System.out.println("this array get is with one argument!");
    		return this;
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


