package natlab.tame.tamerplus.utils;

public class VariableMetadata {
	private Integer graphNumber;
	private String mclass;
	private String isComplex;
	public VariableMetadata(Integer graphNumber, String mclass){
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

}
