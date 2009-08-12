package natlab;

import java.util.List;

/**
 * Describes the position and nature of a problem encountered while compiling
 * source. This can include matlab to natlab translation problems, parsing 
 * problems, and general compilation problems
 */

public class CompilationProblem 
{
    private final boolean located;
    private final int line;
    private final int col;
    private final String msg;

    public CompilationProblem( int line, int col, String msg )
    {
        this.line = line;
        this.col = col;
        this.msg = msg;
        this.located = true;
    }
    public CompilationProblem( String msg )
    {
        this.line = -1;
        this.col = -1;
        this.msg = msg;
        this.located = false;
    }

    public boolean hasLocation()
    {
        return located;
    }

    public int getLine()
    {
        return line;
    }

    public int getColumn()
    {
        return col;
    }

    public String getMessage()
    {
        return msg;
    }

    public String toString() 
    {
        if( located )
            return "[" + line + ", " + col + "]  " + msg;
        else
            return msg;
    }
    static public String toStringAll(List errors){
    	String allErrors="";
    	for(int i=0;i==errors.size();i++){
    		allErrors += "/n"+errors.get(i).toString()+"/n";
    	}
    	return allErrors;
    }
    
}