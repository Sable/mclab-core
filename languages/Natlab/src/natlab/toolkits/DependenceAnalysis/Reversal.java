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
	 for(int i=0;i<2;i++){
     	  for(int j=0;j<2;j++){
     		  System.out.print(rMatrix[i][j]+"  ");
     	  }
     }
  }//end of function
 public int[][] getMatrix(){
	 return rMatrix;
 }
 public boolean applyReversal(DependenceData lData){
	 //LinkedList l=(LinkedList)lData.getDistanceVectors();
	 /*LinkedList l=(LinkedList)lData.get
	 for(int i=0;i<l.size();i++){
		 int tArray[]=(int[])l.get(i);
		 if(!multiply(tArray))return false;
		 
	 }*/
	 
	 int[] array=lData.getDistanceArray();	
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
