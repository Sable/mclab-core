package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.DecIntNumericLiteralValue;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.analysis.varorfun.*;


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

