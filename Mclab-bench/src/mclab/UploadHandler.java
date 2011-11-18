package mclab;

 
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.googlecode.objectify.*;
import javax.xml.namespace.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.*;


import org.apache.tools.ant.filters.StringInputStream;
import org.xml.sax.InputSource;

import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.xpath.XPathEvaluator;

import com.google.appengine.api.taskqueue.*;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

import com.google.appengine.api.users.*;


public class UploadHandler extends HttpServlet{
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private static final Logger log = Logger.getLogger(UploadHandler.class.getName());
    public void doPost(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
		//res.sendRedirect("/Table");
    	UserService userService = UserServiceFactory.getUserService();
    	if (! userService.isUserAdmin())
        	res.sendRedirect("/upload.jsp");

    	Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
    	BlobKey blobKey = blobs.get("myFile");
		System.setProperty("javax.xml.xpath.XPathFactory:"
				+ NamespaceConstant.OBJECT_MODEL_SAXON,
				"net.sf.saxon.xpath.XPathFactoryImpl");
    	

    	if (blobKey == null) {
    		res.sendRedirect("/upload.jsp");
    		return;
    	} 
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(withUrl("/worker").param("key", blobKey.getKeyString()).param("email", userService.getCurrentUser().getEmail()));
    }
}
