package natlab.tame.builtin.isComplexInfoProp;

import natlab.tame.valueanalysis.components.isComplex.HasisComplexInfo;
import natlab.tame.valueanalysis.value.Value;

public class ArgICType {

	public int getArgICType(Value<?> argument)
	{
		//return argument;
		if(null != ((HasisComplexInfo)argument).getisComplexInfo())
		{
		if(((HasisComplexInfo)argument).getisComplexInfo().toString().equals("COMPLEX"))
		{
			return -1;
		}
		else if(((HasisComplexInfo)argument).getisComplexInfo().toString().equals("REAL"))
		{
			return 1;
		}
		else if(((HasisComplexInfo)argument).getisComplexInfo().toString().equals("ANY"))
		{
			return 0;
		}
		}
		else
		{
		return 0;
		}
		return (Integer) null;
	}
	
}
