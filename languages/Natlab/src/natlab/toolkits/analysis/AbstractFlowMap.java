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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

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

    /**
     * Remove all data associated with any of the keys in the given
     * collection. Returns true if the map has changed.
     *
     * @param keys  Collection of keys to be removed
     * @return  Whether or not the map has changed
     */
    public boolean removeKeys( Collection<?> keys )
    {
        if( keys == null )
            return false;
        boolean changed = false;
        for( Object key : keys )
            if( remove( key ) != null )
                changed = true;
        return changed;
    }
    /**
     * Creates and returns an object implementing the Merger
     * interface. This merger checks if its inputs are mergable, and
     * if so, uses their merge operation. Otherwise it thorws a {@code
     * ClassCastException}.
     *
     * @return A Merger object that uses its inputs merge operation
     */
    private Merger<V> makeNaturalMerger(){
        return new Merger<V>(){
            public V merge( V v1, V v2 ){
                if( v1 instanceof Mergable && v2 instanceof Mergable ){
                    //cast is safe based assumptions made by
                    //implementing Mergable
                    @SuppressWarnings("unchecked")
                        Mergable<V> mergableV1 = (Mergable<V>)v1;
                    return mergableV1.merge(v2);
                }
                else
                    throw new ClassCastException( "Values are not mergable and no merger was given" );
            }
        };
    }

    /**
     * Checks if any of the inputs are null.
     */
    private <T> boolean anyNull( T... args )
    {
        for( T o : args )
            if( o == null )
                return true;
        return false;
    }

    /**
     * Checks if the input to one of the merge operations is
     * correct. This means whether or not any of the inputs are
     * null. If any argument is null, an IllegalArgumentException is
     * thrown. 
     *
     * @throws NullPointerException If any of the arguments are null
     */
    private void checkMergeInput( Merger<V> m, FlowMap<K,V>... maps )
    {
        Preconditions.checkNotNull(m);
        checkMergeInput(maps);
    }
    /**
     * Checks if the input to one of the merge operations is
     * correct. This means whether or not any of the inputs are
     * null. If any argument is null, an IllegalArgumentException is
     * thrown. 
     *
     * @throws IllegalArgumentException If any of the arguments are null
     */
    private void checkMergeInput( FlowMap<K,V>... maps )
    {
        Preconditions.checkArgument(!anyNull(maps), "Input FlowMap was null");
    }

    /**
     * Unions {@code other} into {@code this}. Other cannot be
     * null. The union will contain all the keys contained in either
     * {@code other} or {@code this}. If the key was only in one, then
     * the resulting value is the value from the map that had the
     * key. If they both contained the key, the result is the merge of
     * the two values. The merge operation is either from an available
     * Merger, or, if the values implement the {@code Mergable} interface,
     * their merge operation is used. If there is no merger, and the
     * data does not implement {@code Mergable}, a {@code
     * ClassCastException} is thrown.
     *
     * @throws ClassCastException If merging cannot be done
     */
    @SuppressWarnings("unchecked")
    public void union(FlowMap<K,V> other){
        checkMergeInput( other );
        if( other == this )
            return;
        else if( merger != null )
            union( merger, other );
        else{
            Merger<V> m = makeNaturalMerger();
            union( m, other );
        }
    }
    /**
     * Unions {@code other} and {@code this} into {@code dest}. {@code
     * other} and {@code dest} cannot be null. {@code dest} will be
     * cleared during this operation. The union will contain all the
     * keys contained in either {@code other} or {@code this}. If the
     * key was only in one, then the resulting value is the value from
     * the map that had the key. If they both contained the key, the
     * result is the merge of the two values. The merge operation is
     * either from an available Merger, or, if the values implement
     * the {@code Mergable} interface, their merge operation is
     * used. If there is no merger, and the data does not implement
     * {@code Mergable}, a {@code ClassCastException} is thrown.
     *
     * @throws ClassCastException If merging cannot be done
     */
    @SuppressWarnings("unchecked")
    public void union(FlowMap<K,V> other, FlowMap<K,V> dest){
        checkMergeInput( other, dest );
        if( other == dest && other == this )
            return;
        else if( other == this )
            copy(dest);
        else if( merger != null )
            union( merger, other, dest );
        else{
            Merger<V> m = makeNaturalMerger();
            union( m, other, dest );
        }
    }
    /**
     * Unions {@code other} into {@code this}. Other cannot be
     * null. The union will contain all the keys contained in either
     * {@code other} or {@code this}. If the key was only in one, then
     * the resulting value is the value from the map that had the
     * key. If they both contained the key, the result is the merge of
     * the two values. The given {@link Merger} is used to perform the
     * merge operation. 
     */
    @SuppressWarnings("unchecked")
    public void union(Merger<V> m, FlowMap<K,V> other){
        checkMergeInput( m, other );
        if( this == other )
            return;
        for( K key : other.keySet() )
            mergePut( m, key, other.get(key) );
    }
    /**
     * Unions {@code other} and {@code this} into {@code dest}. {@code
     * other} and {@code dest} cannot be null. {@code dest} will be
     * cleared during this operation. The union will contain all the
     * keys contained in either {@code other} or {@code this}. If the
     * key was only in one, then the resulting value is the value from
     * the map that had the key. If they both contained the key, the
     * result is the merge of the two values. The given {@link Merger}
     * is used to perform the merge operation.
     */
    @SuppressWarnings("unchecked")
    public void union(Merger<V> m, FlowMap<K,V> other, FlowMap<K,V> dest){
        checkMergeInput( m, other, dest );
        if( this == other && this == dest )
            return;
        if( this == dest )
            union( m, other );
        else if( other == dest )
            for( K key : keySet() )
                dest.mergePut(m, key, get(key));
        else if( other == this ){
            dest.clear();
            copy(dest);
        }
        else{
            dest.clear();
            copy(dest);
            for( K key : keySet() )
                dest.mergePut(m, key, get(key) );
        }
    }

    /**
     * Unions {@code other} into {@code this}. Other cannot be
     * null. The union will contain all the keys contained in either
     * {@code other} or {@code this}. If the key was only in one, then
     * the resulting value is the value from the map that had the
     * key. If they both contained the key, the result is the merge of
     * the two values. The merge operation is either from an available
     * Merger, or, if the values implement the {@code Mergable} interface,
     * their merge operation is used. If there is no merger, and the
     * data does not implement {@code Mergable}, a {@code
     * ClassCastException} is thrown.
     *
     * @throws ClassCastException If merging cannot be done
     */
    @SuppressWarnings("unchecked")
    public void intersection(FlowMap<K,V> other){
        checkMergeInput( other );
        if( other == this )
            return;
        else if( merger != null )
            intersection( merger, other );
        else{
            Merger<V> m = makeNaturalMerger();
            intersection( m, other );
        }
    }
    @SuppressWarnings("unchecked")
    public void intersection(FlowMap<K,V> other, FlowMap<K,V> dest){
        checkMergeInput( other, dest );
        if( other == dest && other == this )
            return;
        else if( other == this )
            copy(dest);
        else if( merger != null )
            intersection( merger, other, dest );
        else{
            Merger<V> m = makeNaturalMerger();
            intersection( m, other, dest );
        }
    }
    /**
     * Intersect {@code m1} and {@code m2}, putting the result into
     * {@code m2}. The Merger {@code m} is used for merging. This
     * assumes that all inputs are not null.
     */
    private void intersect( Merger<V> m, FlowMap<K,V> m1, FlowMap<K,V> m2 )
    {
        if( m1 == m2 )
            return;
        else{
            List<K> keysToRemove = Lists.newLinkedList();
            List<K> keysToMerge = Lists.newLinkedList();
            for( K key : m2.keySet() )
                if( !m1.containsKey( key ) )
                    keysToRemove.add( key );
                else
                    keysToMerge.add(key);
            m2.removeKeys( keysToRemove );
            for( K key : keysToMerge )
                m2.mergePut( m, key, m1.get(key) );
        }
    }

    @SuppressWarnings("unchecked")
    public void intersection(Merger<V> m, FlowMap<K,V> other){
        checkMergeInput( m, other );
        if( this == other )
            return;
        else{
            intersect(m, other, this);
        }
    }

    @SuppressWarnings("unchecked")
    public void intersection(Merger<V> m, FlowMap<K,V> other, FlowMap<K,V> dest){
        checkMergeInput( m, other, dest );
        if( other == dest && other == this )
            return;
        else if( other == dest ){
            intersect(m, this, dest);
        }
        else if( this == dest )
            intersection( m, other );
        else if( this == other ){
            dest.clear();
            copy(dest);
        }
        else{
            dest.clear();
            for( K key : keySet() )
                if( other.containsKey( key ) )
                    dest.put( key, m.merge( get(key), other.get(key) ) );
        }
    }

}
