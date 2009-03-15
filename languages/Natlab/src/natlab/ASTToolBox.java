package natlab;

import natlab.ast.*;

public class ASTToolBox {

	// Find the close parent node, whose parent is a statment list  
 	public static Stmt getParentStmtNode(ASTNode varNode) {
	    ASTNode parentNode = varNode.getParent();
	    do{
		    if((varNode instanceof Stmt)) {
		    	parentNode = varNode;
		    } else {
			    // varNode/e.getNodeLocation() is NameExpr, it may directly belongs to
		    	// AssignStmt, or ParameterizedExpr.
		    	while ((parentNode!=null) && !(parentNode instanceof Stmt)) {
		    		varNode = parentNode;
		    		parentNode = parentNode.getParent(); 	   
		    	}
		    }
	    } while((parentNode!=null) && !(parentNode instanceof Stmt));
	    
	    return (Stmt) parentNode;
 	}

	// Find the close parent node 
 	public static AssignStmt getParentAssignStmtNode(ASTNode varNode) {
	    ASTNode parentNode = varNode.getParent();
	    // If the varNode is already the assignment-statement, then don't need 
	    // to find it's parent
	    if((varNode instanceof AssignStmt)) {
	    	parentNode = varNode;
	    } else {
		    // varNode/e.getNodeLocation() is NameExpr, it may directly belongs to
	    	// AssignStmt, or ParameterizedExpr.
	    	while ((parentNode!=null) && !(parentNode instanceof AssignStmt)) {
	    		varNode = parentNode;
	    		parentNode = parentNode.getParent(); 	   
	    	}
	    }
	    return (AssignStmt)parentNode;
 	}
	// Find the close parent node 
 	public static natlab.ast.List getParentStmtListNode(ASTNode varNode) {
	    ASTNode parentNode = varNode.getParent();
	    // If the varNode is already the assignment-statement, then don't need 
	    // to find it's parent
	    if((varNode instanceof natlab.ast.List)) {
	    	parentNode = varNode;
	    } else {
		    // varNode/e.getNodeLocation() is NameExpr, it may directly belongs to
	    	// AssignStmt, or ParameterizedExpr.
	    	while ((parentNode!=null) && !(parentNode instanceof natlab.ast.List)) {
	    		varNode = parentNode;
	    		parentNode = parentNode.getParent(); 	   
	    	}
	    }
	    return (natlab.ast.List) parentNode;
 	}
	
 	public static boolean isInsideArray(ASTNode varNode) {
 		boolean bResult = false;
	    ASTNode parentNode = varNode;
	    do{
		    if((parentNode instanceof AssignStmt) ) {
		    	break;
		    } if((parentNode instanceof ParameterizedExpr) ) {
		    	bResult = true;
		    	break;
		    } else {
	    		parentNode = parentNode.getParent(); 	   
		    }
	    } while((parentNode!=null) && !bResult);
	    
	    return bResult;
 	}

}
