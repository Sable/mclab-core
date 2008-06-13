package natlab;

import java.util.*;
import natlab.ast.Program;
import natlab.ast.Script;
import natlab.ast.FunctionList;
import beaver.Parser;

/**
 * Used to resolve fn and script names. Contains lists of scripts, 
 * functions and class constructors. Resolution process is intertwined 
 * with the loading process, and they share a queue of files that need 
 * processing
 */

public class Resolver
{
    private Queue<ProgramEntry> toProcess;

    private List<Script> scripts;

    private List<FunctionList> functions;

    Resolver(Queue<ProgramEntry> toP)
    {
        toProcess = toP;
        scripts = new LinkedList<Script>();
        functions = new LinkedList<FunctionList>();
    }
}