package natlab.tame.builtin.shapeprop;

import java.util.*;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;


public class ShapePropMatch<V extends Value<V>>{
	static boolean Debug = false;
	public ShapeFactory<V> factory = new ShapeFactory<V>();
	int numMatched = 0;             //number of matched arguments, act as the index of arguments 
	int numEmittedResults = 0;      //number of emitted results, I cannot say its index of shape equation,
	                                //because there is also non-matching expression in the language.
	int numInVertcat = 0;           //index in vertcat;
	int previousMatchedNumber = 0;
	HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();  //lowercase is used like m=previousScalar()
	HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();  //mostly, uppercase is used for matching a shape
	boolean whetherLatestMatchedIsNum = false;
	boolean ArrayIndexAssignLeft = false;
	boolean ArrayIndexAssignRight = false;
	boolean isError = false;
	boolean matchingIsDone = false;
	boolean outputIsDone = false;
	boolean isInsideVertcat = false;//this boolean is used for distinguish the lowercase in vertcat or not, like n=previousScalar() and [m,n]
	boolean isInsideAssign = false;
	String previousMatchedLowercase = null;
	String previousMatchedUppercase = null;
	ArrayList<DimValue> needForVertcat = new ArrayList<DimValue>();
	List<Shape<V>> output = new ArrayList<Shape<V>>();  //used for output results 
	
	
	public ShapePropMatch(){}
	
	/**
	 * constructor referring to parent
	 * @param parent
	 */
	public ShapePropMatch(ShapePropMatch<V> parent){
		this.factory = parent.factory;
        this.numMatched = parent.numMatched;
        this.numEmittedResults = parent.numEmittedResults;
        this.numInVertcat = parent.numInVertcat;
        this.previousMatchedNumber = parent.previousMatchedNumber;
        this.lowercase = parent.lowercase;
        this.uppercase = parent.uppercase;
        this.whetherLatestMatchedIsNum = parent.whetherLatestMatchedIsNum;
        this.ArrayIndexAssignLeft = parent.ArrayIndexAssignLeft;
        this.ArrayIndexAssignRight = parent.ArrayIndexAssignRight;
        this.isError = parent.isError;
        this.matchingIsDone = parent.matchingIsDone;
        this.outputIsDone = parent.outputIsDone;
        this.isInsideVertcat = parent.isInsideVertcat;
        this.isInsideAssign = parent.isInsideAssign;
        this.previousMatchedLowercase = parent.previousMatchedLowercase;
        this.previousMatchedUppercase = parent.previousMatchedUppercase;
        this.needForVertcat = parent.needForVertcat;
        this.output = parent.output;
	}
	
	/**
	 * add new lower case or upper case into current result
	 */
	public ShapePropMatch(ShapePropMatch<V> parent, HashMap<String, DimValue> lowercase, HashMap<String, Shape<V>> uppercase){
		this.factory = parent.factory;
		this.numMatched = parent.numMatched;
		this.numEmittedResults = parent.numEmittedResults;
		this.numInVertcat = parent.numInVertcat;
		this.previousMatchedNumber = parent.previousMatchedNumber;
		this.lowercase = parent.lowercase;
		this.uppercase = parent.uppercase;
		if(lowercase!=null){
			this.lowercase.putAll(lowercase);  //using HashMap putAll
		}
		if(uppercase!=null){
			this.uppercase.putAll(uppercase);
		}
		this.whetherLatestMatchedIsNum = parent.whetherLatestMatchedIsNum;
		this.ArrayIndexAssignLeft = parent.ArrayIndexAssignLeft;
		this.ArrayIndexAssignRight = parent.ArrayIndexAssignRight;
	    this.isError = parent.isError;
	    this.matchingIsDone = parent.matchingIsDone;
	    this.outputIsDone = parent.outputIsDone;
	    this.isInsideVertcat = parent.isInsideVertcat;
	    this.isInsideAssign = parent.isInsideAssign;
        this.previousMatchedLowercase = parent.previousMatchedLowercase;
        this.previousMatchedUppercase = parent.previousMatchedUppercase;
        this.needForVertcat = parent.needForVertcat;
        this.output = parent.output;
	}

    /**
     * returns a ShapePropMatch which advances argIndex by one, and refers back to this
     */
    public void comsumeArg(){
    	this.numMatched = this.numMatched+1;
    }
    
    public void setIsError(){
    	this.isError = true;
    }
    //resetIsError is for OR case
    public void resetIsError(){
    	this.isError = false;
    }
    
    public boolean getIsError(){
    	return this.isError;
    }
    
    public void setWhetherLatestMatchedIsNum(boolean condition){
    	this.whetherLatestMatchedIsNum = condition;
    }
    
    public boolean getWhetherLatestMatchedIsNum(){
    	return this.whetherLatestMatchedIsNum;
    }
    
    public void setArrayIndexAssignLeft(boolean condition){
    	this.ArrayIndexAssignLeft = condition;
    }
    
    public boolean isArrayIndexAssignLeft(){
    	return this.ArrayIndexAssignLeft;
    }
    
    public void setArrayIndexAssignRight(boolean condition){
    	this.ArrayIndexAssignRight = condition;
    }
    
    public boolean isArrayIndexAssignRight(){
    	return this.ArrayIndexAssignRight;
    }
    
    public void saveLatestMatchedNumber(int latestMatchedNumber){
    	this.previousMatchedNumber = latestMatchedNumber;
    }
    
    public int getLatestMatchedNumber(){
    	return previousMatchedNumber;
    }
    
    public void saveLatestMatchedLowercase(String latestMatchedLowercase){
    	this.previousMatchedLowercase = latestMatchedLowercase;
    }
    
    public String getLatestMatchedLowercase(){
    	return this.previousMatchedLowercase;
    }
    
    public void saveLatestMatchedUppercase(String latestMatchedUpperCase){
    	this.previousMatchedUppercase = latestMatchedUpperCase;
    }
    
    public String getLatestMatchedUppercase(){
    	return this.previousMatchedUppercase;
    }
    
    public int getNumMatched(){
    	return numMatched;
    }
    
    public int getNumEmittedResults(){
    	return numEmittedResults;
    }
    
    public void setNumInVertcat(int index){
    	this.numInVertcat = index;
    }
    
    public int getNumInVertcat(){
    	return this.numInVertcat;
    }
    
    public void setMatchingIsDone(){
    	this.matchingIsDone = true;
    }
    
    public void setOutputIsDone(){
    	this.outputIsDone = true;
    }
    
    public void setIsInsideVertcat(boolean condition){
    	this.isInsideVertcat = condition;
    }
    
    public boolean matchingIsDone(){
    	return matchingIsDone;
    }
    
    public boolean outputIsDone(){
    	return outputIsDone;
    }
    
    public boolean isInsideVertcat(){
    	return isInsideVertcat;
    }
    
    public void setIsInsideAssign(boolean condition){
    	this.isInsideAssign = condition;
    }
    
    public boolean isInsideAssign(){
    	return isInsideAssign;
    }
    
    public boolean hasValue(String key){
    	if(this.lowercase.get(key).getValue()==null){
    		return false;
    	}
    	else{
    		return true;
    	}
    }
    
    public DimValue getValueOfVariable(String key){
    	DimValue value = this.lowercase.get(key);
    	return value;
    }
    
    public Shape<V> getShapeOfVariable(String key){
    	Shape<V> shape = this.uppercase.get(key);
    	return shape;
    }
    
    public List<Shape<V>> getAllResults(){
    	return this.output;
    }
    
    public HashMap<String, DimValue> getAllLowercase(){
    	return lowercase;
    }
    
    public HashMap<String, Shape<V>> getAllUppercase(){
    	return uppercase;
    }
    
    public void addToVertcatExpr(DimValue i){
    	this.needForVertcat.add(i);
    }
    
    public ArrayList<DimValue> getOutputVertcatExpr(){
    	return needForVertcat;
    }
    
    public void addToOutput(Shape<V> value){
    	this.output.add(value);
    }
    
    public void copyVertcatToOutput(){
    	Shape<V> shape = new ShapeFactory<V>().newShapeFromDimValues(this.getOutputVertcatExpr());
    	if (Debug) System.out.println("inside copy vertcat to output "+needForVertcat);
    	this.addToOutput(shape);
    }
}
