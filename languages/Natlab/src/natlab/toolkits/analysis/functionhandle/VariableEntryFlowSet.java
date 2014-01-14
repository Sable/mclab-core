package natlab.toolkits.analysis.functionhandle;

import java.util.HashSet;

public class VariableEntryFlowSet extends HashSet<VariableEntry> {

	/**
	 * we override add such that if a VariableEntry already exists (by name)
	 * in the set, the existing and the new entries get merged (via union)
	 */
	@Override
	public boolean add(VariableEntry obj) {
		if (contains(obj)){
			//we need the already existing element -- need to iterate -- very bad TODO
			VariableEntry other = null;
			for (VariableEntry ve : this){
				if (ve.equals(obj)){
					other = ve; 
					break;
				}
			}
			remove(other);
			add(other.union(obj));
			return true;
		} else {
			return add(obj);			
		}
	}
	
	/**
	 * remove existing instance, and add the new object
	 */
	public void addNew(VariableEntry obj){
	  remove(obj);
	  add(obj);		
	}
	
}
