package natlab.Static.ir;

import natlab.Static.toolkits.analysis.IRNodeCaseHandler;
import beaver.*;
import ast.*;

/**
 * An empty statement
 * All comments should be stored in here
 * Any analysis or rewrite should just ignore these.
 * Also acts as an empty line
 */
public class IRCommentStmt extends EmptyStmt implements IRStmt {
    public IRCommentStmt(){}
    public IRCommentStmt(java.util.List<Symbol> comments){
        this.setComments(comments);
    }
    public IRCommentStmt(Symbol comment){
        this.addComment(comment);
    }
    
    /**
     * creates a comment statement from a given string, adding "% " at the beginning.
     */
    public IRCommentStmt(String comment){
        this(new beaver.Symbol("% "+comment)); 
        //TODO - should be done via parsing the stmt "% "+comment  
    }
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRCommentStmt(this);
    }

}
