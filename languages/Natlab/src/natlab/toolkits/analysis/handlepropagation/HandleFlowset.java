package natlab.toolkits.analysis.handlepropagation;

import java.util.*;
import natlab.toolkits.analysis.*;

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
public class HandleFlowset extends HashMapFlowSet<String, MayMustTreeSet<HandleTarget>>
{
    /**
     * Generates a set containing only a TopHandleTarget.
     */
    public MayMustTreeSet<HandleTarget> makeTopSet()
    {
        MayMustTreeSet<HandleTarget> set = new MayMustTreeSet();
        set.add( new TopHandleTarget() );
        return set;
    }
    /**
     * Generates a set containing only a TopNamedHandleTarget.
     */
    public MayMustTreeSet<HandleTarget> makeTopNamedSet()
    {
        MayMustTreeSet<HandleTarget> set = new MayMustTreeSet();
        set.add( new TopNamedHandleTarget() );
        return set;
    }
    /**
     * Generates a set containing only a TopAnonymousHandleTarget.
     */
    public MayMustTreeSet<HandleTarget> makeTopAnonymousSet()
    {
        MayMustTreeSet<HandleTarget> set = new MayMustTreeSet();
        set.add( new TopAnonymousHandleTarget() );
        return set;
    }

    public void addAll( String key, Collection<HandleTarget> values )
    {
        if( !map.containsKey(key) )
            map.put(key, new MayMustTreeSet());
        for( HandleTarget target: values ){
            add(key, target);
        }
        if( values instanceof MayMustTreeSet && ((MayMustTreeSet)values).isMay() )
            map.get(key).makeMay();
    }
    public void add(String key, HandleTarget value)
    {
        MayMustTreeSet<HandleTarget> oldSet = map.get(key);
        if( oldSet == null ){
            MayMustTreeSet<HandleTarget> set = new MayMustTreeSet();
            set.add( value );
            map.put(key, set);
        }
        else{
            // note this is using the ordering defined by the handle
            // targets to ensure that if there are top elements in the
            // set then there is only one. It will also merge top name
            // targets and top anonymous targets into simply top
            // targets.
            if( oldSet.size() == 0 )
                oldSet.add( value );
            else if( value instanceof TopHandleTarget )
                map.put(key, makeTopSet());
            else{
                HandleTarget last = oldSet.pollLast();
                if( last instanceof TopHandleTarget )
                    map.put(key, makeTopSet());
                else if( last instanceof TopNamedHandleTarget )
                    if( value instanceof TopAnonymousHandleTarget )
                        map.put(key, makeTopSet());
                    else
                        map.put(key, makeTopAnonymousSet() );
                else if( last instanceof TopAnonymousHandleTarget )
                    if( value instanceof TopNamedHandleTarget )
                        map.put(key, makeTopSet());
                    else
                        map.put(key,makeTopAnonymousSet());
                else if( !(value instanceof NamedHandleTarget) &&
                         !(value instanceof AnonymousHandleTarget) ){
                    //value is a top element and the set had non top elements
                    MayMustTreeSet<HandleTarget> set = new MayMustTreeSet();
                    set.add( value );
                    map.put(key, set);
                }
                else{
                    oldSet.add(last);
                    oldSet.add(value);
                }
            }
        }
    }
    public void add(Map.Entry<String, MayMustTreeSet<HandleTarget>> entry )
    {
        //make sure the set is correct with respect to having top
        //elements. If problems then correct in favor of top elements.
        
        //if it has 0 or 1 entry then it must be correct
        if( entry.getValue().size()==0 || entry.getValue().size()==1 )
            super.add(entry);
        else{
            MayMustTreeSet<HandleTarget> set = entry.getValue();
            //if it has a top in it then just make only have a top
            if( set.last() instanceof TopHandleTarget )
                entry = new AbstractMap.SimpleEntry( entry.getKey(), makeTopSet() );
            else{
                HandleTarget last = set.pollLast();
                boolean lastIsTopNamed = last instanceof TopNamedHandleTarget;
                boolean lastIsTopAnon = last instanceof TopAnonymousHandleTarget;
                if( lastIsTopNamed || lastIsTopAnon ){
                    if( ( lastIsTopNamed && set.last() instanceof TopAnonymousHandleTarget) ||
                        ( lastIsTopAnon && set.last() instanceof TopNamedHandleTarget) )
                        //if two conflicting top elements, make full
                        //top
                        entry = new AbstractMap.SimpleEntry( entry.getKey(), makeTopSet() );
                    else{
                        MayMustTreeSet<HandleTarget> newSet = new MayMustTreeSet();
                        newSet.add(last);
                        entry = new AbstractMap.SimpleEntry( entry.getKey(), newSet );
                    }
                }
                set.add(last);
            }
            super.add(entry);
        }
                     
    }
    public void add(String key, MayMustTreeSet<HandleTarget> entry)
    {
        add( new AbstractMap.SimpleEntry( key, entry ) );
    }

    /**
     * Remove the particular value from the set associated with key.
     */
    public boolean remove( String key, HandleTarget value )
    {
        MayMustTreeSet<HandleTarget> set = map.get(key);
        if( set != null )
            return set.remove( value );
        else
            return false;
    }

    /**
     * Checks if the particular value is in the set associated with
     * the given key.
     */
    public boolean contains( String key, HandleTarget value )
    {
        MayMustTreeSet<HandleTarget> set = map.get(key);
        if( set != null )
            return set.contains( value );
        else
            return false;
    }

    public MayMustTreeSet<HandleTarget> get( String key )
    {
        return map.get(key);
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
            for( Map.Entry<String,MayMustTreeSet<HandleTarget>> entry : map.entrySet() ){
                other.add(entry.getKey(), entry.getValue().clone());
            }
        }
    }
    /**
     * Creates a clone of the flowset, clones the sets in the map as
     * well.
     */
    public HandleFlowset clone()
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
        
        for( Map.Entry<String, MayMustTreeSet<HandleTarget>> entry : this.toList()){
            String key = entry.getKey();
            MayMustTreeSet set = (MayMustTreeSet)entry.getValue().clone();
            if( !other.containsKey( key ) )
                set.makeMay();
            tmpDest.add( key, set );
        }
        for( Map.Entry<String, MayMustTreeSet<HandleTarget>> entry : other.toList()){
            if( tmpDest.containsKey( entry.getKey() ) ){
                MayMustTreeSet tmpSet = tmpDest.get( entry.getKey() );
                if( tmpSet.isEmpty() && !entry.getValue().isEmpty() )
                    tmpSet.makeMay();
                else if( !(tmpSet.isMust() && entry.getValue().isMust()) )
                    tmpSet.makeMay();
                for( HandleTarget value : entry.getValue() ){
                    tmpDest.add( entry.getKey(), value );
                }
            }
            else{
                MayMustTreeSet set = (MayMustTreeSet)entry.getValue().clone();
                set.makeMay();
                tmpDest.add( entry.getKey(), set );
            }
        }
        tmpDest.copy( dest );
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append("{");
        boolean first = true;
        String key;
        int spaceSize;
        StringBuffer spaces;
        for( Map.Entry<String,MayMustTreeSet<HandleTarget>> entry : map.entrySet() ){
            if( first ){
                s.append("\n");
                first = false;
            }
            else
                s.append(", \n");
            spaceSize = entry.getKey().length() + 8;
            spaces = new StringBuffer(spaceSize);
            for( int i=0; i<spaceSize; i++)
                spaces.append(" ");
            s.append(" ");
            s.append( entry.getKey() );
            s.append(" : ");
            s.append(entry.getValue().isMust()?"must":" may");
            s.append("{");

            boolean firstVal = true;
            for( HandleTarget value : entry.getValue() ){
                if( firstVal ){
                    s.append("\n");
                    first = false;
                }
                else 
                    s.append(", \n");
                s.append( spaces );
                s.append( "   " );
                s.append( value.toString() );
            }
            if( !first ){
                s.append("\n");
                s.append(spaces);
                s.append("}");
            }
        }
        s.append("\n}");
        return s.toString();
    }
}
