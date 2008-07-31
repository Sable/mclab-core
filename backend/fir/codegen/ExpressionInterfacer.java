package fir.codegen;

import fir.ast.*;

/* this is a Code Interfacer that requires the insertion of an Expression */

public class ExpressionInterfacer extends CodeGenInterfacer {
	public boolean insertExpression(Expr expression){return false;}
	public boolean insertExpression(String text){return false;}
}
