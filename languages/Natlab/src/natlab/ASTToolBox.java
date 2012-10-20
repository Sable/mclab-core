// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
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
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab;

import natlab.toolkits.utils.NodeFinder;
import ast.ASTNode;
import ast.AssignStmt;
import ast.ForStmt;
import ast.IfStmt;
import ast.ParameterizedExpr;
import ast.Program;
import ast.Stmt;
import ast.WhileStmt;

public class ASTToolBox {
  @Deprecated
  public static Stmt getParentStmtNode(ASTNode varNode) {
    return NodeFinder.findParent(varNode, Stmt.class);
  }

  @Deprecated
  public static AssignStmt getParentAssignStmtNode(ASTNode varNode) {
    return NodeFinder.findParent(varNode,  AssignStmt.class);
  }

  @Deprecated
  public static ast.List getParentStmtListNode(ASTNode varNode) {
    return NodeFinder.findParent(varNode, ast.List.class);
  }

  @Deprecated
  public static ForStmt getParentForStmtNode(ASTNode varNode) {
    return NodeFinder.findParent(varNode, ForStmt.class);
  }

  @Deprecated
  public static IfStmt getParentIfStmtNode(ASTNode varNode) {
    return NodeFinder.findParent(varNode, IfStmt.class);
  }

  @Deprecated
  public static Program getSubtreeRoot(ASTNode varNode) {
    return NodeFinder.findParent(varNode, Program.class);
  }

  public static boolean isInsideLoop(ASTNode varNode) {
    return NodeFinder.findParent(varNode, ForStmt.class) != null
        || NodeFinder.findParent(varNode, WhileStmt.class) != null;
  }

  public static boolean isInsideArray(ASTNode varNode) {
    boolean bResult = false;
    ASTNode parentNode = varNode;
    do {
      if (parentNode instanceof AssignStmt) {
        break;
      } if (parentNode instanceof ParameterizedExpr) {
        bResult = true;
        break;
      } else {
        parentNode = parentNode.getParent();
      }
    } while (parentNode != null && !bResult);

    return bResult;
  }
}
