package natlab.tame.builtin.shapeprop;

import java.util.*;

import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.basicmatrix.*;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.components.shape.Shape;;

public class ShapePropMatch {
	int numMatched = 0;             //number of matched arguments, act as the index of arguments 
	int numEmittedResults = 0;      //number of emitted results, I cannot say its index of shape equation,
	                                //because there is also non-matching expression in the language.
	HashMap<String, Integer> lowerCase = new HashMap<String, Integer>();  //lowerCase is used like m=prevScalar()
	HashMap<String, List<Integer>> upperCase = new HashMap<String, List<Integer>>();  //mostly, upperCase is used for matching a shape
	boolean isError = false;
	boolean matchingIsDone = false;
	boolean outputIsDone = false;
	String previousMatchedLowerCase = null;
	String previousMatchedUpperCase = null;
	ArrayList<Integer> needForVertcat = new ArrayList<Integer>();
	
	
	public ShapePropMatch(){}
	
	/**
	 * constructor referring to parent
	 * @param parent
	 */
	public ShapePropMatch(ShapePropMatch parent){
        this.numMatched = parent.numMatched;
        this.numEmittedResults = parent.numEmittedResults;
        this.lowerCase = parent.lowerCase;
        this.upperCase = parent.upperCase;
        this.isError = parent.isError;
        this.matchingIsDone = parent.matchingIsDone;
        this.outputIsDone = parent.outputIsDone;
        this.previousMatchedLowerCase = parent.previousMatchedLowerCase;
        this.previousMatchedUpperCase = parent.previousMatchedUpperCase;
        this.needForVertcat = parent.needForVertcat;
	}
	
	/**
	 * add new lowerCase or upperCase into current result
	 */
	public ShapePropMatch(ShapePropMatch parent, HashMap<String, Integer> lowerCase, HashMap<String, List<Integer>> upperCase){
		this.numMatched = parent.numMatched;
		this.numEmittedResults = parent.numEmittedResults;
		this.lowerCase = parent.lowerCase;
		this.upperCase = parent.upperCase;
		if(lowerCase!=null){
			this.lowerCase.putAll(lowerCase);  //using HashMap putAll
		}
		if(upperCase!=null){
			this.upperCase.putAll(upperCase);
		}
	    this.isError = parent.isError;
	    this.matchingIsDone = parent.matchingIsDone;
	    this.outputIsDone = parent.outputIsDone;
        this.previousMatchedLowerCase = parent.previousMatchedLowerCase;
        this.previousMatchedUpperCase = parent.previousMatchedUpperCase;
        this.needForVertcat = parent.needForVertcat;
	}

    /**
     * returns a ShapePropMatch which advances argIndex by one, and refers back to this
     */
    public void comsumeArg(){
    	this.numMatched = this.numMatched+1;
    }
    
    public void saveLatestMatchedLowerCase(String latestMatchedLowerCase){
    	this.previousMatchedLowerCase = latestMatchedLowerCase;
    }
    
    public String getLatestMatchedLowerCase(){
    	return this.previousMatchedLowerCase;
    }
    
    public void saveLatestMatchedUpperCase(String latestMatchedUpperCase){
    	this.previousMatchedUpperCase = latestMatchedUpperCase;
    }
    
    public String getLatestMatchedUpperCase(){
    	return this.previousMatchedUpperCase;
    }
    
    public int getNumMatched(){
    	return numMatched;
    }
    
    public int getNumEmittedResults(){
    	return numEmittedResults;
    }
    
    public void setMatchingIsDone(){
    	this.matchingIsDone = true;
    }
    
    public void setOutputIsDone(){
    	this.outputIsDone = true;
    }
    
    public boolean matchingIsDone(){
    	return matchingIsDone;
    }
    
    public boolean outputIsDone(){
    	return outputIsDone;
    }
    
    public int getValueOfVariable(String key){
    	int value = this.lowerCase.get(key);
    	return value;
    }

    public HashMap<String, List<Integer>> getAllResults(){
    	return upperCase;
    }
    
    public HashMap<String, Integer> getAllLowerCase(){
    	return lowerCase;
    }
    
    public void addToVertcatExpr(Integer i){
    	this.needForVertcat.add(i);
    }
    
    public ArrayList<Integer> printVertcatExpr(){
    	return needForVertcat;
    }
/*    @Override
    public String toString() {
        return "machresult-"+numMatched+"-"+getAllResults();
    }*/

}
