package mclab;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.om.NodeInfo;

import org.xml.sax.InputSource;


import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class Finder extends HttpServlet {
	private static final Logger log = Logger.getLogger("Finder");
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		XPathRunner cache = new XPathRunner(Filters.getInstance("refactoring").queries);
		String term = req.getParameter("term");
		Objectify ofy = new DAO().ofy();
		Bench b = ofy.get(new Key<Bench>(Bench.class, req.getParameter("project")));
		SAXSource ss = new SAXSource(new InputSource(new StringReader(ProjectsTable.decompress(b.xmlBlob.getBytes()))));
		NodeInfo doc = cache.setSource(ss);
		List<NodeInfo> n = cache.evalCustom("/CompilationUnits/Script[@name=\""+term+"\"]/@name | /CompilationUnits/FunctionList[Function/@name=\""+term+"\"] |//Name[@nameId=\""+ term +"\"]", doc);
		DetailedProject.renderTable(res, "Occurrencens of "+term, cache, b, n);
	}
}
