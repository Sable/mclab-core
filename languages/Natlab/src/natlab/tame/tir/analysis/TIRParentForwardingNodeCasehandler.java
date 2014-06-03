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

import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRAbstractCreateFunctionHandleStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRAssignLiteralStmt;
import natlab.tame.tir.TIRBreakStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArrayGetStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRCommentStmt;
import natlab.tame.tir.TIRContinueStmt;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRCreateFunctionReferenceStmt;
import natlab.tame.tir.TIRCreateLambdaStmt;
import natlab.tame.tir.TIRDotGetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRGlobalStmt;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRPersistentSmt;
import natlab.tame.tir.TIRReturnStmt;
import natlab.tame.tir.TIRStatementList;
import natlab.tame.tir.TIRStmt;
import natlab.tame.tir.TIRTryStmt;
import natlab.tame.tir.TIRWhileStmt;
import nodecases.NodeCaseHandler;
import analysis.ForwardingNodeCaseHandler;
import ast.Stmt;

/**
 * This is a TIRNodeCaseHandler, that, analogous to AbstractNodeCaseHandler,
 * forwards every call to the parent case of a supplied 
 * (NodeCaseHandler & TIRNodeCaseHandler)
 * 
 * For ast nodes, the call is forwarded to the same case of the callback
 * 
 * 
 * This can be used as a mixin, together with an AbstractNodeCasehandler
 * to handle all nodes, using a TIRNodeForwarder to call analyze on ast nodes.
 * 
 * Note that this should only the case of actual parent classes
 * - thus TIRStmt and TIRNode, being interfaces, will nevr be called
 * 
 * @author ant6n
 *
 */


public class TIRParentForwardingNodeCasehandler extends ForwardingNodeCaseHandler implements TIRNodeCaseHandler {
    NodeCaseHandler callback; //these two refer to the same variable
    TIRNodeCaseHandler irCallback; 
    
    public <T extends NodeCaseHandler & TIRNodeCaseHandler>
    TIRParentForwardingNodeCasehandler(T handler) {
        super(handler);
        this.callback = handler;
        this.irCallback = handler;
    }

    /**
     * a constructor that uses two different handlers for the callbacks to the
     * AST nodes and the IR nodes
     * @param handler
     * @param irHandler
     */
    public TIRParentForwardingNodeCasehandler(NodeCaseHandler handler, TIRNodeCaseHandler irHandler) {
        super(handler);
        this.callback = handler;
        this.irCallback = irHandler;
    }
    
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node) {
        callback.caseAssignStmt(node);
    }

    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node) {
        irCallback.caseTIRAbstractAssignStmt(node);
    }

    @Override
    public void caseTIRAbstractAssignFromVarStmt(TIRAbstractAssignFromVarStmt node){
        irCallback.caseTIRAbstractAssignStmt(node);
    }
    
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt node) {
        irCallback.caseTIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt node) {
        irCallback.caseTIRAbstractAssignFromVarStmt(node);
    }

    @Override
    public void caseTIRCopyStmt(TIRCopyStmt node) {
        irCallback.caseTIRAbstractAssignToVarStmt(node);
    }
    
    @Override
    public void caseTIRAbstractCreateFunctionHandleStmt(TIRAbstractCreateFunctionHandleStmt node) {
        irCallback.caseTIRAbstractAssignToVarStmt(node);
    }
    
    @Override
    public void caseTIRCreateFunctionReferenceStmt(
    		TIRCreateFunctionReferenceStmt node) {
    	irCallback.caseTIRAbstractCreateFunctionHandleStmt(node);
    }
    
    @Override
    public void caseTIRCreateLambdaStmt(TIRCreateLambdaStmt node) {
    	irCallback.caseTIRAbstractCreateFunctionHandleStmt(node);
    }

    @Override
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt node) {
        irCallback.caseTIRAbstractAssignToVarStmt(node);
    }

    @Override
    public void caseTIRAbstractAssignToVarStmt(TIRAbstractAssignToVarStmt node) {
        irCallback.caseTIRAbstractAssignStmt(node);
    }

    @Override
    public void caseTIRCallStmt(TIRCallStmt node) {
        irCallback.caseTIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseTIRCellArrayGetStmt(TIRCellArrayGetStmt node) {
        irCallback.caseTIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node) {
        irCallback.caseTIRAbstractAssignFromVarStmt(node);
    }

    @Override
    public void caseTIRCommaSeparatedList(TIRCommaSeparatedList node) {
        callback.caseList(node);
    }

    @Override
    public void caseTIRCommentStmt(TIRCommentStmt node) {
        callback.caseEmptyStmt(node);
    }

    @Override
    public void caseTIRForStmt(TIRForStmt node) {
        callback.caseForStmt(node);
    }

    @Override
    public void caseTIRFunction(TIRFunction node) {
        callback.caseFunction(node);
    }

    @Override
    public void caseTIRIfStmt(TIRIfStmt node) {
        callback.caseIfStmt(node);
    }

    @Override
    public void caseTIRStatementList(TIRStatementList node) {
        callback.caseList(node);
    }

    @Override
    public void caseTIRStmt(TIRStmt node) {
        callback.caseStmt((Stmt)node);
    }

    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node) {
        callback.caseWhileStmt(node);
    }

    @Override
    public void caseTIRBreakStmt(TIRBreakStmt node) {
        callback.caseBreakStmt(node);
    }

    @Override
    public void caseTIRContinueStmt(TIRContinueStmt node) {
        callback.caseContinueStmt(node);
    }

    @Override
    public void caseTIRDotGetStmt(TIRDotGetStmt node) {
        irCallback.caseTIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseTIRDotSetStmt(TIRDotSetStmt node) {
        irCallback.caseTIRAbstractAssignFromVarStmt(node);
    }

    @Override
    public void caseTIRGlobalStmt(TIRGlobalStmt node) {
        callback.caseGlobalStmt(node);
    }

    @Override
    public void caseTIRPersistentStmt(TIRPersistentSmt node) {
        callback.casePersistentStmt(node);
    }

    @Override
    public void caseTIRTryStmt(TIRTryStmt node) {
        callback.caseTryStmt(node);
    }
    
    public void caseTIRReturnStmt(TIRReturnStmt node){
        callback.caseReturnStmt(node);
    }

}


