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

package natlab.Static.ir.analysis;

import ast.*;
import natlab.Static.ir.*;
import analysis.*;
import nodecases.*;
import natlab.toolkits.analysis.*;

/**
 * This is a IRNodeCaseHandler, that, analogous to AbstractNodeCaseHandler,
 * forwards every call to the parent case of a supplied 
 * (NodeCaseHandler & IRNodeCaseHandler)
 * 
 * For ast nodes, the call is forwarded to the same case of the callback
 * 
 * 
 * This can be used as a mixin, together with an AbstractNodeCasehandler
 * to handle all nodes, using a IRNodeForwarder to call analyze on ast nodes.
 * 
 * Note that this should only the case of actual parent classes
 * - thus IRStmt and IRNode, being interfaces, will nevr be called
 * 
 * @author ant6n
 *
 */


public class IRParentForwardingNodeCasehandler extends ForwardingNodeCaseHandler implements IRNodeCaseHandler {
    NodeCaseHandler callback; //these two refer to the same variable
    IRNodeCaseHandler irCallback; 
    
    public <T extends NodeCaseHandler & IRNodeCaseHandler>
    IRParentForwardingNodeCasehandler(T handler) {
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
    public IRParentForwardingNodeCasehandler(NodeCaseHandler handler, IRNodeCaseHandler irHandler) {
        super(handler);
        this.callback = handler;
        this.irCallback = irHandler;
    }
    
    
    @Override
    public void caseIRAbstractAssignStmt(IRAbstractAssignStmt node) {
        callback.caseAssignStmt(node);
    }

    @Override
    public void caseIRAbstractAssignToListStmt(IRAbstractAssignToListStmt node) {
        irCallback.caseIRAbstractAssignStmt(node);
    }

    @Override
    public void caseIRArrayGetStmt(IRArrayGetStmt node) {
        irCallback.caseIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseIRArraySetStmt(IRArraySetStmt node) {
        irCallback.caseIRAbstractAssignStmt(node);
    }

    @Override
    public void caseIRAssignFunctionHandleStmt(IRAssignFunctionHandleStmt node) {
        irCallback.caseIRAbstractAssignToVarStmt(node);
    }

    @Override
    public void caseIRAssignLiteralStmt(IRAssignLiteralStmt node) {
        irCallback.caseIRAbstractAssignToVarStmt(node);
    }

    @Override
    public void caseIRAbstractAssignToVarStmt(IRAbstractAssignToVarStmt node) {
        irCallback.caseIRAbstractAssignStmt(node);
    }

    @Override
    public void caseIRCallStmt(IRCallStmt node) {
        irCallback.caseIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseIRCellArrayGetStmt(IRCellArrayGetStmt node) {
        irCallback.caseIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseIRCellArraySetStmt(IRCellArraySetStmt node) {
        irCallback.caseIRAbstractAssignStmt(node);
    }

    @Override
    public void caseIRCommaSeparatedList(IRCommaSeparatedList node) {
        callback.caseList(node);
    }

    @Override
    public void caseIRCommentStmt(IRCommentStmt node) {
        callback.caseEmptyStmt(node);
    }

    @Override
    public void caseIRForStmt(IRForStmt node) {
        callback.caseRangeForStmt(node);
    }

    @Override
    public void caseIRFunction(IRFunction node) {
        callback.caseFunction(node);
    }

    @Override
    public void caseIRIfStmt(IRIfStmt node) {
        callback.caseIfStmt(node);
    }

    @Override
    public void caseIRStatementList(IRStatementList node) {
        callback.caseList(node);
    }

    @Override
    public void caseIRStmt(IRStmt node) {
        callback.caseStmt((Stmt)node);
    }

    @Override
    public void caseIRWhileStmt(IRWhileStmt node) {
        callback.caseWhileStmt(node);
    }

    @Override
    public void caseIRBreakStmt(IRBreakStmt node) {
        callback.caseBreakStmt(node);
    }

    @Override
    public void caseIRContinueStmt(IRContinueStmt node) {
        callback.caseContinueStmt(node);
    }

    @Override
    public void caseIRDotGetStmt(IRDotGetStmt node) {
        irCallback.caseIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseIRDotSetStmt(IRDotSetStmt node) {
        irCallback.caseIRAbstractAssignStmt(node);
    }

    @Override
    public void caseIRGlobalStmt(IRGlobalStmt node) {
        callback.caseGlobalStmt(node);
    }

    @Override
    public void caseIRPersistentStmt(IRPersistentSmt node) {
        callback.casePersistentStmt(node);
    }

    @Override
    public void caseIRTryStmt(IRTryStmt node) {
        callback.caseTryStmt(node);
    }
    
    public void caseIRReturnStmt(IRReturnStmt node){
        callback.caseReturnStmt(node);
    }

}


