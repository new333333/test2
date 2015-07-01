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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@page import="java.io.PrintWriter" %>

<h1><spring:message code="exception.generalError.title"/></h1>

<p><spring:message code="exception.contactAdmin"/></p>


<ssf:ifadapter>
<input type="button" value="<ssf:nlt tag="button.returnToForm"/>" onclick="history.go(-1);"/>
<input type="button" value="<ssf:nlt tag="button.close"/>" 
  onclick="ss_cancelButtonCloseWindow();return false;"/>
</ssf:ifadapter>
<ssf:ifnotadapter>
	<c:if test="${!empty ssUser.workspaceId}">
		<p style="text-align:center;">
		<a href="<ssf:url action="view_ws_listing"
					    		binderId="${ssUser.workspaceId}">
					    		<ssf:param name="newTab" value="1"/>
								</ssf:url>">- <spring:message code="button.home"/> -</a>
		</p>
	</c:if>
</ssf:ifnotadapter>

<c:if test="${!empty exception}" >
<p>
This is where the JasperException was.
<%
	Exception exception = (Exception)request.getAttribute("exception");
	String message = exception.getLocalizedMessage() == null ? exception.toString() : exception.getLocalizedMessage();
	if (message != null) {
		message = org.kablink.teaming.util.stringcheck.StringCheckUtil.check(message);
	}
%>
<%=exception.getClass()%><br>
<%= message.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;") %>
</p>

<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
<c:if test="<%= ssUser != null && ssUser instanceof org.kablink.teaming.domain.User && ssUser.isSuper() %>">
	<input type="button" id="ss_show" value="<ssf:nlt tag="button.showDetails"/>" 
	  onclick="document.getElementById('ss_details').style.display='block'; document.getElementById('ss_hide').style.display='inline'; this.style.display='none'"/>
	<input type="button" id="ss_hide" value="<ssf:nlt tag="button.hideDetails"/>" 
	  onclick="document.getElementById('ss_details').style.display='none'; document.getElementById('ss_show').style.display='inline'; this.style.display='none'" style="display:none;"/>
	<div id="ss_details" style="display:none;">
	<c:set var="stackTrace">
	<%((Exception)request.getAttribute("exception")).printStackTrace(new PrintWriter(out)); %>
	</c:set>
	<jsp:useBean id="stackTrace" type="String" />
	<%= org.kablink.teaming.util.stringcheck.StringCheckUtil.check(stackTrace) %>
	</div>
</c:if>
</c:if>
<ssf:ifnotadapter>
	<c:if test="${!empty ssUser.workspaceId}">
		<p style="text-align:center;"><a href="<ssf:url action="view_ws_listing"
					    		binderId="${ssUser.workspaceId}">
					    		<ssf:param name="newTab" value="1"/>
								</ssf:url>">- <spring:message code="button.home"/> -</a></p>
	</c:if>
</ssf:ifnotadapter>
