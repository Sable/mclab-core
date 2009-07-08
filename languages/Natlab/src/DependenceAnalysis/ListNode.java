package natlab.toolkits.DependenceAnalysis;

public class ListNode {
	AffineExpression data;
	ListNode nextNode;
	
	ListNode(AffineExpression aExpression,ListNode nNode)
	{
		data=aExpression;
		nextNode=nNode;
	}
	
	AffineExpression getData()
	{
		return data;		
	}
	ListNode getNext()
	{
		return nextNode;
	}

}
