package natlab.toolkits.DependenceAnalysis;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;


public class ConstraintsGraph {
	private Map<String,ConstraintsList> cMap;
	public ConstraintsGraph()
	{
		cMap=new HashMap<String,ConstraintsList>(); //create a new hash map;
		
		
	}
	//To DO:handle case with constraint bounded on both sides by variables.
	public void createGraph(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		//TO DO:put the constraint in the Map.
		ConstraintsList cList1=new ConstraintsList();
		//ConstraintsList subList1=new ConstraintsList();
		//subList1.insertAtFront(aExpr2);
		//cList1.insertAtFront(aExpr1,subList1);
		cList1.insertAtFront(aExpr1,null);
		cMap.put(aExpr1.getVariable(), cList1);
		//System.out.println("aExpr1 variable is"+cList1.getListData().getData().getLoopVariable() + " "+ aExpr1.getVariable() + " " +aExpr1.getC());
		/*ConstraintsList cList2=new ConstraintsList();
		cList2.insertAtFront(aExpr2);
		cMap.put(aExpr2.getVariable(), cList2);
		System.out.println("aExpr2 variable is"+aExpr2.getVariable());*/
		temp();
		
	}
	public void temp()
	{
		boolean isApplicable=false;
		
		Set s=cMap.entrySet();		
	      //Move next key and value of Map by iterator
        Iterator it=s.iterator();
        /*Map.Entry m =(Map.Entry)it.next();
        String key1=(String)m.getKey();
        System.out.println("value for this key is"+key1);
        ConstraintsList cList=(ConstraintsList)cMap.get(key1);
        //ConstraintsList cList=(ConstraintsList)m.getValue();        
        AffineExpression aExpr1=(AffineExpression)cList.getListData().getData();
        System.out.println("Affine Data for expression 1 "+aExpr1.getLoopVariable()+ " "+ aExpr1.getVariable()+ " "+aExpr1.getC());
        AffineExpression aExpr2=(AffineExpression)cList.getListData().getNext().getData();
        System.out.println("Affine Data for expression 2 "+ aExpr2.getLoopVariable()+" "+ aExpr2.getVariable()+" "+ aExpr2.getC());*/
        
        while(it.hasNext())
        {
            // key=value separator this by Map.Entry to get key and value
            Map.Entry m =(Map.Entry)it.next();
            String key=(String)m.getKey();
            System.out.println("value for this key is"+key);
            AffineExpression aExpr3=new AffineExpression();
    		ConstraintsList cList4=new ConstraintsList();
            
            cList4=(ConstraintsList)m.getValue();
            //ConstraintsList cList=(ConstraintsList)m.getValue();
                        
            aExpr3=(AffineExpression)cList4.getListData(cList4);
            System.out.println("Affine Data for expression 1 "+aExpr3.getLoopVariable()+ " "+ aExpr3.getVariable()+ " "+aExpr3.getC());
            //AffineExpression aExpr4=(AffineExpression)cList1.getListData().getNext().getData();
            //System.out.println("Affine Data for expression 2 "+ aExpr4.getLoopVariable()+" "+ aExpr4.getVariable()+" "+ aExpr4.getC());           
                     
         }//end of while

		

	}
	public Map getGraphData()
	{
		return cMap;
		 		
	}
	public int getGraphSize()
	{
		return cMap.size();
	}

}
