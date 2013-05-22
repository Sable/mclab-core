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
 * Used for flow data that is best represented as a map. 
 *
 * @author Jesse Doherty
 */
@Deprecated
public interface FlowMap<K,V> extends FlowData
{

    /**
     * Returns a copy of {@code this} flow map.
     */
    public FlowMap<K,V> copy();

    /**
     * Copies the data in {@code this} into {@code dest}. This should
     * cause all previous data in {@code dest} to be lost, but no
     * behaviour is guaranteed. 
     */
    public void copy(FlowMap<K,V> dest);
    /**
     * Removes all data in this flow map. This means that if you call
     * {@code clear}, then an immediate call to {@code isEmpty()} will
     * return {@code true}.
     */
    public void clear();
    /**
     * Checks if the flow map is empty. If {@code isEmpty()} returns
     * {@code true}, then {@code size} should return 0.
     */
    public boolean isEmpty();
    /**
     * Returns the number of elements in the map. This is usually the
     * number of keys in the map, but this behaviour is not
     * guaranteed. If {@code size()} returns 0, then {@code isEmpty()}
     * should also return {@code true}.
     */
    public int size();
    /**
     * Associates the given key with the given value. No merging
     * necessarily occurs.
     *
     * @return The previous value mapped to {@code key}, {@code null}
     * if no mapping existed
     */
    public V put(K key, V value );
    /**
     * Associates data with the given key. If the given key has no
     * data associated with it, then the given value is used. If there
     * is data, then a merge of the existing data and the given value
     * is used. The way the merge is performed is dependent on the
     * actual implementation. 
     */
    public V mergePut(K key, V value);
    /**
     * Associates data with the given key. If the given key has no
     * data associates with it, then the given value is used. If there
     * is data, then a merge of the existing data and the given value
     * is used. The merge is performed by the {@link Merger} instance
     * provided to the method. If there is no {@code Merger}
     * available, then, if the data implements {@link Mergable}, the
     * data's {@code merge(...)} operation is used.
     *
     * @param m      The merger used to merge the value data
     * @param key    The key to associate data with
     * @param value  The new data value to be used, can be merged with
     * existing data
     * @return The previous value associated with the given {@code
     * key}, or {@code null} if there was no mapping for that {@code
     * key}
     *
     * @throws ClassCastException  If there is no {@code Merger}
     * available and the data does not implement {@code Mergable<V>}
     */
    public V mergePut(Merger<V> m, K key, V value);
    /**
     * Retrieves the data associated with the given key.
     *
     * @param key  The key used to find data
     * @return The data associated with the given {@code key}, or null
     * if no data is associated
     * @throws ClassCastException If the key is an inappropriate type
     * for this map (optional)
     */
    public V get( Object key );
    /**
     * Removes the key value pair associated with the given key.
     *
     * @return The value that was associated with the key, if no value
     * was associated, then {@code null} is returned 
     * @throws ClassCastException  If the key is an inappropriate type
     * for this map (optional)
     */
    public V remove( Object key );
    /**
     * Removes all key value pairs associated with the keys in the
     * given collection.
     *
     * @param keys  Collection of keys to be removed
     * @return  Whether or not the map has changed
     */
    public boolean removeKeys( Collection<?> keys );
    /**
     * Return {@code true} if this map contains data associated with
     * the given {@code key}.
     *
     * @param key  The key whose presence in this map is to be tested
     * @return {@code true} if the map contains the key
     * @throws ClassCastException If the key is an inappropriate type
     * for this map (optional)
     */
    public boolean containsKey( Object key );
    /**
     * Returns a {@link Set} containing the keys contained in this
     * map. This may or may not be a view of the contained data. This
     * means that if the set is modified, the map may also be modified
     * and vice versa, or not. 
     */
    public Set<K> keySet();
    /**
     * Returns an empty map. 
     */
    public FlowMap<K,V> emptyMap();
    /**
     * Sets the available merger to be used when merging data.
     */
    public void setMerger(Merger<V> m);
    /**
     * Gets the merger used by the map, {@code null} if none
     * exists. This does not consider the merge operation of data
     * implementing the {@link Mergable} interface.
     */
    public Merger<V> getMerger();

}
