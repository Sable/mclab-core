package natlab.toolkits.DependenceAnalysis;
import java.util.LinkedList;


public class TransformationCombinations {
	
	/*
	 * 
	 * This function does the following...
	 * 1.It takes as input the loop no on which to apply reversal.
	 * 2.It also takes as input the nesting level of the loops.
	 * 
	 */
	private int cMatrix[][];
	private int Level;
	public void createInterchangeReversalMatrix(int rLevel,int nLevel){
		  Level=nLevel;
		  cMatrix=new int[nLevel][nLevel];
		  int sum=0;
	      Reversal reversal=new Reversal(nLevel,nLevel);
	      Interchange interchange=new Interchange(nLevel,nLevel);
	      reversal.setMatrix(rLevel);
	      interchange.setMatrix(nLevel);
	      int tIMatrix[][]=interchange.getMatrix();
	      int tRMatrix[][]=reversal.getMatrix();
	      for(int i=0; i<nLevel; i++){
	        for(int j=0; j<nLevel; j++){
	          for(int k=0; k<nLevel; k++){
	             cMatrix[i][j] += tIMatrix[k][j]*tRMatrix[i][k];	             
	           }	          
	         }	        
	      }
	      for(int i=0;i<2;i++){
	      	  for(int j=0;j<2;j++){
	      		  System.out.print(cMatrix[i][j]+"  ");
	      	  }
	      }
	}
	public boolean applyCombination(DependenceData lData){
		int[] array=lData.getDistanceArray();		 
	    if(!multiply(array))return false;
		return true;

		
	}
	 private boolean multiply(int tArray[]){
		 int sum[]=new int[tArray.length];
		 for(int i=0;i<Level;i++){
			 sum[i]=0;
			 for(int j=0;j<Level;j++){
				 sum[i]+=cMatrix[i][j]*tArray[j];
			 }
		 }
		 for(int j=0;j<sum.length;j++){
			 if(sum[j]<0) return false;
		 }
		 return true;
	 }

}
