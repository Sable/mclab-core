package natlab.tame.interproceduralAnalysis.examples.live;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;

/**
 * a class used as input for interprocedural analysis nodes.
 * It contains a list of live values, which denote the live-ness
 * of the output parameters of a function, and an interprocedural
 * analysis node of a previous analysis, which is used to get
 * callsites.
 */

public class LiveInput extends ArrayList<LiveValue> {
	private static final long serialVersionUID = 1L;
	private InterproceduralAnalysisNode<?,?,?> node;
	
	protected LiveInput(InterproceduralAnalysisNode<?,?,?> node){
		super();
		this.node = node;
	}
	protected LiveInput(InterproceduralAnalysisNode<?,?,?> node,List<LiveValue> list){
		super(list);
		this.node = node;
	}
	protected InterproceduralAnalysisNode<?,?,?> getNode(){
		return node;
	}
	
	@Override
	public boolean equals(Object arg0) {
		return (arg0 instanceof LiveInput && ((LiveInput)arg0).node.equals(this.node) && super.equals(arg0));
	}
}
