package fir.ast;

public class Assign extends Stmt {
	LValue lvalue;
	Expr expr;
	public Assign(LValue lvalue,Expr expression){
		this.lvalue = lvalue;
		this.expr = expression;
	}
	public LValue getVar(){ return lvalue;}
	public Expr getExpression(){ return expr;}
}
