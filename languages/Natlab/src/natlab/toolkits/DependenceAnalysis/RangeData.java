package natlab.toolkits.DependenceAnalysis;

public class RangeData {
	private int start;
	private int end;
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public boolean equal(RangeData rData){
		if((this.start==rData.getStart()) && (this.end==rData.getEnd()))return true;
		else return false;
	}

}
