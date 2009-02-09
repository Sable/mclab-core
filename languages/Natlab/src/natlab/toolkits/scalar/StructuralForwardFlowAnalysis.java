
package natlab.toolkits.scalar;
/**
 * Adapted from soot.toolkits.scalar.ForwardFlowAnalysis.java
 * Change Log:
 *  - 2008.06 by Jun Li
 *  	- changed to handle AST-tree instead of UnitGraph
 *  	- Implement ASTVisitor interface
 *  	- Adding fix-point structural flow-analysis, 
 *  	- Adding getNodeList(), getResult()
 *  - 2008.07 by Jun Li
 *		- Adding data class LoopFlowsetList<A>, caseSwitchStmt()
 */
import natlab.ast.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Stack;

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
    
    // Stack of loop statements, for recording the current loop, used by break/continue statement
    Stack<LoopFlowsetList<A>> loopStack = new Stack<LoopFlowsetList<A>>();
    
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
		if(!NodeList.contains(node)) 	
			NodeList.add(node);

		if (DEBUG) 
			out.println("ASTWalker: caseASTNode " + node.getNodeID());
		
		{
			A previousAfterFlow = newInitialFlow();

			{	 
				A beforeFlow;
				A afterFlow;

				//  working on current node
				N s = (N) node;
								
				// Before-Flow-set will be equal to currentAfterFlow, 
				// because in case of a loop, 2nd iteration should use the
				// changed flow-set of 1st iteration.
				// unitToBeforeFlow initialized in AbstractFlowAnalysis()
				// NOT: beforeFlow = unitToBeforeFlow.get(s);
				beforeFlow = newInitialFlow();
				copy(currentAfterFlow, beforeFlow);
				unitToBeforeFlow.put(s, beforeFlow);

				afterFlow = unitToAfterFlow.get(s);
				if(afterFlow == null) {					
					unitToAfterFlow.put(s, newInitialFlow());
					afterFlow = newInitialFlow();
				}
				copy(afterFlow, previousAfterFlow);

				if (DEBUG) out.println(" [<"+node.getNodeID()+">]-before:" + beforeFlow);		
				// Traverse the node,  
				{
					// Compute afterFlow and store it.
					flowThrough(beforeFlow, s, afterFlow);
					copy(afterFlow, currentAfterFlow);
					unitToAfterFlow.put(s, afterFlow);
					numComputations++;
				}
				if (DEBUG) out.println(" [<"+node.getNodeID()+">]-after :" + afterFlow);
			}
		}
    }

	// special cases for branching nodes: BreakStmt, ContinueStmt, ReturnStmt
	public void caseBranchingStmt(ASTNode node)
	{
		// Add it into visited node list  
		if(!NodeList.contains(node)) 	
			NodeList.add(node);

		// The branching statement doesn't change the flow sets			
		A afterFlow = newInitialFlow();
		copy(currentAfterFlow, afterFlow);
		unitToAfterFlow.put((N) node, afterFlow);
		A previousBeforeFlow = newInitialFlow();
		copy(currentAfterFlow, previousBeforeFlow);
		unitToBeforeFlow.put((N)node, previousBeforeFlow);
		
		// The flowset will not flow to next statement, so it becomes empty
		copy(newInitialFlow(), currentAfterFlow);

		// Get the current break/continue flow-set list, add to it
		if(!loopStack.empty()) {
			LoopFlowsetList<A> loopNode = loopStack.peek();		
			if(node instanceof BreakStmt) {
				loopNode.BreakFlowsetList.add(afterFlow);
			} else if(node instanceof ContinueStmt) {
				loopNode.ContinueFlowsetList.add(afterFlow);
			} else if(node instanceof ReturnStmt) {
				// ToDo ...
			}			
		}
	}
	
	// special cases for WhileStmt, ForStmt 
	public void caseLoopStmt(ASTNode node) 
	{
		// Add it into visited node list  
		if(!NodeList.contains(node)) 	
			NodeList.add(node);
		
		if (DEBUG) 
			out.println("ASTWalker: caseLoopStmt " + node.getNodeID());

		// push the node into stack, so inside statements can know which loop current in
		loopStack.push(new LoopFlowsetList<A>(node));
			
		// Save initial after set
		A previousAfterFlow = newInitialFlow();
		A savedAfterFlow = newInitialFlow();
		
		copy(currentAfterFlow, previousAfterFlow);
		if (DEBUG) out.println(" [caseLoopStmt]-curr  :" + currentAfterFlow);

		A previousBeforeFlow = newInitialFlow();
		copy(currentAfterFlow, previousBeforeFlow);
		unitToBeforeFlow.put((N)node, previousBeforeFlow);

		// Perform fixed point flow analysis
		do {
			// save the original flow-set, for later comparison
			copy(previousAfterFlow, savedAfterFlow);
			if (DEBUG) out.println(" [caseLoopStmt]-before:" + previousAfterFlow);
			if (DEBUG) out.println(" [caseLoopStmt]-bef-cur:" + currentAfterFlow);

			// flow through all child-nods
			node.applyAllChild(this);
	        
			numComputations++;
			
			merge(currentAfterFlow, previousAfterFlow, previousAfterFlow);

			if (DEBUG) out.println(" [caseLoopStmt]-curr  :" + currentAfterFlow);
			if (DEBUG) out.println(" [caseLoopStmt]-after :" + previousAfterFlow);
			
			// Merge with continue FlowSet lists
			if(!loopStack.empty() && loopStack.peek()!=null) {
				for(A flowset: loopStack.peek().ContinueFlowsetList) {
					merge(flowset, previousAfterFlow, previousAfterFlow);
				}
			}

			// Using new after flow to recompute
			copy(previousAfterFlow, currentAfterFlow);
			
		} while (!previousAfterFlow.equals(savedAfterFlow));

		// Update the loop-node's after set
		unitToAfterFlow.put((N) node, previousAfterFlow);
		if (DEBUG) out.println(" [caseLoopStmt]-loop:" + previousAfterFlow);

		// Updating the current after flow-set
		copy(previousAfterFlow, currentAfterFlow);
		
		// The current after flow-set merges with break FlowSet lists
		if(!loopStack.empty() && loopStack.peek()!=null) {
			for(A flowset: loopStack.peek().BreakFlowsetList) {
				merge(flowset, currentAfterFlow, currentAfterFlow);
			}
		}		
		loopStack.pop();
	}
	
	// special cases for If statement 
	public void caseIfStmt(IfStmt node) 
	{	
		// Add it into visited node list  
		if(!NodeList.contains(node)) 	
			NodeList.add(node);
			
		if (DEBUG) 
			out.println("ASTWalker: caseIfStmt  " + node.getNodeID());
		
		// Save initial after set
		A previousBeforeFlow = newInitialFlow();
		A previousAfterFlow = newInitialFlow();
		
		copy(currentAfterFlow, previousBeforeFlow);
		unitToBeforeFlow.put((N)node, previousBeforeFlow);
		
		// Save the current after flow-set
		A NodeAfterFlow = newInitialFlow();
		copy(currentAfterFlow, NodeAfterFlow);
		unitToAfterFlow.put((N) node, NodeAfterFlow);	

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
        } else {
        	// Merge with initial in-set  
			merge(previousBeforeFlow, previousAfterFlow, previousAfterFlow);
        }
			
		// Updating the current after flow-set
		copy(previousAfterFlow, currentAfterFlow);
		// Don't update the IfStmt with the after-set of END, there is not back edge.
		// unitToAfterFlow.put((N) node, previousAfterFlow);	
	}
	
	// special cases for Switch statement 
	// SwitchStmt : Stmt ::= Expr SwitchCaseBlock* [DefaultCaseBlock];
	public void caseSwitchStmt(SwitchStmt node)
	{	
		// Add it into visited node list  
		if(!NodeList.contains(node)) 	
			NodeList.add(node);
			
		if (DEBUG) 
			out.println("ASTWalker: caseSwitchStmt  " + node.getNodeID());
		
		// Save initial after set
		A previousBeforeFlow = newInitialFlow();
		A previousAfterFlow = newInitialFlow();
		
		copy(currentAfterFlow, previousBeforeFlow);
		unitToBeforeFlow.put((N)node, previousBeforeFlow);
		
		// Save the current after flow-set
		A NodeAfterFlow = newInitialFlow();
		copy(currentAfterFlow, NodeAfterFlow);
		unitToAfterFlow.put((N) node, NodeAfterFlow);	

		// Get after-set from each branch 
        for(SwitchCaseBlock block : node.getSwitchCaseBlocks()) {
        	// using same before set for each branch
    		copy(previousBeforeFlow, currentAfterFlow);
            block.apply(this);
            // merge the result flow sets 
			merge(currentAfterFlow, previousAfterFlow, previousAfterFlow);
        }
        if(node.hasDefaultCaseBlock()) {
    		copy(previousBeforeFlow, currentAfterFlow);
            node.getDefaultCaseBlock().apply(this);
			merge(currentAfterFlow, previousAfterFlow, previousAfterFlow);
        } else {
        	// Merge with initial in-set  
			merge(previousBeforeFlow, previousAfterFlow, previousAfterFlow);
        }
			
		// Updating the current after flow-set
		copy(previousAfterFlow, currentAfterFlow);
		// Don't update the node with the after-set of END, there is not back edge.
		// unitToAfterFlow.put((N) node, previousAfterFlow);	
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
	// Return result of analysis, After-flow
    public Map<N, A> getResult() {
    	return unitToAfterFlow; 
    }
	// Return result of analysis, before-flow
    public Map<N, A> getBeforeFlow() {
    	return unitToBeforeFlow; 
    }
}

// Data class includs Break and Continue Flowset List
class LoopFlowsetList<A> {
	List<A> BreakFlowsetList = new ArrayList<A>();
	List<A> ContinueFlowsetList = new ArrayList<A>();
	ASTNode loopNode;
	public LoopFlowsetList (ASTNode node) {
		loopNode = node;
	}
}