package natlab.toolkits.rewrite.simplification;

/**
 * This simplification moves comments, that is attached to code, into
 * empty statements. Thus, other rewrites won't accidentally remove comments,
 * and don't have to be explicitly written to preserve them.
 * 
 * Thus this should always be the first step when using rewrites that don't
 * preserve comments, if preserving comments is an objective.
 */

import java.util.*;

import beaver.Symbol;

import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.EmptyStmt;
import ast.Stmt;

public class CommentSimplification extends AbstractSimplification {

    public CommentSimplification(ASTNode tree, VFPreorderAnalysis kind) {
        super(tree, kind);
    }

    /**
     * this simplification depends on nothing
     */
    public Set<Class<? extends AbstractSimplification>> getDependencies() {
        return new HashSet<Class<? extends AbstractSimplification>>();
    }

    /** - this doesn't work!!
     * error case ASTNode - if any ASTNode has comments but is not covered
     * by the implemented cases, throw unsupported operation exception
     *
    public void caseASTNode(ASTNode node) {
        if (node.hasComments()) throw new UnsupportedOperationException(
                "CommentSimplification found comments on unexpected node "
                +node.getClass().getName()+":"+node.getPrettyPrinted()+", child of "
                +node.getParent().getClass().getName()
                );
        super.caseASTNode(node);
    }*/
    
    
    
    /**
     * don't do anything for empty statements
     */
    public void caseEmptyStmt(EmptyStmt node) {
    }
    
    /**
     * for general statements, put each comment attached to the statement into
     * its own empty statement following the statement
     */
    @Override
    public void caseStmt(Stmt node) {
        rewriteChildren(node); //recursively rewrite children
        newNode = new TransformedNode(node);
        newNode.addAll(commentsToEmptyStmtList(removeComments(node)));
    }
    
    /**
     * removes all comments from the given node, and returns them
     * @param node
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Symbol> removeComments(ASTNode node) {
        List<Symbol> list = node.getComments();
        node.setComments(null); 
        return list;
    }
    
    private List<Stmt> commentsToEmptyStmtList(List<Symbol> comments){
        List<Stmt> list = new ArrayList<Stmt>();
        for (Symbol c : comments){
             EmptyStmt stmt = new EmptyStmt();
             stmt.addComment(c);
             list.add(stmt);
        }
        return list;
    }

}
