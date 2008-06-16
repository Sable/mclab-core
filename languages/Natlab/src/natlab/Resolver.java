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
 * processing. This queue is produced by Resolver and consumed by the 
 * loader
 */

public class Resolver
{
    private Queue<ProgramEntry<Program> > toProcess;

    private List<ProgramEntry<Script> > scripts;

    private List<ProgramEntry<FunctionList> > functions;

    Resolver(Queue<ProgramEntry<Program> > toP)
    {
        toProcess = toP;
        scripts = new LinkedList<ProgramEntry<Script> >();
        functions = new LinkedList<ProgramEntry<FunctionList> >();
    }

    public ProgramEntry<Script> addScript(Script s, String name)
    {
        ProgramEntry<Script> script = new ProgramEntry<Script>(s, name);
        scripts.add(script);
        //        toProcess.offer(script); //TODO-JD add some protection
        return script; 
    }
}