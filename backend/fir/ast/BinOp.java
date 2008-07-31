package fir.ast;

import fir.type.Type;
//done4now
public class BinOp extends Operation {
	BinaryOperator operator;
	Expr left,right;
	Type type;
	
	BinOp(Expr left,BinaryOperator operator,Expr right){
		this.operator = operator;
		this.left = left;
		this.right = right;
		this.type = operator.propagate(left.getType(), right.getType());
	}
	
	
	public Type getType() {
		return type;
	}
	public BinaryOperator getOperator(){return operator;}
	public Expr getLeft(){return left;}
	public Expr getRight(){return right;}
}
