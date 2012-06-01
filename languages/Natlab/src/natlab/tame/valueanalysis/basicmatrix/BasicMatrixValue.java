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
    	List<Integer> ls = new ArrayList<Integer>(this.getShape().getDimensions());
    	if(indizes.size()==2){
    		if (Debug) System.out.println("this array get is with two arguments!");
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
            	    	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
            		}
            		//deal with constant value is empty and the shape is not scalar
            		//FIXME
            		return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),this.getShape()));
            	}
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
            	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
    		}
    		else{
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
            	    	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
            		}
            		//deal with constant value is empty and the shape is not scalar
            		//FIXME
            		return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),this.getShape()));
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
            	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
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
        	    	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
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
        	return ValueSet.<AggrValue<BasicMatrixValue>>newInstance(new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs)));
    	}
    	
    }
    
    @Override
    public ValueSet<AggrValue<BasicMatrixValue>> dotSubsref(String field) {
        throw new UnsupportedOperationException("cannot dot-access a matrix!");
        //return ValueSet.newInstance(factory.newErrorValue("cannot dot-access a matrix"));
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
    public AggrValue<BasicMatrixValue> arraySubsasgn(//FIXME not correct, just for test!
            Args<AggrValue<BasicMatrixValue>> indizes,AggrValue<BasicMatrixValue> value) {//XU: we don't need to care about value, but we should care about index!
    	List<Integer> ls = new ArrayList<Integer>(this.getShape().getDimensions());
    	if(indizes.size()==2){
    		if (Debug) System.out.println("this array get is with two arguments!");
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
        	    	return new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs));
        		}
        		//deal with constant value is empty and the shape is not scalar
        		//FIXME
        		return new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),this.getShape());
        	}
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
        	return new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs));
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
        	    	return new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs));
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
        	return new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()),(new ShapeFactory()).newShapeFromIntegers(newLs));
    	}
    	/*for(AggrValue<BasicMatrixValue> index:indizes){
    		try{
    			//deal with constant index
    			if((((BasicMatrixValue)index).getConstant())!=null){
    				double castDou = ((DoubleConstant)((HasConstant)((BasicMatrixValue)index)).getConstant()).getValue();
    				int castInt = (int) castDou;
    				if(castInt>(this.getShape().getDimensions().get(1))){
    					if (Debug) System.out.println("the array is going to be expanded, because the index boundary is larger than the array boundary!");
    					ArrayList<Integer> dim = new ArrayList<Integer>(2);
    					dim.add(1);
    					dim.add(castInt);
    					return new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()), (new ShapeFactory<AggrValue<BasicMatrixValue>>(factory)).newShapeFromIntegers(dim));
    				}
    			}
    			//deal with matrix index
    			if((((BasicMatrixValue)index).getShape())!=null){
    				if((((BasicMatrixValue)index).getShape().getDimensions().get(1))>(this.getShape().getDimensions().get(1))){
    					if (Debug) System.out.println("the array is going to be expanded, because the index boundary is larger than the array boundary!");
                		return new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()), (new ShapeFactory<AggrValue<BasicMatrixValue>>(factory)).newShapeFromIntegers(((HasShape)((BasicMatrixValue)index)).getShape().getDimensions()));
                	}
    			}
    		}catch (Exception e){
    			return new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()), this.getShape());
    		}
    		
    	}
    	return new BasicMatrixValue(new BasicMatrixValue(this.getMatlabClass()), this.getShape());//XU modified @21:24,May 13th
*/    }
    
    @Override
    public AggrValue<BasicMatrixValue> toFunctionArgument(boolean recursive) {
    	throw new UnsupportedOperationException(); //TODO
    }
    @Override
    public AggrValue<BasicMatrixValue> dotSubsasgn(String field,
            AggrValue<BasicMatrixValue> value) {
    	throw new UnsupportedOperationException(); //TODO
    }
}


