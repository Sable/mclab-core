package mclab;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.transform.sax.SAXSource;

import mclab.XPathRunner.pair;
import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.om.NodeInfo;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.InputSource;


import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

public class DetailedProject extends HttpServlet{
	private static final Logger log = Logger.getLogger(UploadHandler.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		int i=0;
		String filter = " query: " + StringEscapeUtils.escapeHtml(req.getParameter("filter"));
		XPathRunner cache = new XPathRunner(java.util.Collections.singletonMap(filter, req.getParameter("filter")));
		if (Filters.getInstance("refactoring").queries.containsValue(req.getParameter("filter"))){
			cache = new XPathRunner(Filters.getInstance("refactoring").queries);
			for (Map.Entry<String, String> e: Filters.getInstance("refactoring").queries.entrySet()){
				if (e.getValue().equals(req.getParameter("filter")))
					filter = e.getKey();
			}
		}
		
		System.setProperty("javax.xml.xpath.XPathFactory:"
			+ NamespaceConstant.OBJECT_MODEL_SAXON,
			"net.sf.saxon.xpath.XPathFactoryImpl");
		
		Objectify ofy = new DAO().ofy();
		Bench b = ofy.get(new Key<Bench>(Bench.class, req.getParameter("project")));

		SAXSource ss = new SAXSource(new InputSource(new StringReader(ProjectsTable.decompress(b.xmlBlob.getBytes()))));
		NodeInfo doc = cache.setSource(ss);
		List<NodeInfo> result= cache.eval(filter, doc);
		renderTable(res, filter, cache, b, result);
	}

	static void renderTable(HttpServletResponse res, String filter, XPathRunner cache,
			Bench b, List<NodeInfo> result) throws IOException {
		
		Map<String, Set<String>> locs = new TreeMap<String, Set<String>>(); 
		for (NodeInfo q: result ){
			pair<String, String> location = cache.location((NodeInfo) q);
			if (!locs.containsKey(location.key))
				locs.put(location.key, new TreeSet<String>());
			locs.get(location.key).add(location.value);
		}
		res.getWriter().printf("<html>"+ProjectsTable.header+"<body>"+ProjectsTable.logosmall+ProjectsTable.queryform+
				"<h3>%s %s</h3>", "",b.name, filter);
		res.getWriter().printf(" <a href='http://py.mclab-bench.appspot.com/?project=%s'>View XML</a> ", b.name);
		res.getWriter().print("<table class=\"table2\" border='1'>");
		for (Entry<String, Set<String>> ent:locs.entrySet()){
			boolean first=true;
			String k="";
			boolean col=false;
			for (String line: ent.getValue()){
				if (col)
					k=k+' ';
				k=k+line;
				col=true;
			}
			
			res.getWriter().printf("<tr><td><a href='http://py.mclab-bench.appspot.com/?project=%s&lines=%s&file=%s#line-%s'>%s</a></td><td>",b.name,k,ent.getKey(), ent.getValue().iterator().next(), ent.getKey() );
			for (String line: ent.getValue()){
				if (!first) {
					res.getWriter().print(", ");
				}
				res.getWriter().printf("<a href='http://py.mclab-bench.appspot.com/?project=%s&lines=%s&file=%s#line-%s'>%s</a> ",b.name,line,ent.getKey(),  line,line);
				first=false;
			}res.getWriter().print("</td></tr>");
		}
		res.getWriter().printf("</table></body></html>");
	}
}
