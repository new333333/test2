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
<% // Profile listing %>
<%@ page import="org.kablink.teaming.domain.Principal" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ page import="org.kablink.teaming.ObjectKeys" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="postingAgentInternalId" value="<%= ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID %>"/>
<c:set var="syncAgentInternalId" value="<%= ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID %>"/>
  <div class="folders">
	<div class="folder-content">
<c:forEach var="entry" items="${ssWorkspaces}" >
  <c:if test="${entry.internalId != postingAgentInternalId && entry.internalId != syncAgentInternalId}">
	<div class="entry">
	  <div class="entry-title">
	    <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    		  binderId="${ssBinder.id}"
				  entryId="${entry.id}"  
				  action="__ajax_mobile" 
				  operation="mobile_show_workspace" actionUrl="false" />"
		>${entry.title}</a>
	  </div>
	  
	  <c:if test="${!empty entry.emailAddress}">
	    <div class="email-2">
		  <span><ssf:mailto email="${entry.emailAddress}"/></span>
	    </div>
	  </c:if>
	
	  <c:if test="${!empty entry.phone}">
	    <div class="phone-2">
		  <span>${entry.phone}</span>
	    </div>
	  </c:if>
	</div>
  </c:if>
</c:forEach>
	<div class="entry-actions">
	      <%@ include file="/WEB-INF/jsp/mobile/folder_next_prev.jsp" %>
	</div>
</div>
</div>
<c:set var="ss_mobileBinderListShown" value="true" scope="request"/>
