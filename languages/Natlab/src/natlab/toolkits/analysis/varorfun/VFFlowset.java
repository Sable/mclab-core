package natlab.toolkits.analysis.varorfun;

import java.util.*;
import natlab.toolkits.analysis.*;

/**
 * Special implementation of FlowSet for the varorfun analysis. This
 * implementation uses a HashTable to store the set detail. The set
 * consists of identifier names associated with VFDatum objects. It is
 * a set in terms of the names, rather than (name,datum) pairs. 
 *
 * One overide of note is add. When adding a pair whose key matches an
 * existing key, rather than simply overriding the old datum, the
 * datums are merged. This means that to get an override you must
 * first remove the item then re add it. 
 *
 * @see VFDatum
 */

public class VFFlowset<V, D extends VFDatum> extends AbstractFlowSet<ValueDatumPair<V,D>>
{

    protected HashMap<V, D> set;
    
    public VFFlowset()
    {
        set = new HashMap();
    }

    public VFFlowset( HashMap<V, D> set )
    {
        this.set = set;

    }
    
    public VFFlowset<V,D> clone()
    {
        HashMap<V, D> newSet = new HashMap();

        for( Map.Entry<V, D> e : set.entrySet() )
            newSet.put( e.getKey(), (D)(e.getValue().clone()) );

        return new VFFlowset( newSet );
    }
    
    public VFFlowset<V, D> emptySet()
    {
        return new VFFlowset();
    }

    public void clear()
    {
        set.clear();
    }

    public boolean isEmpty()
    {
        return set.isEmpty();
    }
    public int size()
    {
        return set.size();
    }
    public void add(ValueDatumPair<V,D> pair)
    {
        VFDatum d = set.get( pair.getValue() );
        if( d != null )
            d.merge( pair.getDatum() );
        else
            set.put( pair.getValue(), pair.getDatum() );
    }

    public boolean remove( ValueDatumPair<V,D> pair )
    {
        return set.remove( pair.getValue() ) != null;
    }
    public boolean contains( ValueDatumPair<V,D> pair)
    {
        return set.containsKey( pair.getValue() );
    }
    public List< ValueDatumPair<V,D> > toList()
    {
        List< ValueDatumPair<V,D> > list = new ArrayList( set.size() );
        for( Map.Entry<V,D> entry : set.entrySet() ){
            list.add( new ValueDatumPair( entry.getKey(), entry.getValue() ) );
        }
        return list;
    }

}