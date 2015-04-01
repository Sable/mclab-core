package natlab.toolkits.analysis.varorfun;

import analysis.Analysis;
import ast.Name;

public interface VFAnalysis extends Analysis {
	public VFDatum getResult(Name n);
}
