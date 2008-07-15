<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div style="border:1px solid black;margin:6px;padding:6px;">
<h3>Twitterings</h3>

<table cellspacing="5" cellpadding="3">
<c:set var="userImageShown" value="false"/>
<c:forEach var="status" items="${ss_users}">
  <jsp:useBean id="status" type="java.util.Map" />
  <%
  	String[] ca = ((String)status.get("created_at")).split(" ");
  	String createdAt = ca[1] + " " + ca[2] + " " + ca[5] + " " + ca[3];
  	java.util.Date createdAtDate = new java.util.Date(createdAt);
  %>
  <tr>
  <td valign="top"><c:if test="${!userImageShown && !empty status.user['profile_image_url']}"><img src="${status.user['profile_image_url']}" /></c:if></td>
  <td valign="top" nowrap><c:if test="${!userImageShown}">${status.user['name']}</c:if></td>
  <td valign="top" nowrap><fmt:formatDate value="<%= createdAtDate %>" pattern="EEE,hh:mm a"/><br/>
    <fmt:formatDate value="<%= createdAtDate %>" dateStyle="long"/></td>
  <td valign="top">${status.text}</td>
  </tr>
<c:set var="userImageShown" value="true"/>
</c:forEach>
</table>

</div>
<br/>
<br/>
