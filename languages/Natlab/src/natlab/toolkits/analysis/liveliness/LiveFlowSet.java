package natlab.toolkits.analysis.liveliness;

import java.util.Iterator;
import java.util.Set;

import natlab.toolkits.analysis.FlowSet;

import com.google.common.collect.Sets;

public class LiveFlowSet implements FlowSet<String> {
	Set<String> set;
	
	public LiveFlowSet(){
		set = Sets.newHashSet();
	}
	
	public LiveFlowSet(LiveFlowSet old){
		set = Sets.newHashSet(old.set);
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
