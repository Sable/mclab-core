package natlab.tame.valueanalysis.components.isComplex;

import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.value.ValueFactory;

public class isComplexInfoFactory {

	public isComplexInfoFactory()
	{

	}
	public isComplexInfo newisComplexInfoFromConst(
			String isComplexInfo) {
		// TODO Auto-generated method stub
		return new isComplexInfo(isComplexInfo);
	}
	
	public isComplexInfo newisComplexInfoFromStr(
			String isComplexInfo) {
		// TODO Auto-generated method stub
		return new isComplexInfo(isComplexInfo);
	}
	
	public isComplexInfo getNullinfo() {
		return null;
	}
	
}
