package natlab.Static.ir;

import natlab.Static.toolkits.analysis.IRNodeCaseHandler;

/**
 * top level interface for every node of the IR
 * @author ant6n
 */
public interface IRNode {
    public void irAnalyize(IRNodeCaseHandler irHandler);
}
