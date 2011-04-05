package natlab.Static.ir;

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
}
