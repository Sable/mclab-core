package fir.ast;

import fir.table.*;
import fir.type.Signature;

/* either a function or subroutine */

public abstract class Procedure implements ASTnode {
	Signature signature;
	Table table;
	Stmt body;
	String name;
	public Procedure(Signature signature,Table table,Stmt body,String name){
		this.signature = signature;
		this.table = table;
		this.body = body;
		this.name = name;
	}
}
