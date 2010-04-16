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
	    	valuePlacement(l);	    	
	    }
	    return pValuesTable;
	}
	
	/*
	 * This function computes region range. 
	 * 
	 */
	private int regionRange(int mValue){
		int divisor=100;
		boolean flag=true;
		while(flag){
			if(numDigits(mValue/divisor)==1){
				flag=false;
			}
			else divisor*=10;
		}
		return divisor;				
	}	
	
	private int numDigits(int num)
    {
	if (num < 10)
	    return (1);
	else
	    return (1 + numDigits(num/10));
    }

	
	/*
	 * This function places the values into their respective regions.
	 * if the value is less than the region range then put it in this first region which starts from 1 to region divisor.
	 * divisor flag is set to true when region divisor is 100,else it is false.
	 * 
	 */
	private void valuePlacement(LinkedList l){
		int maxValue,regionRange=0;
		LinkedList tList=null;		
		maxValue=((UpperBound)((ProfiledData)l.getLast()).getUBound()).getEnd();
    	regionRange=regionRange(maxValue);
    	  int value;
		  Hashtable regionValues=new Hashtable();
		  boolean createList=false;
		  for(int i=0;i<l.size();i++){
			 ProfiledData inData=(ProfiledData)l.get(i);
			 value=((UpperBound)inData.getUBound()).getEnd();
			 if(value < regionRange){			 	
			 	if(regionValues.containsKey(0) && tList!=null){			    	
				    tList.add(inData);
				    regionValues.put(0, tList);		
				}
				else{
					tList=new LinkedList();
					tList.add(inData);					
				    regionValues.put(0, tList);
				}
					
			}//end of if
			else{
				//LinkedList tList=null;
				if(regionValues.containsKey(value/regionRange) && tList!=null){			    	
				    tList.add(inData);
				    regionValues.put(value/regionRange, tList);		
				}
				else{
					tList=new LinkedList();
					tList.add(inData);					
				    regionValues.put(value/regionRange, tList);
				}				
			}
		}//end of for
    	  markImpRegions(regionValues,l.size());		
 }//end of function
	
   private void valuePlacement(LinkedList l,int regionRange){
			//int regionRange=100;
			int value;
			LinkedList tList=null;
			  Hashtable regionValues=new Hashtable();
			  boolean createList=false;
			  for(int i=0;i<l.size();i++){
				 ProfiledData inData=(ProfiledData)l.get(i);
				 value=((UpperBound)inData.getUBound()).getEnd();
				 if(value < regionRange){
				 	//LinkedList tList=null;
				 	if(regionValues.containsKey(0) && tList!=null){			    	
					    tList.add(inData);
					    regionValues.put(0, tList);		
					}
					else{
						tList=new LinkedList();
						tList.add(inData);					
					    regionValues.put(0, tList);
					}
						
				}//end of if
				else{
					//LinkedList tList=null;					
					int digitMod = (int)Math.pow (100, 1);
				    int digit = (value / digitMod) % 10;
					if(regionValues.containsKey(digit) && tList!=null){			    	
					   tList.add(inData);
					   regionValues.put(digit, tList);		
					}
					else{
						tList=new LinkedList();
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
	private void predictValues(Hashtable table,int sum){
		//int sum=0;
		Set s=table.keySet();
	    Iterator it=s.iterator();
	    //while(it.hasNext()){
	    //	LinkedList tList=(LinkedList)table.get(it.next());
	    	//sum+=tList.size();
	    //}
	    int threshold=(sum*20)/100;
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
	    		System.out.println("Predicted Value For this region is"+((UpperBound)((ProfiledData)tList.getLast()).getUBound()).getEnd());
	    	}
	    }
	    System.out.println("Start of new region");
	}
	
	/*
	 * Regions are marked imp if it contains 20% of total values.
	 * TODO:Look for values that are repeated many times in the input dataset.
	 */
	private void markImpRegions(Hashtable table,int nValues){		
		int threshold=(nValues*20)/100;
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
     }
	/*
	 * This function returns the region divisor based on the number of 
	 * digits in the number
	 */
/*	private int computeNoDigits(int value){		
		int num=numDigits(value);
		switch(num){
		 case 4:return 100;
		 case 5:return 1000;
		 case 6:return 10000;
		 case 7:return 100000;
		 case 8:return 1000000;		
		}
		return 100;
	}*/
	



}
