package natlab.toolkits.analysis;

import analysis.*;
import analysis.natlab.*;
import ast.*;
import java.util.*;

/**
 * Interface for structural analysis. Note: implementations should
 * supply a standard constructor that takes in an ASTNode as argument.
 */
public interface FixedPointScriptAnalysis<D> extends Analysis
{
	public void setCurrentResults(Map<Script, D> res);
}