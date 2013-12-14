package natlab.tame.builtin.isComplexInfoProp;

import java.util.*;
import java.util.Map.Entry;

import natlab.tame.classes.reference.ClassReference;
//import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
//import natlab.tame.valueanalysis.basicmatrix.*;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfo;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfoFactory;
import natlab.tame.valueanalysis.components.shape.*;

public class isComplexInfoPropMatch {

	int numMatched =0; /* number of matched arguments */
	int numEmittedResults = 0; /* number of emitted matched results */
	int numXargs = 0; /*number of matched complex args */
	int numRargs = 0;
	int numAargs = 0;
	
	boolean isError = false; 
	boolean lastMatchSucceed = true;
	boolean isMatchDone = false;
	boolean isOutputDone = false;
	
	String lastMatchICType;
	
	List<isComplexInfo<?>> output = new ArrayList<isComplexInfo<?>>();  //used for output results 
	/*TODO - change string to isComplex object defined in component class */
	
//	HashMap<String, isComplexInfo<?>> output = new HashMap<String, isComplexInfo<?>>(); 
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
	
	public String getOutputString()
	{
		String buff = "";
		
		    for (int i=0; i<this.output.size();i++)
			buff = buff+this.output.get(i);
			return buff;
	}
	
	//TODO Add more constructors as required
	
	public void loadOutput(isComplexInfo<?> op)
	{
		this.output.add(op);
	}
	public void loadOutput(String string) {
		
		isComplexInfoFactory fac = new isComplexInfoFactory();
		loadOutput(fac.newisComplexInfoFromStr(string));
		
	}
	
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
	
	public void setError(boolean er)
	{
		this.isError = er;
	}
	public boolean getError()
	{
		return this.isError ;
	}
	
	@SuppressWarnings("unchecked")
	public List<isComplexInfo<?>> getAllResults(){//FIXME better!
    	List<isComplexInfo<?>> results = new LinkedList<isComplexInfo<?>>();
    	// System.out.println(output);
    	
    	for(Object value: output){
    		results.add((isComplexInfo<? extends Value<?>>)(value));    		
    	}
    	return results;
    }
	//TODO - add more methods as required

	
	
}

