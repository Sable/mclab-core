package natlab.toolkits.DependenceAnalysis;

public class ProfiledData {
	private float loopNo;
	private ProfiledLowerBound lBound;
	private ProfiledLIF loopIncFac;
	private UpperBound uBound;
	private String lVName;//This stores the loop variable name
	
	public String getLVName() {
		return lVName;
	}
	public void setLVName(String name) {
		lVName = name;
	}
	public float getLoopNo() {
		return loopNo;
	}
	public void setLoopNo(float loopNo) {
		this.loopNo = loopNo;
	}
	public ProfiledLowerBound getLBound() {
		//return lBound=new LowerBound();
		return lBound;
	}
	public void setLBound(ProfiledLowerBound bound) {
		lBound = bound;
	}
	public ProfiledLIF getLoopIncFac() {
		//return loopIncFac=new LIF();
		return loopIncFac;
	}
	public void setLoopIncFac(ProfiledLIF loopIncFac) {
		this.loopIncFac = loopIncFac;
	}
	public UpperBound getUBound() {
		//return uBound=new UpperBound();
		return uBound;
	}
	public void setUBound(UpperBound bound) {
		uBound = bound;
	}	
	public UpperBound getNewUBound() {
		return uBound=new UpperBound();
	}
	public ProfiledLowerBound getNewLBound() {
		return lBound=new ProfiledLowerBound();
	}
	public ProfiledLIF getNewLoopIncFac() {
		return loopIncFac=new ProfiledLIF();		
	}


	

}//end of InputData
