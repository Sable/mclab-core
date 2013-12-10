package natlab.tame.tamerplus.utils;

import java.util.ArrayList;

public class IntOk {
	private Boolean isInt;
	private Boolean depends;
	
	private ArrayList<String> dependsOn;
	
	
	public IntOk(Boolean isint, Boolean depends, ArrayList<String> dependson){
		this.isInt = isint;
		this.depends = depends;
		this.dependsOn = dependson;
	}
	
	public void setIsInt(Boolean isint){
		this.isInt = isint;
	}
	
	public void setDepends (Boolean depends){
		this.depends = depends;
	}
	
	public void setDependsOn (ArrayList<String> dependson){
		this.dependsOn = dependson;
	}
	
	public Boolean getIsInt(){
		return this.isInt;
	}
	
	public Boolean getDepends (){
		return this.depends;
	}
	
	public ArrayList<String> getDependsOn (){
		return this.dependsOn;
	}
}
