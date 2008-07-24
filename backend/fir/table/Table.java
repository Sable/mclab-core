package fir.table;
import java.util.*;


//the table consists of a set a set of variables
//all internal variables are defined here, too
public class Table {
	HashSet<Variable> variables;	
	HashSet<InternalVar> internalVariables;
	
	public int getVarCount(){ return variables.size();}
	public Iterator<Variable> getVarIterator(){ return variables.iterator();}
	public void AddVar(Variable var){ variables.add(var);}
	public void removeVar(Variable var){ variables.remove(var);}
	
	public int getInternalVarCount(){ return variables.size();}
	public Iterator<InternalVar> getInternalVarIterator(){ return internalVariables.iterator();}
	public void AddInternalVar(InternalVar var){ internalVariables.add(var);}
	public void removeInternalVar(InternalVar var){ internalVariables.remove(var);}
}

