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
<ssf:ifadapter>
<body>
</ssf:ifadapter>
<c:set var="wsTreeName" value="${renderResponse.namespace}_wsTree"/>
<script type="text/javascript">
function ${wsTreeName}_showId(id, obj, action) {
	var formObj = ss_getContainingForm(obj);
	var r = formObj.destination;
	for (var i = 0; i < r.length; i++) {
		r[i].checked = false;
		if (r[i].value == id) {
			r[i].checked = true;
		}
	}
	return false;
}
</script>

<div class="ss_style ss_portlet">
<div style="padding:4px;">
<span class="ss_bold ss_largerprint"><ssf:nlt tag="move.entry"/></span>
<br/>
<br/>
<span><ssf:nlt tag="move.currentEntry"/>: </span>
<span><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>
  //
<span class="ss_bold">${ssEntry.title}</span>
  
<br/>
<form class="ss_style ss_form" method="post" 
	action="<ssf:url
	action="modify_folder_entry"
	operation="move"
	folderId="${ssBinder.id}"
	entryId="${ssEntry.id}"/>" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm">
<br/>

<span class="ss_bold"><ssf:nlt tag="move.selectDestination"/></span>
<br/>
<div class="ss_indent_large">
<ssf:tree treeName="${wsTreeName}"
	treeDocument="${ssWsDomTree}"  
 	rootOpen="true"
	singleSelect="${ssDefaultSaveLocationId}" 
	singleSelectName="destination" />
</div>

<br/>

<!-- Displays the current save location, if one has already been
	specified in current user session-->

<span class="ss_bold"><ssf:nlt tag="move.currentLocation" /></span>
<br/>

<% // similar to Navigation links %>

<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<div class="ss_breadcrumb">
  <ssHelpSpot helpId="workspaces_folders/misc_tools/breadcrumbs" offsetX="0" 
  <c:if test="<%= !isIE %>">
   offsetY="4"
  </c:if>
  <c:if test="<%= isIE %>">
   offsetY="2" xAlignment="center"
  </c:if>
    title="<ssf:nlt tag="helpSpot.breadCrumbs"/>"></ssHelpSpot>

<ul style="margin-left:-15px;">
<c:if test="${!empty ssDefaultSaveLocation.parentBinder}">
<c:set var="parentBinder" value="${ssDefaultSaveLocation.parentBinder}"/>
<jsp:useBean id="parentBinder" type="java.lang.Object" />
<%
	Stack parentTree = new Stack();
	while (parentBinder != null) {
		//if (((Binder)parentBinder).getEntityType().equals(com.sitescape.team.domain.EntityIdentifier.EntityType.profiles)) break;
		parentTree.push(parentBinder);
		parentBinder = ((Binder)parentBinder).getParentBinder();
	}
	while (!parentTree.empty()) {
		Binder nextBinder = (Binder) parentTree.pop();
%>
<c:set var="nextBinder" value="<%= nextBinder %>"/>
<br style="float:left;">
<c:if test="${empty ssNavigationLinkTree[nextBinder.id]}">
<c:if test="${empty nextBinder.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${nextBinder.title}" /></a>
</c:if>
<c:if test="${!empty ssNavigationLinkTree[nextBinder.id]}">
<div style="display:inline">
<ssf:tree treeName="${ss_breadcrumbsTreeName}${nextBinder.id}${renderResponse.namespace}" treeDocument="${ssNavigationLinkTree[nextBinder.id]}" 
  topId="${nextBinder.id}" rootOpen="false" showImages="false" showIdRoutine="${ss_breadcrumbsShowIdRoutine}" />
</div>
</c:if>
</br>
<br style="float:left; padding-top:2px;">&nbsp;&nbsp;//&nbsp;&nbsp;</li>
<%
	}
%>
</c:if>
<br style="float:left;">
<c:if test="${ssDefaultSaveLocation.entityType == 'folderEntry' || empty ssNavigationLinkTree[ssDefaultSaveLocation.id]}">
<c:if test="${empty ssDefaultSaveLocation.title}" >
--<ssf:nlt tag="move.locationUnspecified" />--
</c:if>
<c:out value="${ssDefaultSaveLocation.title}" /><img border="0" <ssf:alt/>
  style="width:1px;height:14px;" src="<html:imagesPath/>pics/1pix.gif"/></a>
</c:if>
<c:if test="${ssDefaultSaveLocation.entityType != 'folderEntry' && !empty ssNavigationLinkTree[ssDefaultSaveLocation.id]}">
<div style="display:inline">
<ssf:tree treeName="${ss_breadcrumbsTreeName}${ssDefaultSaveLocation.id}${renderResponse.namespace}" 
  treeDocument="${ssNavigationLinkTree[ssDefaultSaveLocation.id]}" 
  topId="${ssDefaultSaveLocation.id}" rootOpen="false" 
  showImages="false" showIdRoutine="${ss_breadcrumbsShowIdRoutine}" 
  highlightNode="${ssDefaultSaveLocation.id}" />
</div>
</c:if>
</br>
</ul>
</div>
<div class="ss_clear"></div>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</form>
</div>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
