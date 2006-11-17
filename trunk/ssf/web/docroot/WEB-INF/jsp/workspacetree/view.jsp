<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_portlet_style ss_portlet">

<%
String wsTreeName = renderResponse.getNamespace() + "_wsTree";
%>
<script type="text/javascript">
function <%= wsTreeName %>_showId(id, obj, action) {
	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized">
				<portlet:param name="action" value="ssActionPlaceHolder"/>
				<portlet:param name="binderId" value="ssBinderIdPlaceHolder"/>
				</portlet:renderURL>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
}

</script>

<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
<table width="100%">
	<tr>
	  <td colspan="2">
	    <ssHelpSpot helpId="workspace_tree_portlet/workspace_tree_portlet" 
	      title="<ssf:nlt tag="helpSpot.workspaceTreePortlet"/>"
	      offsetY="-40">
	    </ssHelpSpot>
	  </td>
	</tr>
	<tr>
		<td align="left">
			<div>
			<c:choose>
			<c:when test="${renderRequest.windowState == 'normal'}">
				<ssf:tree treeName="<%= wsTreeName %>" topId="${ssWsDomTreeBinderId}" treeDocument="<%= ssWsDomTree %>"  rootOpen="false" />
			</c:when>
			<c:when test="${renderRequest.windowState == 'maximized'}">
				<ssf:tree treeName="<%= wsTreeName %>" topId="${ssWsDomTreeBinderId}" treeDocument="<%= ssWsDomTree %>"  rootOpen="true" />
			</c:when>
			</c:choose>			

			</div>
		</td>
		<td align="right" width="30" valign="top">
		<a href="#" onClick="ss_helpSystem.run();return false;"><img border="0" 
  		  src="<html:imagesPath/>icons/help.png" 
  		  alt="<ssf:nlt tag="navigation.help" text="Help"/>" /></a>
		</td>
	</tr>
</table>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
</div>
