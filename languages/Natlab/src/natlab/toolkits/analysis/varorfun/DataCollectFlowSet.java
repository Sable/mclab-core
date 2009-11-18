package natlab.toolkits.analysis.varorfun;

import java.util.*;
import natlab.toolkits.analysis.*;

public class DataCollectFlowSet<K,V> extends AbstractFlowSet< DataPair<K,V> >
{

    public static boolean DEBUG = false;

    protected HashMap<K,V> data;

    public void copy(DataCollectFlowSet<K,V> dest)
    {
        if( this == dest) return;
        dest.clear();
        for( DataPair<K,V> element : toList() )
            dest.add(element.clone());
    }
    public DataCollectFlowSet()
    {
        data = new HashMap();
    }


    public DataCollectFlowSet( HashMap<K,V> data ){
        this.data = data;
    }

    public DataCollectFlowSet<K,V> clone()
    {
        HashMap<K,V> newData = new HashMap();

        for( Map.Entry<K,V> e : data.entrySet() )
            newData.put( e.getKey(), e.getValue() );

        return new DataCollectFlowSet( newData );
    }

    public DataCollectFlowSet<K,V> emptySet()
    {
        return new DataCollectFlowSet();
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
    public boolean remove( DataPair<K,V> pair )
    {
        return data.remove( pair.getKey() ) != null;
    }
    public boolean contains( DataPair<K,V> pair )
    {
        return data.containsKey( pair.getKey() );
    }
    public V contains( K key )
    {
        return data.get(key);
    }
    public List< DataPair<K,V> > toList()
    {
        List< DataPair<K,V> > list = new ArrayList( data.size() );
        for( Map.Entry<K,V> entry : data.entrySet() ){
            list.add( new DataPair( entry.getKey(), entry.getValue() ) );
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