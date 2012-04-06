package natlab.tame.builtin.shapeprop.ast;

public class SPNumber extends SPAbstractScalarExpr
{
	Number n;
	public SPNumber (Number n)
	{
		this.n = n;
		System.out.println(n.toString());
	}
	
	public String toString()
	{
		return n.toString();
	}
}
