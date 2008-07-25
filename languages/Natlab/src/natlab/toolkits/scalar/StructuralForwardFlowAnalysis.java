
package natlab.toolkits.scalar;
/**
 * Adapted from soot.toolkits.scalar.ForwardFlowAnalysis.java
 * Change Log:
 *  - 2008.06 by Jun Li
 *  	- changed to handle AST-tree instead of UnitGraph
 *  	- Implement ASTVisitor interface
 *  	- Adding fix-point structural flow-analysis, 
 *  	- Adding getNodeList(), getResult()
 *  
 */
import natlab.ast.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Comparator;

/**
 * Abstract class that provides the fixed point iteration functionality required
 * by all ForwardFlowAnalyses.
 * 
 */
public abstract class StructuralForwardFlowAnalysis<N, A> extends FlowAnalysis<N, A>  
{

	// List of nodes that contains the flow-set, stored by the traversal order
    List<ASTNode> NodeList = new ArrayList<ASTNode>();
    
    A currentAfterFlow;
    int numComputations = 0;
    
	/**
	 * Construct the analysis from a DirectedGraph representation of a Body.
	 */
	public StructuralForwardFlowAnalysis(ASTNode tree) {
		super(tree);
	}

	protected boolean isForward() {
		return true;
	}
	
	protected void doAnalysis() {
		// Old method, empty
	}
	
	// Implement ASTVisitor interface
	// This is called from AssignStmt, Expr node, which are plated by ASTVisitor.jadd 
	// This function will only traverse the node once.
	public void caseASTNode(ASTNode node) 
	{
		// Skip unnecessary nodes
		if(node instanceof natlab.ast.List || node instanceof natlab.ast.Opt ) {
			return;
		}
		// Add current node in visited node list  
		NodeList.add(node);
		
		if (DEBUG) out.println("ASTWalker: caseASTNode " + node.dumpCode());
		
		{
			A previousAfterFlow = newInitialFlow();

			{	 
				A beforeFlow;
				A afterFlow;

				//  working on current node
				N s = (N) node;
				
				beforeFlow = unitToBeforeFlow.get(s);
				if(beforeFlow == null) {
					unitToBeforeFlow.put(s, currentAfterFlow);
					beforeFlow = currentAfterFlow;
				}
			 
				afterFlow = unitToAfterFlow.get(s);
				if(afterFlow == null) {					
					unitToAfterFlow.put(s, newInitialFlow());
					afterFlow = newInitialFlow();
				}
				copy(afterFlow, previousAfterFlow);

				// Traverse the node,  
				{
					// Compute afterFlow and store it.
					flowThrough(beforeFlow, s, afterFlow);
					copy(afterFlow, currentAfterFlow);
					unitToAfterFlow.put(s, afterFlow);
					numComputations++;
				}
			}
		}
    }

	// special cases for WhileStmt, ForStmt 
	public void caseLoopStmt(ASTNode node) 
	{
		// Add it into visited node list  
		NodeList.add(node);
		
		// Save initial after set
		A previousAfterFlow = newInitialFlow();
		A savedAfterFlow = newInitialFlow();
		
		copy(currentAfterFlow, previousAfterFlow);
		
		// Perform fixed point flow analysis
		do {
			// save the original flow-set, for later comparison
			copy(previousAfterFlow, savedAfterFlow);

			// flow through all child-nods
			node.applyAllChild(this);
	        
			numComputations++;
			
			merge(currentAfterFlow, previousAfterFlow, previousAfterFlow);

			/* / DEBUG ------------------------
			System.out.println(" [caseLoopStmt]-after:" + currentAfterFlow);
			System.out.println(" [caseLoopStmt]:" + ((FlowSet)previousAfterFlow).size() 
					+ " numComputations =  " + numComputations);
			System.out.println(" [caseLoopStmt]:" + previousAfterFlow);
			*///-----------------------------------------
		} while (!previousAfterFlow.equals(savedAfterFlow));
		
		// Updating the current after flow-set
		copy(previousAfterFlow, currentAfterFlow);
		unitToAfterFlow.put((N) node, previousAfterFlow);
	}
	
	// special cases for branching statement 
	public void caseIfStmt(IfStmt node) 
	{	
		// Add it into visited node list  
		NodeList.add(node);
		
		// Save initial after set
		A previousBeforeFlow = newInitialFlow();
		A previousAfterFlow = newInitialFlow();
		
		copy(currentAfterFlow, previousBeforeFlow);

		// Get after-set from each branch (if, elseif, else)
        for(IfBlock block : node.getIfBlocks()) {
        	// using same before set for each branch
    		copy(previousBeforeFlow, currentAfterFlow);
            block.apply(this);
            // merge the result flow sets 
			merge(currentAfterFlow, previousAfterFlow, previousAfterFlow);
        }
        if(node.hasElseBlock()) {
    		copy(previousBeforeFlow, currentAfterFlow);
            node.getElseBlock().apply(this);
			merge(currentAfterFlow, previousAfterFlow, previousAfterFlow);
        }
			
		// Updating the current after flow-set
		copy(previousAfterFlow, currentAfterFlow);
		unitToAfterFlow.put((N) node, previousAfterFlow);
	}
	
	// Overload function, accepts the user-defined analysis class,
	// so the tree-walker can add data on it
	protected void doAnalysis(FlowAnalysis analysis) 
	{
		// Initial current after flow
		currentAfterFlow =  entryInitialFlow();
		
		// working on visitor	
		tree.apply(analysis);
		
		/*
		// Sample code for output the result flow-set (after-set)
		{
			for(ASTNode node: NodeList) {
				A flowset = unitToAfterFlow.get((N) node);
				System.out.println("doAnalysis: " + node.dumpCode());
				System.out.println("\t After-Flow: " + flowset);
			}
		} 
		*/
	}

	protected Collection<N> constructWorklist(final Map<N, Integer> numbers) {
		return new TreeSet<N>(new Comparator<N>() {
			public int compare(N o1, N o2) {
				Integer i1 = numbers.get(o1);
				Integer i2 = numbers.get(o2);
				return (i1.intValue() - i2.intValue());
			}
		});
	}

	// Return the visited code-node list
	public List<ASTNode> getNodeList() {
		return NodeList;
	}
	// Return result of analysis
    public Map<N, A> getResult() {
    	return unitToAfterFlow; 
    }
}
