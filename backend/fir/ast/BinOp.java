package fir.ast;

import fir.type.Type;

public class BinOp extends Operation {
	BinaryOperator operator;
	Expr left,right;
	
	BinOp(Expr left,BinaryOperator operator,Expr right){
		this.operator = operator;
		this.left = left;
		this.right = right;
	}
	
	
	
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
