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

//TODO-JD make some operations unsupported 
public class VFFlowset extends AbstractFlowSet<ValueDatumPair<String, VFDatum>>
{

    public static boolean DEBUG = false;
    public void copy(VFFlowset dest) {
    	if (this == dest) return;
        dest.clear();
        for (ValueDatumPair<String,VFDatum> element : toList()){
            dest.add(element.copy());
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
    public void add(ValueDatumPair<String, VFDatum> pair)
    {
        if( DEBUG )
            System.out.println("adding " + pair);
        VFDatum d = set.get( pair.getValue() );
        if( d != null ){
            //TODO-JD Is this safe?
            d = (d.merge( pair.getDatum() ));
            if( DEBUG )
                System.out.println("merged datum is " + d);
            set.put( pair.getValue(), d );
        }
        else{
            set.put( pair.getValue(), pair.getDatum() );
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
    public List< ValueDatumPair<String, VFDatum> > toList()
    {
        List< ValueDatumPair<String, VFDatum> > list = new ArrayList<ValueDatumPair<String, VFDatum>>( set.size() );
        for( Map.Entry<String ,VFDatum> entry : set.entrySet() ){
            list.add( new ValueDatumPair<String,VFDatum>( entry.getKey(), entry.getValue() ) );
        }
        return list;
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
            
            s.append( ValueDatumPair.toString(entry.getKey(), entry.getValue() ) );
            //"< " + entry.getKey().toString() + ", " entry.getValue().toString() + " >");
        }
        if( !first )
            s.append("\n");
        s.append("}");

        return s.toString();
    }
    public HashMap<String,VFDatum> getMap(){return set;}

    public Iterator<ValueDatumPair<String,VFDatum>> iterator()
    {
        return new Iterator<ValueDatumPair<String,VFDatum>>(){
            Iterator<Map.Entry<String,VFDatum>> mapIterator = set.entrySet().iterator();
            public boolean hasNext()
            {
                return mapIterator.hasNext();
            }
            public ValueDatumPair<String,VFDatum> next()
            {
                Map.Entry<String,VFDatum> entry = mapIterator.next();
                return new ValueDatumPair(entry.getKey(), entry.getValue() );
            }
            public void remove()
            {
                mapIterator.remove();
            }
        };
    }

}