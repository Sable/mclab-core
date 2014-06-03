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
	public isComplexInfo<V> newisComplexInfoFromConst(
			String isComplexInfo) {
		// TODO Auto-generated method stub
		return new isComplexInfo<V>(factory,isComplexInfo);
	}
	
	public isComplexInfo<V> newisComplexInfoFromStr(
			String isComplexInfo) {
		// TODO Auto-generated method stub
		return new isComplexInfo<V>(factory,isComplexInfo);
	}
	
	public isComplexInfo<V> getNullinfo() {
		return null;
	}
	
}
