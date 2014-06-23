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

import ast.CSLExpr;
import ast.Name;
import ast.NameExpr;

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
     * Gives a CSL node instance for this temporary.
     */
    public CSLExpr genCSLExpr()
    {
        CSLExpr newCSL = new CSLExpr( genName() );
        newCSL.tmpVar = true;
        return newCSL;
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
        NameExpr newNE = new NameExpr( genFreshTempName() );
        newNE.tmpVar = true;
        return newNE;
    }
    /**
     * Generates multiple NameExpr nodes with same string name.
     */
    public static NameExpr[] genFreshTempNameExpr( int amount )
    {
        Name[] names = genFreshTempName( amount );
        NameExpr[] nameExprs = new NameExpr[amount];
        for( int i=0; i<amount; i++ ){
            nameExprs[i] = new NameExpr( names[i] );
            nameExprs[i].tmpVar = true;
        }
        return nameExprs;
    }
    /**
     * Generates a new temporary CSLExpr node. Each call returns a
     * node with a fresh name.
     */
    public static CSLExpr genFreshTempCSLExpr()
    {
        CSLExpr newCSL = new CSLExpr( genFreshTempName() );
        newCSL.tmpVar = true;
        return newCSL;
    }
    /**
     * Generates multiple CSLExpr nodes with the same string name.
     */
    public static CSLExpr[] genFreshTempCSLExpr( int amount )
    {
        Name[] names = genFreshTempName( amount );
        CSLExpr[] cslExprs = new CSLExpr[amount];
        for( int i=0; i<amount; i++ ){
            cslExprs[i] = new CSLExpr( names[i] );
            cslExprs[i].tmpVar = true;
        }
        return cslExprs;
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
        suffix = s;
    }

}

    