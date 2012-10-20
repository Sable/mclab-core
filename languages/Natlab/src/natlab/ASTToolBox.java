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
import ast.Function;
import ast.IfStmt;
import ast.ParameterizedExpr;
import ast.Program;
import ast.Script;
import ast.Stmt;
import ast.WhileStmt;

public class ASTToolBox {
  public static Stmt getParentStmtNode(ASTNode varNode) {
    return NodeFinder.findParent(varNode, Stmt.class);
  }

  public static AssignStmt getParentAssignStmtNode(ASTNode varNode) {
    return NodeFinder.findParent(varNode,  AssignStmt.class);
  }

  public static ast.List getParentStmtListNode(ASTNode varNode) {
    return NodeFinder.findParent(varNode, ast.List.class);
  }

  public static ForStmt getParentForStmtNode(ASTNode varNode) {
    return NodeFinder.findParent(varNode, ForStmt.class);
  }

  public static IfStmt getParentIfStmtNode(ASTNode varNode) {
    return NodeFinder.findParent(varNode, IfStmt.class);
  }

  public static boolean isInsideArray(ASTNode varNode) {
    boolean bResult = false;
    ASTNode parentNode = varNode;
    do{
      if((parentNode instanceof AssignStmt) ) {
        break;
      } if((parentNode instanceof ParameterizedExpr) ) {
        bResult = true;
        break;
      } else {
        parentNode = parentNode.getParent(); 	   
      }
    } while((parentNode!=null) && !bResult);

    return bResult;
  }
  

  public static boolean isInsideLoop(ASTNode varNode) {
    boolean bResult = false;
    ASTNode parentNode = varNode;
    do{
      if((parentNode instanceof WhileStmt) 
          || (parentNode instanceof ForStmt)) {
        bResult = true;
        break;
      } else {
        parentNode = parentNode.getParent(); 	   
      }
    } while((parentNode!=null) && !(parentNode instanceof Program) && !bResult);

    return bResult;
  }

  public static ASTNode getSubtreeRoot(ASTNode varNode) {
    ASTNode parentNode = varNode.getParent();
    // If the varNode is already the assignment-statement, then don't need 
    // to find it's parent
    if((varNode instanceof Script)|| (varNode instanceof Function)) {
      parentNode = varNode;
    } else {
      // varNode/e.getNodeLocation() is NameExpr, it may directly belongs to
      // AssignStmt, or ParameterizedExpr.
      while ((parentNode!=null) && 
          !((parentNode instanceof Script)||(parentNode instanceof Function))) {
        varNode = parentNode;
        parentNode = parentNode.getParent(); 	   
      }
    }
    return parentNode;
  }
}
