package natlab.toolkits.loadfunction;

import java.util.*;
import ast.*;

/**
 * Some helper methods for dealing with the load function.
 */
public class LoadFunction
{
    /**
     * Determines what the load function can load.
     * Returns either a set of variables to load or null. If it
     * returns null then all variables could be changed and all
     * information should be destroyed.
     */
    public static HashSet<String> loadWhat( ast.List<Expr> args )
    {
        HashSet<String> set = new HashSet();
        String filename;
        if( args.getNumChild() >= 2 ){
            Expr e = args.getChild(0);
            String arg0 = loadArgument(e);
            e = args.getChild(1);
            String arg1 = loadArgument(e);

            if( arg0 == null || arg1 == null )
                return null;

            String argL0 = arg0.toLowerCase();
            String argL1 = arg1.toLowerCase();

            set = handleAsciiFlag( arg0, arg1 );
            if( set!=null )
                return set;
            set = handleAsciiFlag(arg1, arg0);
            if( set!=null )
                return set;

            boolean noFlag = true;
            filename = arg0;
            if( argL0.equals("-mat") ){
                noFlag = false;
                filename = arg1;
            }
            if( argL1.equals("-mat") ){
                noFlag = noFlag && true;
            }
            filename = genVarName(filename);

            if( noFlag )
                set.add( arg1 );
            for( int i = 2; i<args.getNumChild(); i++ ){
                String arg = loadArgument( args.getChild(i) );
                String argL = arg.toLowerCase();
                if( arg==null )
                    return null;

                HashSet<String> asciiSet = handleAsciiFlag( arg, filename );
                if( asciiSet!=null )
                    return asciiSet;
                //ignore -mat
                if( !argL.equals("-mat") )
                    set.add( arg );

            }
            return set;
        }
        return null;
    }
    /**
     * Deals with the ascii flag. Returns a set to return if it is an
     * ascii flag, otherwise returns null.
     */
    public static HashSet<String> handleAsciiFlag(String arg, String filename )
    {
        if( arg.toLowerCase().equals("-ascii") ){
            HashSet<String> set = new HashSet();
            set.add( genVarName(filename) );
            return set;
        }
        return null;
    }
    /**
     * Returns the string representation of a given load argument if
     * it is a StringLiteralExpr. If it isn't then it returns null.
     */
    public static String getLoadArgString( Expr arg )
    {
        if( arg instanceof StringLiteralExpr ){
            return ((StringLiteralExpr)arg).getValue();
        }
        else
            return null;
    }
    /**
     * Processes a single argument to load. If it would cause the load
     * to destroy all info then it returns null, otherwise it returns
     * a string with the literal value of the arg.
     * Things that are rejected are -ascii, -regexp, anything
     * containing a *
     */
    public static String loadArgument( String arg )
    {
        if( arg.toLowerCase().equals("-regexp") )
            return null;
        if( arg.contains("*") )
            return null;
        return arg;
    }
    public static String loadArgument( Expr arg )
    {
        String s = getLoadArgString( arg );
        if( s==null )
            return null;
        return loadArgument( s );
    }
    /**
     * generate a correct variable name from a given string. Strips
     * director, extension and turns any undesirable characters into
     * _. If it starts with a number then prepends an X.
     */
    public static String genVarName( String s )
    {
        //start of name is last after last /
        int start = s.lastIndexOf('/');
        //end is before last .
        int end = s.lastIndexOf('.');
        if( end == -1 )
            end = s.length();
     
        String name = s.substring(start+1,end);

        name = name.replaceAll( "\\W", "_" );
        if( name.matches("\\A\\d.*") )
            name = "X"+name;
        return name;
    }

}