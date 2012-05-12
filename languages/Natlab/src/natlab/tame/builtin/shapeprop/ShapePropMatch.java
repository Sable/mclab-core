package natlab.tame.builtin.shapeprop;

import java.util.*;
import java.util.Map.Entry;

import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.*;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.components.shape.*;

public class ShapePropMatch{
	public BasicMatrixValueFactory factory = new BasicMatrixValueFactory();
	int numMatched = 0;             //number of matched arguments, act as the index of arguments 
	int numEmittedResults = 0;      //number of emitted results, I cannot say its index of shape equation,
	                                //because there is also non-matching expression in the language.
	int numInVertcat = 0;           //index in vertcat;
	int previousMatchedNumber = 0;
	HashMap<String, Integer> lowercase = new HashMap<String, Integer>();  //lowercase is used like m=prevScalar()
	HashMap<String, Shape<?>> uppercase = new HashMap<String, Shape<?>>();  //mostly, uppercase is used for matching a shape
	boolean ArrayIndexAssign = false;   //used for M(2)=0 kind of case, so everytime, in number node, we should check whether or not in a arrayIndex assignment, kind of tricky?
	boolean isError = false;
	boolean matchingIsDone = false;
	boolean outputIsDone = false;
	boolean isInsideVertcat = false;//this boolean is used for distinguish the lowercase in vertcat or not, like n=previousScalar() and [m,n]
	boolean isInsideAssign = false;
	String previousMatchedLowercase = null;
	String previousMatchedUppercase = null;
	ArrayList<Integer> needForVertcat = new ArrayList<Integer>();
	HashMap<String, Shape<?>> output = new HashMap<String, Shape<?>>();  //used for output results 
	
	
	public ShapePropMatch(){}
	
	/**
	 * constructor referring to parent
	 * @param parent
	 */
	public ShapePropMatch(ShapePropMatch parent){
		this.factory = parent.factory;
        this.numMatched = parent.numMatched;
        this.numEmittedResults = parent.numEmittedResults;
        this.numInVertcat = parent.numInVertcat;
        this.previousMatchedNumber = parent.previousMatchedNumber;
        this.lowercase = parent.lowercase;
        this.uppercase = parent.uppercase;
        this.ArrayIndexAssign = parent.ArrayIndexAssign;
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
	public ShapePropMatch(ShapePropMatch parent, HashMap<String, Integer> lowercase, HashMap<String, Shape<?>> uppercase){
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
		this.ArrayIndexAssign = parent.ArrayIndexAssign;
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
    
    public boolean getIsError(){
    	return this.isError;
    }
    
    public void setArrayIndexAssign(boolean condition){
    	this.ArrayIndexAssign = condition;
    }
    
    public boolean getArrayIndexAssign(){
    	return this.ArrayIndexAssign;
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
    
    public int getValueOfVariable(String key){
    	int value = this.lowercase.get(key);
    	return value;
    }
    
    public Shape<?> getShapeOfVariable(String key){
    	Shape<?> shape = this.uppercase.get(key);
    	return shape;
    }
    
    public List<Shape<AggrValue<BasicMatrixValue>>> getAllResults(){//FIXME better!
    	LinkedList<Shape<AggrValue<BasicMatrixValue>>> results = new LinkedList<Shape<AggrValue<BasicMatrixValue>>>();
    	System.out.println(output);
    	System.out.println(needForVertcat);
    	for(Object value: output.values()){
    		results.add((Shape<AggrValue<BasicMatrixValue>>)value);    		
    	}
    	return results;
    }
    
    public HashMap<String, Integer> getAllLowercase(){
    	return lowercase;
    }
    
    public HashMap<String, Shape<?>> getAllUppercase(){
    	return uppercase;
    }
    
    public void addToVertcatExpr(Integer i){
    	this.needForVertcat.add(i);
    }
    
    public ArrayList<Integer> getOutputVertcatExpr(){
    	return needForVertcat;
    }
    
    public void addToOutput(String s,Shape<?> value){
    	this.output.put(s, value);
    }
    
    public Shape<?> getOutput(String key){
    	return output.get(key);
    }
    
    public void copyVertcatToOutput(String defaultM){
    	Shape<?> shape = (new ShapeFactory<AggrValue<BasicMatrixValue>>(factory)).newShapeFromIntegers(this.getOutputVertcatExpr());
    	System.out.println("inside copy vertcat to output "+needForVertcat);
    	this.addToOutput(defaultM, shape);
    }
    
/*    @Override
    public String toString() {
        return "machresult-"+numMatched+"-"+getAllResults();
    }*/

}
