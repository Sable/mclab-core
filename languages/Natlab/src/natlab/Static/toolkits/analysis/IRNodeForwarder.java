package natlab.Static.toolkits.analysis;


import natlab.Static.ir.*;
import natlab.toolkits.analysis.*;
import ast.*;


/**
 *  This is a NodeCaseHandler which extends ForwardingnodeCaseHandler.
 *  For every case it will call back to the same case, except for nodes
 *  which have specializations in the IR - it will then dispatch to the
 *  correct node.
 *  
 *  example
 *  casePlusExpr(node) 
 *    -> callback.casePlusExpr(node)
 *  caseAssignStmt(node)
 *    -> callback.caseIRArrayGetStmt(node)  (if node is an IRArrayGet, etc. etc.)
 * 
 * @author ant6n
 */


public class IRNodeForwarder extends ForwardingNodeCaseHandler  {
    IRNodeCaseHandler irHandler;
    
    public <T extends AbstractNodeCaseHandler & IRNodeCaseHandler> IRNodeForwarder(
            T handler){
        super(handler);
        this.irHandler = handler;
    }
    
    
    //*** all nodes that have specialized IR node types ***************************
    //non-stmt nodes
    //list is comma separated list or statement list
    @Override
    public void caseList(List node) {
        if (node instanceof IRNode){
            ((IRStmt) node).irAnalyize(irHandler);
        } else {
            super.caseList(node);
        }
    }
    
    @Override
    public void caseFunction(Function node) {
        if (node instanceof IRNode){
            ((IRStmt) node).irAnalyize(irHandler);
        } else {
            super.caseFunction(node);
        }
    }
    
    
    //stmts
    @Override
    public void caseAssignStmt(AssignStmt node) {
        if (node instanceof IRNode){
            ((IRStmt) node).irAnalyize(irHandler);
        } else {
            super.caseAssignStmt(node);
        }
    }
    
    @Override
    public void caseEmptyStmt(EmptyStmt node) {
        if (node instanceof IRStmt){
            ((IRStmt) node).irAnalyize(irHandler);
        } else {
            super.caseEmptyStmt(node);
        }
    }

    @Override
    public void caseForStmt(ForStmt node) {
        if (node instanceof IRStmt){
            ((IRStmt) node).irAnalyize(irHandler);
        } else {
            super.caseForStmt(node);
        }
    }

    @Override
    public void caseIfStmt(IfStmt node) {
        if (node instanceof IRStmt){
            ((IRStmt) node).irAnalyize(irHandler);
        } else {
            super.caseIfStmt(node);
        }
    }    
    
    @Override
    public void caseWhileStmt(WhileStmt node) {
        if (node instanceof IRStmt){
            ((IRStmt) node).irAnalyize(irHandler);
        } else {
            super.caseWhileStmt(node);
        }
    }
    
    
}
