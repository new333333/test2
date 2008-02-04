<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // The main forum view - for viewing folder listings and for viewing entries %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<c:set var="showFolderPage" value="true"/>
<c:if test="${ss_displayType == 'ss_workarea'}">
  <ssf:ifnotadapter>
    <c:set var="showWorkspacePage" value="false"/>
  </ssf:ifnotadapter>
</c:if>
<ssf:ifadapter>
<body class="ss_style_body">
</ssf:ifadapter>
<c:if test="${!empty ssReloadUrl}">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
</script>

</c:if>
<c:if test="${empty ssReloadUrl}">
<c:if test="${ss_displayType == 'ss_workarea'}">
<script type="text/javascript">
function ss_workarea_showId(id, action, entryId) {
	if (typeof entryId == "undefined") entryId = "";
	//Build a url to go to
	var url = "<ssf:url     
	    		  adapter="true" 
	    		  portletName="ss_workarea" 
	    		  binderId="ssBinderIdPlaceHolder" 
    			  entryId="ssEntryIdPlaceHolder" 
	    		  action="ssActionPlaceHolder" 
	    		  actionUrl="false" >
	           </ssf:url>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
<ssf:ifnotadapter>
	var iframeDivObj = document.getElementById('ss_viewListingIframe${renderResponse.namespace}')
	if (iframeDivObj != null) {
		iframeDivObj.src = url;
	} else {
		return true;
	}
</ssf:ifnotadapter>
<ssf:ifadapter>
	self.location.href = url;
</ssf:ifadapter>
	return false;
}
</script>
<ssf:ifnotadapter>
<script type="text/javascript">
var ss_workareaIframeOffset = 50;
function ss_setWorkareaIframeSize${renderResponse.namespace}() {
	var iframeDiv = document.getElementById('ss_viewListingIframe${renderResponse.namespace}')
	if (window.frames['ss_viewListingIframe${renderResponse.namespace}'] != null) {
		eval("var iframeHeight = parseInt(window.ss_viewListingIframe${renderResponse.namespace}" + ".document.body.scrollHeight);")
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_workareaIframeOffset + "px"
		}
	}
}
</script>
<iframe id="ss_viewListingIframe${renderResponse.namespace}" 
    name="ss_viewListingIframe${renderResponse.namespace}" 
    style="width:100%; height:400px; display:block; position:relative;"
	src="<ssf:url     
    		adapter="true" 
    		portletName="ss_workarea" 
    		binderId="${ssBinderId}" 
    		action="view_folder_listing" 
    		entryId="${ssEntryIdToBeShown}" 
    		actionUrl="false" >
        </ssf:url>" 
	onLoad="ss_setWorkareaIframeSize${renderResponse.namespace}();" 
	frameBorder="0" >xxx</iframe>

<!-- portlet iframe div -->
<%@ include file="/WEB-INF/jsp/entry/view_iframe_div.jsp" %>
<!-- portlet iframe div -->	

</ssf:ifnotadapter>
</c:if>

<c:if test="${showFolderPage}">
 <jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
 <jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
 <jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />
 <%@ include file="/WEB-INF/jsp/entry/view_listing_common.jsp" %>
 <jsp:useBean id="ss_entryWindowWidth" type="java.lang.Integer" scope="request" />
 <jsp:useBean id="ss_entryWindowHeight" type="java.lang.Integer" scope="request" />

  <c:if test="<%= !reloadCaller %>">
    <c:if test="<%= !isViewEntry %>">

 <div id="ss_showentryhighwatermark" style="position:absolute; visibility:visible;">
 <img border="0" <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif">
 </div>
 <div id="ss_portlet_content" class="ss_style ss_portlet ss_content_outer" style="margin:0px; padding:0px;">
 <%@ include file="/WEB-INF/jsp/entry/view_iframe.jsp" %>
 </div>
     </c:if>
  </c:if>
  <c:if test="<%= reloadCaller %>">
 <script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
 </script>
  </c:if>
</c:if>
</c:if>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

