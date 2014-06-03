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

package natlab.toolkits.rewrite;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.simplification.AbstractSimplification;
import ast.ASTNode;

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
    public static final boolean DEBUG = false;
    
    //List of simplifications. If applied in list order, then all
    //dependencies should be met.
    protected LinkedList<AbstractSimplification> simplifications;

    protected ASTNode tree;
    protected VFPreorderAnalysis kind;

    
    /**
     * Helper constructor using varargs
     * The varags have to be classes extending Simplfication (otherwise there will be a runtime exception)
     */
    @SuppressWarnings("unchecked")
    public Simplifier(ASTNode tree,Class... todo){
        this(tree, new HashSet<>(Arrays.<Class<? extends AbstractSimplification>>asList(todo)));
    }

    /**
     * Helper constructor where the Set of classes gets specified using varargs
     * The varags have to be classes extending Simplfication (otherwise there will be a runtime exception)
     */
    @SuppressWarnings("unchecked")
    public Simplifier(ASTNode tree,
            VFPreorderAnalysis kindAnalysis,
            Class ... todo){
        this(tree,
             new HashSet<>(Arrays.<Class<? extends AbstractSimplification>>asList(todo)),
             kindAnalysis);
    }
    
    /**
     * Helper static method, simply applies simplifications and returns the new tree
     * the list of classes have to extend AbstractSimplification
     */
    @SuppressWarnings("unchecked")
    public static <T extends ASTNode> T simplify(T tree,
            VFPreorderAnalysis kindAnalysis,
            Class ... todo){
        return (T)(new Simplifier(tree,kindAnalysis,todo).simplify());
    }
    /**
     * Helper static method, simply applies simplifications and returns the new tree
     * the list of classes have to extend AbstractSimplification
     */
    @SuppressWarnings("unchecked")
    public static <T extends ASTNode> T simplify(T tree,
            Class ... todo){
        return (T)(new Simplifier(tree,todo).simplify());
    }
    
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
        simplifications = new LinkedList<>();
        kind = kindAna;
        if( !kind.isAnalyzed() )
            kind.analyze();

        this.tree = tree;
        buildDependencies( todo );
    }

    /**
     * Runs the simplifications in a correct order.
     */
    public ASTNode simplify()
    {
        ASTNode currentTree = tree;
        for( AbstractSimplification simp : simplifications ){
            if (DEBUG) System.out.println( simp +":"); 
            simp.setTree( currentTree );
            currentTree = simp.transform();
            if (DEBUG) System.out.println(currentTree.getPrettyPrinted());
        }
        return currentTree;
    }

    /**
     * Builds the dependencies for a given start set.
     */
    protected void buildDependencies( Set<Class<? extends AbstractSimplification>> startSet )
    {
        Set<Class<? extends AbstractSimplification>> seenStartSet = new HashSet<>();
        while( !startSet.isEmpty() ){
            Iterator<Class<? extends AbstractSimplification>> iter = startSet.iterator();
            Class<? extends AbstractSimplification> simpClass = iter.next();

            AbstractSimplification simp = constrSimp(simpClass);
            seenStartSet.add( simpClass );
            iter.remove();
            buildDependencies( simp, startSet, seenStartSet );
        }
    }
    /**
     * Builds the dependencies for a given simplification, puts them
     * in the list of simplifications. Ensures that if the
     * simplifications in the list are executed in order, then all
     * dependencies will be met.
     *
     * Dependencies are assumed to form a tree. If a dependency is in
     * the seenStartSet then we stop recursing on that branch.
     */
    protected void buildDependencies( AbstractSimplification base, 
                                      Set<Class<? extends AbstractSimplification>> startSet,
                                      Set<Class<? extends AbstractSimplification>> seenStartSet)
    {
        for( Class<? extends AbstractSimplification> depClass : base.getDependencies() ){
            if( !seenStartSet.contains( depClass )){
                startSet.remove( depClass );
                seenStartSet.add( depClass );

                buildDependencies(constrSimp(depClass), startSet, seenStartSet);
            }
        }
        simplifications.add( base );
    }

    /**
     * Construct a given simplification class. No recovery is
     * attempted if an exception or error occurs. No error should
     * occur.
     */
    protected AbstractSimplification constrSimp( Class<? extends AbstractSimplification> simpClass )
    {
        try {
            return simpClass.getConstructor(ASTNode.class,
                VFPreorderAnalysis.class ).newInstance(tree, kind);
        } catch (Exception e) {
            //This REALLY should not happen.
            throw new UnsupportedOperationException( "Something very wrong happened.", e );
        }
    }
}
