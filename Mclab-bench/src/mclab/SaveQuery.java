package mclab;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class SaveQuery extends HttpServlet{
	private static final Logger log = Logger.getLogger(UploadHandler.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException{
		UserService userService = UserServiceFactory.getUserService();
		if (!(userService.isUserLoggedIn() && userService.isUserAdmin())){
			res.sendRedirect("/Table?query="+req.getParameter("query"));
			return;
		}
		SavedQuery s = new SavedQuery(req.getParameter("desc"), req.getParameter("query"));
		new DAO().ofy().put(s);
		res.sendRedirect("/?message=done");
	}
}
