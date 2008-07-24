package fir.table;

import fir.ast.*;
import fir.type.*;

//a variable has a type, and vardata
//a variable has to be in not more than one table
//a variable can be in multiple signatures
public class Variable implements LValue {
	String name;
	Type type;
	VarData data;
	
	public Type getType(){return type;}
	public VarData getVarData(){return data;}
	public String getName(){return name;}
}
