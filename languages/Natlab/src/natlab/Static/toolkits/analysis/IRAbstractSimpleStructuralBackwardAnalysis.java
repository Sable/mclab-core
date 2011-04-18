package natlab.Static.toolkits.analysis;

import ast.ASTNode;
import natlab.Static.ir.IRAbstractAssignStmt;
import natlab.Static.ir.IRAbstractAssignToListStmt;
import natlab.Static.ir.IRAbstractAssignToVarStmt;
import natlab.Static.ir.IRArrayGetStmt;
import natlab.Static.ir.IRArraySetStmt;
import natlab.Static.ir.IRAssignFunctionHandleStmt;
import natlab.Static.ir.IRAssignLiteralStmt;
import natlab.Static.ir.IRCallStmt;
import natlab.Static.ir.IRCellArrayGet;
import natlab.Static.ir.IRCellArraySet;
import natlab.Static.ir.IRCommaSeparatedList;
import natlab.Static.ir.IRCommentStmt;
import natlab.Static.ir.IRForStmt;
import natlab.Static.ir.IRFunction;
import natlab.Static.ir.IRIfStmt;
import natlab.Static.ir.IRStatementList;
import natlab.Static.ir.IRStmt;
import natlab.Static.ir.IRWhileStmt;
import natlab.toolkits.analysis.AbstractSimpleStructuralBackwardAnalysis;
import natlab.toolkits.analysis.FlowSet;
import natlab.toolkits.analysis.NodeCaseHandler;

public abstract class IRAbstractSimpleStructuralBackwardAnalysis<F extends FlowSet> extends
        AbstractSimpleStructuralBackwardAnalysis implements IRNodeCaseHandler {

    IRNodeForwarder nodeForwarder = new IRNodeForwarder(this);
    IRParentForwardingNodeCasehandler parentForwarder = new IRParentForwardingNodeCasehandler(this);
    
    public IRAbstractSimpleStructuralBackwardAnalysis(ASTNode tree) {
        super(tree);
        super.setCallback(nodeForwarder);
    }


    /**
     * setting the callback is idsallowed for ir analyses, and will throw an
     * UnsupportedOperation exception.
     */
    @Override
    public void setCallback(NodeCaseHandler handler) {
        throw new UnsupportedOperationException(
                "cannot override callback for "+this.getClass().getName()+
                ", callback is already overriden.");
    }

    
    
    
    //*** mixed in NodeCaseHandler, using the parentForwarder **********************
    @Override
    public void caseIRAbstractAssignStmt(IRAbstractAssignStmt node) {
        parentForwarder.caseIRAbstractAssignStmt(node);
    }
    @Override
    public void caseIRAbstractAssignToListStmt(IRAbstractAssignToListStmt node) {
        parentForwarder.caseIRAbstractAssignStmt(node);
    }
    @Override
    public void caseIRAbstractAssignToVarStmt(IRAbstractAssignToVarStmt node) {
        parentForwarder.caseIRAbstractAssignToVarStmt(node);
    }
    @Override
    public void caseIRArrayGetStmt(IRArrayGetStmt node) {
        parentForwarder.caseIRArrayGetStmt(node);
    }
    @Override
    public void caseIRArraySetStmt(IRArraySetStmt node) {
        parentForwarder.caseIRArraySetStmt(node);
    }
    @Override
    public void caseIRAssignFunctionHandleStmt(IRAssignFunctionHandleStmt node) {
        parentForwarder.caseIRAssignFunctionHandleStmt(node);
    }
    @Override
    public void caseIRAssignLiteralStmt(IRAssignLiteralStmt node) {
        parentForwarder.caseIRAssignLiteralStmt(node);
    }
    @Override
    public void caseIRCallStmt(IRCallStmt node) {
        parentForwarder.caseIRCallStmt(node);
    }
    @Override
    public void caseIRCellArrayGetStmt(IRCellArrayGet node) {
        parentForwarder.caseIRCellArrayGetStmt(node);
    }
    @Override
    public void caseIRCellArraySetStmt(IRCellArraySet node) {
        parentForwarder.caseIRCellArraySetStmt(node);
    }
    @Override
    public void caseIRCommaSeparatedList(IRCommaSeparatedList node) {
        parentForwarder.caseIRCommaSeparatedList(node);
    }
    @Override
    public void caseIRCommentStmt(IRCommentStmt node) {
        parentForwarder.caseIRCommentStmt(node);
    }
    @Override
    public void caseIRForStmt(IRForStmt node) {
        parentForwarder.caseIRForStmt(node);
    }
    @Override
    public void caseIRFunction(IRFunction node) {
        parentForwarder.caseIRFunction(node);
    }
    @Override
    public void caseIRIfStmt(IRIfStmt node) {
        parentForwarder.caseIRIfStmt(node);
    }
    @Override
    public void caseIRStatementList(IRStatementList node) {
        parentForwarder.caseIRStatementList(node);
    }
    @Override
    public void caseIRStmt(IRStmt node) {
        parentForwarder.caseIRStmt(node);
    }
    @Override
    public void caseIRWhileStmt(IRWhileStmt node) {
        parentForwarder.caseIRStmt(node);
    }

}
