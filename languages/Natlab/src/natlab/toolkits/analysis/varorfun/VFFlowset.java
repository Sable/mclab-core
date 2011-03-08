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
            dest.add(element.clone());
        }
    }
    protected HashMap<String, VFDatum> set;
    
    public VFFlowset()
    {
        set = new HashMap();
    }

    public VFFlowset( HashMap<String, VFDatum> set )
    {
        this.set = set;

    }
    
    public VFFlowset clone()
    {
        HashMap<String, VFDatum> newSet = new HashMap();

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

    public boolean remove( ValueDatumPair<String, VFDatum> pair )
    {
        return set.remove( pair.getValue() ) != null;
    }
    public boolean contains( ValueDatumPair<String, VFDatum> pair)
    {
        return set.containsKey( pair.getValue() );
    }
    public VFDatum contains( String value )
    {
        return set.get(value);
    }
    public List< ValueDatumPair<String, VFDatum> > toList()
    {
        List< ValueDatumPair<String, VFDatum> > list = new ArrayList( set.size() );
        for( Map.Entry<String ,VFDatum> entry : set.entrySet() ){
            list.add( new ValueDatumPair( entry.getKey(), entry.getValue() ) );
        }
        return list;
    }

    public void union(VFFlowset other, 
                      VFFlowset dest)
    {
        if( dest == this && dest == other )
            return;
        if( this == other ){
            copy(dest);
            return;
        }

        VFFlowset tmpDest = new VFFlowset();

        //add all the elements in this to the tmpDest
        for( ValueDatumPair<String,VFDatum> pair : this.toList() ){
                tmpDest.add( pair );
        }
        //add all elements in other to tmpDest
        for( ValueDatumPair<String,VFDatum> pair : other.toList() ){
                tmpDest.add( pair );
        }
        tmpDest.copy(dest);
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

}