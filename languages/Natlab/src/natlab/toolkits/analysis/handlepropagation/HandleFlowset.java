package natlab.toolkits.analysis.handlepropagation;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import natlab.toolkits.analysis.handlepropagation.handlevalues.AbstractValue;
import natlab.toolkits.analysis.handlepropagation.handlevalues.Value;

import com.google.common.collect.ForwardingMap;

/**
 * Special implementation of FlowSet for HandlePropagation
 * analysis. It is a set in terms of variable names and maps those
 * names to sets of possible handle targets for that variable. Note
 * that there is a special add method that takes a String and a
 * HandleTarget and will merge that target into the set associated
 * with the String. If overriding the handle targets is desired, the
 * standard add methods will work, or a remove and add is required.
 *
 * @see HandleTarget
 */
public class HandleFlowset extends ForwardingMap<String, TreeSet<Value>>
{
    private Map<String, TreeSet<Value>> map = new HashMap<>();
    
    @Override
    public Map<String, TreeSet<Value>> delegate() {
      return map;
    }
  
    public void addAll( String key, Collection<Value> values )
    {
        if(containsKey(key) )
            put(key, new TreeSet<Value>());
        for( Value target: values ){
            add(key, target);
        }
    }
    
    /**
     * Used to add an abstract DataValue to a given set. it keeps
     * track of the fact that we want at most one such value in the
     * set, and keeps track of the correct value to have in the set
     * based on the partial order on the values. 
     */
    protected void addAbstractDataValue( AbstractValue value, TreeSet<Value> oldSet )
    {
        boolean hasDO, hasDHO, hasDWH;
        hasDO = oldSet.contains(AbstractValue.newDataOnly());
        hasDHO = oldSet.contains(AbstractValue.newDataHandleOnly());
        hasDWH = oldSet.contains(AbstractValue.newDataWithHandles());

        if( hasDWH )
            return;
        else if( hasDO && (value.isDataHandleOnly() || value.isDataWithHandles()) ){
            oldSet.remove(AbstractValue.newDataOnly());
            oldSet.add(AbstractValue.newDataWithHandles());
            return;
        }
        else if( hasDHO && (value.isDataOnly() || value.isDataWithHandles()) ){
            oldSet.remove(AbstractValue.newDataHandleOnly() );
            oldSet.add(AbstractValue.newDataWithHandles());
            return;
        }
        else if( !hasDO && !hasDHO && !hasDWH ){
            oldSet.add(value);
        }
    }
    public void add(String key, Value value)
    {
        TreeSet<Value> oldSet = get(key);
        if( oldSet == null ){
            TreeSet<Value> set = new TreeSet<Value>();
            set.add( value );
            map.put(key, set);
        }
        else{
            if( oldSet.size() == 0 )
                oldSet.add( value );
            else if( value instanceof AbstractValue ){
                AbstractValue abstractValue = (AbstractValue)value;
                if( abstractValue.isDataWithHandles() ||
                    abstractValue.isDataOnly() ||
                    abstractValue.isDataHandleOnly() )
                    addAbstractDataValue( abstractValue, oldSet );
                else
                    oldSet.add( value );
            }
            else{
                oldSet.add( value );
            }
        }
    }
    public void add(Map.Entry<String, TreeSet<Value>> entry )
    {
        //make sure the set is correct with respect to the abstract
        //data values, there should be only one

        //if it has 0 or 1 entry then it must be correct
        if( entry.getValue().size()==0 || entry.getValue().size()==1 )
            map.put(entry.getKey(), entry.getValue());
        else{
            TreeSet<Value> set = entry.getValue();
            boolean hasDO, hasDHO, hasDWH;
            hasDO = set.contains(AbstractValue.newDataOnly());
            hasDHO = set.contains(AbstractValue.newDataHandleOnly());
            hasDWH = set.contains(AbstractValue.newDataWithHandles());
            if( hasDO && hasDHO ){
                TreeSet<Value> newSet = new TreeSet<Value>(set);
                newSet.remove(AbstractValue.newDataOnly());
                newSet.remove(AbstractValue.newDataHandleOnly());
                newSet.add(AbstractValue.newDataWithHandles());
                entry = new AbstractMap.SimpleEntry<String,TreeSet<Value>>( entry.getKey(), newSet );
            }
            if( hasDO && hasDWH ){
                TreeSet<Value> newSet = new TreeSet<Value>(set);
                newSet.remove(AbstractValue.newDataOnly());
                entry = new AbstractMap.SimpleEntry<String,TreeSet<Value>>( entry.getKey(), newSet );
            }
            if( hasDHO && hasDWH ){
                TreeSet<Value> newSet = new TreeSet<Value>(set);
                newSet.remove(AbstractValue.newDataHandleOnly());
                entry = new AbstractMap.SimpleEntry<String,TreeSet<Value>>( entry.getKey(), newSet );
            }
            map.put(entry.getKey(), entry.getValue());
        }
    }

    public void add(String key, TreeSet<Value> entry)
    {
        add( new AbstractMap.SimpleEntry<String,TreeSet<Value>>( key, entry ) );
    }

    /**
     * Remove the particular value from the set associated with key.
     */
    public boolean remove( String key, Value value )
    {
        TreeSet<Value> set = get(key);
        return set != null && set.remove(value);
    }

    /**
     * Checks if the particular value is in the set associated with
     * the given key.
     */
    public boolean contains( String key, Value value )
    {
        TreeSet<Value> set = get(key);
        return set != null && set.contains(value);
    }

    /**
     * Copies the flowset into another one. Does a clone of the sets
     * in map.
     */
    public void copy( HandleFlowset other )
    {
        if( this == other )
            return;
        else{
            other.clear();
            for( Map.Entry<String,TreeSet<Value>> entry : entrySet() ){
                other.add(entry.getKey(), new TreeSet<Value>(entry.getValue()));
            }
        }
    }
    /**
     * Creates a clone of the flowset, clones the sets in the map as
     * well. If one set has an entry for an id and the other doesn't
     * then Undef is added to the merged version.
     */
    public HandleFlowset copy()
    {
        HandleFlowset newSet = new HandleFlowset();
        copy(newSet);
        return newSet;
    }
    public void union( HandleFlowset other, HandleFlowset dest )
    {
        if( dest == this && dest == other )
            return;
        if( this == other ){
            copy( dest );
            return;
        }

        HandleFlowset tmpDest = new HandleFlowset();
        
        for( Map.Entry<String, TreeSet<Value>> entry : entrySet()){
            String key = entry.getKey();
            TreeSet<Value> set = new TreeSet<Value>(entry.getValue());
            if( !other.containsKey( entry.getKey() ) )
                set.add( AbstractValue.newUndef() );
            tmpDest.add( key, set );
        }
        for( Map.Entry<String, TreeSet<Value>> entry : entrySet()){
            if( tmpDest.containsKey( entry.getKey() ) ){
                TreeSet<Value> tmpSet = tmpDest.get( entry.getKey() );
                for( Value value : entry.getValue() ){
                    tmpDest.add( entry.getKey(), value );
                }
            }
            else{
                TreeSet<Value> set = new TreeSet<Value>(entry.getValue());
                set.add( AbstractValue.newUndef());
                tmpDest.add( entry.getKey(), set );
            }
        }
        tmpDest.copy( dest );
    }

  public String toString() {
    return map.entrySet().stream()
        .map((Map.Entry<String, TreeSet<Value>> e) ->
            String.format(
                "    %s: %s",
                e.getKey(),
                e.getValue().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", ", "{", "}"))))
        .collect(Collectors.joining(",\n", "{\n", "\n}"));
  }
}
