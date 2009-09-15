package natlab;

import ast.*;

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
 	public static ast.List getParentStmtListNode(ASTNode varNode) {
	    ASTNode parentNode = varNode.getParent();
	    // If the varNode is already the assignment-statement, then don't need 
	    // to find it's parent
	    if((varNode instanceof ast.List)) {
	    	parentNode = varNode;
	    } else {
		    // varNode/e.getNodeLocation() is NameExpr, it may directly belongs to
	    	// AssignStmt, or ParameterizedExpr.
	    	while ((parentNode!=null) && !(parentNode instanceof ast.List)) {
	    		varNode = parentNode;
	    		parentNode = parentNode.getParent(); 	   
	    	}
	    }
	    return (ast.List) parentNode;
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

 	public static boolean isInsideLoop(ASTNode varNode) {
 		boolean bResult = false;
	    ASTNode parentNode = varNode;
	    do{
		    if((parentNode instanceof WhileStmt) 
		    		|| (parentNode instanceof ForStmt)) {
		    	bResult = true;
		    	break;
		    } else {
	    		parentNode = parentNode.getParent(); 	   
		    }
	    } while((parentNode!=null) && !(parentNode instanceof Program) && !bResult);
	    
	    return bResult;
 	}
 	public static ForStmt getParentForStmtNode(ASTNode varNode) {
	    ASTNode parentNode = varNode.getParent();
	    // If the varNode is already the assignment-statement, then don't need 
	    // to find it's parent
	    if((varNode instanceof ForStmt)) {
	    	parentNode = varNode;
	    } else {
		    // varNode/e.getNodeLocation() is NameExpr, it may directly belongs to
	    	// AssignStmt, or ParameterizedExpr.
	    	while ((parentNode!=null) && !(parentNode instanceof ForStmt)) {
	    		varNode = parentNode;
	    		parentNode = parentNode.getParent(); 	   
	    	}
	    }
	    return (ForStmt)parentNode;
 	}

 	public static IfStmt getParentIfStmtNode(ASTNode varNode) {
	    ASTNode parentNode = varNode.getParent();
	    // If the varNode is already the assignment-statement, then don't need 
	    // to find it's parent
	    if((varNode instanceof IfStmt)) {
	    	parentNode = varNode;
	    } else {
		    // varNode/e.getNodeLocation() is NameExpr, it may directly belongs to
	    	// AssignStmt, or ParameterizedExpr.
	    	while ((parentNode!=null) && !(parentNode instanceof IfStmt)) {
	    		varNode = parentNode;
	    		parentNode = parentNode.getParent(); 	   
	    	}
	    }
	    return (IfStmt)parentNode;
 	}
 	public static ASTNode getSubtreeRoot(ASTNode varNode) {
	    ASTNode parentNode = varNode.getParent();
	    // If the varNode is already the assignment-statement, then don't need 
	    // to find it's parent
	    if((varNode instanceof Script)|| (varNode instanceof Function)) {
	    	parentNode = varNode;
	    } else {
		    // varNode/e.getNodeLocation() is NameExpr, it may directly belongs to
	    	// AssignStmt, or ParameterizedExpr.
	    	while ((parentNode!=null) && 
	    			!((parentNode instanceof Script)||(parentNode instanceof Function))) {
	    		varNode = parentNode;
	    		parentNode = parentNode.getParent(); 	   
	    	}
	    }
	    return parentNode;
 	}
}
