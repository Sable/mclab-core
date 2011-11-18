package mclab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.NodeListIterator;
import net.sf.saxon.xpath.XPathEvaluator;

class XPathRunner {
	private Map<String, XPathExpression> queryCache;
	XPath xpe;
	private Object Scripts;

	public class pair<T, E> {
		final T key;
		final E value;

		public pair(T k, E v) {
			key = k;
			value = v;
		}
	}

	public XPathRunner(Map<String, String> queries) {
		try {
			queryCache = new HashMap<String, XPathExpression>();
			XPathFactory xpf = XPathFactory
					.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
			xpe = xpf.newXPath();
			xpe.setNamespaceContext(new MyNamespaceContext());
			xpe.setXPathFunctionResolver(new XPathFunctionResolver() {

				@Override
				public XPathFunction resolveFunction(QName functionName,
						int arity) {
					if (functionName == null)
						return null;
					if (functionName.equals(new QName("http://ext.com", "test", "ext")))
						return new XPathFunction() {

							@Override
							public Object evaluate(List args)
									throws XPathFunctionException {
								if (args==null)
									return null;
								Object o=args.get(0);
								if (!(args.get(0) instanceof ArrayList)){
									ArrayList tmp=new ArrayList<NodeInfo>();
									tmp.add(o);
									o=tmp;
								}
								ArrayList<NodeInfo> arg = (ArrayList<NodeInfo>) o;
								ArrayList<String> res = new ArrayList<String>();
								for (NodeInfo n: arg){
									String[] s = n.getStringValue().split("/");
									String target =s[s.length-1];
									target=target.substring(0,target.length()-2);
									res.add(target);
								}
								return res;
								// TODO Auto-generated method stub
							}
						};

					return null;
				}
			});
			xpe.setXPathVariableResolver(new XPathVariableResolver() {

				@Override
				public Object resolveVariable(QName variableName) {
					if (new QName("scripts").equals(variableName)) {
						return Scripts;
					}
					return null;
				}
			});
			for (Entry<String, String> y : queries.entrySet()) {
				queryCache.put(y.getKey(), xpe.compile(y.getValue()));
			}
			for (Entry<String, String> y : Filters.getInstance("refactoring").queries.entrySet()) {
				queryCache.put(y.getKey(), xpe.compile(y.getValue()));
			}
			queryCache.put("selfFile",
					xpe.compile("@fullpath"));
			queryCache.put("parentFile",
							xpe.compile("ancestor::FunctionList/@fullpath|ancestor::Script/@fullpath"));
			queryCache.put("parentLine", xpe
					.compile("ancestor::*[@line]/@line"));
			queryCache.put("selfLine", xpe.compile("@line"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<NodeInfo> eval(String name, NodeInfo doc) {
		try {
			return (List<NodeInfo>) queryCache.get(name).evaluate(doc,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	public List<NodeInfo> evalCustom(String query, NodeInfo doc) {
		try {
			XPathExpression x = xpe.compile(query);
			return (List<NodeInfo>) x.evaluate(doc,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public pair<String, String> location(NodeInfo doc) {
		List<NodeInfo> s = eval("selfLine", doc);
		if (s.size() < 1)
			s = eval("parentLine", doc);
		List<NodeInfo> s2 = eval("selfFile", doc);
		if (s2.size() < 1)
			s2 = eval("parentFile", doc);
		int max_line=0;
		for (int i =0;i<s.size();i++){
			int tmp_line= Integer.parseInt(s.get(i).getStringValue());
			if (tmp_line>max_line)max_line=tmp_line;
		}
		String file = (s2.get(s2.size()-1)).getStringValue();
		return new pair<String, String>(file, String.valueOf(max_line));
	}

	public NodeInfo setSource(SAXSource ss) {
		try {
			NodeInfo res = ((XPathEvaluator) xpe).setSource(ss);
			Scripts = eval("Script files", res);
			return res;
		} catch (XPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
