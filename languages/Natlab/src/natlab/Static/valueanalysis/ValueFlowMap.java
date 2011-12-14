package natlab.Static.valueanalysis;

import java.util.*;
import java.util.Map.Entry;

import natlab.Static.valueanalysis.value.*;
import natlab.toolkits.analysis.*;

/**
 * represents a flow map for the value analysis.
 * This is a map from variable name (String) to ValueSet.
 * Note that this is one of the few classes in this package representing mutable objects.
 * 
 * A Flow set may be non viable if
 * - all control flow paths to the associated node result in an error
 * - as a first approximation for a recursive call - i.e. 'no knowledge'
 * 
 * A non-viable flowset can be erroneous or just have no knowledge. Either way, merging
 * a viable and a non-viable flowset will result in just viable the flowset (plus, possibly,
 * some annotations for errors). a non viable flowset is empty.
 * 
 * 
 * @author ant6n
 */
public class ValueFlowMap<D extends MatrixValue<D>> //extends AbstractFlowMap<String, ValueSet<D>> 
       implements Mergable<ValueFlowMap<D>>,FlowData{  
    LinkedHashMap<String,ValueSet<D>> map = new LinkedHashMap<String,ValueSet<D>>();
    private boolean isViable = true;
    
    public ValueFlowMap(){
       // super(null);
    }
    public static <D extends MatrixValue<D>> ValueFlowMap<D> newInstance(){
        return new ValueFlowMap<D>();
    }
    public ValueFlowMap(boolean isViable){
       // super(null);
        this.isViable = isViable;
    }
    public static <D extends MatrixValue<D>> ValueFlowMap<D> newInstance(boolean isViable){
        return new ValueFlowMap<D>(isViable);
    }    
    public ValueFlowMap(Map<String, ValueSet<D>> aMap){
       // super(null);
        this.putAll(aMap);
    }
    
    
    /**
     * copy constructor
     * @param aMap
     */
    public ValueFlowMap(ValueFlowMap<D> aMap){
       // super(null);
        map.putAll(aMap.map);
        isViable = aMap.isViable;
    }
    
    //@Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    //@Override
    public ValueSet<D> get(Object K) {
        return map.get(K);
    }

    //@Override
    public Set<String> keySet() {
        return map.keySet();
    }

    //@Override
    public ValueSet<D> put(String key, ValueSet<D> values) {
        if (!isViable) return null;
        return map.put(key,values);
    }
    
    public void putAll(Map<String,ValueSet<D>> map) {
        if (!isViable) return;
        this.map.putAll(map);
    }
    
    public void putAll(ValueFlowMap<D> other){
        if (!isViable) return;
        this.map.putAll(other.map);
    }

    //@Override
    public ValueSet<D> remove(Object key) {
        if (!isViable) return null;
        return map.remove(key);
    }

    //@Override
    public int size() {
        return map.size();
    }

    //@Override
    public ValueFlowMap<D> emptyMap() {
        return new ValueFlowMap<D>();
    }
    
    //@Override
    public ValueFlowMap<D> copy() {
        return new ValueFlowMap<D>(this);
    }
    
    /**
     * Returns a new ValueFlowMap, which is a merge of the two maps
     */
    @Override
    public ValueFlowMap<D> merge(ValueFlowMap<D> o) {
        if (!isViable() || !o.isViable()){
            return isViable?new ValueFlowMap<D>(this):new ValueFlowMap<D>(o);            
        }
        ValueFlowMap<D> result, smaller;
        //clone the bigger set
        if (this.size() > o.size()){
            result = new ValueFlowMap<D>(this);
            smaller = o;
        } else {
            result = new ValueFlowMap<D>(o);
            smaller = this;
        }
        //merge in smaller set
        for (Entry<String, ValueSet<D>> e : smaller.map.entrySet()){
            result.mergePut(e.getKey(), e.getValue());
        }
        return result;
    }
    
    public void mergePut(String key,ValueSet<D> value){
        if (!isViable) return;
        if (!map.containsKey(key)){
            map.put(key,value);
        } else {
            map.put(key,map.get(key).merge(value));
        }
    }
    
    
    /**
     * makes this value flow map a copy of the other
     * overwrites whatever is in the current flowset
     */
    public void copyOtherIntoThis(ValueFlowMap<D> other){
        this.isViable = other.isViable;
        this.map = new LinkedHashMap<String,ValueSet<D>>(other.map);
    }
    
    
    /**
     * we'll make the print method go across multiple lines if the list is long
     */
    public static final int PRINT_COLUMNS_TO_NEWLINE = 100;
    @Override
    public String toString() {
        if (!isViable) return "{not viable}";
        StringBuffer buf = new StringBuffer();
        int indexOflastBreak = 0;
        buf.append('{');
        Iterator<Entry<String, ValueSet<D>>> i = map.entrySet().iterator();
        while(i.hasNext()){
            Entry<String, ValueSet<D>> entry = i.next();
            String item = entry.getKey() + '=' + entry.getValue();
            if (buf.length()+item.length() > indexOflastBreak + PRINT_COLUMNS_TO_NEWLINE){
                buf.append('\n');
                indexOflastBreak = buf.length();
            }
            buf.append(item);
            if (i.hasNext()){
                buf.append(", ");
            }
        }
        buf.append('}');
        return buf.toString();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof ValueFlowMap<?>){
            ValueFlowMap<D> flowMap = (ValueFlowMap<D>)obj;
            return flowMap.isViable==isViable && flowMap.map.equals(map);
        }
        return false;   
    }
    
    
    @Override
    public int hashCode() {
        return isViable?43:19 + this.map.hashCode();
    }
    
    
    public boolean isViable(){
        return isViable;
    }
    
    /**
     * fast merge does the same as merge, except it destroys the arguments or this.
     * It's faster by returning a modified version of the argument - usually
     * whichever ValueFlowMap had more elements in it
     * TODO - remove this, probably
    public ValueFlowMap<D> fastMerge(ValueFlowMap<D> other){
        if (this.map.size() > other.map.size()){
            this.union(other);
            return this;
        } else {
            other.union(this);
            return other;
        }
    }*/
}
