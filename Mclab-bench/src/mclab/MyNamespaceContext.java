package mclab;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class MyNamespaceContext implements NamespaceContext {
	public String getNamespaceURI(String prefix) {
		if (prefix == null)
			throw new IllegalArgumentException("The prefix cannot be null.");

		if (prefix.equals("ext"))
			return "http://ext.com";
		else
			return null;
	}

	public String getPrefix(String namespace) {
		if (namespace == null)
			throw new IllegalArgumentException(
					"The namespace uri cannot be null.");
		if (namespace.equals("http://ext.com"))
			return "ext";
		else
			return null;
	}

	public Iterator getPrefixes(String namespace) {
		return null;
	}
}
