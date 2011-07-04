package natlab.toolkits.analysis.varorfun;

import java.util.*;
import natlab.toolkits.analysis.*;

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
                return new DataPair(entry.getKey(), entry.getValue() );
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
        for( DataPair<K,V> element : toList() )
            dest.add(element.copy());
    }
    @Override public void copy(FlowSet<? super DataPair<K,V>> dest)
    {
        if( dest instanceof DataCollectFlowSet ){
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
        else
            throw new IllegalArgumentException("copy only accepts DataCollectFlowSets");
    }

    public DataCollectFlowSet()
    {
        data = new HashMap<K,V>();
    }


    public DataCollectFlowSet( HashMap<K,V> data ){
        this.data = data;
    }

    public DataCollectFlowSet<K,V> copy()
    {
        /*HashMap<K,V> newData = new HashMap();

        for( Map.Entry<K,V> e : data.entrySet() )
        newData.put( e.getKey(), e.getValue() );*/
        HashMap<K,V> newData = new HashMap<K,V>(data);
        return new DataCollectFlowSet<K,V>( newData );
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
        List< DataPair<K,V> > list = new ArrayList<DataPair<K,V>>( data.size() );
        for( Map.Entry<K,V> entry : data.entrySet() ){
            list.add( new DataPair<K,V>( entry.getKey(), entry.getValue() ) );
        }
        return list;
    }

    /*public void union(DataCollectFlowSet<K,D> other,
                      DataCollectFlowSet<K,D> dest )
    {
        throw new UnsupportedOperationException;
        }*/

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append("{");
        boolean first = true;
        for( Map.Entry<K,V> entry : data.entrySet() ){
            if( first ){
                s.append("\n");
                first = false;
            }
            else
                s.append(", \n");
            
            s.append( DataPair.toString(entry.getKey(), entry.getValue() ) );
        }
        if( !first )
            s.append("\n");
        s.append("}");

        return s.toString();
    }
}