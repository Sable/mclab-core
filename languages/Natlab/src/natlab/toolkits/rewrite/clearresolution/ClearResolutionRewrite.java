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

package natlab.toolkits.rewrite.clearresolution;

import natlab.toolkits.rewrite.AbstractLocalRewrite;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.ExprStmt;
import ast.GlobalStmt;
import ast.List;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.StringLiteralExpr;

/**
 * A rewrite that do a single pass down to locate all "end" expression, and then backtrack for every subsequent indented paramExp until a variable is found.
 * End is then remplaced by a call to size.
 *
 * Note: need to move this rewrite in aspectmatlab
 */
public class ClearResolutionRewrite extends AbstractLocalRewrite
{
	

    public ClearResolutionRewrite( ASTNode tree )
    {
        super( tree );
    }
    
    public void caseExprStmt(ExprStmt node){
    	
    		if(node.getExpr() instanceof ParameterizedExpr){
    			ParameterizedExpr paramExpr = (ParameterizedExpr)node.getExpr();
    			if(!(paramExpr.getTarget().getStructureString().equals("clear")))
    				return;
    	        List<Expr> listArg = paramExpr.getArgList();
    	        	
    	        for(Expr argument:listArg){
    	        	if(argument instanceof StringLiteralExpr){
    	        		String argumentStr = ((StringLiteralExpr)argument).getValue();
    	        		if(argumentStr.equals("global")){
    	        			rewriteclearGlobal(node);
    	        			return;
    	        		}else if(argumentStr.equals("")||argumentStr.equals("all")||argumentStr.equals("classes")){
    	        			rewriteclearAll(node);
    	        			return;			
    	        		}	
    	        	}
    	        }
    	        rewriteclearLocal(node);
    		}
    	}

    
    //create a new statment around "clear global", and replace it in the ast
    private void rewriteclearGlobal(ExprStmt node){
    	TransformedNode newClear = new TransformedNode();
    	
    	//'AM_GLOBAL_B = AM_GLOBAL
    	Name amglobal = new Name("AM_GLOBAL");
    	NameExpr rhsGB = new NameExpr(amglobal);
    	Name amglobalb = new Name("AM_GLOBAL_B");
    	NameExpr lhsGB = new NameExpr(amglobalb);
    	AssignStmt assignGlobalB = new AssignStmt(lhsGB,rhsGB);
    	newClear.add(assignGlobalB);
    	
    	//original clear statement
    	newClear.add(node);
    	
    	//'global AM_GLOBAL
    	List<Name> globalStmtarg = new List<Name>();
    	globalStmtarg.add(amglobal);//"AM_Global"
    	GlobalStmt globalGlobal = new GlobalStmt(globalStmtarg);
    	newClear.add(globalGlobal);
    	
    	//'AMGLOBAL = AM_GLOBAL_B
    	AssignStmt assignGlobal = new AssignStmt(rhsGB,lhsGB);
    	newClear.add(assignGlobal);
    	
    	//'clear AM_GLOBAL_B
    	Name clearStr = new Name("clear");
    	NameExpr clearGB = new NameExpr(clearStr);
    
    	StringLiteralExpr amglobalbStr = new StringLiteralExpr("AM_GLOBAL_B");
    	List<Expr> rhslistGB = new List<Expr>();
    	rhslistGB.add(amglobalbStr);
    	
    	ParameterizedExpr clearParamExpr = new ParameterizedExpr(clearGB,rhslistGB);
    	
    	ExprStmt clearGlobalB = new ExprStmt(clearParamExpr);
    	newClear.add(clearGlobalB);
    	
    	//replace node Clear by newClear in the ast
    	newNode = newClear;
    }
    
    //create a new statment around "clear all", and replace it in the ast
    private void rewriteclearAll(ExprStmt node){
    	TransformedNode newClear = new TransformedNode();
    	
		//'global AM_Entrypoint_B
		Name amentryB = new Name("Am_Entrypoint_b");
		List<Name> globalStmtarg = new List<Name>();
		globalStmtarg.add(amentryB);//"AM_Global"
		GlobalStmt globalEntryB = new GlobalStmt(globalStmtarg);
		newClear.add(globalEntryB);

		//'AM_Entrypoint_b = AM_Entrypoint_0
		NameExpr rhsEB = new NameExpr(amentryB);
		Name amentry0 = new Name("AM_Entrypoint_0");
		NameExpr lhsEB = new NameExpr(amentry0);
		AssignStmt assignEntryb = new AssignStmt(rhsEB,lhsEB);
		newClear.add(assignEntryb);

    	
    	//clear('functions')
    	Name clearStr = new Name("clear");
    	NameExpr clearExpr = new NameExpr(clearStr);
    
    	StringLiteralExpr functionStr = new StringLiteralExpr("functions");
    	List<Expr> rhsclearFunc = new List<Expr>();
    	rhsclearFunc.add(functionStr);
    	
    	ParameterizedExpr clearParamExpr1 = new ParameterizedExpr(clearExpr,rhsclearFunc);
    	
    	ExprStmt clearFunc = new ExprStmt(clearParamExpr1);
    	newClear.add(clearFunc);
    	
    	//clear('variables')
    	StringLiteralExpr varStr = new StringLiteralExpr("variables");
    	List<Expr> rhsclearVars = new List<Expr>();
    	rhsclearVars.add(varStr);
    	
    	ParameterizedExpr clearParamExpr2 = new ParameterizedExpr(clearExpr,rhsclearVars);
    	
    	ExprStmt clearVars = new ExprStmt(clearParamExpr2);
    	newClear.add(clearVars);
    	
    	//'global AM_EntryPoint_b
    	newClear.add(globalEntryB);
    	
    	//'AM_Entrypoint_0 = AM_Entrypoint_b
    	AssignStmt assignEntry0 = new AssignStmt(lhsEB,rhsEB);
    	newClear.add(assignEntry0);
    	
    	//'clear('global','AM_Entrypoint_b')

    
    	StringLiteralExpr globalStr = new StringLiteralExpr("global");
    	StringLiteralExpr amentryBStr = new StringLiteralExpr("AM_entrypoint_b");
    	List<Expr> rhsGEB = new List<Expr>();
    	rhsGEB.add(globalStr);
    	rhsGEB.add(amentryBStr);
    	
    	ParameterizedExpr clearParamExpr3 = new ParameterizedExpr(clearExpr,rhsGEB);
    	
    	ExprStmt clearGlobalEntryB = new ExprStmt(clearParamExpr3);
    	newClear.add(clearGlobalEntryB);
    	
    	//'AM_Global_B = Am_Global
    	Name amglobal = new Name("AM_GLOBAL");
    	NameExpr rhsGB = new NameExpr(amglobal);
    	Name amglobalb = new Name("AM_GLOBAL_B");
    	NameExpr lhsGB = new NameExpr(amglobalb);
    	AssignStmt assignGlobalB = new AssignStmt(lhsGB,rhsGB);
    	newClear.add(assignGlobalB);
    	
    	//'clear('global')
        
    	StringLiteralExpr amglobalStr = new StringLiteralExpr("global");
    	List<Expr> rhslistG = new List<Expr>();
    	rhslistG.add(amglobalStr);
    	
    	ParameterizedExpr clearParamExpr4 = new ParameterizedExpr(clearExpr,rhslistG);
    	
    	ExprStmt clearGlobal = new ExprStmt(clearParamExpr4);
    	newClear.add(clearGlobal);
    	
    	//'global AM_GLOBAL
    	globalStmtarg = new List<Name>();
    	globalStmtarg.add(amglobal);//"AM_Global"
    	GlobalStmt globalGlobal = new GlobalStmt(globalStmtarg);
    	newClear.add(globalGlobal);
    	
    	//'AM_Global = AM_Global_B
    	AssignStmt assignGlobal = new AssignStmt(rhsGB,lhsGB);
    	newClear.add(assignGlobal);
    	
    	//'clear AM_GLOBAL_B
    
    	StringLiteralExpr amglobalbStr = new StringLiteralExpr("AM_GLOBAL_B");
    	List<Expr> rhslistGB = new List<Expr>();
    	rhslistGB.add(amglobalbStr);
    	
    	ParameterizedExpr clearParamExpr5 = new ParameterizedExpr(clearExpr,rhslistGB);
    	
    	ExprStmt clearGlobalB = new ExprStmt(clearParamExpr5);
    	newClear.add(clearGlobalB);
    	
    	// replace the old clear node by newClear
    	newNode = newClear;
    	
    	
    }
    
    //create a new statment around "clear local", and replace it in the ast
    private void rewriteclearLocal(ExprStmt node){
    	TransformedNode newClear = new TransformedNode();
    	
    	//'global AM_Entrypoint_B
    	Name amentryB = new Name("Am_Entrypoint_b");
    	List<Name> globalStmtarg = new List<Name>();
    	globalStmtarg.add(amentryB);//"AM_Global"
    	GlobalStmt globalEntryB = new GlobalStmt(globalStmtarg);
    	newClear.add(globalEntryB);
    	
    	//'AM_Entrypoint_b = AM_Entrypoint_0
    	NameExpr rhsEB = new NameExpr(amentryB);
    	Name amentry0 = new Name("AM_Entrypoint_0");
    	NameExpr lhsEB = new NameExpr(amentry0);
    	AssignStmt assignEntryb = new AssignStmt(rhsEB,lhsEB);
    	newClear.add(assignEntryb);
    	
    	//original clear statement
    	newClear.add(node);
    	
    	//'global AM_EntryPoint_b
    	newClear.add(globalEntryB);
    	
    	//'AM_Entrypoint_0 = AM_Entrypoint_b
    	AssignStmt assignEntry0 = new AssignStmt(lhsEB,rhsEB);
    	newClear.add(assignEntry0);
    	
    	//'clear('global','AM_Entrypoint_b')
    	Name clearStr = new Name("clear");
    	NameExpr lhsGEB = new NameExpr(clearStr);
    
    	StringLiteralExpr globalStr = new StringLiteralExpr("global");
    	StringLiteralExpr amentryBStr = new StringLiteralExpr("AM_entrypoint_b");
    	List<Expr> rhsGEB = new List<Expr>();
    	rhsGEB.add(globalStr);
    	rhsGEB.add(amentryBStr);
    	
    	ParameterizedExpr clearParamExpr = new ParameterizedExpr(lhsGEB,rhsGEB);
    	
    	ExprStmt clearGlobalEntryB = new ExprStmt(clearParamExpr);
    	newClear.add(clearGlobalEntryB);
    	
    	
    	
    	//replace node Clear by the list of node newClear in the ast
    	newNode = newClear;
    	
    	
    	
    }
    
   
    
    
    
    
    }
