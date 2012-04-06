package natlab.tame.builtin.shapeprop.ast;

public class SPCase extends SPNode
{
	SPAbstractPattern first;
	SPOutput next;
	
	public SPCase(SPAbstractPattern p, SPOutput o)
	{
		this.first = p;
		System.out.println("->");
		this.next = o;
	}
	
	public String toString()
	{
		return first.toString()+"->"+next.toString();
	}
}