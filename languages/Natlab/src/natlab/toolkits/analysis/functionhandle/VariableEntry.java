package natlab.toolkits.analysis.functionhandle;

import java.util.HashSet;


/**
 * stores variable name and known value in the lattice
 *       
 *          value or function
 *             /          \  
 *        value            function
 *             \          /     
 *              unassigned
 */

public class VariableEntry {
	boolean assignedValue=false;
	boolean assignedFunction=false;
	String name;
	HashSet<FunctionReference> references = new HashSet<FunctionReference>();

	/**
	 * creates an unassigned variable
	 */
	public VariableEntry(String name){
		this.name = name;
	}
	
	/**
	 * creates a variable which is assinged a reference
	 */
	public VariableEntry(String name,FunctionReference ref){
		this.name = name;
		references.add(ref);	
		assignedFunction = true;
	}
	
	
	/**
	 * creates a variable that is either a value or unassigned, based on te argument
	 */
	public VariableEntry(String name,boolean assignedValue){
		this.name = name;
		this.assignedValue = assignedValue;
	}

	
	/**
	 * creates a variable to which is assigned whatever value the given variable has
	 */
	public VariableEntry(String name,VariableEntry other){
		this.name = name;
		this.assignedFunction = other.assignedFunction;
		this.assignedValue = other.assignedValue;
		references.addAll(other.references);
	}
	
	/**
	 * returns the union of this and the argument in a new reference
	 * the name is taken from 'this'
	 */
	public VariableEntry union(VariableEntry other){
		VariableEntry ret = new VariableEntry("");
		ret.name=this.name;
		ret.assignedFunction = assignedFunction || other.assignedFunction;
		ret.assignedValue = assignedValue || other.assignedValue;
		ret.references.addAll(references);
		ret.references.addAll(other.references);
		return ret;
	}
	
	
	@Override
	/**
	 * equality of the object is just the equivalence of the name
	 * ==> this is bad, but makes all the has set stuff easier
	 */	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof VariableEntry){
			return ((VariableEntry)obj).name.equals(name);
		} else return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	
	@Override
	public String toString() {
		String s=name+"={";
		if(assignedValue){
			s+="<value>"+(assignedValue?",":"");			
		}
		if(assignedFunction){
			for (FunctionReference fn : references){
				s+=fn+",";
			}
			s=s.substring(0,s.length()-1);			
		}
		return s+"}";
	}
	
}
