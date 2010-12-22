package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.DecIntNumericLiteralValue;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.analysis.varorfun.*;


/**
 * Simplifies expressions so that there is at most one complex
 * operation, all operands must me literals or variables. 
 * @author Jesse Doherty
 */
public class RightSimplification extends AbstractSimplification
{
    public RightSimplification( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( RightSimplification.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        dependencies.add(LeftSimplification.class);
        dependencies.add(ForSimplification.class);
        dependencies.add(ConditionalSimplification.class);
        dependencies.add(CommaSepListRightSimplification.class);
        return dependencies;
    }

}
