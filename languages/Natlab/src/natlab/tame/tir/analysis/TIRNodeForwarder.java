// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.tame.tir.analysis;


import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRStmt;
import nodecases.AbstractNodeCaseHandler;
import analysis.ForwardingNodeCaseHandler;
import ast.AssignStmt;
import ast.BreakStmt;
import ast.ContinueStmt;
import ast.EmptyStmt;
import ast.ForStmt;
import ast.Function;
import ast.GlobalStmt;
import ast.IfStmt;
import ast.PersistentStmt;
import ast.ReturnStmt;
import ast.TryStmt;
import ast.WhileStmt;


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


public class TIRNodeForwarder extends ForwardingNodeCaseHandler  {
    TIRNodeCaseHandler irHandler;
    
    public <T extends AbstractNodeCaseHandler & TIRNodeCaseHandler> TIRNodeForwarder(
            T handler){
        super(handler);
        this.irHandler = handler;
    }
    
    
    //*** all nodes that have specialized IR node types ***************************
    //non-stmt nodes
    //list is comma separated list or statement list
    @Override
    @SuppressWarnings("unchecked")
    public void caseList(ast.List node) {
        if (node instanceof TIRNode){
            ((TIRNode) node).tirAnalyze(irHandler);
        } else {
            super.caseList(node);
        }
    }
    
    @Override
    public void caseFunction(Function node) {
        if (node instanceof TIRNode){
            ((TIRFunction) node).tirAnalyze(irHandler);
        } else {
            super.caseFunction(node);
        }
    }
    
    
    //stmts
    @Override
    public void caseAssignStmt(AssignStmt node) {
        if (node instanceof TIRNode){
            ((TIRStmt) node).tirAnalyze(irHandler);
        } else {
        	super.caseAssignStmt(node);
        }
    }
    
    @Override
    public void caseEmptyStmt(EmptyStmt node) {
        if (node instanceof TIRStmt){
            ((TIRStmt) node).tirAnalyze(irHandler);
        } else {
            super.caseEmptyStmt(node);
        }
    }

    @Override
    public void caseForStmt(ForStmt node) {
        if (node instanceof TIRStmt){
            ((TIRStmt) node).tirAnalyze(irHandler);
        } else {
            super.caseForStmt(node);
        }
    }

    @Override
    public void caseIfStmt(IfStmt node) {
        if (node instanceof TIRStmt){
            ((TIRStmt) node).tirAnalyze(irHandler);
        } else {
            super.caseIfStmt(node);
        }
    }    
    
    @Override
    public void caseWhileStmt(WhileStmt node) {
        if (node instanceof TIRStmt){
            ((TIRStmt) node).tirAnalyze(irHandler);
        } else {
            super.caseWhileStmt(node);
        }
    }
    
    @Override
    public void caseBreakStmt(BreakStmt node) {
        if (node instanceof TIRStmt){
            ((TIRStmt) node).tirAnalyze(irHandler);
        } else {
            super.caseBreakStmt(node);
        }
    }
    
    @Override
    public void caseContinueStmt(ContinueStmt node) {
        if (node instanceof TIRStmt){
            ((TIRStmt) node).tirAnalyze(irHandler);
        } else {
            super.caseContinueStmt(node);
        }
    }
    
    @Override
    public void caseReturnStmt(ReturnStmt node) {
        if (node instanceof TIRStmt){
            ((TIRStmt) node).tirAnalyze(irHandler);
        } else {
            super.caseReturnStmt(node);
        }
    }
    
    @Override
    public void caseGlobalStmt(GlobalStmt node) {
        if (node instanceof TIRStmt){
            ((TIRStmt) node).tirAnalyze(irHandler);
        } else {
            super.caseGlobalStmt(node);
        }
    }
    
    @Override
    public void casePersistentStmt(PersistentStmt node) {
        if (node instanceof TIRStmt){
            ((TIRStmt) node).tirAnalyze(irHandler);
        } else {
            super.casePersistentStmt(node);
        }
    }
    
    @Override
    public void caseTryStmt(TryStmt node) {
        if (node instanceof TIRStmt){
            ((TIRStmt) node).tirAnalyze(irHandler);
        } else {
            super.caseTryStmt(node);
        }
    }
}
