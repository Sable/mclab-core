// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
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

package natlab.toolkits;

/**
 * This represenst a bi-directional map using HashMaps.
 * This map does not represent a bijection, since a value can occur many times.
 * Thus the inverse maps from a value to a set of keys. 
 * This map should only be modified via put, putAll and remove.
 */

import java.util.*;

public class BiMap<K, V> extends HashMap<K,V> {
    HashMap<V,HashSet<K>> inverse = new HashMap<V,HashSet<K>>();
    
    
    public BiMap(){
        super();
    }
    
    public BiMap(Map<K,V> map){
        putAll(map);
    }
    
    
    public void clear() {
        inverse.clear();
        clear();
    }

    /**
     * returns a copy of the keys hashset
     */
    public HashSet<K> getKeys(V v){
        return new HashSet<K>(inverse.get(v));
    }
        
    public V put(K k, V v) {
        //if the value is not already there, create a set
        HashSet<K> keys;
        if (!inverse.containsKey(v)){
            keys = new HashSet<K>();
            inverse.put(v,keys);
        } else {
            keys = inverse.get(v);
        }
        //add key to set
        keys.add(k);
        
        return super.put(k,v);
    }

    public void putAll(Map<? extends K, ? extends V> arg0) {
        for (K k : arg0.keySet()){
            put(k,arg0.get(k));
        }
    }

    public V remove(Object k) {
        if (this.containsKey(k)){
            //remove object from set
            HashSet<K> keys = inverse.get(get(k));
            keys.remove(k);
            //remove from inverse mapping if set is empty
            if (keys.isEmpty()){
                inverse.remove(get(k));
            }
        }        
        return super.remove(k);
    }
}
