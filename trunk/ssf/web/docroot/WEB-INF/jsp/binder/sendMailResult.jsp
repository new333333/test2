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
<%@ page import="org.kablink.teaming.util.NLT" %>
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>

<div class="ss_style ss_portlet" style="padding:10px;">

<ssf:form titleTag="sendMail.status">
<form class="ss_style ss_form" method="post">
<br/>

<c:if test="${empty ssErrorList && empty ss_errorMessage && empty ssFailedEmailAddresses && empty ssFailedEmailAccess && empty ssQueuedEmailAddresses && empty ssSentEmailAddresses}">
	<span class="ss_titlebold"><ssf:nlt tag="sendMail.noErrors"/></span>
	<br/>
	<br/>
</c:if>

<c:if test="${!empty ssErrorList || !empty ss_errorMessage}">
	<span class="ss_titlebold"><ssf:nlt tag="sendMail.mailErrors"/></span><br/>
	<c:if test="${!empty ss_errorMessage}">
		<span>${ss_errorMessage}</span>
		<br/>
		<br/>
	</c:if>
	<c:if test="${!empty ssErrorList}">
		<ul class="ss_nobullet">
		<c:forEach var="item" items="${ssErrorList}">
			<li>${item}</li>
		</c:forEach>
		</ul>
		<br/>
	</c:if>
</c:if>

<c:if test="${!empty ssFailedEmailAddresses}">
<span class="ss_titlebold"><ssf:nlt tag="sendMail.mailFailed"/></span><br/>
<ul class="ss_nobullet">
<c:forEach var="item" items="${ssFailedEmailAddresses}">
	<li>${item}</li>
</c:forEach>
</ul>
<br/>
</c:if>

<c:if test="${!empty ssFailedEmailAccess}">
<span class="ss_titlebold"><ssf:nlt tag="sendMail.mailFailedAccess"/></span><br/>
<ul class="ss_nobullet">
<c:forEach var="item" items="${ssFailedEmailAccess}">
	<li>${item}</li>
</c:forEach>
</ul>
<br/>
</c:if>

<c:if test="${!empty ssQueuedEmailAddresses}">
<span class="ss_titlebold"><ssf:nlt tag="sendMail.mailQueued"/></span><br/>
<ul class="ss_nobullet">
<c:forEach var="item" items="${ssQueuedEmailAddresses}">
	<li>${item}</li>
</c:forEach>
</ul>
<br/>
</c:if>

<c:if test="${!empty ssSentEmailAddresses}">
<span class="ss_titlebold"><ssf:nlt tag="sendMail.mailSent"/></span><br/>
<ul class="ss_nobullet">
<c:forEach var="item" items="${ssSentEmailAddresses}">
	<li>${item}</li>
</c:forEach>
</ul>
<br/>
</c:if>

<%
	String backOCHandler = "self.window.history.back()";
	if (org.kablink.util.BrowserSniffer.is_ie(request)) {
		backOCHandler = "self.window.location.reload(" + backOCHandler + ")";
	}
%>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" 
  onClick="self.window.close();return false;">
<input type="submit" class="ss_submit" name="backBtn" value="<ssf:nlt tag="button.goBack"/>" 
  onClick="<%= backOCHandler %>;return false;"
  style="padding-left:20px;">
</div>
</form>
</ssf:form>

</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
