package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.DecIntNumericLiteralValue;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.analysis.varorfun.*;


/**
 * Makes comma separated list expansion explicit on the left hand side
 * of assignments. This will only modify multi-assignment statements. 
 *
 * This simplification introduces the non-syntactic CSL node.
 * @author Jesse Doherty
 */
public class CommaSepListLeftSimplification extends AbstractSimplification
{
    public CommaSepListLeftSimplification( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( CommaSepListLeftSimplification.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        return dependencies;
    }

}
