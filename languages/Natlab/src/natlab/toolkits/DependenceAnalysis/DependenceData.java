package natlab.toolkits.DependenceAnalysis;
import java.util.LinkedList;
import java.util.StringTokenizer;

/*
 * This class contains loop Data which is then written to dependenceFile.xml
 * DateCreated:Mar31,2010
 * Author:Amina Aslam 
 */

public class DependenceData {
	float loopNo;
	char dependence;
	int startRange;
	int endRange;
	int nestingLevel;
	//LinkedList distanceVectors;
	ProfiledNestedLoop nestedLoop;
	String arrayAccess;
	int distanceArray[];
	String statementAccessed;
	
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
	public ProfiledNestedLoop getNestedLoop() {
		return nestedLoop;		
	}

	public void setNestedLoop(ProfiledNestedLoop nestedLoop) {
		this.nestedLoop = nestedLoop;
	}	
	public int getNestingLevel() {
		return nestingLevel;
	}
	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
		if(nestingLevel>0){
		  nestedLoop=new ProfiledNestedLoop();	
		}
	}
	public float getLoopNo() {
		return loopNo;
	}
	public void setLoopNo(float loopNo) {
		this.loopNo = loopNo;
	}
	public char getDependence() {
		return dependence;
	}
	public void setDependence(char dependence) {
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


}
