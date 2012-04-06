package natlab.tame.builtin.shapeprop.ast;

public class SPOr extends SPAbstractMatchExpr
{
	SPAbstractMatchExpr first;
	SPAbstractMatchExpr next;
	
	public SPOr (SPAbstractMatchExpr first,SPAbstractMatchExpr next)
	{
		this.first = first;
		this.next = next;
		System.out.println("|");
	}
	
	public String toString()
	{
		return first.toString()+"|"+next.toString();
	}
}
