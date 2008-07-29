package fir.table;

import java.util.*;

import fir.ast.*;
import fir.type.*;

//a variable has a type, name, and a mapping to internal variables
//a variable has to be in not more than one table
//the internal variables are
// - data: the variable where the actual data is stored (i.e. the array) - multiple for shadow variables
// - rank: the variable where the rank is stored
// - dim: the size of a dimension

//TODO -- add implementation for union types/shadow variables

public class Variable implements LValue {
	String name;
	Type type;
	Table table;
	InternalVar data; //at the moment no shadow variables supported
	InternalVar rank;
	InternalVar shape;
	
	
	public Type getType(){return type;}
	public String getName(){return name;}
	/* query *******************************************************/
	public boolean hasUnionType(){
		return false;
	}
	
	public InternalVar getData(){ //returns an internal data variable
		return data;
	}
	public Vector<InternalVar> getAllData(){ //returns all internal data variables
		Vector<InternalVar> list = new Vector<InternalVar>();
		list.add(data);
		return list;
	}
	
	/* create ******************************************************/
	//create Variable - creates a new internalVariable for any unknown component
	//in the type within the given table
	//if used, this may create many superflous variables (because no two variables
	//can share rank-,shape-references), but it is a quick and dirty way to create them
	public Variable(Type type,String name,Table table){
		this.table = table;
		if (type.isUnionType()){
			System.err.println("union type variables not supported.");
		} else {
			//create data variable
			
			
		}
	}
	
	//creates Variable - takes the data, rank and shape variables and uses them to build the
	//variables - note that the rank/shape components can be null if it is given in the type,
	//or not given at all (i.e. never used).
	public Variable(Type type,String name,Table table,InternalVar dataRef,InternalVar rankRef,InternalVar[] shapeRef){
		this.table = table;
		//TODO
	}
}



