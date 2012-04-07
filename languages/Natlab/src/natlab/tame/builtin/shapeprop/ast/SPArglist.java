package natlab.tame.builtin.shapeprop.ast;

public class SPArglist extends SPAbstractVertcatExprArg
{
	SPAbstractVertcatExprArg first;
	SPAbstractVertcatExprArg next;
	public SPArglist(SPAbstractVertcatExprArg first, SPAbstractVertcatExprArg next)
	{
		this.first = first;
		this.next = next;
		//System.out.println(",");
	}
	
	public String toString()
	{
		return first.toString()+(next==null?"":","+next);
	}
}
