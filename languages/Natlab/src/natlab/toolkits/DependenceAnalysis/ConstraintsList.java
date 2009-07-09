package natlab.toolkits.DependenceAnalysis;

public class ConstraintsList {
	private ListNode firstNode;
	private ListNode lastNode;
	
	public ConstraintsList()
	{
		firstNode=lastNode=null;
	}
	public void insertAtFront(AffineExpression insertItem)
	{
		//if(isEmpty())//first node and last node refers to the same object.That its the firstNode in the list
		//{
			firstNode=new ListNode(insertItem,null);
			lastNode=null;
		//}
		//else //firstNode refers to the new node.
		//	firstNode=new ListNode(insertItem,firstNode);
	}
	public void insertAtFront(AffineExpression insertItem,ConstraintsList subList)
	{
		firstNode=new ListNode(insertItem,null);
		//firstNode=new ListNode(insertItem,new ListNode(subList.getListData().getData(),null));
		//firstNode=new ListNode(insertItem,new ListNode(subList.getListData(),null));
		lastNode=null;
	}
	public AffineExpression getListData(ConstraintsList cList4)
	{
		
		if(firstNode.nextNode==null && lastNode==null)
		{
			ListNode current=firstNode;
			System.out.println("first node data" + current.data.getLoopVariable());
			
		}
		return firstNode.data;
	}
	
	private boolean isEmpty()
	{
		 if(firstNode==null)
		 {
			 return true;
		 }
		 else 
			 return false;
	}

}
