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
		cList1.insertAtFront(aExpr1);
		cMap.put(aExpr1.getLoopVariable(), cList1);
		ConstraintsList cList2=new ConstraintsList();
		cList1.insertAtFront(aExpr2);
		cMap.put(aExpr2.getLoopVariable(), cList2);
		
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
