// =========================================================================== //
//                                                                             //
// Copyright 2011 Jesse Doherty and McGill University.                         //
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

package natlab.toolkits.rewrite.simplification;

import java.util.Set;

import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.AbstractLocalRewrite;
import ast.ASTNode;
import ast.Expr;
import ast.NameExpr;

/**
 * A simplification specific implementation of
 * AbstractLocalRewrite. Adds dependencies and requires the kind
 * analysis. 
 *
 * @author Jesse Doherty
 */
public abstract class AbstractSimplification extends AbstractLocalRewrite
{
    protected VFPreorderAnalysis kindAnalysis;

    public AbstractSimplification( ASTNode tree, 
                                   VFPreorderAnalysis kind )
    {
        super(tree);
        kindAnalysis = kind;
    }

    public abstract Set<Class<? extends AbstractSimplification>> getDependencies();
    
    private VFDatum getKind(NameExpr expr) {
      return kindAnalysis.getFlowSets().get(expr.getName()).get(expr.getName().getID());
    }

    private boolean isVar(NameExpr expr) {
      VFDatum kind = getKind(expr);
      return kind != null && kind.isVariable();
    }
    
    private boolean isFun(NameExpr expr) {
      VFDatum kind= getKind(expr);
      return kind != null && kind.isFunction();
    }

    public boolean isVar( Expr expr )
    {
      if (!(expr instanceof NameExpr)) {
        return false;
      }
      NameExpr nameExpr = (NameExpr)expr;
      if( nameExpr.tmpVar ) {
        return true;
      }
      if (!kindAnalysis.getFlowSets().containsKey(nameExpr.getName())) {
        /* if we don't find the node in the kindAnalysis, re-analyze
           TODO -- is this correct?
           this was added because a simplification added calls to 'false'
           which couldn't be found in the flow sets
         */
        kindAnalysis.analyze();
      }
      return isVar(nameExpr);
    }

    public boolean isFun( Expr expr )
    {
      if (!(expr instanceof NameExpr)) {
        return false;
      }
      NameExpr nameExpr = (NameExpr)expr;
      if( nameExpr.tmpVar ) {
        return false;
      }
      return isFun(nameExpr);
    }
}
