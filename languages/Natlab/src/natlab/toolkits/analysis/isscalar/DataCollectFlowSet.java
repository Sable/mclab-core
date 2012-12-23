package natlab.toolkits.analysis.isscalar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import natlab.toolkits.analysis.AbstractFlowSet;
import natlab.toolkits.analysis.FlowSet;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DataCollectFlowSet<K,V> extends AbstractFlowSet< DataPair<K,V> >
{

    public static boolean DEBUG = false;

    protected HashMap<K,V> data;

    /**
     * Returns an iterator over the data in this flow set. The {@code
     * DataPair}s returned by the iterator are mutable but do not
     * effect the data in the set.
     */
    public Iterator<DataPair<K,V>> iterator()
    {
        return new Iterator<DataPair<K,V>>(){
            Iterator<Map.Entry<K,V>> mapIterator = data.entrySet().iterator();
            public boolean hasNext()
            {
                return mapIterator.hasNext();
            }
            public DataPair<K,V> next()
            {
                Map.Entry<K,V> entry = mapIterator.next();
                return new DataPair<K,V>(entry.getKey(), entry.getValue() );
            }
            public void remove()
            {
                mapIterator.remove();
            }
        };
    }

    public void copy(DataCollectFlowSet<K,V> dest)
    {
        if( this == dest) return;
        dest.clear();
        for( DataPair<K,V> element : this)
            dest.add(element.copy());
    }
    @Override public void copy(FlowSet<? super DataPair<K,V>> dest)
    {
        Preconditions.checkArgument(dest instanceof DataCollectFlowSet,
            "copy only accepts DataCollectFlowSets");
        /*
          dest contains something that is a super type of
          DataPair<K,V>, so if the raw type of dest is
          DataCollectFlowSet then, by definition and by the
          invariant nature of generics, it must be of type
          DataCollectFlowSet<K,V>
        */
        @SuppressWarnings("unchecked") 
        DataCollectFlowSet<K,V> dataCollectorDest = (DataCollectFlowSet<K,V>)dest;
        copy(dataCollectorDest);

    }

    public DataCollectFlowSet()
    {
        data = Maps.newHashMap();
    }

    public DataCollectFlowSet( HashMap<K,V> data ){
        this.data = data;
    }

    public DataCollectFlowSet<K,V> copy()
    {
        return new DataCollectFlowSet<K,V>(Maps.newHashMap(data));
    }

    public DataCollectFlowSet<K,V> emptySet()
    {
        return new DataCollectFlowSet<K,V>();
    }

    public void clear()
    {
        data.clear();
    }
    public boolean isEmpty()
    {
        return data.isEmpty();
    }
    public int size()
    {
        return data.size();
    }
    public void add(DataPair<K,V> pair)
    {
        if( DEBUG )
            System.out.println("adding " + pair);
        data.put( pair.getKey(), pair.getValue() );
    }
    public void add(K k, V v)
    {
        if( DEBUG )
            System.out.println("adding " + k + " " + v);
        data.put(k,v);
    }
    public boolean remove( Object obj )
    {
        return data.entrySet().remove(obj);
    }
    public boolean contains( Object pair )
    {
        return data.entrySet().contains( pair );
    }
    public V containsKey( K key )
    {
        return data.get(key);
    }
    public List< DataPair<K,V> > toList()
    {
        return Lists.newArrayList(this);
    }

    public String toString()
    {
        return String.format("{%s}", Joiner.on(",\n").join(this));
    }
}