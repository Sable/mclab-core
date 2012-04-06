package natlab.tame.builtin.shapeprop.ast;

public class SPUpperIndex extends SPAbstractVertcatExprArg
{
	String s;
	SPAbstractScalarExpr n;
	public SPUpperIndex(String s, SPAbstractScalarExpr n)
	{
		this.s = s;
		this.n = n;
		System.out.println(s+"()");
	}
	
	public String toString()
	{
		return s.toString()+"("+n.toString()+")";
	}
}
