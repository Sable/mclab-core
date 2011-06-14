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
import java.util.*;
public class Reversal {
 private int Rows;
 private int Cols;
 private int nLevel;
 private int rMatrix[][];
 public Reversal(int r,int c){
	 Rows=r;
	 Cols=c;
	 rMatrix=new int[r][c];
	 //nLevel=level;	 
 }
 public void setMatrix(int level){
	 for(int i=0;i<Rows;i++){		
	  for(int j=0;j<Cols;j++){	  
		if((i==j) && (i==level-1)){rMatrix[i][j]=-1;}
		else if(i==j){rMatrix[i][j]=1;}
		else {rMatrix[i][j]=0;}
	  }//end of 2nd for
	}//end of 1st for
	 //for(int i=0;i<Rows;i++){
     	//  for(int j=0;j<Cols;j++){
     	//	  System.out.print(rMatrix[i][j]+"  ");
     	//  }
     //}
  }//end of function
 public int[][] getMatrix(){
	 return rMatrix;
 }
 public boolean applyReversal(int[] array){
	 //LinkedList l=(LinkedList)lData.getDistanceVectors();
	 /*LinkedList l=(LinkedList)lData.get
	 for(int i=0;i<l.size();i++){
		 int tArray[]=(int[])l.get(i);
		 if(!multiply(tArray))return false;
		 
	 }*/
	 
	 //int[] array=lData.getDistanceArray();	
	 if(!multiply(array))return false;	 
	 return true;
	 
 }
 private boolean multiply(int tArray[]){
	 int sum[]=new int[tArray.length];
	 for(int i=0;i<Rows;i++){
		 sum[i]=0;
		 for(int j=0;j<Cols;j++){
			 sum[i]+=rMatrix[i][j]*tArray[j];
		 }
	 }
	 for(int j=0;j<sum.length;j++){
		 if(sum[j]<0) return false;
	 }
	 return true;
 }
 
}
