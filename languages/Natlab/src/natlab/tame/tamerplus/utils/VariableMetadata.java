package natlab.tame.tamerplus.utils;

import natlab.tame.tamerplus.analysis.UDDUWeb;
import natlab.tame.tir.TIRNode;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;

public class VariableMetadata {
	private Integer graphNumber;
	private String mclass;
	private String isComplex;
	public VariableMetadata(Integer graphNumber, String mclass, String isComplex){
		this.graphNumber = graphNumber;
		this.mclass = mclass;
		this.isComplex = isComplex;
	}
	
	public Integer getGraphNumber(){
		return this.graphNumber;
	}
	
	public String getMclass(){
		return this.mclass;
	}
	
	public String getIsComplex(){
		return this.isComplex;
	}
	
	public static VariableMetadata getVariableMetadata(
			ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis,
			TIRNode statement, int graphIndex, String var, UDDUWeb web) {

		if (analysis.getNodeList().get(graphIndex).getAnalysis()
				.getOutFlowSets().get(statement).isViable()) {

			AdvancedMatrixValue temp = ((AdvancedMatrixValue) (analysis
					.getNodeList().get(graphIndex).getAnalysis()
					.getOutFlowSets().get(statement).get(var).getSingleton()));
			return new VariableMetadata(web.getNodeAndColorForDefinition(var)
					.get(statement), temp.getMatlabClass().getName(), temp
					.getisComplexInfo().toString());

		}

		return null;
	}
}
