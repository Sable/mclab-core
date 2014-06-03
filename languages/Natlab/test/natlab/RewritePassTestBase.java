package natlab;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

import junit.framework.TestCase;
import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;
import ast.CheckScalarStmt;
import ast.ExprStmt;
import ast.Function;
import ast.List;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Program;

public class RewritePassTestBase extends TestCase {


    protected Program parseFile( String fName ) throws Exception
    {
        ArrayList<CompilationProblem> errList = new ArrayList<CompilationProblem>();
        Program prog = Parse.parseNatlabFile( fName, errList );
        if( prog == null )
            throw new Exception( "Error parsing file " +fName+ ":\n" +
                errList.stream().map(Object::toString).collect(Collectors.joining("\n")));
        return prog;
    }


    public void assertEquiv( ASTNode actual, ASTNode expected )
    {
        StringBuilder errmsg = new StringBuilder();
        boolean test = checkEquiv( actual, expected, errmsg );
        assertTrue( errmsg.toString(), test );
    }

    /**
     * Checks if the two given trees are equivalent. If they aren't,
     * then an error message is appended to {@code errmsg}. It returns
     * {@code true} if they are equivalent, and {@code false}
     * otherwise.
     */
    protected boolean checkEquiv( ASTNode actual, ASTNode expected, StringBuilder errmsg )
    {
        if( errmsg == null || actual==null || expected == null )
            throw new IllegalArgumentException("no arguments should be null");

        TreeAplhaEquivTester tester = new TreeAplhaEquivTester( actual, expected );
        boolean test = tester.testEquiv();
        String msg = tester.getReason();
        if( !test ){
            errmsg.append( msg ).append( "\n" );
            errmsg.append("ACTUAL: \n\n");
            errmsg.append( actual.getPrettyPrinted() ).append( "\n\n" );
            errmsg.append("EXPECTED: \n\n");
            errmsg.append( expected.getPrettyPrinted() ).append( "\n" );
        }
        return test;
    }

    /**
     * Checks if the two given trees are equivalent. If they aren't,
     * an error message specific to the given test name is appended to
     * {@code errmsg}. It returns {@code true} if they are equivalent,
     * and {@code false} otherwise.
     */
    protected boolean checkEquiv( ASTNode actual, ASTNode expected, 
                                  String testName, StringBuilder errmsg )
    {
        StringBuilder localErrmsg = new StringBuilder();
        boolean test = checkEquiv(actual, expected, localErrmsg);
        if( !test ){
            errmsg.append("********************************************************************************\n");
            errmsg.append("*                                                                              *\n");
            errmsg.append( "  FAILED CASE: ").append( testName ).append("\n");
            errmsg.append( localErrmsg );
            errmsg.append("*                                                                              *\n");
            errmsg.append("********************************************************************************\n");
        }
        return test;
    }
    /**
     * Class used to verify that two ASTs are structurally the same up
     * to alpha renaming. This should only be used for testing the
     * simplifying rewrites, not general transformations.
     */
    class TreeAplhaEquivTester extends AbstractNodeCaseHandler
    {
        private TreeMap<String, String> nameEquivalence;
        private boolean fail = false;
        private ASTNode tree1, tree2, tree2Current;
        private String reason = "";

        public TreeAplhaEquivTester( ASTNode tree1, ASTNode tree2 )
        {
            this.tree1 = tree1;
            this.tree2 = tree2;
        }

        public boolean testEquiv()
        {
            tree2Current = tree2;
            nameEquivalence = new TreeMap<String, String>();
            tree1.analyze(this);

            return !fail;
        }
        public String getReason()
        {
            return reason;
        }

        public void caseASTNode( ASTNode node )
        {
            ASTNode tree1Next, tree2Next;
            ASTNode current = tree2Current;
            
            //Check if they are the same class
            if( current.getClass().isInstance(node) ){
                int numChild = node.getNumChild();
                if( numChild == current.getNumChild() ){
                    //if they are the same class and have the same
                    //number of children, then loop each child and
                    //check equiv
                    for( int i=0; i<numChild; i++ ){
                        tree1Next = node.getChild(i);
                        tree2Next = current.getChild(i);
                        tree2Current = tree2Next;
                        tree1Next.analyze(this);
                        if( fail )
                            break;
                    }
                }
                else{
                    // if they have differing number of children,
                    // figure out a failure reason and fail.
                    if( current instanceof List ){
                        reason = "Number of elements don't match for actual and expected lists\n";
                    }
                    else
                        reason = "Number of children don't match for actual subtree: \n\n"+
                            node.getPrettyPrinted() + "\n\n"+
                            "and expected subtree: \n\n" +
                            current.getPrettyPrinted() + "\n";
                    reason += "\nNumbers of children are "+ numChild + " and "+current.getNumChild()+" respectively\n";
                    fail = true;
                }
            }
            // If they aren't the same class, figure out a failure
            // reason, except when one is a CheckScalarStmt
            else{
                if( current instanceof List || node instanceof List )
                    reason = "Node types don't match for actual node: "+node+"\n\n"+
                        "and expected: "+ current +"\n";
                //see if one is a CheckScalarStmt
                else if( current instanceof CheckScalarStmt || node instanceof CheckScalarStmt ){
                    //if they both are then check the parameter
                    if( current instanceof CheckScalarStmt && node instanceof CheckScalarStmt ){
                        tree1Next = node.getChild(0);
                        tree2Next = current.getChild(0);
                        tree2Current = tree2Next;
                        tree1Next.analyze(this);
                    }
                    //if only one is, make sure the other is a
                    //ParameterizedExpr that looks like a CheckScalarStmt
                    else{
                        tree1Next = null;
                        tree2Next = null;
                        if( current instanceof CheckScalarStmt ){
                            ParameterizedExpr paramExpr = getParamExpr( node );
                            if( paramExpr != null && 
                                checkIsScalar( paramExpr )){
                                tree1Next = paramExpr.getArg(0);
                                tree2Next = current.getChild(0);
                            }
                            else{
                                fail = true;
                            }
                        }
                        else{
                            ParameterizedExpr paramExpr = getParamExpr( current );
                            if( paramExpr != null &&
                                checkIsScalar( paramExpr )){
                                tree1Next = node.getChild(0);
                                tree2Next = paramExpr.getArg(0);
                            }
                            else{
                                fail = true;
                            }
                        }
                        if( fail )
                            reason = "check_scalar expected: \n\n"+
                                node.getPrettyPrinted() + "\n\n"+
                                "and expected subtree: \n\n" +
                                current.getPrettyPrinted() + "\n";
                        else{
                            tree2Current = tree2Next;
                            tree1Next.analyze(this);

                            tree2Current = current;
                            return;
                        }
                    }
                }
                else
                    reason = "Node types don't match for actual subtree: \n\n"+
                        node.getPrettyPrinted() + "\n\n"+
                        "and expected subtree: \n\n" +
                        current.getPrettyPrinted() + "\n";                
                fail = true;
            }
            tree2Current = current;
        }
        
        /**
         * Gets the ParameterizedExpr from a node if that node is an
         * expression statement, returns null otherwise.
         */
        private ParameterizedExpr getParamExpr( ASTNode node ){
            if( node instanceof ExprStmt )
                if( node.getChild(0) instanceof ParameterizedExpr )
                    return (ParameterizedExpr)node.getChild(0);
            return null;
        }

        /**
         * Checks if a given {@code ParameterizedExpr} is a valid
         * {@code check_scalar} call. This is intended to deal with
         * the fact that {@code CheckScalarStmt} nodes can't be placed
         * in actual source code.
         */
        private boolean checkIsScalar( ParameterizedExpr expr )
        {
            if( expr.getTarget() instanceof NameExpr ){
                NameExpr nameExpr = (NameExpr)expr.getTarget();
                return nameExpr.getName().getID().equals("check_scalar") &&
                    expr.getNumArg() == 1;
            }
            else
                return false;
        }
                    
        public void caseFunction( Function node )
        {
            caseASTNode( node );
            String name1 = node.getName();
            String name2 = ((Function)tree2Current).getName();
            if( name1.equals(name2) ){
                if( nameEquivalence.containsKey( name1 ) ){
                    reason = "Function names don't match for actual subtree: \n\n"+
                        node.getPrettyPrinted() + "\n\n"+
                        "and expected subtree: \n\n" +
                        tree2Current.getPrettyPrinted() + "\n";
                    fail = true;
                }
                else
                    nameEquivalence.put( name1, name2 );
            }
        }

        public void caseName( Name node )
        {
            caseASTNode( node );
            if( !fail ){
                String name1 = node.getID();
                String name2 = ((Name)tree2Current).getID();
                if( nameEquivalence.containsKey( name1 ) ){
                    if( ! nameEquivalence.get( name1 ).equals( name2 ) ){
                        reason = "Names don't match for actual subtree: \n\n"+
                            node.getPrettyPrinted() + "\n\n"+
                            "and expected subtree: \n\n" +
                            tree2Current.getPrettyPrinted() + "\n";
                        fail = true;
                    }
                }
                else
                    nameEquivalence.put( name1, name2 );
            }
        }
    }
}
