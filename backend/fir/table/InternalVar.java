package fir.table;

import fir.type.*;
import fir.codegen.*;
/* an internal variable represents a fortran variable, with its attributes 
 * an internal var has to be associated with a table */

public class InternalVar implements CodeComponent {
	InternalType type;
	String name; //does not have to be a uid
	Table table; //has to be in this table
	/** construct *************************************************************/
	public InternalVar(String name,InternalType type,Table table){
		this.type = type;
		this.name = name;
		this.table = table;
	}
	//this var will return
	public InternalVar(String name,Type type,Table table){
		//TODO
		this.table = table;
	}
	
	/** query *********************************************************/
	public InternalType getInternalType(){
		return type;
	}
	
}
