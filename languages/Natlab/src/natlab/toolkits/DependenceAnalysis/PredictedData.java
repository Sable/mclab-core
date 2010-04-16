package natlab.toolkits.DependenceAnalysis;

public class PredictedData {
	private float loopNo;
	private int lowerBound;
	private int upperBound;
	private int loopIncFactor;
	private String lVName;
	public void setLoopNo(float loopNo) {
		this.loopNo = loopNo;
	}
	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}
	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}
	public void setLoopIncFactor(int loopIncFactor) {
		this.loopIncFactor = loopIncFactor;
	}
	public float getLoopNo() {
		return loopNo;
	}
	public int getLowerBound() {
		return lowerBound;
	}
	public int getUpperBound() {
		return upperBound;
	}
	public int getLoopIncFactor() {
		return loopIncFactor;
	}
	public String getLVName() {
		return lVName;
	}
	public void setLVName(String name) {
		lVName = name;
	}
	

}
