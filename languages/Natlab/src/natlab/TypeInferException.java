package natlab;

public class TypeInferException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Object node;
	public TypeInferException() {
		// TODO Auto-generated constructor stub
	}

	public TypeInferException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public TypeInferException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public TypeInferException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	// Passing some object back, such as node pointer, 
	public TypeInferException(String arg0, Object node) {
		super(arg0);
		this.node = node; 
	}
}
