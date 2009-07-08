package natlab.toolkits.DependenceAnalysis;
import natlab.ast.PlusExpr;
import natlab.ast.MinusExpr;
import natlab.ast.NameExpr;
import natlab.ast.IntLiteralExpr;

public class AffineExpression {
	
	private int C;
	private String variable;
	private boolean isConstant; //to check if there is a constraint that has more than one variable.
	private IntLiteralExpr lowerBound;
	private IntLiteralExpr upperBound;
	private PlusExpr pLBound;
	private PlusExpr pUBound;
	private NameExpr nLBound;
	private NameExpr nUBound;
	private MinusExpr mLBound;
	private MinusExpr mUBound;
	private String loopVariable;
	
	public String getLoopVariable() {
		return loopVariable;
	}
	public void setLoopVariable(String loopVariable) {
		this.loopVariable = loopVariable;
	}
	public PlusExpr getPLBound() {
		return pLBound;
	}
	public void setPLBound(PlusExpr bound) {
		pLBound = bound;
	}
	public PlusExpr getPUBound() {
		return pUBound;
	}
	public void setPUBound(PlusExpr bound) {
		pUBound = bound;
	}
	public NameExpr getNLBound() {
		return nLBound;
	}
	public void setNLBound(NameExpr bound) {
		nLBound = bound;
	}
	public NameExpr getNUBound() {
		return nUBound;
	}
	public void setNUBound(NameExpr bound) {
		nUBound = bound;
	}
	public MinusExpr getMLBound() {
		return mLBound;
	}
	public void setMLBound(MinusExpr bound) {
		mLBound = bound;
	}
	public MinusExpr getMUBound() {
		return mUBound;
	}
	public void setMUBound(MinusExpr bound) {
		mUBound = bound;
	}
	
	public IntLiteralExpr getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(IntLiteralExpr lowerBound) {
		this.lowerBound = lowerBound;
	}
	public IntLiteralExpr getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(IntLiteralExpr upperBound) {
		this.upperBound = upperBound;
	}
	public boolean getisConstant() 
	{
		return isConstant;
	}
	public void setisConstant(boolean isConst) {
		isConstant = isConst;
	}
	
	public int getC() {
		return C;
	}
	public void setC(int c) {
		C = c;
	}
	public String getVariable() {
		return variable;
	}
	public void setVariable(String variable) {
		this.variable = variable;
	}
	
	
	
	/*private class Expression{
		private float C;
		private String variable;		
	}*/	

}

