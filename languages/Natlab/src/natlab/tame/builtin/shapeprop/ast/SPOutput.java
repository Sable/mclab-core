package natlab.tame.builtin.shapeprop.ast;

public class SPOutput extends SPNode
{
	SPAbstractVectorExpr first;
	SPOutput next;
	
	public SPOutput(SPAbstractVectorExpr first, SPOutput next)
	{
		this.first = first;
		this.next = next;
		if (next!=null)
		{
			System.out.println(",");
		}
	}
	
	public SPOutput()
	{
		
	}
	
	public String toString()
	{
		return first.toString()+(next==null?"":","+next);
	}
}
