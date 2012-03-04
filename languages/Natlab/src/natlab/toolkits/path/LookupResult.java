package natlab.toolkits.path;

import natlab.toolkits.filehandling.genericFile.GenericFile;

public class LookupResult {
	public enum TYPE{
		CLASS,
		FUNCTION, //OR SCRIPT
		PACKAGE,
	}
	
	public final TYPE type;
	public final GenericFile file;
	public LookupResult(TYPE t, GenericFile f) {
		type=t;
		file=f;
	}
}
