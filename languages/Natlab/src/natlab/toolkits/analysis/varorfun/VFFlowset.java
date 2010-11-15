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
public class VFFlowset<V, D extends VFDatum> extends AbstractFlowSet<ValueDatumPair<V,D>>
{

    public static boolean DEBUG = false;
    public void copy(VFFlowset<V, D> dest) {
    	if (this == dest) return;
        dest.clear();
        for (ValueDatumPair<V,D> element : toList()){
            dest.add(element.clone());
        }
    }
    protected HashMap<V, D> set;
    
    public VFFlowset()
    {
        set = new HashMap();
    }

    public VFFlowset( HashMap<V, D> set )
    {
        this.set = set;

    }
    
    public VFFlowset<V,D> clone()
    {
        HashMap<V, D> newSet = new HashMap();

        for( Map.Entry<V, D> e : set.entrySet() )
            newSet.put( e.getKey(), (D)(e.getValue().clone()) );

        return new VFFlowset( newSet );
    }
    
    public VFFlowset<V, D> emptySet()
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
    public void add(ValueDatumPair<V,D> pair)
    {
        if( DEBUG )
            System.out.println("adding " + pair);
        D d = set.get( pair.getValue() );
        if( d != null ){
            //TODO-JD Is this safe?
            d = (D)(d.merge( pair.getDatum() ));
            if( DEBUG )
                System.out.println("merged datum is " + d);
            set.put( pair.getValue(), d );
        }
        else{
            set.put( pair.getValue(), pair.getDatum() );
        }
    }

    public boolean remove( ValueDatumPair<V,D> pair )
    {
        return set.remove( pair.getValue() ) != null;
    }
    public boolean contains( ValueDatumPair<V,D> pair)
    {
        return set.containsKey( pair.getValue() );
    }
    public D contains( V value )
    {
        return set.get(value);
    }
    public List< ValueDatumPair<V,D> > toList()
    {
        List< ValueDatumPair<V,D> > list = new ArrayList( set.size() );
        for( Map.Entry<V,D> entry : set.entrySet() ){
            list.add( new ValueDatumPair( entry.getKey(), entry.getValue() ) );
        }
        return list;
    }

    public void union(VFFlowset<V,D> other, 
                      VFFlowset<V,D> dest)
    {
        if( dest == this && dest == other )
            return;
        if( this == other ){
            copy(dest);
            return;
        }

        VFFlowset<V,D> tmpDest = new VFFlowset();

        //add all the elements in this to the tmpDest
        for( ValueDatumPair<V,D> pair : this.toList() ){
            if( !other.contains( pair ) && pair.getDatum().isExactlyAssignedVariable() ){
                D newDatum = (D)(pair.getDatum().clone());
                newDatum.makeVariable();
                tmpDest.add( new ValueDatumPair( pair.getValue(), newDatum ) );
            }
            else
                tmpDest.add( pair );
        }
        //add all elements in other to tmpDest
        for( ValueDatumPair<V,D> pair : other.toList() ){
            if( !contains( pair ) && pair.getDatum().isExactlyAssignedVariable() ){
                D newDatum = (D)(pair.getDatum().clone());
                newDatum.makeVariable();
                tmpDest.add( new ValueDatumPair( pair.getValue(), newDatum ) );
            }
            else
                tmpDest.add( pair );
        }
        //copy tmpDest to dest
        tmpDest.copy(dest);
    }
    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append("{");
        boolean first = true;
        for( Map.Entry<V,D> entry : set.entrySet() ){
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
    public HashMap<V,D> getMap(){return set;}

}