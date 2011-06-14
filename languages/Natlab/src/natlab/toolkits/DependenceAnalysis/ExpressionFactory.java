// =========================================================================== //
//                                                                             //
// Copyright 2011 Amina Aslam and McGill University.                           //
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

package natlab.toolkits.DependenceAnalysis;

import ast.List;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Expr;

public class ExpressionFactory {
	/*
	 * This function creates a NameExpr with the name mentioned in arguments
	 */
	
	public NameExpr createNameExpr(String sName)
	{
		NameExpr nExpr=new NameExpr();
		Name name=new Name();
		name.setID(sName);
		nExpr.setName(name);
		return nExpr;
	}
	/*
	 * This function returns a parameterized expression with following parameters.
	 * 1.Expr node
	 * 2.List 
	 */
    public ParameterizedExpr createParaExpr(Expr expr,List list)
    {
      ParameterizedExpr paraExpr=new ParameterizedExpr();  	  	  
  	  paraExpr.setTarget(expr);  	  
  	  paraExpr.setArgList(list);
  	  return paraExpr;
    }
    /*
	 * This function returns a parameterized expression with following parameters.
	 * 1.Expr node
	 * 2.index
	 */
    public ParameterizedExpr createParaExpr(Expr targetExpr,Expr argExpr,int index)
    {
      ParameterizedExpr paraExpr=new ParameterizedExpr();  	  	  
  	  paraExpr.setTarget(targetExpr);  	  
  	  paraExpr.setArg(argExpr, index);
  	  return paraExpr;
    }
    public Name createName(String Name)
    {
    	Name n=new Name();
    	n.setID(Name);
    	return n;
    }

}
