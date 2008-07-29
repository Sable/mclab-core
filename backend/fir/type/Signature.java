package fir.type;
import java.util.*;

import fir.table.InternalVar;
import fir.table.*;
import fir.type.*;

/* sets signature for function or procedure
 * 
 * - all arguments
 * - types
 * - rank/shape for partially known types
 * TODO :
 * - shadows
 * - allocatable,pointer,target,in,out,inout,changeable(value,shape,type)
 * 
 * the signature represents how data is passed to a function/subroutine.
 * inputVars and outputVars are ordered like inputTypes and outputTypes of SiginatureType, respectively
 * internalVars denote the internal (i.e. fortran) ordering of the variables.
 * 
 * the mapping should be in the Signature itself, no extra map needed
 * a function needs at least one return, otherwise there will be errors.
 * 
 */

public class Signature {
	Vector<Type> inputVars;
	Vector<Type> outputVars;
	Vector<InternalType> internalVars;
	InternalType internalReturn; //may be null - internal
	boolean isFunction;
	/* construction of signature ****************************************/
	//constructors - will not add any mapping
	public Signature(Vector<Type> inputVars,Vector<Type> outputVars,Vector<InternalType> internalVars){
		this.inputVars = new Vector<Type>(inputVars);
		this.outputVars = new Vector<Type>(outputVars);
		this.internalVars = new Vector<InternalType>(internalVars);
	}
	
	//returns signature as it is defined by the given variables
	//the ordering of the internal variables is such that the mapping is canonical
	public Signature(Vector<Variable> inputs,Vector<Variable> outputs,boolean isFunction){
		//TODO - make work with union types
		inputVars = new Vector<Type>();
		outputVars = new Vector<Type>();
		//put types
		for (Variable v:inputs){
			inputVars.add(v.getType());
		}
		for (Variable v:outputs){
			outputVars.add(v.getType());
		}
		this.isFunction = isFunction;
		//add mapping -- first add data info -- first list all actual variables
		Vector<InternalVar> internalActualVars = new Vector<InternalVar>();
		InternalVar actualReturn = null;
		//go through all inputs
		for (Variable v:inputs){
			if (!internalActualVars.contains(v.getData())){
				internalActualVars.add(v.getData()); //add the internal only if not there already
			}
		}
		//if we have a function, we put the the first return as the actual return
		if (isFunction){
			actualReturn = outputs.firstElement().getData();
		} else {
			if (outputs.size()>0){
				if (!internalActualVars.contains(outputs.firstElement().getData())){
					internalActualVars.add(outputs.firstElement().getData());
				}
			}
		}
		//go through all remaining output and insert
		for (int i = 1;i<outputs.size();i++){
			if (!internalActualVars.contains(outputs.firstElement().getData())){
				internalActualVars.add(outputs.firstElement().getData());
			}
		}
		//go through input again, now add all rank, shape variables
		for (Variable v:inputs){
			if (!v.getType().isFixedRank()){
				
				
			}
			
		}
		
	}
	
	//copy constructor
	public Signature(Signature anotherSignature){
		//TODO
	}
	
	//change/make mapping
	
	//make mapping canonical
	//this changes the ordering of the internal variables, so that given
	//the same input/ouput types, and the same map from types to internal types,
	//will result in 'equal' returning true
	//the ordering of internal variables gets changed!
	//returns true if the mapping was changed.
	public boolean makeCanonical(){
		//TODO
		return false;
	}
	
	/* query **********************************************************/
	public boolean isFunction(){return isFunction;};
	
	//is canonical
	public boolean isCanonical(){
		return !(new Signature(this)).makeCanonical();		
	}
	
	
	
}


