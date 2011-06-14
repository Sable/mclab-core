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
import java.io.File;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ast.ParameterizedExpr;
public class HeuristicEngine {
	Hashtable loopTable;
	Vector<PredictedData> predictedValues;
	Hashtable<String,LinkedList<PredictedData>> pValuesTable;
	public HeuristicEngine(Hashtable lTable){
		loopTable=lTable;
		 //predictedValues=new Vector<PredictedData>();
		 pValuesTable=new Hashtable<String,LinkedList<PredictedData>>();
	}
	
	/*
	 * This function will divide the input space into equal sized regions
	 */
	public Hashtable<String,LinkedList<PredictedData>> computeRegionDivisors(){
		Set s=loopTable.keySet();
		//System.out.println("loopTable size is"+loopTable.size());
	    Iterator it=s.iterator();
	    while(it.hasNext()){
	    	//System.out.println("Newlist");
	    	LinkedList l=(LinkedList)loopTable.get(it.next());  
	    	//screenInput(l);
	    	valuePlacement(l);	    	
	    }
	    return pValuesTable;
	}
	
	/*
	 * This function computes region range. 
	 * 
	 */
	/*private int regionRange(int mValue){
		int divisor=100;
		boolean flag=true;
		while(flag){
			if(numDigits(mValue/divisor)==1){
				flag=false;
			}
			else divisor*=10;
		}
		return divisor;				
	}*/
	
  private void regionRange(int mValue,regionInformation rInfo){	
	int no=1;
	int nDig=numDigits(mValue);
	int divisor=100;
	boolean flag=true;
	while(flag){
		if(no>3){divisor*=10;}
		if(nDig==no){
			flag=false;
		}
		else no++;		 
	}//end of while
	rInfo.setRegionDivisor(divisor);
	rInfo.setURegionBound(divisor*10);
  }//end of function
	
	private int numDigits(int num){
	if (num < 10)
	    return (1);
	else
	    return (1 + numDigits(num/10));
    }//end of function

	
	/*
	 * This function places the values into their respective regions.
	 * if the value is less than the region range then put it in this first region which starts from 1 to region divisor.
	 * divisor flag is set to true when region divisor is 100,else it is false.
	 * 
	 */
	private void valuePlacement(LinkedList<ProfiledData> l){
		//int maxValue,regionDivisor=0;
		int minValue=0;
		minValue=((UpperBound)((ProfiledData)l.getFirst()).getUBound()).getEnd(); 		    	
    	regionInformation rInfo=new regionInformation();
    	regionRange(minValue,rInfo);    	
    	int value;
    	int dividend=0;
    	LinkedList<Hashtable<String,LinkedList<ProfiledData>>> regionsList=new LinkedList<Hashtable<String,LinkedList<ProfiledData>>>();//for storing all the hashtables.    	
		Hashtable<String,LinkedList<ProfiledData>> region=new Hashtable<String,LinkedList<ProfiledData>>(); //this contains sR no e.g.1,2,3 and linkedlist of values.
		regionsList.add(region);	
		for(int i=0;i<l.size();i++){
			 ProfiledData inData=(ProfiledData)l.get(i);
			 value=((UpperBound)inData.getUBound()).getEnd();			 
			 if((value%rInfo.getRegionDivisor())==value){//this is the case for 10,50,40,70 of region range 100-1000
			   if(region.containsKey("1")){
				  LinkedList<ProfiledData> tList=(LinkedList<ProfiledData>)region.get("1");
				  tList.add(inData);							
			      region.put(Integer.toString(1), tList);
			   }//end of if
			   else{
					LinkedList<ProfiledData> tList=new LinkedList<ProfiledData>();
					tList.add(inData);			 
					region.put(Integer.toString(1), tList);
				}//end of else 
			 }//end of main if
			 if(value>rInfo.getURegionBound()){ //this is the case where you need to start a new region range.
				  region=new Hashtable<String,LinkedList<ProfiledData>>(); //this contains sR no e.g.1,2,3 and linked list of values.	  		 		    	
			      rInfo=new regionInformation();
			      regionRange(value,rInfo);
			      dividend=value/rInfo.getRegionDivisor();
				  regionsList.add(region);	
			  }
			 else{dividend=value/rInfo.getRegionDivisor();//}//end of else for values above 100 e.g.101 dividend would be 1 to handle this case
			 //put 101 uptill 199 into sR 1
			 if(dividend>=1){
				 if(region.containsKey(Integer.toString(dividend))){
					  LinkedList<ProfiledData> tList=(LinkedList<ProfiledData>)region.get(Integer.toString(dividend));
					  tList.add(inData);							
					  region.put(Integer.toString(dividend), tList);
				  }//end of if
				  else{
				      LinkedList<ProfiledData> tList=new LinkedList<ProfiledData>();
					  tList.add(inData);			 
					  region.put(Integer.toString(dividend), tList);
				  }//end of 2nd else
			 }//end of if
		    }//end of else 
		  }//end of for		
    	  markImpRegions(regionsList,l.size());		
 }//end of function
	
 /*  private void valuePlacement(LinkedList l,int regionRange){
			//int regionRange=100;
			int value;
			//LinkedList tList=null;
			  Hashtable regionValues=new Hashtable();
			  boolean createList=false;
			  for(int i=0;i<l.size();i++){
				 ProfiledData inData=(ProfiledData)l.get(i);
				 value=((UpperBound)inData.getUBound()).getEnd();
				 if(value < regionRange){
				 	//LinkedList tList=null;
				 	if(regionValues.containsKey(0)){ //&& tList!=null){
				 		LinkedList tList=(LinkedList)regionValues.get(0);
					    tList.add(inData);
					    //regionValues.put(0, tList);		
					    regionValues.put(inData.getLoopNo(), tList);
					}
					else{
						LinkedList tList=new LinkedList();
						tList.add(inData);					
					    //regionValues.put(0, tList);
						regionValues.put(inData.getLoopNo(), tList);
					}
						
				}//end of if
				else{
					//LinkedList tList=null;					
					int digitMod = (int)Math.pow (100, 1);
				    int digit = (value / digitMod) % 10;
					if(regionValues.containsKey(digit)){ //&& tList!=null){
					   LinkedList tList=(LinkedList)regionValues.get(digit);	
					   tList.add(inData);
					   regionValues.put(digit, tList);		
					}
					else{
						LinkedList tList=new LinkedList();
						tList.add(inData);					
					    regionValues.put(digit, tList);
					}				
				}
			}//end of for
			predictValues(regionValues,l.size());   
   }
   
	
	/*
	 * This will predict the value within the imp region depending on the concentration of values within the imp region.
	 * if the no of values exceed a certain threshold then predicted value for that region.
	 * threshold =(totalnoOfvaluesin hashtable/100)*10;
	 */
/*	private void predictValues(Hashtable table,int sum){
		//int sum=0;
		Set s=table.keySet();
	    Iterator it=s.iterator();
	    //while(it.hasNext()){
	    //	LinkedList tList=(LinkedList)table.get(it.next());
	    	//sum+=tList.size();
	    //}
	    int threshold=(sum*10)/100;
	    while(it.hasNext()){
	    	LinkedList tList=(LinkedList)table.get(it.next());
	    	if(tList.size()>=threshold){
	    		PredictedData pData=new PredictedData();
	    		ProfiledData tpData=(ProfiledData)tList.getLast();
	    		
	    		float loopNumber=tpData.getLoopNo();
	    		pData.setLoopNo(tpData.getLoopNo());
	    		pData.setLowerBound(tpData.getLBound().getStart());
	    		pData.setLoopIncFactor(tpData.getLoopIncFac().getStart());
	    		pData.setUpperBound(tpData.getUBound().getEnd());	
	    		pData.setLVName(tpData.getLVName());
	    		if(!pValuesTable.containsKey(Float.toString(loopNumber))){
	    			LinkedList<PredictedData> lList=new LinkedList<PredictedData>();
	    			lList.add(pData);
	    			pValuesTable.put(Float.toString(loopNumber), lList);
	    		}
	    		else{
	    			LinkedList<PredictedData> tempList=pValuesTable.get(Float.toString(loopNumber));
	    			tempList.add(pData);	    			
	    		}	    		   
	    		//predictedValues.add(pData);
	    		//System.out.println("Predicted Value For this region is"+((UpperBound)((ProfiledData)tList.getLast()).getUBound()).getEnd());
	    	}
	    }
	    //System.out.println("Start of new region");
	}
	
	/*
	 * Regions are marked imp if it contains 20% of total values.
	 * 
	 */
	/*private void markImpRegions(Hashtable table,int nValues){		
		int threshold=(nValues*10)/100;
		Set s=table.keySet();
	    Iterator it=s.iterator();
	    while(it.hasNext()){
	    	LinkedList tList=(LinkedList)table.get(it.next());
	    	if(tList.size()>=threshold){
	    		int initialValue=((UpperBound)((ProfiledData)tList.getFirst()).getUBound()).getEnd();
	    		int finalValue=((UpperBound)((ProfiledData)tList.getLast()).getUBound()).getEnd();
	    	if(finalValue-initialValue<=1000)valuePlacement(tList,100); //TODO:needs to fix this for higher region divisors.
	    	else if(finalValue-initialValue>1000)valuePlacement(tList);
	    	//	int divisor=computeNoDigits(finalValue);
	    		//valuePlacement(tList,divisor);
	    	}
	    }		
     }*/
	
	/*
	 * Regions are marked imp if it contains 20% of total values.
	 * 
	 */
	private void markImpRegions(LinkedList<Hashtable<String,LinkedList<ProfiledData>>> impList,int nValues){		
		int threshold=(nValues) * 2/100;
		for(int i=0;i<impList.size();i++){
			Hashtable<String,LinkedList<ProfiledData>> table=(Hashtable<String,LinkedList<ProfiledData>>)impList.get(i);
			Set s=table.keySet();
			Iterator it=s.iterator();
			while(it.hasNext()){
		      LinkedList<ProfiledData> tList=(LinkedList<ProfiledData>)table.get(it.next());
		      //System.out.println(tList.size());
		      if(tList.size()>=threshold){
		    	PredictedData pData=new PredictedData();
		    	ProfiledData tpData=(ProfiledData)tList.getLast();		    		
		    	float loopNumber=tpData.getLoopNo();
		    	pData.setLoopNo(tpData.getLoopNo());
		    	pData.setLowerBound(tpData.getLBound().getStart());
		    	pData.setLoopIncFactor(tpData.getLoopIncFac().getStart());
		    	pData.setUpperBound(tpData.getUBound().getEnd());	
		    	pData.setLVName(tpData.getLVName());
		    	if(!pValuesTable.containsKey(Float.toString(loopNumber))){
		    	   LinkedList<PredictedData> lList=new LinkedList<PredictedData>();
		    	   lList.add(pData);
		    	   pValuesTable.put(Float.toString(loopNumber), lList);
		         }//end of if
		    	else{
		    	   LinkedList<PredictedData> tempList=pValuesTable.get(Float.toString(loopNumber));
		    	   tempList.add(pData);	    			
		    	}//end of else 	         		
		      }//end of if
		   }//end of while			
		}//end of for	    		
     }//end of function
	
	/* 
	 * This function does the following 
	 * Look for values that are repeated many times in the input dataset.
	 * 
	 */
	/*private void screenInput(LinkedList l){
	  int threshold=(l.size()*10)/100;
	  //System.out.println(threshold);
	  boolean nflag=false;
	  int count=0;
	  int  initValue=((ProfiledData)l.get(0)).getUBound().getEnd();	
	  for(int i=1;i<l.size();i++){
		//System.out.println(((ProfiledData)l.get(i)).getUBound().getEnd());
		ProfiledData tpData=((ProfiledData)l.get(i));
		if(initValue!=((ProfiledData)l.get(i)).getUBound().getEnd()){
		   initValue=((ProfiledData)l.get(i)).getUBound().getEnd();
		   nflag=false;
		}//end of if
		else{
			count++;
			if(count>=threshold && !nflag){
				PredictedData pData=new PredictedData();	    		
	    		float loopNumber=tpData.getLoopNo();
	    		pData.setLoopNo(tpData.getLoopNo());
	    		pData.setLowerBound(tpData.getLBound().getStart());
	    		pData.setLoopIncFactor(tpData.getLoopIncFac().getStart());
	    		pData.setUpperBound(tpData.getUBound().getEnd());	
	    		pData.setLVName(tpData.getLVName());
				if(!pValuesTable.containsKey(Float.toString(loopNumber))){
	    			LinkedList<PredictedData> lList=new LinkedList<PredictedData>();
	    			lList.add(pData);
	    			pValuesTable.put(Float.toString(loopNumber), lList);
	    		}//end of if.
	    		else{
	    			LinkedList<PredictedData> tempList=pValuesTable.get(Float.toString(loopNumber));
	    			tempList.add(pData);	    			
	    		}//end of else.	
				nflag=true;
			}//end of if
		}//end of else
	  }//end of for
	}//end of function*/
	
	//This is an inner class for storing region information.
	private class regionInformation{
		int regionDivisor;
		int uRegionBound; //this stores the upper bound of region e.g.1000,10,000,100,000.
		public int getRegionDivisor() {
			return regionDivisor;
		}
		public void setRegionDivisor(int regionDivisor) {
			this.regionDivisor = regionDivisor;
		}
		public int getURegionBound() {
			return uRegionBound;
		}
		public void setURegionBound(int regionBound) {
			uRegionBound = regionBound;
		}		
	}//end of inner class
}//end of outer class.
