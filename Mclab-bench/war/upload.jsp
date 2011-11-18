<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

<html>
    <head>
        <title>Upload</title>
        <%= mclab.ProjectsTable.header %>
    </head>
    <body>

<%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
%>
        <p>Hello, <%= user.getNickname() %>! (You can
        <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
<%
        if (!userService.isUserAdmin()) {
%>
            You Need to be in administrators group to upload to this project 
<%
        } else {
%>
        You can upload a Zip file containing a collection of xml files. XML files larger than 1MB are ignored. 
        If the Zip file is more than 30MB please split it to multiple uploads.  
        <form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
            <input type="file" name="myFile">
            <input type="submit" value="Submit">
        </form>
<%
        }
    } else {
%>
<p>Hello!
<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
to Upload you files.</p>
<%
    }
%>

        
    </body>
</html>
