package natlab.toolkits.DependenceAnalysis;

public class ConstraintsList {
	private ListNode firstNode;
	private ListNode lastNode;
	//private AffineExpression data;
	
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
			//data=insertItem;
		//}
		//else //firstNode refers to the new node.
		//	firstNode=new ListNode(insertItem,firstNode);
	}
	public void insertAtFront(AffineExpression insertItem,ConstraintsList subList)
	{
		//firstNode=new ListNode(insertItem,null);
		firstNode=new ListNode(insertItem,new ListNode(subList.getListNode().getData(),null));
		//firstNode=new ListNode(insertItem,new ListNode(subList.getListData(),null));
		lastNode=null;
		//data=insertItem;
	}
	public ListNode getListNode()
	{
		
		if(firstNode.nextNode==null && lastNode==null)
		{
			
			ListNode currentNode=firstNode;
			return currentNode;
			//System.out.println("first node data" + current.data.getLoopVariable() + current.data.getVariable());			
		}
		else if(firstNode.nextNode!=null && lastNode==null)
		{
			return firstNode;
		}
		return null;
	
	}
	/*public AffineExpression getInsertItem()
	{
		if(data!=null)
		{
			System.out.println("i am in insert item" + data.getVariable());
			return data;
		}
		else
			return null;
	}*/
	
	
	private boolean isEmpty()
	{
		 if(firstNode==null)
		 {
			 return true;
		 }
		 else 
			 return false;
	}
	
	/*public String toString()
	{
		AffineExpression aExpr3=this.getInsertItem();
	 	return aExpr3.getLoopVariable()+ " "+ aExpr3.getVariable()+ " "+aExpr3.getC();
	}*/
}
