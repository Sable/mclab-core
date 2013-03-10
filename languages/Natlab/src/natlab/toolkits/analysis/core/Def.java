package natlab.toolkits.analysis.core;

/**
 * Just a marker interface to capture those AST nodes we consider defs for the
 * purposes of reaching defs analysis; these are assignments (AssignStmt),
 * global declarations (GlobalStmt) and function parameters (Name).
 * 
 * (These classes are made to implement this interface in Def.jadd).
 */
public interface Def {

}
