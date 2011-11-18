package mclab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.xpath.XPathEvaluator;

import org.apache.tools.ant.filters.StringInputStream;
import org.xml.sax.InputSource;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class UploadWorker extends HttpServlet{
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private static final Logger log = Logger.getLogger(UploadHandler.class.getName());
    public void doPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {
    	BlobKey blobKey = new BlobKey(req.getParameter("key"));
    	Objectify ofy = new DAO().ofy();
		BlobstoreInputStream input = new BlobstoreInputStream(blobKey);	
		ZipInputStream zio = new ZipInputStream(input);
    	ZipEntry entry;
    	Bench bench = new Bench();
    	ArrayList<String> files = new ArrayList<String>();
    	int BUFLIMIT = 1024*1000;
    	int total = 0 , succeeded = 0; 
    	while ((entry=zio.getNextEntry()) != null){
    		byte[] buf = new byte[BUFLIMIT];
    		int count =0;
    		total += 1;
			succeeded += createBench(ofy, zio);

    	}
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "Your upload is complete.";
        msgBody += " From "+ total + " projects, " + succeeded+" were added to the database successfully.";

        try {        	
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("noreply@mclab-bench.appspotmail.com", "Notifier"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(req.getParameter("email"), "---"));
            msg.setSubject("Your Upload is now complete.");
            msg.setText(msgBody);
            Transport.send(msg);

        } catch (AddressException e) {
        	log.info("Mail not sent: "+ e);
        } catch (MessagingException e) {
        	log.info("Mail not sent: "+ e);
        } catch (Exception e){
        	log.info("Mail not sent: "+ e);
        }
    }
    private int createBench(Objectify ofy, InputStream input) {
		try {
			
			Reader buf = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			XPathFactory xpf = XPathFactory
						.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
			XPath xpe = xpf.newXPath();
			StringBuffer xml = new StringBuffer();
			int BUFFER=104096;
			
			char[] data= new char[BUFFER];
			int count=0;
			try{
				while ((count = buf.read(data)) != -1) {
					   //System.out.write(x);
					xml.append(data,0,count);
					}
			}
			catch(Exception e){
				
			}
			String res = xml.toString();
			InputSource is = new InputSource(new StringInputStream(res));
			SAXSource ss = new SAXSource(is);
			NodeInfo doc = ((XPathEvaluator) xpe).setSource(ss);
			XPathExpression findLine = xpe.compile("string(/CompilationUnits/@name)");
			String project=(String) findLine.evaluate(doc, XPathConstants.STRING);
			log.warning("looking for "+ project);
			
			Query<Bench> q = ofy.query(Bench.class).filter("name = ",project).limit(1);
			Bench b=null;
			if ((b=q.get())==null){
				b = new Bench();
				b.name=project;
			}else{
				log.warning("already in "+ project);
			}
			b.xmlBlob=new Blob(ProjectsTable.compress(res)) ;
			b.parsed = true;
			log.warning("putting in "+ b.name);
	
			ofy.put(b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return 1;
    }
}