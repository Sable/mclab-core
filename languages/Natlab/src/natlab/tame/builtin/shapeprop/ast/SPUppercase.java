package natlab.tame.builtin.shapeprop.ast;

public class SPUppercase extends SPAbstractVectorExpr
{
	String s;
	public SPUppercase (String s)
	{
		this.s = s;
		//System.out.println(s);
	}
	
	public String toString()
	{
		return s.toString();
	}
}
