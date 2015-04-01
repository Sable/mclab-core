package natlab.tame.classes;

import java.util.HashMap;

import natlab.tame.classes.reference.BuiltinClassReference;
import natlab.tame.classes.reference.ClassReference;
import natlab.toolkits.path.FileEnvironment;

/**
 * Like the function collection is a a colleciton of funcitons, this
 * class represents a collection of matlab classes. Classes are rerenced
 * by class references, just like functions are referenced by funciton references.
 * 
 * As new classes are encountered, they should be loaded using this repository,
 * just like new functions are loaded into function collections.
 * 
 * TODO - so far this only implements the builtin classes. So far there is onlay a very
 * minimal api to find overloaded functions, and to find the relationship between
 * classes (superior/inferior).
 * 
 * Eventually this should evolve to support user-defined classes.
 */

public class ClassRepository {
	private FileEnvironment fileEnvironment;
	private HashMap<ClassReference,MatlabClass> classes = new HashMap<ClassReference, MatlabClass>();
	
	public ClassRepository(FileEnvironment fileEnvironment){
		this.fileEnvironment = fileEnvironment;
	}
	
	public MatlabClass getClass(ClassReference classRef){
		if (!classes.containsKey(classRef)){
			load(classRef);
		}
		return classes.get(classRef);
	}
	
	private void load(ClassReference classRef){
		if (classRef instanceof BuiltinClassReference){
			classes.put(classRef, 
					new BuiltinMatlabClass((BuiltinClassReference)classRef, this.fileEnvironment));
		} else {
			throw new UnsupportedOperationException("no support for user-defined classes");
		}
	}
}


