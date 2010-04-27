package natlab.toolkits.DependenceAnalysis;
/*
 * This class contains NestedLoopData which is then written to dependenceFile.xml
 * DateCreated:Mar31,2010
 * Author:Amina Aslam 
 */

public class NestedLoop extends Data{
	private float loopNo;
	private int startRange;
	private int endRange;
	private String lVarName;
	private char dependence;
	
	public char getDependence() {
		return dependence;
	}
	public void setDependence(char dependence) {
		this.dependence = dependence;
	}
	public String getLVarName() {
		return lVarName;
	}
	public void setLVarName(String varName) {
		lVarName = varName;
	}
	public float getLoopNo() {
		return loopNo;
	}
	public void setLoopNo(float loopNo) {
		this.loopNo = loopNo;
	}
	public int getStartRange() {
		return startRange;
	}
	public void setStartRange(int startRange) {
		this.startRange = startRange;
	}
	public int getEndRange() {
		return endRange;
	}
	public void setEndRange(int endRange) {
		this.endRange = endRange;
	}
}
