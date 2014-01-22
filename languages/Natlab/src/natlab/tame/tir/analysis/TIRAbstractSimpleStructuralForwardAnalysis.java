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

import ast.ASTNode;
import analysis.*;
import nodecases.*;
import natlab.tame.tir.*;
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


abstract public class TIRAbstractSimpleStructuralForwardAnalysis<F> extends
        ForwardAnalysis<F> implements TIRNodeCaseHandler {
    TIRNodeForwarder nodeForwarder = new TIRNodeForwarder(this);
    TIRParentForwardingNodeCasehandler parentForwarder = new TIRParentForwardingNodeCasehandler(this);
    
    public TIRAbstractSimpleStructuralForwardAnalysis(ASTNode<?> tree) {
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
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node) {
        parentForwarder.caseTIRAbstractAssignStmt(node);
    }
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node) {
        parentForwarder.caseTIRAbstractAssignStmt(node);
    }
    @Override
    public void caseTIRAbstractAssignToVarStmt(TIRAbstractAssignToVarStmt node) {
        parentForwarder.caseTIRAbstractAssignToVarStmt(node);
    }
    @Override
    public void caseTIRAbstractAssignFromVarStmt(TIRAbstractAssignFromVarStmt node){
        parentForwarder.caseTIRAbstractAssignFromVarStmt(node);
    }
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt node) {
        parentForwarder.caseTIRArrayGetStmt(node);
    }
    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt node) {
        parentForwarder.caseTIRArraySetStmt(node);
    }
    @Override
    public void caseTIRAbstractCreateFunctionHandleStmt(TIRAbstractCreateFunctionHandleStmt node) {
        parentForwarder.caseTIRAbstractCreateFunctionHandleStmt(node);
    }
    @Override
    public void caseTIRCreateFunctionReferenceStmt(TIRCreateFunctionReferenceStmt node) {
    	parentForwarder.caseTIRCreateFunctionReferenceStmt(node);
    }
    @Override
    public void caseTIRCreateLambdaStmt(TIRCreateLambdaStmt node) {
    	parentForwarder.caseTIRCreateLambdaStmt(node);
    }
    @Override
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt node) {
        parentForwarder.caseTIRAssignLiteralStmt(node);
    }
    @Override
    public void caseTIRCallStmt(TIRCallStmt node) {
        parentForwarder.caseTIRCallStmt(node);
    }
    @Override
    public void caseTIRCellArrayGetStmt(TIRCellArrayGetStmt node) {
        parentForwarder.caseTIRCellArrayGetStmt(node);
    }
    @Override
    public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node) {
        parentForwarder.caseTIRCellArraySetStmt(node);
    }
    @Override
    public void caseTIRCopyStmt(TIRCopyStmt node) {
        parentForwarder.caseTIRCopyStmt(node);
    }
    @Override
    public void caseTIRCommaSeparatedList(TIRCommaSeparatedList node) {
        parentForwarder.caseTIRCommaSeparatedList(node);
    }
    @Override
    public void caseTIRCommentStmt(TIRCommentStmt node) {
        parentForwarder.caseTIRCommentStmt(node);
    }
    @Override
    public void caseTIRForStmt(TIRForStmt node) {
        parentForwarder.caseTIRForStmt(node);
    }
    @Override
    public void caseTIRFunction(TIRFunction node) {
        parentForwarder.caseTIRFunction(node);
    }
    @Override
    public void caseTIRIfStmt(TIRIfStmt node) {
        parentForwarder.caseTIRIfStmt(node);
    }
    @Override
    public void caseTIRStatementList(TIRStatementList node) {
        parentForwarder.caseTIRStatementList(node);
    }
    @Override
    public void caseTIRStmt(TIRStmt node) {
        parentForwarder.caseTIRStmt(node);
    }
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node) {
        parentForwarder.caseTIRWhileStmt(node);
    }

    @Override
    public void caseTIRBreakStmt(TIRBreakStmt node) {
        parentForwarder.caseTIRBreakStmt(node);
    }
    @Override
    public void caseTIRContinueStmt(TIRContinueStmt node) {
        parentForwarder.caseTIRContinueStmt(node);
    }
    @Override
    public void caseTIRDotGetStmt(TIRDotGetStmt node) {
        parentForwarder.caseTIRDotGetStmt(node);
    }
    @Override
    public void caseTIRDotSetStmt(TIRDotSetStmt node) {
        parentForwarder.caseTIRDotSetStmt(node);
    }
    @Override
    public void caseTIRGlobalStmt(TIRGlobalStmt node) {
        parentForwarder.caseTIRGlobalStmt(node);
    }
    @Override
    public void caseTIRPersistentStmt(TIRPersistentSmt node) {
        parentForwarder.caseTIRPersistentStmt(node);
    }
    @Override
    public void caseTIRTryStmt(TIRTryStmt node) {
        parentForwarder.caseTIRTryStmt(node);
    }
    @Override
    public void caseTIRReturnStmt(TIRReturnStmt node) {
        parentForwarder.caseTIRReturnStmt(node);
    }
}
