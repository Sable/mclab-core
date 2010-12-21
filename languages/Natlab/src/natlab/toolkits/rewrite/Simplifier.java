package natlab.toolkits.rewrite;

import java.util.*;
import java.lang.reflect.*;

import ast.*;
import natlab.toolkits.rewrite.simplification.*;
import natlab.toolkits.analysis.varorfun.*;

/**
 * Performs simplifications. Takes care of dependencies and any needed
 * book keeping.
 *
 * Dependencies are assumed to form a tree. Dependencies are built
 * from a start set and recorded in post order, avoiding repetition. 
 *
 * @author Jesse Doherty
 */
public class Simplifier
{

    //List of simplifications. If applied in list order, then all
    //dependencies should be met.
    LinkedList<AbstractSimplification> simplifications;

    ASTNode tree;
    VFPreorderAnalysis kind;

    /**
     * Constructs a simplifier for a given set of simplification
     * classes. 
     */
    public Simplifier( ASTNode tree, Set<Class<? extends AbstractSimplification>> todo )
    {
        this( tree, todo, new VFPreorderAnalysis(tree) );
    }
    public Simplifier( ASTNode tree, 
                       Set<Class<? extends AbstractSimplification>> todo, 
                       VFPreorderAnalysis kindAna )
    {
        simplifications = new LinkedList();
        kind = kindAna;
        if( !kind.isAnalyzed() )
            kind.analyze();

        this.tree = tree;
        for( Class<? extends AbstractSimplification> simpClass : todo ){
            AbstractSimplification simpInst = constrSimp( simpClass );
            buildDependencies( simpInst, todo );
            simplifications.add( simpInst );
        }
    }

    /**
     * Runs the simplifications in a correct order.
     */
    public ASTNode simplify()
    {
        ASTNode currentTree = tree;
        for( AbstractSimplification simp : simplifications ){
            simp.setTree( currentTree );
            currentTree = simp.transform();
        }
        return currentTree;
    }

    /**
     * Builds the dependencies for a given simplification, puts them
     * in the list of simplifications. Ensures that if the
     * simplifications in the list are executed in order, then all
     * dependencies will be met.
     *
     * Dependencies are assumed to form a tree. If a dependency is in
     * the startSet then we stop recursing on that branch.
     */
    protected void buildDependencies( AbstractSimplification base, Set startSet )
    {
        for( Class<? extends AbstractSimplification> depClass : base.getDependencies() ){
            if( !startSet.contains(depClass) ){
                AbstractSimplification depInst = constrSimp( depClass );
                buildDependencies( depInst, startSet );
                simplifications.add( depInst );
            }
        }
    }

    /**
     * Construct a given simplification class. No recovery is
     * attempted if an exception or error occurs. No error should
     * occur.
     */
    protected AbstractSimplification constrSimp( Class<? extends AbstractSimplification> simpClass )
    {
        AbstractSimplification simpInst;
        Constructor<? extends AbstractSimplification> constructor;
        
        try{
            constructor = simpClass.getConstructor( ASTNode.class, 
                                                    VFPreorderAnalysis.class );
            simpInst = constructor.newInstance( tree,kind );  
        }catch( Exception e ){
            //This REALLY should not happen.
            throw new UnsupportedOperationException( "Something very wrong happened.", e );
        }
        
        return simpInst;
    }

    //Some shortcuts for creating a simplifier

    public static Simplifier newForSimplifier(ASTNode tree)
    {
        return newForSimplifier(tree, new VFPreorderAnalysis( tree ) );
    }
    public static Simplifier newForSimplifier(ASTNode tree, VFPreorderAnalysis kindAna)
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( ForSimplification.class );
        return new Simplifier( tree, set, kindAna );
    }

    public static Simplifier newSimpleIfSimplifier(ASTNode tree)
    {
        return newSimpleIfSimplifier(tree, new VFPreorderAnalysis( tree ) );
    }
    public static Simplifier newSimpleIfSimplifier(ASTNode tree, VFPreorderAnalysis kindAna)
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( SimpleIfSimplification.class );
        return new Simplifier( tree, set, kindAna );
    }

    /**
     * Creates ShortCircuitArraySimplification simplifier. 
     */
    public static Simplifier newSCASimplifier(ASTNode tree)
    {
        return newSCASimplifier(tree, new VFPreorderAnalysis( tree ) );
    }
    /**
     * Creates ShortCircuitArraySimplification simplifier. 
     */
    public static Simplifier newSCASimplifier(ASTNode tree, VFPreorderAnalysis kindAna)
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( ShortCircuitArraySimplification.class );
        return new Simplifier( tree, set, kindAna );
    }
}
