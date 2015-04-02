package natlab.options;
import com.beust.jcommander.Parameter;

public class Options {
	
	@Parameter(names={"--help", "-h"},description="Display help and exit",help=true)
	protected boolean help;		

	@Parameter(names={"-p", "--pretty"}, description="Prettyprint the files")
	protected boolean pretty;	

	@Parameter(names={"--simplify", "-s"}, description="Simplify the AST after parsing")
	protected boolean simplify;	
	
	@Parameter(names={"--xml", "-x"},description="Prints the XML IR")
	protected boolean xml;
	
	@Parameter(names={"--json","j"}, description="Prints the JSON IR")
	protected boolean json;

	@Parameter(names={"--matlab", "-m"}, description="No-op, allowed for backwards compatibility")
	protected boolean matlab;

	@Parameter(names={"--quiet", "-q"}, description="Suppress all information messages")	
	protected boolean quiet;

	@Parameter(names={"--natlab","-n"}, description="Use Natlab input"}
	protected boolean natlab;
}
