package natlab;

import natlab.ast.Program;

/**
 * A entry for a Program used in the Resolver's and loaders lists
 */

public class ProgramEntry
{
    private Program program;
    private String name;

    public ProgramEntry(Program p, String n)
    {
        program = p;
        name = n;
    }
    public Program getProgram()
    {
        return program;
    }
    public String getName()
    {
        return name;
    }
}