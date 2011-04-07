package natlab;

import java.util.TreeMap;
import java.util.ArrayList;
import java.io.*;

import junit.framework.TestCase;
import natlab.toolkits.analysis.AbstractNodeCaseHandler;
import ast.*;

public class RewritePassTestBase extends TestCase {


    protected Program parseFile( String fName ) throws Exception
    {
        FileReader fileReader = new FileReader( fName );
        ArrayList<CompilationProblem> errList = new ArrayList<CompilationProblem>();
        Program prog = Parse.parseFile( fName, fileReader, errList );
        if( prog == null )
            throw new Exception( "Error parsing file " +fName+ ":\n" + 
                                 CompilationProblem.toStringAll( errList ) );
        return prog;
    }


    public void assertEquiv( ASTNode actual, ASTNode expected )
    {
        TreeAplhaEquivTester tester = new TreeAplhaEquivTester( actual, expected );
        Boolean test = tester.testEquiv();
        String msg = tester.getReason();
        assertTrue( msg+ "\n" +
                    "ACTUAL: \n\n"+
                    actual.getPrettyPrinted() + "\n\n"+
                    "EXPECTED: \n\n"+
                    expected.getPrettyPrinted() + "\n",
                    test );
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
            
            if( current.getClass().isInstance(node) ){
                int numChild = node.getNumChild();
                if( numChild == current.getNumChild() ){
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
            else{
                if( current instanceof List || node instanceof List )
                    reason = "Node types don't match for actual node: "+node+"\n\n"+
                        "and expected: "+ current +"\n";
                else
                    reason = "Node types don't match for actual subtree: \n\n"+
                        node.getPrettyPrinted() + "\n\n"+
                        "and expected subtree: \n\n" +
                        current.getPrettyPrinted() + "\n";                
                fail = true;
            }
            tree2Current = current;
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
