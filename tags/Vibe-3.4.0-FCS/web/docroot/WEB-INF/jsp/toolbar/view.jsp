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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:if test="${empty ss_portletInitialization}">
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ssComponentId}">
  <c:set var="ssNamespace" value="${renderResponse.namespace}_${ssComponentId}"/>
</c:if>

<div class="ss_portlet_style ss_portlet ss_style">
<% // Navigation bar %>
<c:if test="${renderRequest.windowState == 'normal'}">
<c:set var="ss_navbar_style" value="portlet" scope="request"/>
</c:if>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />
<script type="text/javascript">
function ${renderResponse.namespace}_wsTree_showId(id, obj, action) {
	if (typeof ss_workarea_showId !== "undefined") {
		return ss_workarea_showId(id, action);
	}
	//Build a url to go to
	var url = "<ssf:url windowState="maximized" action="ssActionPlaceHolder"><ssf:param 
			name="binderId" value="ssBinderIdPlaceHolder"/></ssf:url>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
}


</script>

<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
<ssHelpSpot helpId="navigation_bar/workspace_tree_nav_bar" 
  title="<ssf:nlt tag="helpSpot.workspaceTreePortlet"/>"
  offsetX="-13" offsetY="10">
<div style="padding-top:10px; background: url(<html:brandedImagesPath/>icons/toolbar_teaming.gif) no-repeat bottom right;
">
<table cellspacing="0" cellpadding="0" border="0"><tbody><tr>
<td valign="top">
<c:choose>
<c:when test="${renderRequest.windowState == 'normal'}">
	<ssf:tree treeName="${renderResponse.namespace}_wsTree" 
	  topId="${ssWsDomTreeBinderId}" 
	  treeDocument="<%= ssWsDomTree %>"  
	  rootOpen="true"
	  showIdRoutine="${renderResponse.namespace}_wsTree_showId"
	   />
</c:when>
<c:when test="${renderRequest.windowState == 'maximized'}">
	<ssf:tree treeName="${renderResponse.namespace}_wsTree" 
	  topId="${ssWsDomTreeBinderId}" 
	  treeDocument="<%= ssWsDomTree %>"  
	  rootOpen="true"
	  showIdRoutine="${renderResponse.namespace}_wsTree_showId"
	  />
</c:when>
</c:choose>			
</td><td><img src="<html:imagesPath/>pics/1pix.gif" style="height: 80px;"/></td>
</tr></tbody></table>
</div>
</ssHelpSpot>
<div style="padding-top: 10px;" align="right">
   <a href="javascript:;" onClick="ss_showAbout('${renderResponse.namespace}_aboutBox');"><span class="ss_light ss_smallprint"><ssf:nlt tag="navigation.about"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></span></a>
</div>
<div class="ss_themeMenu" onClick="ss_cancelAbout('${renderResponse.namespace}_aboutBox');" style="display: none; width: 350px;" id="${renderResponse.namespace}_aboutBox">
  <div style="padding-top: 10px; padding-bottom: 10px; padding-left: 10px; border-bottom: 1px solid #333333;">
    <table style="line-height: 30px;" cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody><tr>
     <c:choose>
     <c:when test="${ssProductName == 'Novell Vibe'}">
        <td><img src="<html:brandedImagesPath/>pics/novell-teaming-about.png"></td>
     </c:when>
     <c:otherwise>
    	<td style="line-height: 30px;">
        <span style="line-height: 30px; font-size: 24px">${ssProductTitle}</span>
        </td>
     </c:otherwise>
     </c:choose>
     </tr></tbody>
    </table>
  </div>
  <div class="ss_style" style="padding-top: 10px; padding-bottom: 10px; padding-left: 20px; font-family:Arial, Helvetica, sans-serif; font-size: 12px; line-height: 20px;">
    <p>
     ${releaseInfo}<br/>
    <c:if test="${ssProductName == 'Novell Vibe'}">
    &copy; 2007, Novell, Inc. and its licensors. All rights reserved<br/> 
    </c:if>
     &copy; 2007, SiteScape, Inc. All rights reserved<br/>
    </p>
    <div align="right">
    <p style="padding-top: 10px;">
     <a href="http://www.icecore.org" target="_blank"><img src="<html:imagesPath/>pics/powered_by_icecore.png" /></a>
    </p>
    </div>
  </div>
</div>
</div>
</c:if>