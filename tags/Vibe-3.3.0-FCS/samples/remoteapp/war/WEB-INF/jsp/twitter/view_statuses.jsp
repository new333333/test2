<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
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
    String[] ca;
    String createdAt;
    java.util.Date createdAtDate;
    
  	java.util.Map status = (java.util.Map) user.get("status");
    if (null == status) {
    	ca = new String[0];
    	createdAt = "";
    	createdAtDate = null;
    }
    else {
	  	ca = ((String)status.get("created_at")).split(" ");
	  	createdAt = ca[1] + " " + ca[2] + " " + ca[5] + " " + ca[3];
	  	createdAtDate = new java.util.Date(createdAt);
    }
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
  <td valign="top" nowrap><% if (null == createdAtDate) { %>&lt;No Tweets&gt;<% } else { %><fmt:formatDate value="<%= createdAtDate %>" pattern="EEE, hh:mm a"/><% } %><br/>
    <% if (null != createdAtDate) { %><fmt:formatDate value="<%= createdAtDate %>" dateStyle="long"/><% } %></td>
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
