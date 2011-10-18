/*
Copyright 2011 Soroush Radpour and McGill University.

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

package natlab.toolkits.path;

import java.util.*;

import natlab.toolkits.filehandling.genericFile.*;

/* For a folder finds all the files that are accessible */
public class FolderHandler{
    private final Set<String> ACCEPTED_EXTENSIONS=new TreeSet<String>(Arrays.asList(new String[]{".m"}));
//    private static HashMap<GenericFile, FolderHandler> cache=new HashMap<GenericFile, FolderHandler>(); //TODO only gets big over time. Garbage collect!
    public Map<String, GenericFile> classes=new HashMap<String, GenericFile>();
    public Map<String, GenericFile> packages=new HashMap<String, GenericFile>();
    public Map<String, GenericFile> functions=new HashMap<String, GenericFile>();
    public Map<String, Map<String, GenericFile>> specializedFunction=new HashMap<String, Map<String, GenericFile>>();
    public Map<String, GenericFile> privateFunctions=new HashMap<String, GenericFile>();
    
    private Collection<GenericFile> getFilteredChildren(GenericFile dir){
	List<GenericFile> res = new LinkedList<GenericFile>();
	for (GenericFile f: dir.listChildren()){
	    if (f.isDir()) 
		continue;
	    if (ACCEPTED_EXTENSIONS.contains(f.getExtensionComplete())){
		res.add(f);
	    }	
	}
	return res;
    }
    
    public static String getFileName(GenericFile f){
	String extension = f.getExtensionComplete();
	String name=f.getName().substring(0, f.getName().length()-extension.length());
	return name;
    }
    
    private void addpath(GenericFile path){
	for (GenericFile f: path.listChildren()){
	    String fname = f.getName();	
	    if (f.isDir()){
		if (fname.charAt(0)=='@'){
		    fname=fname.substring(1);
		    for (GenericFile subfile:getFilteredChildren(f)){
			if (getFileName(subfile).equals(fname)){
			    classes.put(getFileName(subfile), subfile);
			}
			else{
			    if (!specializedFunction.containsKey(subfile))
				specializedFunction.put(getFileName(subfile), new HashMap<String, GenericFile>());
			    specializedFunction.get(getFileName(subfile)).put(fname, subfile);
			}
		    }
		}
		if (fname.charAt(0)=='+'){
		    fname=fname.substring(1);
		    packages.put(fname, f);
		}
		if (fname.toLowerCase().equals("private")){
		    for (GenericFile subfile:getFilteredChildren(f)){
			privateFunctions.put(getFileName(subfile), subfile);
		    }
		}
	    }
	    if (ACCEPTED_EXTENSIONS.contains(f.getExtensionComplete()))
		functions.put(getFileName(f), f);
	}
    }
    public static FolderHandler getFolderHandler(GenericFile folder) {
//	if (cache.containsKey(folder))
//	    return cache.get(folder);
	FolderHandler res = new FolderHandler();
	res.addpath(folder);
//	cache.put(folder,res);
	return res;
    }
    
    public GenericFile lookupPrivateFunctions(String name){
	if (privateFunctions.containsKey(name))
	    return privateFunctions.get(name);
	return null;
    }
    

    public GenericFile lookupPackage(String name){
	if (packages.containsKey(name))
	    return packages.get(name);
	return null;
    }
    
    public GenericFile lookupClasses(String name){
	if (classes.containsKey(name))
	    return classes.get(name);
	return null;
    }
    
    public GenericFile lookupSpecialized(String name, String type){
	if (specializedFunction.containsKey(name)){
	    if (specializedFunction.get(name).containsKey(type))
		return specializedFunction.get(name).get(type);
	}
	return null;
    }

    public Map<String, GenericFile> lookupSpecializedAll(String name){
	if (specializedFunction.containsKey(name)){
	    return new HashMap<String, GenericFile>(specializedFunction.get(name));
	}
	return new HashMap<String, GenericFile>();
    }

    public GenericFile lookupFunctions(String name){
	if (functions.containsKey(name)){
	    return functions.get(name);
	}
	return null;
    }
    public static void main(String[] args){
	FolderHandler self = FolderHandler.getFolderHandler(new FileFile("/home/soroush/Examples/PathEx"));	
	System.out.println(""+self.classes+ self.packages+ self.functions+self.specializedFunction);
    }
}
