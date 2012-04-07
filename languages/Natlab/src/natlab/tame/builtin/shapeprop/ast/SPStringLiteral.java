package natlab.tame.builtin.shapeprop.ast;

public class SPStringLiteral extends SPAbstractVertcatExprArg
{
	String id;
	public SPStringLiteral(String id)
	{
		this.id = id;
		//System.out.println(id);
	}
	
	public String toString()
	{
		return "'"+id.toString()+"'";
	}
}
