package natlab.Static.valueanalysis;

import java.util.*;
import java.util.Map.Entry;

import natlab.Static.valueanalysis.value.*;
import natlab.toolkits.analysis.*;

/**
 * represents a flow map for the value analysis.
 * This is a map from variable name (String) to ValueSet.
 * Note that this is one of the view classes in this package representing mutable objects.
 * 
 * @author ant6n
 */
public class ValueFlowMap<D extends MatrixValue<D>> extends AbstractFlowMap<String, ValueSet<D>> 
       implements Mergable<ValueFlowMap<D>>{  
    LinkedHashMap<String,ValueSet<D>> map = new LinkedHashMap<String,ValueSet<D>>();
    public ValueFlowMap(){
        super(null);
    }
    
    public ValueFlowMap(Map<String, ValueSet<D>> aMap){
        super(null);
        this.putAll(aMap);
    }
    
    
    /**
     * copy constructor
     * @param aMap
     */
    public ValueFlowMap(ValueFlowMap<D> aMap){
        super(null);
        map.putAll(aMap.map);
    }
    
    @Override
    public boolean containsKey(Object key) {
        return containsKey(key);
    }

    @Override
    public ValueSet<D> get(Object K) {
        return map.get(K);
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public ValueSet<D> put(String key, ValueSet<D> values) {
        return map.put(key,values);
    }
    
    public void putAll(Map<String,ValueSet<D>> map) {
        map.putAll(map);
    }
    
    public void putAll(ValueFlowMap<D> other){
        map.putAll(other.map);
    }

    @Override
    public ValueSet<D> remove(Object key) {
        return map.remove(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public ValueFlowMap<D> emptyMap() {
        return new ValueFlowMap<D>();
    }
    
    @Override
    public ValueFlowMap<D> copy() {
        return new ValueFlowMap<D>(this);
    }
    
    /**
     * Returns a new ValueFlowMap, which is a merge of the two maps
     */
    @Override
    public ValueFlowMap<D> merge(ValueFlowMap<D> o) {
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
    
    
    /**
     * we'll make the print method go across multiple lines if the list is long
     */
    public static final int PRINT_COLUMNS_TO_NEWLINE = 100;
    @Override
    public String toString() {
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
            return flowMap.map.equals(map);
        }
        return false;   
    }
}
