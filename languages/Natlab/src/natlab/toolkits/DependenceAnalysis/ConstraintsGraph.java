package natlab.toolkits.DependenceAnalysis;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class ConstraintsGraph {
	private Map<String,LinkedList> cMap;
	
	public ConstraintsGraph()
	{
		//cMap=new HashMap<String,ConstraintsList>(); //create a new hash map;		
		cMap=new HashMap<String,LinkedList>(); //create a new hash map;
	}
	
	//To DO:handle case with constraint bounded on both sides by variables.
	public void addToGraph(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		
		////ConstraintsList cList1=new ConstraintsList();
		//ConstraintsList subList1=new ConstraintsList();
		//subList1.insertAtFront(aExpr2);
		//cList1.insertAtFront(aExpr1,subList1);		
		//cList1.insertAtFront(aExpr1,null);	
		LinkedList<AffineExpression> cList=new LinkedList<AffineExpression>();
		cList.add(aExpr1);
		cList.add(aExpr2);
		cMap.put(aExpr1.getKey(), cList);
		
				
	}
	/*public void temp()
	{
		boolean isApplicable=false;
		
		//Set s=cMap.entrySet();		
		Set s1=cMap.entrySet();
	      //Move next key and value of Map by iterator
        Iterator it=s1.iterator();
       
        while(it.hasNext())
        {
            // key=value separator this by Map.Entry to get key and value
            Map.Entry m =(Map.Entry)it.next();
            /*String key=(String)m.getKey();
            System.out.println("key for cMap is "+ key);
            
            String value=(String)m.getValue();
            System.out.println("value for cMap is "+ value);*/
            
            /*String key2=(String)m.getKey();
            System.out.println("key for cMap is "+ key2);
            //AffineExpression aExpr3=new AffineExpression();
    		//ConstraintsList cList4=new ConstraintsList();
             //cList4=(ConstraintsList)m.getValue();  
       
             if(cMap.containsKey(key2))
             {
            	 	
            	    ConstraintsList cList4=(ConstraintsList)cMap.get(key2);
            	 	AffineExpression aExpr3=cList4.getInsertItem();
            	 	System.out.println(cList4.toString());
            	 	//aExpr3=(AffineExpression)cList4.getListData(key2);
            	 	System.out.println("Affine Data for cMap  "+aExpr3.getLoopVariable()+ " "+ aExpr3.getKey()+ " "+aExpr3.getC());
             }
            
            //AffineExpression aExpr4=(AffineExpression)cList1.getListData().getNext().getData();
            //System.out.println("Affine Data for expression 2 "+ aExpr4.getLoopVariable()+" "+ aExpr4.getKey()+" "+ aExpr4.getC());           
                     
         }//end of while

		

	}*/
	public Map getGraphData()
	{
		return cMap;
		 		
	}
	public int getGraphSize()
	{
		return cMap.size();
	}

}
