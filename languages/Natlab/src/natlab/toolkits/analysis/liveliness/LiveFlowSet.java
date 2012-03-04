package natlab.toolkits.analysis.liveliness;

import java.util.*;
import analysis.*;
import analysis.natlab.*;
import natlab.toolkits.analysis.*;

public class LiveFlowSet implements FlowSet<String> {
	Set<String> set;
	
	public LiveFlowSet(){
		set = new HashSet<String>();
	}
	
	public LiveFlowSet(LiveFlowSet old){
		set = new HashSet<String>(old.set);
	}
	
	@Override
	public void add(String obj) {
		set.add(obj);
	}

	@Override
	public void clear() {
		set.clear();		
	}

	@Override
	public boolean contains(Object obj) {
		return set.contains(obj);
	}

	@Override
	public FlowSet<String> copy() {
		return new LiveFlowSet(this);
	}

	@Override
	public void copy(FlowSet<? super String> dest) {
		for (String s : set)
			dest.add(s);
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public Iterator<String> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object obj) {
		return set.remove(obj);
	}

	@Override
	public int size() {
		return set.size();
	}

}
