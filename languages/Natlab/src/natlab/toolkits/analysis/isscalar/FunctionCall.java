package natlab.toolkits.analysis.isscalar;

import natlab.toolkits.analysis.varorfun.DataCollectFlowSet;

public class FunctionCall {
	String calledName;
	DataCollectFlowSet<String, IsScalarType> inset;
	
	public FunctionCall(String calledName,
			DataCollectFlowSet<String, IsScalarType> inset) {
		this.calledName = calledName;
		this.inset = inset;
	}
}
