package natlab.tame.builtin.shapeprop.ast;

public class SPCaselist extends SPNode
{
	SPCase first;
	SPCaselist next;
	
	public SPCaselist(SPCase first, SPCaselist next)
	{
		this.first = first;
		this.next = next;
		/*if (next!=null)
		{
			System.out.println("||");
		}*/
	}
	
	public String toString()
	{
		return first.toString()+(next==null?"":"||"+next);
	}
}
