package natlab.tame.valueanalysis.components.isComplex;

import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.value.ValueFactory;

public class isComplexInfoFactory<V extends Value<V>> {
	ValueFactory<V> factory;
	
	public isComplexInfoFactory()
	{
		
	}
	public isComplexInfoFactory(ValueFactory<V> factory)
	{
		this.factory = factory;
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
