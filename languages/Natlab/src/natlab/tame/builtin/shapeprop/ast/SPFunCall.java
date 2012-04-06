package natlab.tame.builtin.shapeprop.ast;

public class SPFunCall extends SPAbstractVertcatExprArg
{
	String i;
	SPAbstractVertcatExprArg ls;
	public SPFunCall(String i, SPAbstractVertcatExprArg ls)
	{
		this.i = i;
		this.ls = ls;
		System.out.println("functionCall:"+i);
	}
	
	public String toString()
	{
		return i.toString()+"("+(ls==null?"":ls.toString())+")";
	}
}
