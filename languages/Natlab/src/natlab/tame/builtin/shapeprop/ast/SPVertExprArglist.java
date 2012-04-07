package natlab.tame.builtin.shapeprop.ast;

public class SPVertExprArglist extends SPNode
{
	SPAbstractVertcatExprArg first;
	SPVertExprArglist next;
	public SPVertExprArglist (SPAbstractVertcatExprArg first, SPVertExprArglist next)
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
