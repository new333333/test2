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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:if test="${empty ss_portletInitialization}">
<div class="ss_style ss_portlet_style ss_portlet">

<%
String wsTreeName = renderResponse.getNamespace() + "_wsTree";
%>
<script type="text/javascript">
function <%= wsTreeName %>_showId(id, obj, action) {
	if (typeof ss_workarea_showId !== "undefined") {
		return ss_workarea_showId(id, action);
	}
	//Build a url to go to
	var url = "<ssf:url action="ssActionPlaceHolder"><ssf:param 
			name="binderId" value="ssBinderIdPlaceHolder"/></ssf:url>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
}

</script>

<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
<table width="100%">
	<tr>
		<td align="left">
	    <ssHelpSpot helpId="portlets/workspace_tree_portlet" offsetX="-13" 
	      title="<ssf:nlt tag="helpSpot.workspaceTreePortlet"/>">
			<div>
			<c:choose>
			<c:when test="${renderRequest.windowState == 'normal'}">
				<ssf:tree treeName="<%= wsTreeName %>" 
				  topId="${ssWsDomTreeBinderId}" 
				  treeDocument="<%= ssWsDomTree %>"  
				  rootOpen="false"
				  showIdRoutine="<%= wsTreeName + "_showId" %>"
				   />
			</c:when>
			<c:when test="${renderRequest.windowState == 'maximized'}">
				<ssf:tree treeName="<%= wsTreeName %>" 
				  topId="${ssWsDomTreeBinderId}" 
				  treeDocument="<%= ssWsDomTree %>"  
				  rootOpen="true"
				  showIdRoutine="<%= wsTreeName + "_showId" %>"
				  />
			</c:when>
			</c:choose>			
			</div>
	    </ssHelpSpot>
		</td>
		<c:if test="${ss_windowState != 'maximized'}">
		  <td align="right" width="30" valign="top">
		  <a href="javascript:;" onClick="ss_helpSystem.run();return false;"><img border="0" 
  		    src="<html:imagesPath/>icons/help.png" 
  		    alt="<ssf:nlt tag="navigation.help" text="Help"/>" /></a>
		  </td>
		</c:if>
	</tr>
</table>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
</div>
</c:if>

