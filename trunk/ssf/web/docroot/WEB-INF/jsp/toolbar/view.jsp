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

function ss_showMyTeams<portlet:namespace/>() {
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="show_my_teams" />
    	</ssf:url>"
	url += "\&rn=" + ss_random++;
	
	if (ss_userDisplayStyle == "accessible") {
		ss_fetchUrlInIframe(url, "<portlet:namespace/>ss_myTeams", 300, 400)
		return
	}
	
	var targetDiv = document.getElementById('<portlet:namespace/>ss_myTeams')
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden'
			targetDiv.style.display = 'none'
		} else {
			targetDiv.innerHTML = "<ssf:nlt tag="Loading"/><br/>";
			targetDiv.style.visibility = 'visible';
			targetDiv.style.display = 'block';
			ss_fetch_url(url, ss_showMyTeamsCallback<portlet:namespace/>);
		}
	}
}
function ss_showMyTeamsCallback<portlet:namespace/>(s) {
	var targetDiv = document.getElementById('<portlet:namespace/>ss_myTeams')
	if (targetDiv != null) targetDiv.innerHTML = s;
}

</script>

<table width="100%">
<tr>
<td valign="top" width="50%">
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
<ssHelpSpot helpId="workspace_tree_portlet/workspace_tree_portlet" 
  title="<ssf:nlt tag="helpSpot.workspaceTreePortlet"/>"
  offsetX="-13" offsetY="10">
<div style="padding-top:10px;">
<c:choose>
<c:when test="${renderRequest.windowState == 'normal'}">
	<ssf:tree treeName="<%= wsTreeName %>" 
	  topId="${ssWsDomTreeBinderId}" 
	  treeDocument="<%= ssWsDomTree %>"  
	  rootOpen="true"
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
<td valign="top" width="30%">
<div style="padding-top:10px;">
<a href="javascript: ;" onClick="ss_showMyTeams<portlet:namespace/>();return false;">
  <ssf:nlt tag="navigation.myTeams"/>
</a>
<div id="<portlet:namespace/>ss_myTeams"></div>
</div>
</td>
<td valign="top" width="20%">
<div style="padding-top:10px;">
<c:if test="${empty ss_inlineHelpDivIdNumber}">
  <c:set var="ss_inlineHelpDivIdNumber" value="0" scope="request"/>
</c:if>
<c:set var="ss_inlineHelpDivIdNumber" value="${ss_inlineHelpDivIdNumber + 1}" scope="request"/>
<a href="javascript: ;" 
  onClick="ss_helpSystem.showInlineHelpSpotInfo(this, 'navigation_bar/create_team_button', '', 0, 20, '', '');return false;"
><ssf:nlt tag="navigation.createTeam"/></a>
<div id="<portlet:namespace/>ss_inlineHelpDiv${ss_inlineHelpDivIdNumber}" class="ss_inlineHelp">
<div align="right"><a href="#" 
  onClick="ss_hideDivNone('<portlet:namespace/>ss_inlineHelpDiv${ss_inlineHelpDivIdNumber}');return false;"><img 
  border="0" alt="<ssf:nlt tag="button.close"/>" 
  src="<html:imagesPath/>pics/sym_s_delete.gif"></a></div>
<div id="<portlet:namespace/>ss_inlineHelpDiv${ss_inlineHelpDivIdNumber}"></div>
</div>
</div>
</td>
</tr>
</table>

</div>
</c:if>