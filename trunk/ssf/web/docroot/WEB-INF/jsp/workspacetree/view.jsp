<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:if test="${empty ss_portletInitialization}">
<div class="ss_portlet_style ss_portlet">

<c:if test="${ss_windowState == 'maximized'}">
<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />
</c:if>

<%
String wsTreeName = renderResponse.getNamespace() + "_wsTree";
%>
<script type="text/javascript">
function <%= wsTreeName %>_showId(id, obj, action) {
	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
			name="action" value="ssActionPlaceHolder"/><portlet:param 
			name="binderId" value="ssBinderIdPlaceHolder"/></portlet:renderURL>"
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
	    <ssHelpSpot helpId="workspace_tree_portlet/workspace_tree_portlet" offsetX="-13" 
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
		  <a href="#" onClick="ss_helpSystem.run();return false;"><img border="0" 
  		    src="<html:imagesPath/>icons/help.png" 
  		    alt="<ssf:nlt tag="navigation.help" text="Help"/>" /></a>
		  </td>
		</c:if>
	</tr>
</table>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
</div>
</c:if>

