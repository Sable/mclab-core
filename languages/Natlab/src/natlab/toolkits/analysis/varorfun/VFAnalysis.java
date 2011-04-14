package natlab.toolkits.analysis.varorfun;

import ast.*;
import natlab.LookupFile;
import natlab.toolkits.analysis.Analysis;
import natlab.toolkits.analysis.FlowSet;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;


public interface VFAnalysis extends Analysis<VFFlowset> {
	public VFDatum getResult(Name n);
}
