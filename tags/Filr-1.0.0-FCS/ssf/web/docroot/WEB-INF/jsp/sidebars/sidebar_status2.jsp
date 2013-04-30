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
<% // Status %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%@ page import="org.kablink.util.PropertyNotFoundException" %>
<%@ page import="org.kablink.teaming.ObjectKeys" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="guestInternalId" value="<%= ObjectKeys.GUEST_USER_INTERNALID %>"/>
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
<c:if test="${ssUser.internalId != guestInternalId}">
  <div id="ss_myStatusDiv">
    <script type="text/javascript">
	  var ss_miniblogTextTooBigErrorMsg = "<ssf:nlt tag="miniblog.textTooBig"/>";
	  ss_statusCurrent = "";
	  <c:if test="${!empty ssUser.status}">
	    ss_statusCurrent = "<%= java.net.URLEncoder.encode(ssUser.getStatus()) %>";
	  </c:if>
	</script>
	<table cellspacing="0" cellpadding="0" width="100%" style="padding:0px 4px;">
	 <tr>
	  <td valign="top">
	  <a href="javascript: ;" onclick="ss_viewMiniBlog('${ssUser.id}', '0', true);return false;"
	   title="<ssf:nlt tag="miniblog.title"/>">
	   <span class="ss_status_header"><ssf:nlt tag="relevance.userStatus"/></span>
	  </a>
	  </td>
	  <td valign="top" align="right">
	    <span id="ss_myStatusTitleSrc" class="ss_fineprint">
	      <c:if test="${!empty ssUser.statusDate && !empty ssUser.status}">
	        <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
              value="${ssUser.statusDate}" type="both" 
	          timeStyle="short" dateStyle="short" />
	      </c:if>
	    </span>
	  </td>
	 </tr>
	 <c:if test="${!empty ssUser.status}">
	 <tr>
	  <td colspan="2">
  		<table cellspacing="0" cellpadding="0">
  		  <tr>
  		    <td valign="top" width="90%">
			  	<div class="ss_input_myStatus">
			  	  <span id="ss_myStatusTextSrc">
			  	    <ssf:makeWrapable><c:out value="${ssUser.status}" escapeXml="true"/></ssf:makeWrapable>
			  	  </span>
			  	</div>
  		    </td>
	  		<td valign="top" align="right" width="10%">
	  		  <input type="button" style="padding:0px 2px 0px 2px;"
	  		    value="<ssf:nlt tag="button.clear"/>" class="ss_inlineButtonSmall"
	  		    onclick="ss_clearStatus('ss_status_textarea${renderResponse.namespace}');return false;"/>
	  		</td>
  		  </tr>
  		</table>
	  </td>
	 </tr>
	 </c:if>
	</table>
 <c:set var="ss_sidebarStatusTitle" scope="request">
   <label for="ss_status_textarea${renderResponse.namespace}">
     <span class="ss_fineprint"><ssf:nlt tag="sidebar.statusPost"/></span>
   </label>
 </c:set>
 <c:set var="ss_sidebarStatusTitleInfo"><ssf:nlt tag="sidebar.statusPostInfo"/></c:set>
 <ssf:sidebarPanel titleHTML="${ss_sidebarStatusTitle}" id="ss_miniblog_sidebar" 
    initOpen="true" sticky="true" noColorChange="true" titleInfo="${ss_sidebarStatusTitleInfo}">
	<ssf:ifLoggedIn>
		<textarea cols="20" rows="2" id="ss_status_textarea${renderResponse.namespace}"
			wrap="virtual" class="ss_input_myStatus" 
  			onFocus="ss_setStatusBackground(this, 'focus');"
  			onKeyPress="ss_updateStatusSoon(this, event, <%= ObjectKeys.USER_STATUS_DATABASE_FIELD_LENGTH %>);"
  			onBlur="ss_setStatusBackground(this, 'blur')"
  			onMouseover="ss_setStatusBackground(this, 'mouseOver');"
  			onMouseout="ss_setStatusBackgroundCheck(this);"
  		></textarea>
  		
  		<div >
  		  <input type="button" value="<ssf:nlt tag="button.ok"/>" class="ss_inlineButtonSmall"
  		    onclick="ss_updateStatusNowId('ss_status_textarea${renderResponse.namespace}');return false;"/>
	  	</div>
	</ssf:ifLoggedIn> 
 </ssf:sidebarPanel>
 </div>
</c:if>
