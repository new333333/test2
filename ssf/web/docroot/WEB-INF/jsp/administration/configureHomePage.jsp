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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>

<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.configure_homePage") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
function ss_saveBinderId(id) {
	var obj = self.document.getElementById("homePageId")
	obj.value = id;
}
function ss_saveGuestBinderId(id) {
	var obj = self.document.getElementById("guestHomePageId")
	obj.value = id;
}

function handleCloseBtn() {
	<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			// Tell the Teaming GWT ui to close the administration content panel.
			window.top.ss_closeAdministrationContentPanel();
			return false;
	<% 	}
		else { %>
			self.window.close();
			return false;
	<%	} %>
		
} // end handleCloseBtn()
</script>"

<body class="ss_style_body tundra">
<div class="ss_style ss_portlet">
<c:set var="titleTag" value='<%= NLT.get("administration.configure_homePage") %>'/>
<ssf:form title="${titleTag}" >

	<c:if test="${!empty ssException}">
	<span class="ss_largerprint"><ssf:nlt tag="administration.errors"/> (<c:out value="${ssException}"/>)</span></br>
	</c:if>

	<form class="ss_style ss_form" name="${renderResponse.namespace}fm" method="post" 
	  action="<ssf:url action="configure_home_page" actionUrl="true"/>">
		<div class="ss_buttonBarRight">
		<br/>
		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
			<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
				  onClick="return handleCloseBtn();"/>
		</div>
	  <div>
	    <div>
			<span class="ss_labelAbove">
			    <ssf:nlt tag="administration.config.defaultHomePage"/>
			</span><br/>
	        <ssf:find 
	    		formName="fm${renderResponse.namespace}" 
	    		formElement="searchText" 
	    		type="places"
	    		singleItem="true"
	    		width="300px"
	    		accessibilityText="search.Places.alt"
	    		clickRoutine="ss_saveBinderId"/>
	    	<input type="hidden" id="homePageId" name="homePageId" value=""/>
			<br/>
			<br/>
	    </div>
	    <div>
			<span class="ss_labelAbove">
			    <ssf:nlt tag="administration.config.defaultGuestHomePage"/>
			</span><br/>
	        <ssf:find 
	    		formName="fm${renderResponse.namespace}" 
	    		formElement="searchText" 
	    		type="places"
	    		singleItem="true"
	    		width="300px"
	    		accessibilityText="search.Places.alt"
	    		clickRoutine="ss_saveGuestBinderId"/>
	    	<input type="hidden" id="guestHomePageId" name="guestHomePageId" value=""/>
			<br/>
			<br/>
	    </div>
	  </div>
	<br/>
	<div class="ss_buttonBarLeft">
		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
		<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
			  onClick="return handleCloseBtn();"/>
	</div>
	</form>
</ssf:form>
</div>
</body>
</html>