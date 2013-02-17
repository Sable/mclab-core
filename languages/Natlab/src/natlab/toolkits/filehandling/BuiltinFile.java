package natlab.toolkits.filehandling;
import java.io.*;
import java.util.*;


public class BuiltinFile extends GenericFile{
	private final String name;
	private final String path;
	private Collection<BuiltinFile> directoryList;

	private static Map<String, BuiltinFile> fileCache = new HashMap<String, BuiltinFile>();
	
	public BuiltinFile(String[] fileList){
		BuiltinFile root = this;
		this.name = "";
		this.path = "";
		directoryList = new HashSet<BuiltinFile>();
		fileCache.put("", this);
		for (String s: fileList){
			String parent = "";
			String path = "";
			BuiltinFile folder = root;
			StringTokenizer st = new StringTokenizer(s, File.separator);
			String token;
			if (st.hasMoreTokens())
				token = st.nextToken();
			else 
				continue;
			if (token.equals(".")){
				if (st.hasMoreTokens())
					token = st.nextToken();
				else 
					continue;
			}
			while (st.hasMoreTokens()){
				parent = path;
				path += token + File.separator;
				if (fileCache.containsKey(path))
					folder = fileCache.get(path);
				else{
					BuiltinFile f = new BuiltinFile(token, parent);
					folder.addFile(f);
					folder = f;
					fileCache.put(path, folder);
				}
				token = st.nextToken();
			}
			BuiltinFile f = new BuiltinFile(token, path);
			fileCache.put(path+File.separator+token, f);
			folder.addFile(f);
		}
		
	}
	
	private void addFile(BuiltinFile f){
		directoryList.add(f);
	}
	
	private  BuiltinFile(String name, String path){
		this.name = name;
		this.path = path;
		this.directoryList = new HashSet<BuiltinFile>();
	}
		
	@Override
	public String getName() {
		return name;
	}
	
	public GenericFile getBuiltin(String name){
		if (fileCache.containsKey(name)){
			return fileCache.get(name);
		}
		throw new RuntimeException(new FileNotFoundException());
	}
	
	@Override
	public GenericFile getParent() {
		return getBuiltin(path);
	}

	@Override
	public String getPath() {
		return path+File.separator+name;
	}
	
	@Override
	public Reader getReader() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDir() {
		return directoryList.isEmpty();
	}

	@Override
	public Collection<? extends GenericFile > listChildren() {
		return new ArrayList(directoryList);
	}
	
	@Override
	public int hashCode(){
		return ("builtin://"+path+name).hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof BuiltinFile){
			BuiltinFile O=(BuiltinFile)o;
			if (O.path.equals(path) && name.equals(O.name))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean exists() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public GenericFile getChild(String name) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public long lastModifiedDate() {
		throw new UnsupportedOperationException();
	}
	
	public String dump(){
		return directoryList.toString()+"\n _----------------_____--_________-----------------------__________"+fileCache;
	}
}
