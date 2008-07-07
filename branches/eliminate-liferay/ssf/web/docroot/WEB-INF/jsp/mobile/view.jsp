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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%
	boolean isWap = BrowserSniffer.is_wap_xhtml(request);
	if (isWap) {
%>
<c:set var="ss_noEnableAccessibleLink" value="1" scope="request"/>
<%
	}
%>
<c:if test="${empty ss_portletInitialization}">
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
</c:if>
<%
	if (isWap) {
%>
<style>
.ss_mobile, .ss_mobile td, .ss_mobile th {
  font-family: Lucida Sans Unicode, Arial, sans-serif;
  font-size: 10px; 
}
</style>
<%@ include file="/WEB-INF/jsp/mobile/show_front_page_data.jsp" %>
<%
	} else {
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript">
var ss_mobileIframeOffset = 30;
function ss_setMobileIframeSize() {
	var targetDiv = document.getElementById('ss_mobileDiv')
	var iframeDiv = document.getElementById('ss_mobileIframe')
	if (window.frames['ss_mobileIframe'] != null) {
		eval("var iframeHeight = parseInt(window.ss_mobileIframe" + ".document.body.scrollHeight);")
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_mobileIframeOffset + "px"
		}
	}
}
</script>

<ssf:ifLoggedIn>
<c:if test="${!empty ss_mobileBinderList}">
<div align="right">
  <a class="ss_linkButton" 
    href="<portlet:renderURL 
      portletMode="edit" 
      windowState="maximized" />"
    ><ssf:nlt tag="button.configure"/></a>
</div>
</c:if>
</ssf:ifLoggedIn>

<c:set var="folderIdList" value=""/>
<jsp:useBean id="folderIdList" type="java.lang.String" />

<div class="ss_portlet_style ss_portlet">

<div class="ss_style" style="padding:4px;">
<ssf:inlineHelp jsp="portlets/mobile_portlet"/>

<ssHelpSpot helpId="portlets/mobile_portlet" offsetX="0" offsetY="-10" 
			    title="<ssf:nlt tag="helpSpot.mobilePortlet"/>"></ssHelpSpot>
<ssf:ifLoggedIn>
<c:if test="${empty ss_mobileBinderList}">
<div align="right">
  <a class="ss_linkButton" 
    href="<portlet:renderURL 
      portletMode="edit" 
      windowState="maximized" />"
    ><ssf:nlt tag="button.configure"/></a>
</div>
</c:if>
</ssf:ifLoggedIn>

<div id="ss_mobileDiv">
  <iframe id="ss_mobileIframe" 
    name="ss_mobileIframe" 
    style="width:100%; display:block; position:relative;"
	src="<ssf:url     
    		adapter="true" 
    		portletName="ss_forum" 
    		folderId="${ssFolder.id}" 
    		action="__ajax_mobile" 
    		entryId="${entryId}" 
    		actionUrl="false" >
        </ssf:url>" 
	height="50" width="100%" 
	onLoad="ss_setMobileIframeSize();" 
	frameBorder="0" >xxx</iframe>
</div>
<script type="text/javascript">
ss_createOnLoadObj("ss_setMobileIframeSize", ss_setMobileIframeSize);
</script>

</div>
</div>
<%
	}
%>
