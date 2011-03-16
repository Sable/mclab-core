package natlab.Static.mc4.symbolTable;

public class Variable implements Symbol {

	
	/**
	 * constructor without argument creates symbol table entry referring to variable
	 * with Unknown Type
	 */
	public Variable(){
		
	}
	
	public String toString(){
		return "variable of type "+"unknown type"; //TODO
	}

	@Override
	public Symbol copy() {
		return new Variable();
	}
	
}
