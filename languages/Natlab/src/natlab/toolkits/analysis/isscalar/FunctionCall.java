package natlab.toolkits.analysis.isscalar;


public class FunctionCall {
	String calledName;
	DataCollectFlowSet<String, IsScalarType> inset;
	
	public FunctionCall(String calledName,
			DataCollectFlowSet<String, IsScalarType> inset) {
		this.calledName = calledName;
		this.inset = inset;
	}
}
