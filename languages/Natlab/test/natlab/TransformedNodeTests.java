package natlab;

import natlab.toolkits.rewrite.*;
import junit.framework.TestCase;
import java.util.*;

public class TransformedNodeTests extends TestCase
{
    public ast.Name[] genNameArray()
    {
        ast.Name[] names = {new ast.Name("jesse"),
                new ast.Name("anton"),
                new ast.Name("casey"),
                new ast.Name("jun")};
        return names;
    }
    public void testMultipleEquality( TransformedNode trans, ast.ASTNode[] nodes )
    {
        List<ast.ASTNode> newNodes = trans.getMultipleNodes();
        int i = 0;
        for( ast.ASTNode n : newNodes ){
            assertEquals( nodes[i], n );
            i++;
        }
    }
    public void assertEmpty( TransformedNode trans )
    {
        assertTrue( trans.isEmptyNode() );
        assertFalse( trans.isSingleNode() );
        assertFalse( trans.isMultipleNodes() );
    }
    public void assertSingle( TransformedNode trans )
    {
        assertFalse( trans.isEmptyNode() );
        assertTrue( trans.isSingleNode() );
        assertFalse( trans.isMultipleNodes() );
    }
    public void assertMultiple( TransformedNode trans )
    {
        assertFalse( trans.isEmptyNode() );
        assertFalse( trans.isSingleNode() );
        assertTrue( trans.isMultipleNodes() );
    }

    //Pass tests

    /**
     * Tests the empty constructor.
     */
    public void test_transformednodepass_create1()
    {
        TransformedNode trans = new TransformedNode();
        assertEmpty( trans );
    }
    /** 
     * Tests that a transformed node object created with a single
     * node is a single node and not a multiple node
     */
    public void test_transformednodepass_create2()
    {
        ast.Name name = new ast.Name("jesse");
        TransformedNode trans = new TransformedNode( name );
        assertSingle( trans );
    }
    /**
     * Tests the array constructor 
     */
    public void test_transformednodepass_create3()
    {
        ast.Name[] names = genNameArray();
        TransformedNode trans = new TransformedNode( names );
        assertMultiple( trans );
    }
    /**
     * Tests the collection constructor with a list.
     */
    public void test_transformednodepass_create4()
    {
        ast.Name[] names = genNameArray();
        TransformedNode trans = new TransformedNode( Arrays.asList(names) );
        assertMultiple( trans );
    }
    /**
     * Tests the node constructor and getSingleNode method for sanity
     */
    public void test_transformednodepass_access1()
    {
        ast.Name name = new ast.Name("jesse");
        TransformedNode trans = new TransformedNode( name );
        
        assertEquals( name, trans.getSingleNode() );
    }
    /**
     * Tests the array constructor and getMultipleNodes for length
     * sanity.
     */
    public void test_transformednodepass_access2()
    {
        ast.Name[] names = genNameArray();
        TransformedNode trans = new TransformedNode( names );

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
        TransformedNode trans = new TransformedNode( names );

        testMultipleEquality(trans, names);
    }

    /**
     * Tests the ability to grow from a single to multiple.
     */
    public void test_transformednodepass_grow1()
    {
        ast.Name[] names = genNameArray();
        TransformedNode trans = new TransformedNode( names[0] );
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
        TransformedNode trans = new TransformedNode( names );

        ast.Name newName = new ast.Name( "maxime" );
        trans.add( newName );
        ast.Name[] allNames = Arrays.copyOf( names, names.length+1 );
        allNames[names.length] = newName;

        testMultipleEquality(trans, allNames);
    }

    /**
     * Test the ability to grow from an empty.
     */
    public void test_transformednodepass_grow3()
    {
        ast.Name name = new ast.Name("jesse");
        TransformedNode trans = new TransformedNode();
        trans.add( name );
        assertSingle( trans );

        assertEquals( name, trans.getSingleNode() );
    }

    //Fail tests

    /**
     * Tests the node constructor and getMultipleNodes method for
     * failure.
     */
    public void test_transformednodefail_access1()
    {
        ast.Name name = new ast.Name("jesse");
        TransformedNode trans = new TransformedNode( name );

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
        TransformedNode trans = new TransformedNode(names);

        try{
            trans.getSingleNode();
        }catch(UnsupportedOperationException e){
            return;
        }
        fail("No exception when trying to access a multiple nodes as a single node");
    }
    /**
     * Tests the node constructor and get methods for failure when empty.
     */
    public void test_transformednodefail_access3()
    {
        TransformedNode trans = new TransformedNode();
        try{
            trans.getSingleNode();
        }catch(UnsupportedOperationException e1){
            try{
                trans.getMultipleNodes();
            }catch(UnsupportedOperationException e2){
                return;
            }
            fail("No exception when trying to access an empty node as multiple nodes");
        }
        fail("No exception when trying to access an empty node as a single node");
    }
}