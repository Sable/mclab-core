package natlab.toolkits.DependenceAnalysis;

import ast.List;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Expr;

public class ExpressionFactory {
	/*
	 * This function creates a NameExpr with the name mentioned in arguments
	 */
	
	public NameExpr createNameExpr(String sName)
	{
		NameExpr nExpr=new NameExpr();
		Name name=new Name();
		name.setID(sName);
		nExpr.setName(name);
		return nExpr;
	}
	/*
	 * This function returns a parameterized expression with following parameters.
	 * 1.Expr node
	 * 2.List 
	 */
    public ParameterizedExpr createParaExpr(Expr expr,List list)
    {
      ParameterizedExpr paraExpr=new ParameterizedExpr();  	  	  
  	  paraExpr.setTarget(expr);  	  
  	  paraExpr.setArgList(list);
  	  return paraExpr;
    }
    /*
	 * This function returns a parameterized expression with following parameters.
	 * 1.Expr node
	 * 2.index
	 */
    public ParameterizedExpr createParaExpr(Expr targetExpr,Expr argExpr,int index)
    {
      ParameterizedExpr paraExpr=new ParameterizedExpr();  	  	  
  	  paraExpr.setTarget(targetExpr);  	  
  	  paraExpr.setArg(argExpr, index);
  	  return paraExpr;
    }

}
