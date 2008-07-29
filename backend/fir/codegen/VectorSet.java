package fir.codegen;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

/* this is a special vector in which every element (including null) can only be added once.
 * adding an existing element does nothing.
 * changing an element at index i to one that is already contained will result in only the
 * removal of the element at i.
 * */

public class VectorSet<T> extends Vector<T> implements Set<T> {
	public static final long serialVersionUID = 0x704a44a; //there was some warning about that, dont know what they want
	public boolean add(T e){
		if (this.contains(e)) return false;
		else return super.add(e);		
	}
	public void add(int index,T element){
		if (!this.contains(element)){
			super.add(index,element);
		}
	}
	public boolean addAll(Collection<? extends T> c){
		boolean changed = false;
		for(T e:c){
			changed |= add(e);
		}
		return changed;
	}
	public boolean addAll(int index, Collection<? extends T> c){
		LinkedList<T> scrap = new LinkedList<T>();
		for (T e:c){ //put all unique elements in a list
			if (!this.contains(e) && !scrap.contains(e)){
				scrap.add(e);				
			}
		}
		return super.addAll(scrap);		
	}
	public void addElement(T obj){add(obj);}
	public void insertElementAt(T o,int index){add(index, o);}
	public  T set(int index,T element){
		T oldElement = get(index);
		if (contains(element)){
			remove(index); //if the new element is already contained in the set, just remove the old one
		} else {
			set(index,element);
		}
		return oldElement;
	}
	public void setElementAt(T obj,int index){set(index,obj);}
}

