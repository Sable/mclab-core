<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="mclab.*" %>
<%@ page import="com.googlecode.objectify.*" %>


<html>
    <head>
        <title>McBench</title>
        <%= mclab.ProjectsTable.header %>
    </head>
    <body>
    <center>
        <form action="/Table" method="GET" >
            <textarea name="query"></textarea>
            <input type="submit" value="Search" />
        </form>
        <table border=1 cellpadding=5>
        <%
        Query<SavedQuery> queries = new DAO().ofy().query(SavedQuery.class);
        for (SavedQuery q: queries.fetch()){
        %>
         <tr><td><a href="/Table?query=<%= q.getContent().getValue() %>" ><%= q.getName() %></a></td><td><%= q.getContent().getValue() %> </td></tr>
        <% } %>
        </table>
    </center>     
    </body>
</html>