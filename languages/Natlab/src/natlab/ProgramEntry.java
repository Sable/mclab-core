package natlab;

import natlab.ast.Program;

/**
 * A entry for a Program used in the Resolver's and loaders lists
 */

public class ProgramEntry<T extends Program>
{
    private T program;
    private String name;

    public ProgramEntry(T p, String n)
    {
        program = p;
        name = n;
    }
    public T getProgram()
    {
        return program;
    }
    public String getName()
    {
        return name;
    }
}