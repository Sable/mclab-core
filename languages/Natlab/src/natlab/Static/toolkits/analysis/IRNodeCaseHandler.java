package natlab.Static.toolkits.analysis;
import natlab.Static.ir.*;
import natlab.toolkits.analysis.*;

/**
 * extension of the node case handler to handle IRNode cases.
 * Analaguous to NodeCaseHandler
 * 
 * note that IRNode and IRStmt are just interfaces
 * See IRNodeForwarder, a helper to extend AbstractNodeCaseHandler 
 * 
 * 
 * @author ant6n
 */

public interface IRNodeCaseHandler {
    
    //all the statements
    //parent stmt
    public void caseIRStmt(IRStmt node);
    //all assignment statements
    public void caseIRAbstractAssignStmt(IRAbstractAssignStmt node);
    public void caseIRAbstractAssignToListStmt(IRAbstractAssignToListStmt node);
    public void caseIRAbstractAssignToVarStmt(IRAbstractAssignToVarStmt node);
    public void caseIRArrayGetStmt(IRArrayGetStmt node);
    public void caseIRArraySetStmt(IRArraySetStmt node);
    public void caseIRAssignFunctionHandleStmt(IRAssignFunctionHandleStmt node);
    public void caseIRAssignLiteralStmt(IRAssignLiteralStmt node);
    public void caseIRCallStmt(IRCallStmt node);
    public void caseIRCellArrayGetStmt(IRCellArrayGet node);
    public void caseIRCellArraySetStmt(IRCellArraySet node);
    //all other stmts
    public void caseIRCommentStmt(IRCommentStmt node);
    public void caseIRForStmt(IRForStmt node);
    public void caseIRIfStmt(IRIfStmt node);
    public void caseIRWhileStmt(IRWhileStmt node);
    
    
    //non statement nodes
    public void caseIRFunction(IRFunction node);
    public void caseIRStatementList(IRStatementList node);
    public void caseIRCommaSeparatedList(IRCommaSeparatedList node);
   
}
