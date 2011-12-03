package mclab;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.util.*;

import net.sf.jsr107cache.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.*;
import java.util.logging.Logger;
import java.util.zip.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.http.*;
import javax.xml.transform.sax.SAXSource;
import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.style.XSLNumber;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.InputSource;


import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.*;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.users.*;
import com.googlecode.objectify.*;


public class ProjectsTable extends HttpServlet{
	private static final Logger log = Logger.getLogger(UploadHandler.class.getName());
	public static final String queryform = "<form id=\"query\" action=\"/Table\"><textarea name=\"query\" >%s</textarea><input type=\"submit\" " +
			"value=\"Search\" /><input type=\"checkbox\" id=\"all\" name=\"all\"/><label for=\"quick\">Run on All projects</lable></form><br />";
	
	public static final String saveform = "<form id=\"save\" action=\"/Save\" method=\"POST\">Descrpition: <textarea name=\"desc\" ></textarea>" +
			"<input type=\"hidden\" name=\"query\" value=\"%s\"><input type=\"submit\" value=\"Save\" />";
	
	public static final String logobig = "<a href=\"/\"><img src=\"http://mclab-bench.appspot.com/static/mcbench.png\" alt=\"Mclab Bench\"/></a><br />";
	public static final String logosmall = "<a href=\"/\"><img src=\"http://mclab-bench.appspot.com/static/mcbenchs.png\" alt=\"Mclab Bench\"/></a>";
	public static final String header = "<head><link rel=\"stylesheet\" type=\"text/css\" href=\"http://mclab-bench.appspot.com/static/style.css\" /></head>";
    public void doPost(HttpServletRequest req, HttpServletResponse res){
    	try {
			doGet(req, res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
        Cache cache=null;        
        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
        } catch (CacheException e) {
            log.warning("Cache failed");
        }
        String name = req.getParameter("query");
        
        Map<String, String>  q;
        if (name == null || Filters.getInstance(name)!=null){
            name = req.getParameter("qs");
        	if (name==null)
        		name = "refactoring";
        	q = Filters.getInstance(name).queries;
        }
        else 
        	q = java.util.Collections.singletonMap("Query results", name);
        String cache_key = "3"+"tab"+q.hashCode();
        String table = "";
        byte[] cached_bytes = (byte[]) cache.get(cache_key);
        if (cached_bytes!=null ){
			res.getWriter().write(decompress(cached_bytes));
        	return;
        }
        log.severe(req.getQueryString());
        boolean all = "on".equals(req.getParameter("all"));
        boolean background =  "background".equals(req.getParameter("type"));
        UserService userService = UserServiceFactory.getUserService();
        if (!background && all){
        	if ((!userService.isUserLoggedIn())){
        		res.sendRedirect(userService.createLoginURL(req.getRequestURI()));
        		return;
        	}
        	if (!userService.isUserAdmin()){
        		res.getWriter().write("Please request for an admin status.");
        		return;
        	}
        }
        StringWriter out = new StringWriter();
		PrintWriter writer=new PrintWriter(out);
		writer.printf("<html>"+header+"<body>");
		writer.print(logosmall);
		writer.printf(queryform, name);
		if (name !=null)
			writer.printf("Results for: <strong>%s</strong>", StringEscapeUtils.escapeHtml(name));
		
		if (req.getParameter("query") != null && userService.isUserLoggedIn() && userService.isUserAdmin()){
    				writer.printf(saveform, req.getParameter("query"));
		}
		if ((!all) || (all && background)){
			writer.print("<table class=\"table2\"><thead><tr><th>Project Name</th>");
			runQueries(q, writer, all);
			writer.printf("</html></body>");
			writer.close();
			table= out.toString();
			if (all){
				cache.put(cache_key, compress(table));
	        	sendMail(req, table);
			}
			else
				res.getWriter().write(table);

		}
		else{
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(withUrl("/Table").param("query", req.getParameter("query")).param("type", "background").param("all", "on").param("email", userService.getCurrentUser().getEmail()));
	    	res.getWriter().write("Please wait. The results will be emailed to you once ready...");
		}
			
    }
	public static String decompress(byte[] cached_bytes){
		try{
			ByteArrayInputStream x = new ByteArrayInputStream(cached_bytes);
			GZIPInputStream gz =  new GZIPInputStream(x);
			ObjectInputStream object_reader = new ObjectInputStream(gz);
			return (String)object_reader.readObject();
		}
		catch(IOException e){
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}		
		
	}
	public static byte[] compress(String table) {
		try{
			ByteArrayOutputStream x = new ByteArrayOutputStream(2024*1024);
			GZIPOutputStream gz =  new GZIPOutputStream(x);
			ObjectOutputStream object_out = new ObjectOutputStream(gz); 
			object_out.writeObject(table);
			object_out.close();
			gz.close();
			x.close();
			return x.toByteArray();
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	private static String shorten(String s ){
		int t = 110;
		if (s.length()>t){
			return s.substring(0, t-3)+"...";
		}
		return s;
	}
	private void runQueries(Map<String, String> q, 
			PrintWriter writer, boolean all) {
		Objectify ofy = new DAO().ofy();
		Set<String> keyset = q.keySet();
		XPathRunner query_cache = new XPathRunner(q);
		System.setProperty("javax.xml.xpath.XPathFactory:"
			+ NamespaceConstant.OBJECT_MODEL_SAXON,
			"net.sf.saxon.xpath.XPathFactoryImpl");

		double[] total = new double[keyset.size()];
		for (String s : q.keySet()) {
			writer.printf("<th>%s</th>",s);
		}
		writer.printf("</tr></thead><tbody>");

//		QueryImpl<Bench> a;
		if(all){
			Query<Bench> iterator = ofy.query(Bench.class).filter("parsed =", true);
			QueryResultIterator<Bench> a= iterator.iterator();
			int iter=0;
			while (a.hasNext()){
				handle(a.next(), writer, query_cache, keyset, total, q);
				iter ++;
				if ((iter % 300) ==0){
					iterator = ofy.query(Bench.class).filter("parsed =", true);
					iterator.startCursor(a.getCursor());
					a = iterator.iterator();
				}
			}
		}
		else{
			Query<Bench> iterator = ofy.query(Bench.class).filter("parsed =", true);
			iterator.limit(60);
			for (Bench b: iterator.fetch()){
				handle(b, writer, query_cache, keyset, total, q);
			}
		}
//		iterator;
		writer.printf("</tbody><tfoot><tr><th>Total</th>");
	    for (int i=0;i<keyset.size();i++)
	    	writer.printf("<td>%.2f</td>", total[i]);
		writer.printf("</tr></tfoot>");
		writer.printf("</table>");
	}
	
	private void handle(Bench b, PrintWriter writer, XPathRunner query_cache, Set<String> keyset, double[] total, Map<String, String> q){
		String xmlsource = (String)decompress(b.xmlBlob.getBytes());
		SAXSource ss = new SAXSource(new InputSource(new StringReader(xmlsource)));
		NodeInfo doc = query_cache.setSource(ss);
		writer.printf("<tr><td ><a href='%s'><img src=\"http://www.gstatic.com/codesite/ph/images/filedownload-g16.png\"/>  </a> %s</td>",b.downloadURL(),shorten(b.name));
		int i=0;
		for (String s : keyset) {
			boolean linkify = true;
			List res = query_cache.eval(s, doc);
			double tmp = res.size();
			if (tmp > 0 && (res.get(0) instanceof String)){
				tmp = 0;
				linkify = false;
			}
			if (tmp > 0 && (res.get(0) instanceof Boolean)){
				tmp = 0;
				for (Boolean bi: (List<Boolean>)res){
					tmp += bi?1:0;
				}
				linkify = false;
			}
			if (tmp > 0 && (res.get(0) instanceof BigDecimal)){
				tmp = 0;
				for (BigDecimal bi: (List<BigDecimal>)res){
					tmp += bi.floatValue();
				}
				linkify = false;
			}
			if (tmp > 0 && (res.get(0) instanceof BigInteger)){
				tmp = 0;
				for (BigInteger bi: (List<BigInteger>)res){
					tmp += bi.intValue();
				}
				linkify = false;
			}
			total[i] += tmp;

			try {
				if (linkify )
					writer.printf("<td><a href='http://mclab-bench.appspot.com/Detail?project=%s&filter=%s'>%s</a></td>",
								b.name, URLEncoder.encode(q.get(s),"UTF-8"), tmp);
				else{
					writer.printf("<td>%s</td>", res.toString());
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
		writer.printf("</tr>");
	}
	
	private void sendMail(HttpServletRequest req, String table) {
		try {        	
			Properties props = new Properties();
		    Session session = Session.getDefaultInstance(props, null);

		    Message msg = new MimeMessage(session);
		    msg.setFrom(new InternetAddress("noreply@mclab-bench.appspotmail.com", "Notifier"));
		    msg.addRecipient(Message.RecipientType.TO,
		                     new InternetAddress(req.getParameter("email"), "---"));
		    msg.setSubject("Process is now complete.");
		    msg.setText("Please find the results attached"+ req.getParameter("query"));
		    MimeMultipart mp = new MimeMultipart();
	        MimeBodyPart htmlPart = new MimeBodyPart();
	        htmlPart.setContent(table, "text/html");
	        mp.addBodyPart(htmlPart);
	        
		    MimeBodyPart attachment = new MimeBodyPart();
	        attachment.setFileName("Table.html");
	        attachment.setContent(table, "text/html");
	        mp.addBodyPart(attachment);
	        msg.setContent(mp);
		    Transport.send(msg);

		} catch (AddressException e) {
			log.info("Mail not sent: "+ e);
		} catch (MessagingException e) {
			log.info("Mail not sent: "+ e);
		} catch (Exception e){
			log.info("Mail not sent: "+ e);
		}
	}

}
