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
import ast.PlusExpr;
import ast.MinusExpr;
import ast.NameExpr;
import ast.IntLiteralExpr;
import ast.Expr;

public class AffineExpression {
	
	private int C;
	private String key; //this serves as the key for the constraints graph.
	//private boolean isConstant; //to check if there is a constraint that has more than one variable.
	private String loopVariable; //to check which index variable 
	private Expr lowerBound;
	private Expr upperBound;
	private Expr indexExpr; //this is to handle variables in Affine Expression e.g. 2i+10.this variable would store 2i.
	private int uBound;
	private int lBound;
	
	public int getUBound() {
		return uBound;
	}
	public int getLBound() {
		return lBound;
	}
	public void setUBound(int bound) {
		uBound = bound;
	}
	public void setLBound(int bound) {
		lBound = bound;
	}
	public Expr getIndexExpr() {
		return indexExpr;
	}
	public void setIndexExpr(Expr indexExpr) {
		this.indexExpr = indexExpr;
	}
	public Expr getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(Expr lowerBound) {
		this.lowerBound = lowerBound;
	}
	public Expr getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(Expr upperBound) {
		this.upperBound = upperBound;
	}
	public String getLoopVariable() {
		return loopVariable;
	}
	public void setLoopVariable(String loopVariable) {
		this.loopVariable = loopVariable;
	}
	public int getC() {
		return C;
	}
	public void setC(int c) {
		C = c;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	
	

}

