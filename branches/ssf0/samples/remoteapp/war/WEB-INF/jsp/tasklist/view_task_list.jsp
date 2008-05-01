<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>

You have the following tasks to do...<br/><br/>

<%
	java.util.List taskList = (java.util.List) request.getAttribute("taskList");
	Iterator taskIt = taskList.iterator();
	while (taskIt.hasNext()) {
		Map task = (Map) taskIt.next();
		%>
<a href='<%= task.get("href") %>'
  onClick="window.open(this.href,'_blank','width=600,height=500,directories=no,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no');return false;"
><%= task.get("title") %></a><br/>
		<%
	}
%>

