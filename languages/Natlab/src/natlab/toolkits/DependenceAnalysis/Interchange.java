package natlab.toolkits.DependenceAnalysis;
import java.util.LinkedList;

public class Interchange {
 private int Rows;
 private int Cols; 
 private int iMatrix[][];
 public Interchange(int r,int c){
	 Rows=r;
	 Cols=c;	 
	 iMatrix=new int[r][c];
 }
 
 //indicate the loop nesting level to create the identity matrix.
 public void setMatrix(int level){
	 int tLevel=level-1;
	 for(int i=0;i<Rows;i++){		
	  for(int j=0;j<Cols;j++){
		if(j==tLevel){iMatrix[i][j]=1;tLevel--;}		
		else {iMatrix[i][j]=0;}
	  }//end of 2nd for
	}//end of 1st for
	 
  }//end of function
 public int[][] getMatrix(){
	 return iMatrix;
  }
 
 public boolean applyInterchange(DependenceData lData){
	 /*LinkedList l=(LinkedList)lData.getDistanceVectors();
	 for(int i=0;i<l.size();i++){
		 int tArray[]=(int[])l.get(i);
		 if(!multiply(tArray))return false;
	 }*/
	 
	 int[] array =lData.getDistanceArray();
	 //for(int i=0;i<l.size();i++){
		// int tArray[]=(int[])l.get(i);
		 if(!multiply(array))return false;
	 //}
	 return true;
	 
 }
 
 private boolean multiply(int tArray[]){
	 int sum[]=new int[tArray.length];
	 for(int i=0;i<Rows;i++){
		 sum[i]=0;
		 for(int j=0;j<Cols;j++){
			 sum[i]+=iMatrix[i][j]*tArray[j];
		 }
	 }
	 for(int j=0;j<sum.length;j++){
		 if(sum[j]<0) return false;
	 }
	 return true;
 }

}
