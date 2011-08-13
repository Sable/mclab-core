package natlab.toolkits.analysis.test;

import ast.*;
import java.util.*;
import nodecases.*;

public class Uidtest extends AbstractNodeCaseHandler
{

    protected Set<Long> ids;
    protected boolean fail = false;
    protected ASTNode tree;

    public Uidtest()
    {
        ids = new TreeSet<Long>();
    }
    public Uidtest( ASTNode tree )
    {
        this.tree = tree;
        ids = new TreeSet<Long>();
    }

    public void caseASTNode( ASTNode node ) 
    {
        System.out.println(node.nodeCounter());
        if( ids.add( new Long(node.nodeCounter() ) ) )
            for( int i = 0; i<node.getNumChild(); i++ ){
                if( node.getChild(i) != null )
                    node.getChild(i).analyze( this );
            }
        else{
            fail = true;
            throw new RuntimeException("uh oh, we failed!");
        }
    }
    public boolean run()
    {
        try{
            tree.analyze( this );
            return true;
        }catch( Exception e){
            if( fail ){
                System.err.println("hmm seems not unique");
                return false;
            }
            else{
                System.err.println("hmm there was some unknown problem");
                return false;
            }
        }
    }
}