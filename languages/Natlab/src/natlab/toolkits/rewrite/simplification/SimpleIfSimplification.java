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

import java.util.HashSet;
import java.util.Set;

import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.ElseBlock;
import ast.IfBlock;
import ast.IfStmt;
import ast.Opt;


/**
 * Simplifies if statements to only include if and else. elseifs are
 * reduced to else containing an if.
 *
 * @author Jesse Doherty
 */
public class SimpleIfSimplification extends AbstractSimplification
{
    public SimpleIfSimplification( ASTNode tree,
                                   VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }
    
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( SimpleIfSimplification.class );
        return set;
    }
    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        return new HashSet();
    }

    /*
      if C1
        B1
      if C2
        B2
      [ifblocks]
      else
        B3
      end
      ==========
      if C1
        B1
      else
        if C2
          B2
        [ifblocks]
        else
          B3
        end
      end

      and similarly for more if blocks
    */
    public void caseIfStmt( IfStmt node )
    {
        ast.List<IfBlock> ifs = node.getIfBlocks();
        IfBlock firstIf = ifs.getChild(0);
        if( ifs.getNumChild() > 1 ){
            ast.List<IfBlock> restOfIfs = ifs;
            restOfIfs.removeChild(0);
            IfStmt newIf = new IfStmt( new ast.List().add(firstIf),
                                       new Opt( new ElseBlock( new ast.List().add( node ) ) ) );
            rewriteChildren( newIf );
            newNode = new TransformedNode( newIf );
        }
        else{
            rewriteChildren( node );
        }
    }
}

