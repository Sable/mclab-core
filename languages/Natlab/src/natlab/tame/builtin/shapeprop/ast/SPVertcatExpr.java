package natlab.tame.builtin.shapeprop.ast;

public class SPVertcatExpr extends SPAbstractVectorExpr
{
	SPVertExprArglist vl;
	public SPVertcatExpr(SPVertExprArglist vl)
	{
		this.vl = vl;
		System.out.println("[]");
	}
	
	public String toString()
	{
		return "["+vl.toString()+"]";
	}
}
