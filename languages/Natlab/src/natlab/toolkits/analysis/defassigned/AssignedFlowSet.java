package natlab.toolkits.analysis.defassigned;

import java.util.*;
import analysis.*;
import analysis.natlab.*;
import natlab.toolkits.analysis.*;
import natlab.toolkits.analysis.varorfun.ValueDatumPair;


public class AssignedFlowSet extends AbstractFlowSet<String>{
    private TreeSet<String> set;
    
    public void add(String v)
    {
        set.add(v);
    }
    public AssignedFlowSet() {
        set = new TreeSet();
    }
    
    
    public AssignedFlowSet clone() {
	return new AssignedFlowSet(set);
    }
    
    public AssignedFlowSet( Set<String> set ){
        this.set = new TreeSet(set);
    }
    
    void merge(AssignedFlowSet other, AssignedFlowSet dest){
    	dest.set=new TreeSet(set);
    	dest.set.retainAll(other.set);
    }
    
    public Iterator<String> iterator()
    {
	return set.iterator();
    }

    
    public boolean contains(Object obj) {
	return set.contains( obj );
    }
    
    public boolean remove (Object obj){
	return set.remove(obj);
    }

    public AssignedFlowSet emptySet(){
	return new AssignedFlowSet();
    }
    public boolean isEmpty() {
	return set.isEmpty();
    }
    
    
    public boolean remove(String obj) {
	return set.remove( obj);
    }
    

    public int size() {
        return set.size();
    }
    

    public List<String> toList() {
        return new ArrayList(set);
    }
	
    public void copy(AssignedFlowSet dest) {
	if (this == dest) return;
        dest.set=new TreeSet<String>(set);
    }
    public String toString(){
	return ""+set;
    }
    public Set<String> getSet(){
	return new TreeSet<String>(set);
    }
    
}