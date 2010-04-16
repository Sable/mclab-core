package natlab;

import natlab.toolkits.rewrite.*;
import junit.framework.TestCase;
import java.util.*;

public class RewriteTests extends TestCase
{
    public ast.Name[] genNameArray()
    {
        ast.Name[] names = {new ast.Name("jesse"),
                new ast.Name("anton"),
                new ast.Name("casey"),
                new ast.Name("jun")};
        return names;
    }
    public void testMultipleEquality( TransformedNode<ast.ASTNode> trans, ast.ASTNode[] nodes )
    {
        List<ast.ASTNode> newNodes = trans.getMultipleNodes();
        int i = 0;
        for( ast.ASTNode n : newNodes ){
            assertEquals( nodes[i], n );
            i++;
        }
    }
    //Pass tests
    /** 
     * Tests that a transformed node object created with a single
     * node is a single node and not a multiple node
     */
    public void test_transformednodepass_create1()
    {
        ast.Name name = new ast.Name("jesse");
        TransformedNode<ast.ASTNode> trans = new TransformedNode<ast.ASTNode>( name );
        assertTrue( trans.isSingleNode() );
        assertFalse( trans.isMultipleNodes() );
    }
    /**
     * Tests the array constructor 
     */
    public void test_transformednodepass_create2()
    {
        ast.Name[] names = genNameArray();
        TransformedNode<ast.ASTNode> trans = new TransformedNode<ast.ASTNode>( names );
        assertTrue( trans.isMultipleNodes() );
        assertFalse( trans.isSingleNode() );
    }
    /**
     * Tests the collection constructor with a list.
     */
    public void test_transformednodepass_create3()
    {
        ast.Name[] names = genNameArray();
        TransformedNode<ast.ASTNode> trans = new TransformedNode<ast.ASTNode>( Arrays.asList(names) );
        assertTrue( trans.isMultipleNodes() );
        assertFalse( trans.isSingleNode() );
    }
    /**
     * Tests the node constructor and getSingleNode method for sanity
     */
    public void test_transformednodepass_access1()
    {
        ast.Name name = new ast.Name("jesse");
        TransformedNode<ast.ASTNode> trans = new TransformedNode<ast.ASTNode>( name );
        
        assertEquals( name, trans.getSingleNode() );
    }
    /**
     * Tests the array constructor and getMultipleNodes for length
     * sanity.
     */
    public void test_transformednodepass_access2()
    {
        ast.Name[] names = genNameArray();
        TransformedNode<ast.ASTNode> trans = new TransformedNode<ast.ASTNode>( names );

        List newNames = trans.getMultipleNodes();
        assertEquals( names.length , newNames.size() );
    }
    /**
     * Tests the array constructor and getMultipleNodes for equality
     * sanity.
     */
    public void test_transformednodepass_access3()
    {
        ast.Name[] names = genNameArray();
        TransformedNode<ast.ASTNode> trans = new TransformedNode<ast.ASTNode>( names );

        testMultipleEquality(trans, names);
    }

    /**
     * Tests the ability to grow from a single to multiple.
     */
    public void test_transformednodepass_grow1()
    {
        ast.Name[] names = genNameArray();
        TransformedNode<ast.ASTNode> trans = new TransformedNode<ast.ASTNode>( names[0] );
        for( int i=1; i<names.length; i++ )
            trans.add( names[i] );

        assertTrue( "TransformedNode was still not multiple",
                    trans.isMultipleNodes() );
        assertFalse( " TransformedNode was still single :'(",
                     trans.isSingleNode() );

        testMultipleEquality(trans, names);
    }

    /**
     * Tests the ability to grow a multiple.
     */
    public void test_transformednodepass_grow2()
    {
        ast.Name[] names = genNameArray();
        TransformedNode<ast.ASTNode> trans = new TransformedNode<ast.ASTNode>( names );

        ast.Name newName = new ast.Name( "maxime" );
        trans.add( newName );
        ast.Name[] allNames = Arrays.copyOf( names, names.length+1 );
        allNames[names.length] = newName;

        testMultipleEquality(trans, allNames);
    }

    //Fail tests
    /**
     * Tests the node constructor and getMultipleNodes method for
     * failure.
     */
    public void test_transformednodefail_access1()
    {
        ast.Name name = new ast.Name("jesse");
        TransformedNode<ast.ASTNode> trans = new TransformedNode<ast.ASTNode>( name );

        try{
            trans.getMultipleNodes();
        }catch(UnsupportedOperationException e){
            return;
        }
        fail("No exception when trying to access a single node as multiple nodes");
    }
    /**
     * Tests the node constructor and getSingleNode method for
     * failure.
     */
    public void test_transformednodefail_access2()
    {
        ast.Name[] names = genNameArray();
        TransformedNode<ast.ASTNode> trans = new TransformedNode<ast.ASTNode>(names);

        try{
            trans.getSingleNode();
        }catch(UnsupportedOperationException e){
            return;
        }
        fail("No exception when trying to access a multiple nodes as a single node");
    }
}