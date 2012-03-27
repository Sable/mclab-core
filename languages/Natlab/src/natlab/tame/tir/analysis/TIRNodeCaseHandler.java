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
import natlab.tame.tir.*;

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

