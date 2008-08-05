package fir.ast;

import java.util.*;
import fir.table.*;

public class Program implements ASTnode {
	HashSet<Procedure> procedures;
	Procedure main;
	Table globals;
	
	public Program(Procedure main,Table globals){
		this.main = main;
		this.globals = globals;
		procedures = new HashSet<Procedure>();
		procedures.add(main);
	}
	
	public void addProcedure(Procedure procedure){
		procedures.add(procedure);
	}
	public void removeProcedure(Procedure procedure){
		procedures.remove(procedure);
	}
	public Iterator<Procedure> getProcedures(Procedure procedure){
		return procedures.iterator();
	}
	
	public Procedure getMain(){ return main;}
	public void setMain(Procedure main){ this.main = main;}
	public Table getGlobals(){return globals;}
}


