package fir.ast;

import fir.builtin.Builtin;
import fir.table.Variable;
import fir.type.*;

/* will call a function, either a Builtin or a Function */

public class Invoke extends Expr {
	boolean callsBuiltin;
	Function function;
	Builtin builtin;
	Variable[] arguments;
		
	Invoke(Function function,Variable[] arguments){
		callsBuiltin = false;
		this.function = function;
		this.builtin = null;
		this.arguments = arguments;
	}
	Invoke(Builtin function,Variable[] arguments){
		callsBuiltin = true;
		this.function = null;
		this.builtin = function;
		this.arguments = arguments;
	}
	
	public Type getType(){
		if (callsBuiltin){
			Type[] inputTypes = new Type[arguments.length];
			for (int i=0;i<arguments.length;i++){
				inputTypes[i] = arguments[i].getType();
			}
			return builtin.getReturnType(inputTypes);
		} else {
			return function.getReturnType();			
		}
	}
}
