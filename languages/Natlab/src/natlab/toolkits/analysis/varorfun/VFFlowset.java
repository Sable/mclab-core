/*
Copyright Jesse Doherty, Soroush Radpour and McGill University.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

*/

package natlab.toolkits.analysis.varorfun;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import natlab.toolkits.analysis.AbstractFlowSet;

import com.google.common.collect.Lists;

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

//TODO-JD make some operations unsupported 
public class VFFlowset extends AbstractFlowSet<Map.Entry<String, VFDatum>>
{

    public static boolean DEBUG = false;
    public void copy(VFFlowset dest) {
    	if (this == dest) return;
        dest.clear();
        for (Map.Entry<String,VFDatum> element : this) {
            dest.add(element);
        }
    }
    protected HashMap<String, VFDatum> set;
    
    public VFFlowset()
    {
        set = new HashMap<String, VFDatum>();
    }

    public VFFlowset( HashMap<String, VFDatum> set )
    {
        this.set = set;

    }
    
    public VFFlowset copy()
    {
        HashMap<String, VFDatum> newSet = new HashMap<String, VFDatum>();

        for( Map.Entry<String, VFDatum> e : set.entrySet() )
            newSet.put( e.getKey(), e.getValue() );

        return new VFFlowset( newSet );
    }
    
    //    public boolean equals(Object o) {
        
    //  return ((VFFlowset)o).set.equals(this.set);
    //}

    public VFFlowset emptySet()
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
    public void add(ValueDatumPair<String, VFDatum> p){
        add(p.getValue() , p.getDatum());
    }


    public void add(Map.Entry<String, VFDatum> p){
        add(p.getKey() , p.getValue());
    }

    public void add(String name, VFDatum value)
    {
        if( DEBUG )
            System.out.println("adding " + name + "->" + value);
        VFDatum d = set.get(name );
        if( d != null ){
            d = (d.merge( value ));
            if( DEBUG )
                System.out.println("merged datum is " + d);
            set.put( name, d );
        }
        else{
            set.put( name, value );
        }
    }

    public boolean remove( Object obj )
    {
        //return set.remove( pair.getValue() ) != null;
        return set.entrySet().remove( obj );
    }
    public boolean contains( Object pair)
    {
        return set.entrySet().contains( pair );
    }
    public VFDatum contains( String value )
    {
    	return set.get(value);
    	
    }
    
    public VFDatum getKind(String n){
    	if (set.containsKey(n))
    		return set.get(n);
    	return VFDatum.UNDEF;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append("{");
        boolean first = true;
        for( Map.Entry<String,VFDatum> entry : set.entrySet() ){
            if( first ){
                s.append("\n");
                first = false;
            }
            else
                s.append(", \n");
            
            s.append( entry.toString() );
            //"< " + entry.getKey().toString() + ", " entry.getValue().toString() + " >");
        }
        if( !first )
            s.append("\n");
        s.append("}");

        return s.toString();
    }

    public Iterator<Map.Entry<String,VFDatum>> iterator()
    {
        return set.entrySet().iterator();
    }

}