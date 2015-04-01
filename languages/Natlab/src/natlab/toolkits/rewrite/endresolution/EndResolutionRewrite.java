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

package natlab.toolkits.rewrite.endresolution;

import java.util.LinkedList;

import natlab.toolkits.rewrite.AbstractLocalRewrite;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.AssignStmt;
import ast.ElseBlock;
import ast.EndExpr;
import ast.Expr;
import ast.ExprStmt;
import ast.IfBlock;
import ast.IfStmt;
import ast.IntLiteralExpr;
import ast.List;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Stmt;
import ast.StringLiteralExpr;


/**
 * A rewrite that do a single pass down to locate all "end" expression, and then backtrack for every subsequent indented paramExp until a variable is found.
 * End is then remplaced by a call to size.
 *
 * Note: not working
 */

public class EndResolutionRewrite extends AbstractLocalRewrite
{
	private int tempCount = 0;
    public EndResolutionRewrite( ASTNode tree )
    {
        super( tree );
    }
    //1- find ENDExpr, replace them  with  builtin(end,...) as a new AssignStmt.
    //TODO: 2- indented case(add if/else construct)
    
    public void caseExprStmt(ExprStmt node){
    	if(node.getExpr() instanceof ParameterizedExpr){
    		ParameterizedExpr paramExpr = (ParameterizedExpr)node.getExpr();
    		
 

    			List<Expr> listArg = paramExpr.getArgList(); 
    			
    			LinkedList<indentedAccess> paramList = new LinkedList<indentedAccess>();
    			
    			//Create the first indentedAccess in the list, arg pos and dims will be set at exit.
    			paramList.add(new indentedAccess(paramExpr.getTarget().getStructureString()));
    			
            newNode = buildnewNode(node,listArg,paramList);
    			
    		}
    	}
    private TransformedNode buildnewNode(ExprStmt node,List<Expr> listArg,LinkedList<indentedAccess> paramList){
		for(int i = 0;i< listArg.getNumChild();i++){
			Expr argument = listArg.getChild(i);
			if(!(argument instanceof EndExpr )){
				if(argument instanceof ParameterizedExpr){
					//System.err.println("Inside EndResolution"+indentedParam);
					
					//set pos and dims for the previous indentedAcces
					
					paramList.getLast().setPos(i+1);
					paramList.getLast().setDims(listArg.getNumChild());
					
					//recursively search for end in the next paramexp.
					ParameterizedExpr argumentParamExpr = (ParameterizedExpr)argument;
					paramList.add(new indentedAccess(argumentParamExpr.getTarget().getStructureString()));
					
					listArg = argumentParamExpr.getArgList();
					return buildnewNode(node,listArg,paramList);
					
				}
				continue;
			}
			
			//set pos and dims for the previous indentedAcces
			paramList.getLast().setPos(i+1);
			paramList.getLast().setDims(listArg.getNumChild());
			
			
			
			Stmt tempBuiltinExpr =  maketempEndStmt(paramList) ;
			String tempName = ("AM_tempEnd"+Integer.toString(tempCount));
			tempCount++;
			
			NameExpr tempEndNameExpr = new NameExpr(new Name(tempName));
			listArg.setChild(tempEndNameExpr, i);
			
			TransformedNode newEnd = new TransformedNode();
			newEnd.add(tempBuiltinExpr);
			newEnd.add(node);
			return newEnd;
    	
		}
		return null;
    }
    
    /*
    public void caseParameterizedExpr( ParameterizedExpr node )
    {
		String paramName = node.getTarget().getStructureString();
        List<Expr> listArg = node.getArgList(); 
        
        for(int i = 0;i< listArg.getNumChild();i++){
        	Expr argument = listArg.getChild(i);
        	if(!(argument instanceof EndExpr )){
        		if(argument instanceof ParameterizedExpr){
        			//indented ParamExpr 
        			findIndentBuiltinExpr(node,(ParameterizedExpr)argument);
        		}
        		return;
        	}
        	ParameterizedExpr builtinExpr =  makeBuiltinExpr(paramName,i,listArg.getNumChild()) ;
        	listArg.setChild(builtinExpr, i);
        	
        }
        
     }
    
    private void findIndentBuiltinExpr(ParameterizedExpr node,ParameterizedExpr argument){
    	
    	
    	return;
    }
    */
    
    //Receive a linked list of intended ParamExpr and their position in the Expr.
    //Create an if/else construct using the list to write the tempVariable.
	private Stmt maketempEndStmt(LinkedList<indentedAccess>  listArg){

		if(listArg.size() == 1){
			String name = listArg.element().getName();
			int pos = listArg.element().getPos();
			int dims = listArg.element().getDims();
		
		
			return makeAssignEndStmt(name,pos,dims);
		}else{

			 
			 IfStmt assignEndIfStmt = new IfStmt();
			 
			 //create the else statement using the outer varfun call/set
			 indentedAccess outerVarfun = listArg.poll(); 
			 List<Stmt> elseBody = new List<Stmt>();
			 elseBody.add(makeAssignEndStmt(outerVarfun.getName(),outerVarfun.getPos(),outerVarfun.getDims()));
			 ElseBlock IfOuter = new ElseBlock(elseBody);
			 assignEndIfStmt.setElseBlock(IfOuter);
			 
			 while(!listArg.isEmpty()){

				 //get the closest varfun from the Endexpr
				 indentedAccess curVarfun = listArg.getLast();
				 listArg.removeLast();
				 
				 //create the conditional expr.
				 NameExpr isVar = new NameExpr(new Name("isvariable"));
				 
				 List<Expr> ifParamList = new List<Expr>();
				 NameExpr paramNameExpr = new NameExpr(new Name(curVarfun.getName()));
				 ifParamList.add(paramNameExpr);
				 
				 ParameterizedExpr ifParam = new ParameterizedExpr(isVar,ifParamList);
				 
				 //create the assignement of the temp with builtin
				 AssignStmt assignBuiltin = makeAssignEndStmt(curVarfun.getName(),curVarfun.getPos(),curVarfun.getDims());
				 List<Stmt> ifStmtList = new List<Stmt>();
				 ifStmtList.add(assignBuiltin);
				 
				 //create the IfBlock and add it to the IfStmt
				 IfBlock curIf = new IfBlock(ifParam,ifStmtList);
				 assignEndIfStmt.addIfBlock(curIf);
	
				 
			 }
			 
			 
			 return assignEndIfStmt;
		}
		
	}
	private AssignStmt makeAssignEndStmt (String name,int pos, int dims){
		
		ParameterizedExpr builtinExpr = makeBuiltinExpr(name,pos,dims);
		String tempName = ("AM_tempEnd"+Integer.toString(tempCount));
	
		NameExpr tempNameExpr = new NameExpr(new Name(tempName));
	
		AssignStmt assignEndStmt = new AssignStmt();
		assignEndStmt.setLHS(tempNameExpr);
		assignEndStmt.setRHS(builtinExpr);
	
		return assignEndStmt;
	}
    private ParameterizedExpr makeBuiltinExpr(String name,int pos,int dims){
    	//build the right child of builtin
    	StringLiteralExpr endStrLE = new StringLiteralExpr("end");
    	IntLiteralExpr positionIntLE = new IntLiteralExpr(new natlab.DecIntNumericLiteralValue(Integer.toString(pos)));
    	IntLiteralExpr dimsIntLE = new IntLiteralExpr(new natlab.DecIntNumericLiteralValue(Integer.toString(dims)));
		NameExpr nExpr = new NameExpr(new Name(name));
		
		List<Expr> builtinArg = new List<Expr>();
		builtinArg.add(endStrLE);
		builtinArg.add(nExpr);
		builtinArg.add(positionIntLE);
		builtinArg.add(dimsIntLE);
		
		//build the left child of builtin
		NameExpr builtinNExpr = new NameExpr(new Name("builtin"));
		
		
    	ParameterizedExpr newBuiltinNode = new ParameterizedExpr();
    	newBuiltinNode.setChild(builtinNExpr,0);
    	newBuiltinNode.setChild(builtinArg,1);
    	
    	
    	
    	
    	
    	return newBuiltinNode;
    }
   

    
    
    }
