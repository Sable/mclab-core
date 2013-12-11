package natlab.tame.tamerplus.utils;

import natlab.tame.tir.TIRNode;

public class VarIsIntOk {
	private String varName;
	private Integer colour;
	private TIRNode node;
	private IntOk isIntOk ;
	
	
	public void setVarName(String varName){
		this.varName = varName;
	}
	
	public void setColour (Integer colour){
		this.colour = colour;
	}
	
	public void setNode (TIRNode node){
		this.node = node;
	}
	
	public void setIsIntOk (IntOk isIntOk){
		this.isIntOk = isIntOk;
	}
	
	public String getVarName(){
		return this.varName;
	}
	
	public Integer getColour (){
		return this.colour;
	}
	
	public TIRNode getNode (){
		return this.node;
	}
	
	public IntOk getIsIntOk (){
		return this.isIntOk;
	}
}
