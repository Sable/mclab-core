package natlab.toolkits.DependenceAnalysis;

/*
 * This class contains loop Data which is then written to dependenceFile.xml
 * DateCreated:Mar31,2010
 * Author:Amina Aslam 
 * 
 */
public class DependenceData implements Cloneable {
	private float loopNo;
	private String dependence;
	private int startRange;
	private int endRange;
	private int nestingLevel;
	private String lVarName;
	private String transformation;
	//LinkedList distanceVectors;
	private NestedLoop nestedLoop;
	private String arrayAccess;
	private int distanceArray[];
	private String statementAccessed;
	private NestedLoop nLoopArray[];
	private int count=0;
	private int nestedLRanges;
	
	public int getNestedLRanges() {
		return nestedLRanges;
	}
	public void setNestedLRanges(int nestedLRanges) {
		this.nestedLRanges = nestedLRanges;
		nLoopArray=new NestedLoop[nestedLRanges];
	}
	public NestedLoop[] getNLoopArray() {
		return nLoopArray;
	}
	public void setNLoopArray(NestedLoop[] array) {
		//nLoopArray = new NestedLoop[nLevel];
		nLoopArray=array;
	}
	public String getLVarName() {
		return lVarName;
	}
	public void setLVarName(String varName) {
		lVarName = varName;
	}
	public String getStatementAccessed() {
		return statementAccessed;
	}
	public void setStatementAccessed(String statementAccessed) {
		this.statementAccessed = statementAccessed;
	}
	public String getArrayAccess() {
		return arrayAccess;
	}
	public void setArrayAccess(String arrayAccess) {
		this.arrayAccess = arrayAccess;
	}
	public DependenceData(){
		//distanceVectors=new LinkedList();		
	}	
	public NestedLoop getNestedLoop() {
	    nestedLoop=new NestedLoop();
	    nLoopArray[count]=nestedLoop;
	    System.out.println("Count size"+count);
	    count++;
	    return nestedLoop;
	}

	public void setNestedLoop(NestedLoop nestedLoop) {
		this.nestedLoop = nestedLoop;
	}	
	public int getNestingLevel() {
		return nestingLevel;
	}
	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
		//this.setNLoopArray(nestingLevel);
		//if(nestingLevel>0){
		  //nestedLoop=new NestedLoop();	
		//}
	}
	public float getLoopNo() {
		return loopNo;
	}
	public void setLoopNo(float loopNo) {
		this.loopNo = loopNo;
	}
	public String getDependence() {
		return dependence;
	}
	public void setDependence(String dependence) {
		this.dependence = dependence;
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
	//public LinkedList getDistanceVectors() {
	//	return distanceVectors;
	//}
	/*public void setDistanceVectors(int[] array) {
		/*StringTokenizer st = new StringTokenizer(value,",");
		int tempArray[]=new int[st.countTokens()];
		int i=0;		
        while (st.hasMoreTokens()) {
        	tempArray[i]=Integer.parseInt(st.nextToken());
            //System.out.println("fdsfsdsd"+tempArray[i]);
            i++;
        }
		
	/*	for(int i=0;i<size;i++){
		if(i==0){tempArray[0]=Character.getNumericValue(value.charAt(i));
		         System.out.print("Value is:::"+tempArray[0]);}			
		else if(i%2==0){tempArray[i-1]=Character.getNumericValue(value.charAt(i));
			           System.out.print("Value is:::"+tempArray[i-1]);}
		}*/
		//distanceVectors.add(array);
	//}
	public int[] getDistanceArray() {
		return distanceArray;
	}
	public void setDistanceArray(int[] distanceArray) {
		this.distanceArray = distanceArray;
	}
	public Object clone(){
		DependenceData dData=new DependenceData();		
		dData.setLoopNo(this.getLoopNo());
		dData.setNestingLevel(this.getNestingLevel());
		dData.setStatementAccessed(this.getStatementAccessed());		
		return dData;
	}
	public String getTransformation() {
		return transformation;
	}
	public void setTransformation(String transformation) {
		this.transformation = transformation;
	}


}
