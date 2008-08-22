<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div style="border:1px solid black;margin:6px;padding:6px;">
<c:if test="${empty ss_twitterId}">
  <h4>Do you twitter? Add your Twitter name to your profile and see what your friends are saying.</h4>
</c:if>
<c:if test="${!empty ss_twitterId}">

<h3>My Friends are Twittering</h3>

<table cellpadding="3">
<c:forEach var="user" items="${ss_users}">
  <jsp:useBean id="user" type="java.util.Map" />
  <%
  	java.util.Map status = (java.util.Map) user.get("status");
  	String[] ca = ((String)status.get("created_at")).split(" ");
  	String createdAt = ca[1] + " " + ca[2] + " " + ca[5] + " " + ca[3];
  	java.util.Date createdAtDate = new java.util.Date(createdAt);
  %>
  <tr>
  <td valign="top" nowrap>
    <c:if test="${!empty user['profile_image_url']}">
      <a href="/remoteapp/twitter/id/${user['id']}" target="_blank" >
        <img src="${user['profile_image_url']}" title="${user['name']}" />
      </a>
    </c:if>
  </td>
  <td valign="top" nowrap><a href="/remoteapp/twitter/id/${user['id']}" target="_blank" >${user['name']}</a></td>
  <td valign="top" nowrap><fmt:formatDate value="<%= createdAtDate %>" pattern="EEE, hh:mm a"/><br/>
    <fmt:formatDate value="<%= createdAtDate %>" dateStyle="long"/></td>
  <td valign="top">${user.status.text}</td>
  </tr>
</c:forEach>
</table>
<br/>
[Twitterer: ${ss_twitterId}]
</c:if>

</div>
<br/>
<br/>
