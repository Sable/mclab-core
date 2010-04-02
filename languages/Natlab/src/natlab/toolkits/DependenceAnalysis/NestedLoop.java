package natlab.toolkits.DependenceAnalysis;
/*
 * This class contains NestedLoopData which is then written to dependenceFile.xml
 * DateCreated:Mar31,2010
 * Author:Amina Aslam 
 */

public class NestedLoop {
	float loopNo;
	int startRange;
	int endRange;
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
