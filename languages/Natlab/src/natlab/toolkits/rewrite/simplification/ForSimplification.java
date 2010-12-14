package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.toolkits.rewrite.AbstractLocalRewrite;
import natlab.toolkits.analysis.varorfun.*;

/**
 * Simplifies for statements. Reduces them so that there are only
 * simple range for loops. 
 */
public class ForSimplification extends AbstractSimplification
{

    public ForSimplification( ASTNode tree,
                              AbstractLocalRewrite callback,
                              VFStructuralForwardAnalysis kind )
    {
        super( tree, callback, kind );
    }

    public Set<AbstractSimplification> getDependencies()
    {
        return new HashSet();
    }
}