package natlab.toolkits.DependenceAnalysis;
import ast.PlusExpr;
import ast.MinusExpr;
import ast.NameExpr;
import ast.IntLiteralExpr;
import ast.Expr;

public class AffineExpression {
	
	private int C;
	private String key; //this serves as the key for the constraints graph.
	//private boolean isConstant; //to check if there is a constraint that has more than one variable.
	private String loopVariable; //to check which index variable 
	private Expr lowerBound;
	private Expr upperBound;
	private Expr indexExpr; //this is to handle variables in Affine Expression e.g. 2i+10.this variable would store 2i.
	
	public Expr getIndexExpr() {
		return indexExpr;
	}
	public void setIndexExpr(Expr indexExpr) {
		this.indexExpr = indexExpr;
	}
	public Expr getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(Expr lowerBound) {
		this.lowerBound = lowerBound;
	}
	public Expr getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(Expr upperBound) {
		this.upperBound = upperBound;
	}
	public String getLoopVariable() {
		return loopVariable;
	}
	public void setLoopVariable(String loopVariable) {
		this.loopVariable = loopVariable;
	}
	public int getC() {
		return C;
	}
	public void setC(int c) {
		C = c;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	
	

}

