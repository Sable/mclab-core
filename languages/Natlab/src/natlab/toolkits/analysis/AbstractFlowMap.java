// =========================================================================== //
//                                                                             //
// Copyright 2011 Jesse Doherty and McGill University.                         //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.toolkits.analysis;

import java.util.*;

/**
 * An abstract implementation of {@link FlowMap} intended to make new
 * flow maps easier to implement. Many operations are implemented, but
 * may not be very efficient.
 * 
 * @author Jesse Doherty
 */
public abstract class AbstractFlowMap<K,V> implements FlowMap<K,V>
{
    protected Merger<V> merger;

    public AbstractFlowMap(){
        setMerger( null );
    }
    public AbstractFlowMap( Merger<V> m ){
        setMerger( m );
    }

    public void setMerger(Merger<V> m)
    {
        merger = m;
    }
    public Merger<V> getMerger()
    {
        return merger;
    }

    /**
     * Returns a {@code FlowMap} containing the same data as this map,
     * this includes using the same merger. An inefficient
     * implementation of copy based on the {@code copy(dest)} and
     * {@code emptyMap()} methods.
     *
     * This implementation should be overridden in a child class, if a
     * more efficient version is possible.
     */
    public FlowMap<K,V> copy(){
        FlowMap<K,V> theCopy = emptyMap(); 
        copy( theCopy );
        return theCopy;
    }
    /**
     * Copies the data and merger of {@code this} into {@code
     * dest}. This causes {@code dest} to be cleared. This is an
     * inefficient implementation based on the {@code clear()} and
     * {@code put(...)} methods.
     *
     * This implementation should be overridden in a child class, if a
     * more efficient version is possible.
     */
    public void copy(FlowMap<K,V> dest)
    {
        if( this == dest ) return;
        dest.clear();
        for( K k : keySet() )
            dest.put(k, get(k));
        dest.setMerger( getMerger() );
    }

    /**
     * Clears {@code this} map. This is an inefficient implementation
     * based on the {@code keySet()} and {@code remove(...)} methods,
     * and the {@code toArray()} method on the set returned by {@code keySet()}.
     *
     * This implementation should be overridden in a child class, if a
     * more efficient version is possible.
     */
    public void clear()
    {
        for( Object i : keySet().toArray() )
            remove( i );
    }
    public boolean isEmpty()
    {
        return size()==0;
    }
    abstract public int size();
    abstract public V put(K key, V value );

    public V mergePut(K key, V value)
    {
        V curVal = get( key );
        if( curVal == null )
            return put(key, value);
        else if( merger != null )
            return put( key, merger.merge( curVal, value ) );
        else if( curVal instanceof Mergable ){
            //Cast is legal based on Mergable contract. Something of
            //type V implementing Mergable should implement Mergable<V>.
            @SuppressWarnings("unchecked")
            Mergable<V> mergableCurVal = (Mergable<V>)curVal;
            return put( key, mergableCurVal.merge(value) );
        }
        else
            throw new ClassCastException( "Values are not mergable and no merger was given" );
    }
            
    public V mergePut(Merger<V> m, K key, V value)
    {
        V curVal = get( key );
        if( curVal == null )
            return put( key, value );
        else
            return put( key, m.merge(curVal, value) );
    }
    abstract public V get( Object K );
    abstract public V remove( Object key );
    abstract public boolean containsKey( Object key );
    abstract public Set<K> keySet();
    abstract public AbstractFlowMap<K,V> emptyMap();
}
