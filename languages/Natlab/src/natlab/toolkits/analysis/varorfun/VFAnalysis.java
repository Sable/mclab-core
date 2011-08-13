package natlab.toolkits.analysis.varorfun;

import ast.*;
import analysis.*;
import natlab.LookupFile;
import natlab.toolkits.analysis.FlowSet;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;


public interface VFAnalysis extends Analysis {
	public VFDatum getResult(Name n);
}
