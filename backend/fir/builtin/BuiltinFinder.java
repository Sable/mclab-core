package fir.builtin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import fir.type.*;

/* given the name of a function, and the in/out types, will
 * return a Builtin object - or none, if it doesnt exist
 * there will only be one unique builtin object returned for every Builtin
 * 
 * TODO: add the same function but with ...ignoreCase..
 */


public abstract class BuiltinFinder {
	//returns a builtin given its signature
	public Builtin getBuiltin(String name){ //returns null if ambiguous or absent
		Builtin[] builtins = getBuiltins(name);
		if (builtins.length == 1) return builtins[0];
		return null;
	}
	public Builtin getBuiltin(String name,Type[] in){ //returns the first found function with given name/types
		Builtin[] builtins = getBuiltins(name);
		for (Builtin builtin:builtins){
			if (builtin.isDefined(in)){
				return builtin;
			}
		}
		return null;
	}
	
	//returns whether a builtin with given name/signature exists
	boolean exists(String name,Type[] in){
		return (getBuiltin(name,in)!=null);		
	}
	
	
	//actual function which is used to find functions
	Pair[] list;
	public Builtin[] getBuiltins(String name){
		LinkedList<Builtin> founds = new LinkedList<Builtin>();
		for (Pair pair:list){
			if (pair.name.compareTo(name)==0){
				founds.add(getUnique(pair.aClass));
			}
		}
		if (founds.isEmpty()) return null;
		return (Builtin[])founds.toArray();
	}	
	
	//helper function - given a builtin class, returns a builtin object -- but only one for
	//each Builtin class per BuiltinFinder object
	HashMap<Class,Builtin> builtins = new HashMap<Class,Builtin>();
	Builtin getUnique(Class aClass){
		if (builtins.containsKey(aClass)){
			return builtins.get(aClass);
		}
		try {
			Builtin builtin = (Builtin)aClass.newInstance();
			builtins.put(aClass, builtin);
			return builtin;
		} catch (Exception e){ 
			System.err.println("couldn't create builtin in BuiltinFinder, got exception:\n"+e.getMessage());
			return null;
		}		
	}

	protected class Pair{
		Class aClass;
		String name;
		public Pair(String name,Class aClass){
			this.aClass = aClass;
			this.name = name;
		}
	}
}


