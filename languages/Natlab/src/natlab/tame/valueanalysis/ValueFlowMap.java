package natlab.tame.valueanalysis;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import natlab.tame.valueanalysis.value.Value;
import natlab.toolkits.analysis.FlowData;
import natlab.toolkits.analysis.Mergable;

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
public class ValueFlowMap<V extends Value<V>> //extends AbstractFlowMap<String, ValueSet<V>> 
       implements Mergable<ValueFlowMap<V>>,FlowData{  
    LinkedHashMap<String,ValueSet<V>> map = new LinkedHashMap<String,ValueSet<V>>();
    private boolean isViable = true;
    
    public ValueFlowMap(){
       // super(null);
    }
    public static <V extends Value<V>> ValueFlowMap<V> newInstance(){
        return new ValueFlowMap<V>();
    }
    public ValueFlowMap(boolean isViable){
       // super(null);
        this.isViable = isViable;
    }
    public static <V extends Value<V>> ValueFlowMap<V> newInstance(boolean isViable){
        return new ValueFlowMap<V>(isViable);
    }    
    public ValueFlowMap(Map<String, ValueSet<V>> aMap){
       // super(null);
        this.putAll(aMap);
    }
    
    
    /**
     * copy constructor
     * @param aMap
     */
    public ValueFlowMap(ValueFlowMap<V> aMap){
       // super(null);
        map.putAll(aMap.map);
        isViable = aMap.isViable;
    }
    
    //@Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    //@Override
    public ValueSet<V> get(Object K) {
        return map.get(K);
    }

    //@Override
    public Set<String> keySet() {
        return map.keySet();
    }

    //@Override
    public ValueSet<V> put(String key, ValueSet<V> values) {
        if (!isViable) return null;
        return map.put(key,values);
    }
    
    public void putAll(Map<String,ValueSet<V>> map) {
        if (!isViable) return;
        this.map.putAll(map);
    }
    
    public void putAll(ValueFlowMap<V> other){
        if (!isViable) return;
        this.map.putAll(other.map);
    }

    //@Override
    public ValueSet<V> remove(Object key) {
        if (!isViable) return null;
        return map.remove(key);
    }

    //@Override
    public int size() {
        return map.size();
    }

    //@Override
    public ValueFlowMap<V> emptyMap() {
        return new ValueFlowMap<V>();
    }
    
    //@Override
    public ValueFlowMap<V> copy() {
        return new ValueFlowMap<V>(this);
    }
    
    /**
     * Returns a new ValueFlowMap, which is a merge of the two maps
     */
    @Override
    public ValueFlowMap<V> merge(ValueFlowMap<V> o) {
    	if (!isViable() || !o.isViable()){
            return isViable?new ValueFlowMap<V>(this):new ValueFlowMap<V>(o);            
        }
        ValueFlowMap<V> result, smaller;
        //clone the bigger set
        if (this.size() > o.size()){
            result = new ValueFlowMap<V>(this);
            smaller = o;
        } else {
            result = new ValueFlowMap<V>(o);
            smaller = this;
        }
        //merge in smaller set
        for (Entry<String, ValueSet<V>> e : smaller.map.entrySet()){
            result.mergePut(e.getKey(), e.getValue());
        }
        return result;
    }
    
    public void mergePut(String key,ValueSet<V> value){
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
    public void copyOtherIntoThis(ValueFlowMap<V> other){
        this.isViable = other.isViable;
        this.map = new LinkedHashMap<String,ValueSet<V>>(other.map);
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
        Iterator<Entry<String, ValueSet<V>>> i = map.entrySet().iterator();
        while(i.hasNext()){
            Entry<String, ValueSet<V>> entry = i.next();
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
        if (obj instanceof ValueFlowMap){
            ValueFlowMap<V> flowMap = (ValueFlowMap<V>)obj;
            boolean res1 = flowMap.isViable==isViable;
            boolean res2 = this.toString().equals(flowMap.toString()); 
            /*
             *  it is so weird, for some benchmarks, if use 
             *  "flowMap.map.equals(map);" instead of toString comparison, 
             *  it will end up in infinite loop, because map comparison 
             *  return false, while toString comparison return true, which 
             *  apparently equals by our observation. TODO figure it out.
             */
            boolean result = res1 && res2;
            return result;
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
}
