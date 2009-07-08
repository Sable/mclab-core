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
			firstNode=lastNode=new ListNode(insertItem,null);
		//}
		//else //firstNode refers to the new node.
		//	firstNode=new ListNode(insertItem,firstNode);
	}
	public void insertAtFront(AffineExpression insertItem,ListNode nNode)
	{
		
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
