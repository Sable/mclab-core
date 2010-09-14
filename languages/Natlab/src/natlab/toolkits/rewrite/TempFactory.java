package natlab.toolkits.rewrite;

import ast.*;
import java.lang.UnsupportedOperationException;

/** 
 * Factory for producing new temporary names. There is a static
 * component that is used to generate fresh names. There is also an
 * instance component that is used for generating different temporary
 * instances with the same string name.
 */
public class TempFactory
{

    private static int counter = 0;
    private static String prefix = "mc_t";
    private static String suffix = "";
    private String myName;

    private TempFactory( String name )
    {
        myName = name;
    }

    /**
     * Returns a name node with given string name. This is here to
     * allow changes to how a name node is build e.g. giving it a temp
     * flag.
     */
    private static Name makeNameNode( String name )
    {
        Name newName = new Name( name );
        newName.tmpVar = true;
        return newName;
    }
 
    public TempFactory()
    {
        throw new UnsupportedOperationException("Cannot directly instantiate a TempFactory");
    }

    /**
     * Gives a Name node instance for this temporary.
     */
    public Name genName()
    {
        return makeNameNode( myName );
    }
    /**
     * Gives a NameExpr node instance for this temporary.
     */
    public NameExpr genNameExpr()
    {
        NameExpr newName = new NameExpr( genName() );
        newName.tmpVar = true;
        return newName;
    }
    /**
     * Gives a Sring name for this temporary.
     */
    public String genString()
    {
        return myName;
    }
    /**
     * Generates a new temporary Name node. Each call returns a fresh
     * name.
     */
    public static Name genFreshTempName()
    {
        return makeNameNode( genFreshTempString() );
    }
    /**
     * Generates multiple Name nodes with the same string name.
     */
    public static Name[] genFreshTempName( int amount )
    {
        String name = genFreshTempString();
        Name[] names = new Name[amount];
        for( int i=0; i<amount; i++ )
            names[i] = makeNameNode( name );
        return names;
    }

    /**
     * Generates a new temporary NameExpr node. Each call returns a
     * node with a fresh name.
     */
    public static NameExpr genFreshTempNameExpr()
    {
        return new NameExpr( genFreshTempName() );
    }
    /**
     * Generates multiple NameExpr nodes with same string name.
     */
    public static NameExpr[] genFreshTempNameExpr( int amount )
    {
        Name[] names = genFreshTempName( amount );
        NameExpr[] nameExprs = new NameExpr[amount];
        for( int i=0; i<amount; i++ )
            nameExprs[i] = new NameExpr( names[i] );
        return nameExprs;
    }
    /**
     * Generates a new temporary String name. Each call returns a
     * fresh name. 
     */
    public static String genFreshTempString()
    {
        counter++;
        return prefix + (counter-1) + suffix;
    }

    /**
     * Generates a TempFactory instance that can be used to produce
     * multiple temporaries with the same name value;
     */
    public static TempFactory genFreshTempFactory()
    {
        return new TempFactory( genFreshTempString() );
    }
    /**
     * Resets the generator. If prefix and suffix are not changed this
     * will cause repetition of temporary names.
     */
    public static void reset()
    {
        counter = 0;
    }
    public static String getPrefix()
    {
        return prefix;
    }
    public static void setPrefix( String p )
    {
        prefix = p;
    }
    public static String getSuffix()
    {
        return suffix;
    }
    public static void setSuffix( String s )
    {
        suffix = suffix;
    }

}

    