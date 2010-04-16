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
        List<ast.ASTNode> newNames = trans.getMultipleNodes();
        int i = 0;
        for( ast.ASTNode n : newNames ){
            assertEquals( names[i], n );
            i++;
        }
    }
}