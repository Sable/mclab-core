package natlab.toolkits.analysis.functionhandle;

import natlab.toolkits.analysis.HashSetFlowSet;

public class VariableEntryFlowSet extends HashSetFlowSet<VariableEntry> {

	/**
	 * we override add such that if a VariableEntry already exists (by name)
	 * in the set, the existing and the new entries get merged (via union)
	 */
	@Override
	public void add(VariableEntry obj) {
		if (set.contains(obj)){
			//we need the already existing element -- need to iterate -- very bad TODO
			VariableEntry other = null;
			for (VariableEntry ve : set){
				if (ve.equals(obj)){
					other = ve; 
					break;
				}
			}
			set.remove(other);
			set.add(other.union(obj));
		} else {
			set.add(obj);			
		}
	}
	
	/**
	 * remove existing instance, and add the new object
	 */
	public void addNew(VariableEntry obj){
		set.remove(obj);
		set.add(obj);		
	}
	
}
