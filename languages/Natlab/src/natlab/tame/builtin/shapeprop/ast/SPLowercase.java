package natlab.tame.builtin.shapeprop.ast;

public class SPLowercase extends SPAbstractScalarExpr
{
	String s;
	public SPLowercase(String s)
	{
		this.s = s;
		//System.out.println(s);
	}
	
	public String toString()
	{
		return s.toString();
	}
}
