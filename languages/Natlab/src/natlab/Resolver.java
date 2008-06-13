package natlab;

import java.util.*;

/**
 * Used to resolve fn and script names. Contains lists of scripts, 
 * functions and class constructors. Resolution process is intertwined 
 * with the loading process, and they share a queue of files that need 
 * processing
 */

public class Resolver
{
    private Queue<ProgramEntry> toProcess;
}