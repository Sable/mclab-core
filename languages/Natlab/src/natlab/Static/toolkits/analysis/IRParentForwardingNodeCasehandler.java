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

package natlab.Static.toolkits.analysis;

import ast.*;
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
import natlab.Static.ir.IRNode;
import natlab.Static.ir.IRStatementList;
import natlab.Static.ir.IRStmt;
import natlab.Static.ir.IRWhileStmt;
import natlab.toolkits.analysis.AbstractNodeCaseHandler;
import natlab.toolkits.analysis.ForwardingNodeCaseHandler;
import natlab.toolkits.analysis.NodeCaseHandler;
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
        irCallback.caseIRAbstractAssignToVarStmt(node);
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
    public void caseIRCellArrayGetStmt(IRCellArrayGet node) {
        irCallback.caseIRAbstractAssignToListStmt(node);
    }

    @Override
    public void caseIRCellArraySetStmt(IRCellArraySet node) {
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

}


