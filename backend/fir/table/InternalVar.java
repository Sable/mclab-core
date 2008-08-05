package fir.table;

import fir.type.*;
import fir.codegen.*;
/* an internal variable represents a fortran variable, with its attributes 
 * an internal var has to be associated with a table */

public class InternalVar extends InternalLValue {
	String name; //does not have to be a uid
	Table table; //has to be in this table
	/** construct *************************************************************/
	public InternalVar(String name,InternalType type,Table table){
		super(type);
		this.name = name;
		this.table = table;
	}
	//this var will return
	public InternalVar(String name,Type type,Table table){
		//TODO
		super(null);
		this.table = table;
	}
	
	/** query *********************************************************/

	
}
