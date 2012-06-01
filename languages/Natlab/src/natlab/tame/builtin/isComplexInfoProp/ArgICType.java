package natlab.tame.builtin.isComplexInfoProp;

import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.advancedMatrix.*;

public class ArgICType {

	public int getArgICType(Value<?> argument)
	{
		//return argument;
		if(null != ((AdvancedMatrixValue)argument).getisComplexInfo())
		{
		if(((AdvancedMatrixValue)argument).getisComplexInfo().toString().equals("COMPLEX"))
		{
			return -1;
		}
		else if(((AdvancedMatrixValue)argument).getisComplexInfo().toString().equals("REAL"))
		{
			return 1;
		}
		else if(((AdvancedMatrixValue)argument).getisComplexInfo().toString().equals("ANY"))
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
