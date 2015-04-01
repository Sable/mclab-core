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

/**
 * extension of the node case handler to handle TIRNode cases.
 * Analaguous to NodeCaseHandler
 * 
 * note that TIRNode and TIRStmt are just interfaces
 * See TIRNodeForwarder, a helper to extend AbstractNodeCaseHandler 
 * 
 * 
 * @author ant6n
 */

public interface TIRNodeCaseHandler {
    
    //all the statements
    //parent stmt
    public void caseTIRStmt(TIRStmt node);
    //all assignment statements
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node);
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node);
    public void caseTIRAbstractAssignToVarStmt(TIRAbstractAssignToVarStmt node);
    public void caseTIRAbstractAssignFromVarStmt(TIRAbstractAssignFromVarStmt node);
    public void caseTIRArrayGetStmt(TIRArrayGetStmt node);
    public void caseTIRArraySetStmt(TIRArraySetStmt node);
    public void caseTIRCopyStmt(TIRCopyStmt node);
    public void caseTIRAbstractCreateFunctionHandleStmt(TIRAbstractCreateFunctionHandleStmt node);
    public void caseTIRCreateLambdaStmt(TIRCreateLambdaStmt node);
    public void caseTIRCreateFunctionReferenceStmt(TIRCreateFunctionReferenceStmt node);
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt node);
    public void caseTIRCallStmt(TIRCallStmt node);
    public void caseTIRCellArrayGetStmt(TIRCellArrayGetStmt node);
    public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node);
    public void caseTIRDotGetStmt(TIRDotGetStmt node);
    public void caseTIRDotSetStmt(TIRDotSetStmt node);
    //all other stmts
    public void caseTIRCommentStmt(TIRCommentStmt node);
    public void caseTIRForStmt(TIRForStmt node);
    public void caseTIRIfStmt(TIRIfStmt node);
    public void caseTIRWhileStmt(TIRWhileStmt node);
    public void caseTIRTryStmt(TIRTryStmt node);
    public void caseTIRPersistentStmt(TIRPersistentSmt node);
    public void caseTIRGlobalStmt(TIRGlobalStmt node);
    public void caseTIRContinueStmt(TIRContinueStmt node);
    public void caseTIRBreakStmt(TIRBreakStmt node);
    public void caseTIRReturnStmt(TIRReturnStmt node);
    
    //non statement nodes
    public void caseTIRFunction(TIRFunction node);
    public void caseTIRStatementList(TIRStatementList node);
    public void caseTIRCommaSeparatedList(TIRCommaSeparatedList node);
   
}

