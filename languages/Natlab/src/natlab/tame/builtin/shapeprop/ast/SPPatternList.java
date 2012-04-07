package natlab.tame.builtin.shapeprop.ast;

public class SPPatternList extends SPAbstractPattern
{
	SPAbstractPattern first;
	SPAbstractPattern next;
	
	public SPPatternList (SPAbstractPattern first, SPAbstractPattern next)
	{
		this.first = first;
		this.next = next;
		/*if (next!=null)
		{
			System.out.println(",");
		}*/
	}
	
	public String toString()
	{
		return first.toString()+(next==null?"":","+next);
	}
}
