<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div style="border:1px solid black;margin:6px;padding:6px;">
<h3>Remote application demonstration - Twitter statuses</h3>

<table cellpadding="3">
<c:forEach var="user" items="${ss_users}">
  <tr>
  <td valign="top"><c:if test="${!empty user['profile_image_url']}"><img src="${user['profile_image_url']}" /></c:if></td>
  <td valign="top">${user['name']}</td>
  <td valign="top">${user.status.created_at}</td>
  <td valign="top">${user.status.text}</td>
  </tr>
</c:forEach>
</table>

</div>
<br/>
<br/>
