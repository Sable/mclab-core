package natlab.tame.builtin.shapeprop.ast;

public class SPAssignStmt extends SPAbstractPattern
{
	SPAbstractVertcatExprArg lhs;
	SPAbstractVertcatExprArg rhs;
	public SPAssignStmt(SPAbstractVertcatExprArg lhs, SPAbstractVertcatExprArg rhs)
	{
		this.lhs = lhs;
		this.rhs = rhs;
		System.out.println("=");
	}
	
	public String toString()
	{
		return lhs.toString()+"="+rhs.toString();
	}
}
