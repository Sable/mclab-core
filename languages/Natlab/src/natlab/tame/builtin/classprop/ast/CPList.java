package natlab.tame.builtin.classprop.ast;


import beaver.Symbol;

/**
 * A list of CP values. can be indexed by integers. It's the only non-expr node.
 * Used while parsing, but shouldn't pop up in an actual CP tree.
 */

public class CPList extends Symbol {
	CP element;
	CPList next;
	
	public CPList(CP element, CPList next){
		this.element = element;
		this.next = next;
	}
	
	public CP get(int index){
		if (index == 0) return element;
		return (next.get(index-1));
	}
	
	public int size(){
		if (next == null) return 1;
		return next.size()+1;
	}
	
	/**
	 * returns the list as bunch of chain nodes
	 */
	public CP asChain(){
		if (next == null) return element;
		return new CPChain(element,next.asChain());
	}
	
	/**
	 * returns the list as bunch of union nodes
	 */
	public CP asUnion(){
		if (next == null) return element;
		return new CPUnion(element,next.asUnion());
	}

	@Override
	public String toString() {
		return element.toString()+(next==null?":":":"+next);
	}

}



