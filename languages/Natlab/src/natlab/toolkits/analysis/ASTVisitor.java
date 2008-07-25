/**
 * Interface of visitor pattern for traversing the AST.
 * Change Log:
 *  - Created by Jun Li 2008.06
 */

package natlab.toolkits.analysis;

import java.util.*;
import java.util.List;

import natlab.ast.*;
import natlab.toolkits.scalar.*;


public interface ASTVisitor 
{
	public void caseASTNode(ASTNode node);
	public void caseLoopStmt(ASTNode node);
	public void caseIfStmt(IfStmt node);
}
