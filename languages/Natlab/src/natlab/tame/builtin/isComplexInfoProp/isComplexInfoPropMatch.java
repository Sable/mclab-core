package natlab.tame.builtin.isComplexInfoProp;

import java.util.*;
import java.util.Map.Entry;

import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.*;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.components.shape.*;

public class isComplexInfoPropMatch {

	int numMatched =0; /* number of matched arguments */
	int numEmittedResults = 0; /* number of emitted matched results */
	int numXargs = 0; /*number of matched complex args */
	int numRargs = 0;
	int numAargs = 0;
	
	boolean isError = false; 
	boolean lastMatchSucceed = false;
	boolean isMatchDone = false;
	boolean isOutputDone = false;
	
	String lastMatchICType;
	
	HashMap<String, String> output = new HashMap<String, String>();  //used for output results 
	/*TODO - change string to isComplex object defined in component class */
	
	
	public isComplexInfoPropMatch(){} //default constructor
	
	public isComplexInfoPropMatch(isComplexInfoPropMatch parent)  //copy parent
	{
		this.numMatched = parent.numMatched;
		this.numEmittedResults =  parent.numEmittedResults;
		this.numXargs = parent.numXargs;
		this.numRargs = parent.numRargs;
		this.numAargs = parent.numAargs;
		
		this.isError = parent.isError;
		this.isMatchDone = parent.isMatchDone;
		this.isOutputDone = parent.isOutputDone;
		this.output = parent.output;
	}
	
	//TODO Add more constructors as required
	
	public void setLastMatchSucceed(boolean success)
	{
		this.lastMatchSucceed = success;
	}
	
	public boolean getLastMatchSucceed()
	{
		return this.lastMatchSucceed;
	}
	
	public void consumeArg()
	{
		this.numMatched = 1 + this.numMatched;
	}
	
	public int getNumMatched()
	{
		return this.numMatched;
	}
	
	public int getNumXargs()
	{
		return this.numXargs;
	}
	
	public int getNumRargs()
	{
		return this.numRargs;
	}
	
	public int getNumAargs()
	{
		return this.numAargs;
	}
	
	public void setMatchIsDone()
	{
		this.isMatchDone = true;
	}
	
	public void setOutputIsDone()
	{
		this.isOutputDone = true;
	}
	
	public boolean getIsMatchDone()
	{
		return this.isMatchDone;
	}
	
	public boolean getIsOutputDone()
	{
		return this.isOutputDone;
	}
	
	public void incNumXargs(int howMany)
	{
		this.numXargs = howMany+this.numXargs;
	}
	
	
	public void incNumRargs(int howMany)
	{
		this.numRargs = howMany+this.numRargs;
	}
	
	public void incNumAargs(int howMany)
	{
		this.numAargs = howMany+this.numAargs;
	}
	
	public String getLastMatchICType()
	{
		return lastMatchICType;
	}
	
	public void setLastMatchICType(String ICType)
	{
		this.lastMatchICType = ICType;
	}
	
	//TODO - add getAllResults method
	//TODO - add more methods as required
	
}

