<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<!-- Modified December 31, 2009 9:42 (GWT) -->

<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/initializeGWT.jsp"     %>
<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_appConfig.jsp" /> 

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <title>GwtTeaming</title>
    
	<script type="text/javascript" src="<html:tinyMcePath/>tiny_mce.js"></script>
	
	<script type="text/javascript" language="javascript">
		// Save away information such as the binder id and the adapted url for the request we are working with.
		// Through an overlay we will access m_requestInfo from java.
		var m_requestInfo = {
			binderId : '${binderId}',
			userName : '${userFullName}',
			adaptedUrl : '${adaptedUrl}',
			imagesPath : '<ssf:escapeJavaScript><html:imagesPath/></ssf:escapeJavaScript>',
			myWSUrl : '${myWorkspaceUrl}',
			errMsg : '${errMsg}',
			isNovellTeaming : ${isNovellTeaming},
			language : '${ssUser.locale.language}',
			loginRefererUrl : '${ssUrl}',
			loginPostUrl : '<ssf:escapeJavaScript>${ss_loginPostUrl}</ssf:escapeJavaScript>',
			isUserLoggedIn : ${isUserLoggedIn},
			promptForLogin : ${promptForLogin},
			loginError : '<ssf:escapeJavaScript>${ss_loginError}</ssf:escapeJavaScript>',
			contentCss : '<ssf:url webPath="viewCss"><ssf:param name="sheet" value="editor"/></ssf:url>',
			simpleSearchUrl: '<ssf:escapeJavaScript><ssf:url action="advanced_search" actionUrl="true"><ssf:param name="newTab" value="1"/><ssf:param name="quickSearch" value="true"/><ssf:param name="operation" value="ss_searchResults"/></ssf:url></ssf:escapeJavaScript>',
			advancedSearchUrl: '<ssf:escapeJavaScript><ssf:url action="advanced_search" actionUrl="true" windowState="maximized"><ssf:param name="action" value="advancedSearch"/><ssf:param name="tabTitle" value="SEARCH FORM"/><ssf:param name="newTab" value="0"/></ssf:url></ssf:escapeJavaScript>',
			savedSearchUrl: '<ssf:escapeJavaScript><ssf:url action="advanced_search" actionUrl="true"><ssf:param name="newTab" value="1"/><ssf:param name="operation" value="ss_savedQuery"/></ssf:url></ssf:escapeJavaScript>'
		};

		var ss_workareaIframeOffset = 50;
		function ss_setWorkareaIframeSize() {
			//If possible, try to directly set the size of the iframe
			//This may fail if the iframe is showing something in another domain
			//If so, the alternate method (via ss_communicationFrame) is used to set the window height
			try {
				var iframeDiv = document.getElementById('contentControl')
				if (window.frames['gwtContentIframe'] != null) {
					eval("var iframeHeight = parseInt(window.gwtContentIframe" + ".document.body.scrollHeight);")
					if (iframeHeight > 100) {
						iframeDiv.style.height = iframeHeight + ss_workareaIframeOffset + "px"
					}
				}
			} catch(e) {
			}
		}
		</script>
	<script type="text/javascript" src="<html:rootPath/>js/common/ss_common.js"></script>
	<script type="text/javascript" src="<html:rootPath/>js/forum/view_iframe.js"></script>
	<script type="text/javascript" language="javascript" src="<html:rootPath />js/gwt/gwtteaming/gwtteaming.nocache.js"></script>
  </head>

  <body>
    <c:set var="gwtUI" value="true" scope="request"/>

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>

	<!-- This div will hold the content of the main Teaming page. -->
	<div id="gwtMainPageDiv">
	</div>

    <%@ include file="/WEB-INF/jsp/dashboard/portletsupport.jsp" %>
  </body>
</html>
