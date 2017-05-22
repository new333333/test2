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
<%@ page import="org.kablink.teaming.ObjectKeys" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<script type="text/javascript">
function ss_logoff() {
	var x = '<%= org.kablink.teaming.web.util.WebUrlUtil.getSsoProxyLogoffUrl(request) %>';
	if (x == null || x == "") {
		var y = '${ss_logoutUrl}';
		//alert(y);
		self.location.href=y;
	} else {
		//alert (x);
		var y = '${ss_logoutUrl}';
		ss_logoff_from_teaming_then_sso(y);
	}
}
function ss_logoff_from_teaming_then_sso(logoutURL) {
	callbackRoutine = ss_logoff_from_sso
	var x;

	if (window.XMLHttpRequest) {
	x = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
	x = new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	x.open("POST", logoutURL, true);
	
	x.onreadystatechange = function() {
		if (x.readyState != 4) {
			return;
		}
		if (x.status == 200) {
			callbackRoutine(x.responseText)        	
		} else {		
			callbackRoutine(x.statusText)
		}
	}
	x.send(null);
	delete x;
}      
function ss_logoff_from_sso(s) {
	self.location.href='<%= org.kablink.teaming.web.util.WebUrlUtil.getSsoProxyLogoffUrl(request) %>';
}
</script>
<div id="footer">
  <ul>
	<li>
		<a href="<ssf:url adapter="true" portletName="ss_forum" 
			action="__ajax_mobile" operation="mobile_show_front_page" actionUrl="false" />"
			><img src="<html:rootPath/>css/images/mobile/home_icon.png" 
			 <ssf:alt tag=""/> width="14" height="15" 
			 hspace="3" border="0" align="bottom" /><ssf:nlt tag="mobile.returnToTop"/></a>
	</li>
	<li>
		<c:set var="guestInternalId" value="<%= ObjectKeys.GUEST_USER_INTERNALID %>"/>
		<c:if test="${ssUser.internalId == guestInternalId}">
		  <c:if test='<%= !org.kablink.teaming.util.SPropsUtil.getBoolean("form.login.auth.disallowed",false) %>' >
		    <a href="<ssf:url action="__ajax_mobile" actionUrl="false" 
							operation="mobile_login" />"
		    >
		    <span><ssf:nlt tag="login"/></span>
		    </a>
		  </c:if>
		</c:if>
		
		<c:if test="${ssUser.internalId != guestInternalId}">
			<a href="javascript: ;" onClick="ss_logoff();return false;"><span><ssf:nlt tag="logout"/></span></a>
		</c:if>
	</li>
	<li>
	<c:if test="${!empty ssBinder}">
	  <a href="<ssf:url folderId="${ssBinder.id}" 
	    action="view_folder_listing"><ssf:param
		name="captive" value="true"/></ssf:url>"><ssf:nlt tag="mobile.teamingUI"/></a>
	</c:if>
	<c:if test="${empty ssBinder}">
		<c:if test="${ssUser.internalId != guestInternalId}">
			<c:if test="${!empty ssUser.workspaceId}">
				<a href="<ssf:url action="view_ws_listing"
				    binderId="${ssUser.workspaceId}"><ssf:param
				    name="captive" value="true"/></ssf:url>"><ssf:nlt tag="mobile.teamingUI"/></a>
			</c:if>
			<c:if test="${empty ssUser.workspaceId}">
				<ssf:userTitle user="${ssUser}"/>
			</c:if>
		</c:if>
	</c:if>
	</li>
  </ul>
</div>
