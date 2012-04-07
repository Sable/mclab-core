package natlab.tame.builtin.shapeprop.ast;

public class SPPlusFunCall extends SPAbstractVertcatExprArg
{
	String l;
	SPAbstractPattern f;
	public SPPlusFunCall(String l, SPAbstractPattern f)
	{
		this.l = l;
		this.f = f;
		//System.out.println("a plus of lowercase and fnCall");
	}
	
	public String toString()
	{
		return l.toString()+"+"+f.toString();
	}
}
