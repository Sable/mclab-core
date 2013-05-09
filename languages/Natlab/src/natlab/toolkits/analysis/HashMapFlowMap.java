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

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
/**
 * A {@link FlowMap} implementation based on a {@link
 * HashMap}. Operations are created to take advantage of the
 * underlying map.
 *
 * @author Jesse Doherty
 */
@Deprecated
public class HashMapFlowMap<K,V> extends AbstractFlowMap<K,V>
{
    Map<K,V> map = Maps.newHashMap();

    public HashMapFlowMap( Merger<V> m){
        super(m);
    }
    public HashMapFlowMap(){
        super();
    }

    private HashMapFlowMap( HashMapFlowMap<K,V> hmfm ){
        super();
        setMap(Maps.newHashMap(hmfm.getMap()) );
        setMerger( hmfm.getMerger() );
    }
      
    /**
     * Getter for the underlying map. This is private because it
     * should only be used internally.
     */
    private Map<K,V> getMap()
    {
        return map;
    }
    /**
     * Setter for the underlying map. This is private because it
     * should only be used internally.
     */
    private void setMap(Map<K,V> map)
    {
        this.map = map;
    }
    
    @Override
    public HashMapFlowMap<K,V> copy()
    {
        return new HashMapFlowMap<K,V>(this);
    }

    @Override
    public void copy(FlowMap<K,V> dest)
    {
        if( dest instanceof HashMapFlowMap ){
            HashMapFlowMap<K,V> hmDest = (HashMapFlowMap<K,V>)dest;
            hmDest.setMap(Maps.newHashMap(getMap()) );
            hmDest.setMerger(getMerger());
        }
        else
            super.copy(dest);
    }
    @Override
    public void clear()
    {
        map.clear();
    }
    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }
    
    
    @Override
    public int size()
    {
        return map.size();
    }
    @Override
    public V put(K key, V value )
    {
        return map.put(key,value);
    }
    @Override
    public V get( Object key )
    {
        return map.get( key );
    }
    @Override
    public V remove( Object key )
    {
        return map.remove(key);
    }
    @Override
    public boolean containsKey( Object key )
    {
        return map.containsKey(key);
    }
    
    /**
     * Returns a set view of the keys in the map.
     */
    @Override
    public Set<K> keySet()
    {
        return map.keySet();
    }
    
    @Override
    public HashMapFlowMap<K,V> emptyMap()
    {
        return new HashMapFlowMap<K, V>();
    }

    @Override
    public boolean equals(Object o){
    	if (o!=null && (o instanceof HashMapFlowMap))
    		return ((HashMapFlowMap<?,?>)o).map.equals(map);
    	return false;
    }
    
    
    public Map<K, V> toMap(){
    	return Maps.newHashMap(map);
    }
    
    @Override
    public String toString(){
    	return map.toString();
    }
}
