package natlab.toolkits.analysis;

import java.util.Map;

import analysis.Analysis;
import ast.Script;

/**
 * Interface for structural analysis. Note: implementations should
 * supply a standard constructor that takes in an ASTNode as argument.
 */
public interface FixedPointScriptAnalysis<D> extends Analysis
{
	public void setCurrentResults(Map<Script, D> res);
}