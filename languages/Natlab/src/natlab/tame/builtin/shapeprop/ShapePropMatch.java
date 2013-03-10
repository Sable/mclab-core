package natlab.tame.builtin.shapeprop;

import java.util.*;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class ShapePropMatch<V extends Value<V>> {
	
	static boolean Debug = false;
	public ShapeFactory<V> factory = new ShapeFactory<V>();
	// numMatched counts how many arguments are matched.
	int howManyMatched = 0;
	// howManyEmitted counts how many results are emitted.
	int howManyEmitted = 0;
	// index in vertcat;
	int numInVertcat = 0;
	int previousMatchedNumber = 0;
	// lowercase is used like m=previousScalar()
	HashMap<String, DimValue> lowercase = new HashMap<String, DimValue>();
	// mostly, uppercase is used for matching a shape
	HashMap<String, Shape<V>> uppercase = new HashMap<String, Shape<V>>();
	boolean whetherLatestMatchedIsNum = false;
	boolean ArrayIndexAssignLeft = false;
	boolean ArrayIndexAssignRight = false;
	boolean isError = false;
	boolean isMatchingDone = false;
	boolean isOutputDone = false;
	// isInsideVertcat is used for distinguish the lowercase in vertcat or not, like n=previousScalar() and [m,n]
	boolean isInsideVertcat = false;
	boolean isInsideAssign = false;
	String previousMatchedLowercase = "";
	String previousMatchedUppercase = "";
	ArrayList<DimValue> needForVertcat = new ArrayList<DimValue>();
	// used to store results
	List<Shape<V>> output = new ArrayList<Shape<V>>(); 
	
	/**
	 * default constructor.
	 */
	public ShapePropMatch() {}
	
	/**
	 * constructor for taking in another ShapePropMatch object.
	 * @param parent
	 */
	public ShapePropMatch(ShapePropMatch<V> parent) {
		this.factory = parent.factory;
        this.howManyMatched = parent.howManyMatched;
        this.howManyEmitted = parent.howManyEmitted;
        this.numInVertcat = parent.numInVertcat;
        this.previousMatchedNumber = parent.previousMatchedNumber;
        this.lowercase = parent.lowercase;
        this.uppercase = parent.uppercase;
        this.whetherLatestMatchedIsNum = parent.whetherLatestMatchedIsNum;
        this.ArrayIndexAssignLeft = parent.ArrayIndexAssignLeft;
        this.ArrayIndexAssignRight = parent.ArrayIndexAssignRight;
        this.isError = parent.isError;
        this.isMatchingDone = parent.isMatchingDone;
        this.isOutputDone = parent.isOutputDone;
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
	public ShapePropMatch(ShapePropMatch<V> parent, HashMap<String, DimValue> lowercase, HashMap<String, Shape<V>> uppercase) {
		this.factory = parent.factory;
		this.howManyMatched = parent.howManyMatched;
		this.howManyEmitted = parent.howManyEmitted;
		this.numInVertcat = parent.numInVertcat;
		this.previousMatchedNumber = parent.previousMatchedNumber;
		this.lowercase = parent.lowercase;
		this.uppercase = parent.uppercase;
		if (lowercase!=null) this.lowercase.putAll(lowercase);  //using HashMap putAll
		if (uppercase!=null) this.uppercase.putAll(uppercase);
		this.whetherLatestMatchedIsNum = parent.whetherLatestMatchedIsNum;
		this.ArrayIndexAssignLeft = parent.ArrayIndexAssignLeft;
		this.ArrayIndexAssignRight = parent.ArrayIndexAssignRight;
	    this.isError = parent.isError;
	    this.isMatchingDone = parent.isMatchingDone;
	    this.isOutputDone = parent.isOutputDone;
	    this.isInsideVertcat = parent.isInsideVertcat;
	    this.isInsideAssign = parent.isInsideAssign;
        this.previousMatchedLowercase = parent.previousMatchedLowercase;
        this.previousMatchedUppercase = parent.previousMatchedUppercase;
        this.needForVertcat = parent.needForVertcat;
        this.output = parent.output;
	}

    /**
     * matching successfully, increment the index pointing to the input arguments.
     */
    public void comsumeArg() {
    	howManyMatched += 1;
    }
    
    public int getHowManyMatched() {
    	return howManyMatched;
    }
    
    public void emitOneResult() {
    	howManyEmitted += 1;
    }
    
    public int getHowManyEmitted() {
    	return howManyEmitted;
    }
    
    public void setIsError(boolean condition) {
    	this.isError = condition;
    }
    
    public boolean getIsError() {
    	return this.isError;
    }
    
    public void setWhetherLatestMatchedIsNum(boolean condition) {
    	this.whetherLatestMatchedIsNum = condition;
    }
    
    public boolean getWhetherLatestMatchedIsNum() {
    	return this.whetherLatestMatchedIsNum;
    }
    
    public void setArrayIndexAssignLeft(boolean condition) {
    	this.ArrayIndexAssignLeft = condition;
    }
    
    public boolean getIsArrayIndexAssignLeft() {
    	return this.ArrayIndexAssignLeft;
    }
    
    public void setArrayIndexAssignRight(boolean condition) {
    	this.ArrayIndexAssignRight = condition;
    }
    
    public boolean getIsArrayIndexAssignRight(){
    	return this.ArrayIndexAssignRight;
    }
    
    public void saveLatestMatchedNumber(int latestMatchedNumber) {
    	this.previousMatchedNumber = latestMatchedNumber;
    }
    
    public int getLatestMatchedNumber(){
    	return previousMatchedNumber;
    }
    
    public void saveLatestMatchedLowercase(String latestMatchedLowercase) {
    	this.previousMatchedLowercase = latestMatchedLowercase;
    }
    
    public String getLatestMatchedLowercase() {
    	return this.previousMatchedLowercase;
    }
    
    public void saveLatestMatchedUppercase(String latestMatchedUpperCase) {
    	this.previousMatchedUppercase = latestMatchedUpperCase;
    }
    
    public String getLatestMatchedUppercase() {
    	return this.previousMatchedUppercase;
    }
    
    public int gethowManyEmitted() {
    	return howManyEmitted;
    }
    
    public void setNumInVertcat(int index) {
    	this.numInVertcat = index;
    }
    
    public int getNumInVertcat() {
    	return this.numInVertcat;
    }
    
    public void setIsMatchingDone() {
    	this.isMatchingDone = true;
    }
    
    public boolean getIsMatchingDone() {
    	return isMatchingDone;
    }
    
    public void setIsOutputDone() {
    	this.isOutputDone = true;
    }
    
    public boolean getIsoutputDone() {
    	return isOutputDone;
    }
    
    public void setIsInsideVertcat(boolean condition) {
    	this.isInsideVertcat = condition;
    }
    
    public boolean getIsInsideVertcat() {
    	return isInsideVertcat;
    }
    
    public void setIsInsideAssign(boolean condition) {
    	this.isInsideAssign = condition;
    }
    
    public boolean getIsInsideAssign() {
    	return isInsideAssign;
    }
    
    public boolean hasValue(String key) {
    	DimValue value = getValueOfVariable(key);
    	if (value.hasIntValue()||value.hasSymbolic()) return true;
    	return false;
    }
    
    public DimValue getValueOfVariable(String key) {
    	DimValue value = this.lowercase.get(key);
    	return value;
    }
    
    public Shape<V> getShapeOfVariable(String key) {
    	Shape<V> shape = this.uppercase.get(key);
    	return shape;
    }
    
    public HashMap<String, DimValue> getAllLowercase() {
    	return lowercase;
    }
    
    public HashMap<String, Shape<V>> getAllUppercase() {
    	return uppercase;
    }
    
    public void addToVertcatExpr(DimValue i) {
    	this.needForVertcat.add(i);
    }
    
    public ArrayList<DimValue> getOutputVertcatExpr() {
    	return needForVertcat;
    }
    
    public void copyVertcatToOutput() {
    	Shape<V> shape = new ShapeFactory<V>().newShapeFromDimValues(this.getOutputVertcatExpr());
    	if (Debug) System.out.println("inside copy vertcat to output "+needForVertcat);
    	addToOutput(shape);
    }
    
    public void addToOutput(Shape<V> value) {
    	this.output.add(value);
    }
    
    public List<Shape<V>> getAllResults() {
    	return this.output;
    }
}
