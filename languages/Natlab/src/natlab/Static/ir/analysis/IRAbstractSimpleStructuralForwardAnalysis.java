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

import ast.ASTNode;
import natlab.Static.ir.*;
import analysis.*;
import nodecases.*;
import natlab.toolkits.analysis.*;

/**
 * this is an extension of the AbstractSimpleStructuralForwardAnalysis which
 * adds the IR nodes. Just like it's parent, this analysis provides fixed
 * point cases; and analysis cases flow up to the parent case.
 * 
 * This handles the correct dispatch to the IR nodes, and the flow control 
 * to the parent type node cases.
 * 
 * @author ant6n
 * @param <F>
 */


abstract public class IRAbstractSimpleStructuralForwardAnalysis<F> extends
        AbstractSimpleStructuralForwardAnalysis<F> implements IRNodeCaseHandler {
    IRNodeForwarder nodeForwarder = new IRNodeForwarder(this);
    IRParentForwardingNodeCasehandler parentForwarder = new IRParentForwardingNodeCasehandler(this);
    
    public IRAbstractSimpleStructuralForwardAnalysis(ASTNode<?> tree) {
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
    public void caseIRCellArrayGetStmt(IRCellArrayGetStmt node) {
        parentForwarder.caseIRCellArrayGetStmt(node);
    }
    @Override
    public void caseIRCellArraySetStmt(IRCellArraySetStmt node) {
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

    @Override
    public void caseIRBreakStmt(IRBreakStmt node) {
        parentForwarder.caseIRBreakStmt(node);
    }
    @Override
    public void caseIRContinueStmt(IRContinueStmt node) {
        parentForwarder.caseIRContinueStmt(node);
    }
    @Override
    public void caseIRDotGetStmt(IRDotGetStmt node) {
        parentForwarder.caseIRDotGetStmt(node);
    }
    @Override
    public void caseIRDotSetStmt(IRDotSetStmt node) {
        parentForwarder.caseIRDotSetStmt(node);
    }
    @Override
    public void caseIRGlobalStmt(IRGlobalStmt node) {
        parentForwarder.caseIRGlobalStmt(node);
    }
    @Override
    public void caseIRPersistentStmt(IRPersistentSmt node) {
        parentForwarder.caseIRPersistentStmt(node);
    }
    @Override
    public void caseIRTryStmt(IRTryStmt node) {
        parentForwarder.caseIRTryStmt(node);
    }
    @Override
    public void caseIRReturnStmt(IRReturnStmt node) {
        parentForwarder.caseIRReturnStmt(node);
    }
}
