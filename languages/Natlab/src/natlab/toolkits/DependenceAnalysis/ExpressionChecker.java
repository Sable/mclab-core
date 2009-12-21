package natlab.toolkits.DependenceAnalysis;
import ast.*;
public class ExpressionChecker {
	public Expr checkRangeExpression(Expr expr)
	{
		if(expr instanceof RangeExpr)
		{
			RangeExpr rExpr=(RangeExpr)expr;			
			return rExpr.getLower();
		}
		else return null;
			
	}

}
