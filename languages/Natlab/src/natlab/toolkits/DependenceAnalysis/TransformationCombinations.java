// =========================================================================== //
//                                                                             //
// Copyright 2011 Amina Aslam and McGill University.                           //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

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
		  Level=nLevel+1;
		  cMatrix=new int[Level][Level];
		  int sum=0;
	      Reversal reversal=new Reversal(Level,Level);
	      Interchange interchange=new Interchange(Level,Level);
	      reversal.setMatrix(rLevel);
	      interchange.setMatrix(Level);
	      int tIMatrix[][]=interchange.getMatrix();
	      int tRMatrix[][]=reversal.getMatrix();
	      for(int i=0; i<Level; i++){
	        for(int j=0; j<Level; j++){
	          for(int k=0; k<Level; k++){
	             cMatrix[i][j] += tIMatrix[k][j]*tRMatrix[i][k];	             
	           }	          
	         }	        
	    }	
	}
	
	public void createReversalInterchangeMatrix(int rLevel,int nLevel){
		  Level=nLevel+1;
		  cMatrix=new int[Level][Level];
		  int sum=0;
	      Reversal reversal=new Reversal(Level,Level);
	      Interchange interchange=new Interchange(Level,Level);
	      reversal.setMatrix(rLevel);
	      interchange.setMatrix(Level);
	      int tIMatrix[][]=interchange.getMatrix();
	      int tRMatrix[][]=reversal.getMatrix();
	      for(int i=0; i<Level; i++){
	        for(int j=0; j<Level; j++){
	          for(int k=0; k<Level; k++){
	             cMatrix[i][j] += tRMatrix[i][k]*tIMatrix[k][j];	             
	           }	          
	         }	        
	      }
	      //for(int i=0;i<Level;i++){
	      	  //for(int j=0;j<Level;j++){
	      		///  System.out.print(cMatrix[i][j]+"  ");
	      	  //}
	      //}
	}
	public boolean applyCombination(int[] array){
		//int[] array=lData.getDistanceArray();		 
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
