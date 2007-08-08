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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<% //Blog title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="title_entry" value="${ssDefinitionEntry}"/>

<c:if test="${empty ss_namespace}">
	<c:set var="ss_namespace" value="${renderResponse.namespace}" />
</c:if>

<jsp:useBean id="title_entry" type="com.sitescape.team.domain.FolderEntry" />

<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<%
	//Get a dispalyable number for the replies
	String docNumber = "";
	String fontSize = "ss_largeprint";
	if (title_entry.getTopEntry() != null) fontSize = "ss_largerprint";
	if (title_entry.getTopEntry() != null && title_entry.getDocNumber() != null) {
		docNumber = title_entry.getDocNumber();
		int i = docNumber.indexOf(".");
		if (i > 0) {
			docNumber = docNumber.subSequence(i+1, docNumber.length()) + ". ";
		}
	}
%>

<div class="ss_blog_title">
<div class="ss_header_bar_timestamp">
<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${ssDefinitionEntry.creation.date}" type="both" 
	 timeStyle="short" dateStyle="medium" />
 <ssf:nlt tag="general.title.timestamp.by"/> <a 
 	href="<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
			value="${ssDefinitionEntry.creation.principal.parentBinder.id}"/><ssf:param name="entryId" 
			value="${ssDefinitionEntry.creation.principal.id}"/>
	    <ssf:param name="newTab" value="1" />
		</ssf:url>"
 ><ssf:showUser user="${ssDefinitionEntry.creation.principal}"/></a>
</div>
<%
	if (!ssSeenMap.checkIfSeen(title_entry)) {
		%><img border="0" <ssf:alt tag="alt.unseen"/> src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%>
<div class="ss_header_bar_title_text">
<span class="ss_header_bar_title_text">
	<%= docNumber %>
	<ssf:menuLink displayDiv="false" action="view_folder_entry" adapter="true" entryId="${ssDefinitionEntry.id}" 
	binderId="${ssDefinitionEntry.parentFolder.id}" entityType="${ssDefinitionEntry.entityType}"
	imageId='menuimg_${ssDefinitionEntry.id}_${ss_namespace}_${ssDefinitionEntry.id}' 
    menuDivId="ss_emd_${ss_namespace}_${ssDefinitionEntry.id}"
	linkMenuObjIdx="${ss_namespace}_${ssDefinitionEntry.id}" 
	namespace="${ss_namespace}_${ssDefinitionEntry.id}"
	entryCallbackRoutine="${showEntryCallbackRoutine}">

		<ssf:param name="url" useBody="true">
			<ssf:url adapter="true" portletName="ss_forum" folderId="${ssDefinitionEntry.parentFolder.id}" 
			action="view_folder_entry" entryId="${ssDefinitionEntry.id}" actionUrl="true" />
		</ssf:param>

		<c:if test="${empty ssDefinitionEntry.title}">
		  <span class="ss_light">
		    --<ssf:nlt tag="entry.noTitle"/>--
		  </span>
		</c:if>

		<c:out value="${ssDefinitionEntry.title}"/>
	</ssf:menuLink>

</span>
</div>
</div>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<%-- Subscribe, Ratings bar, visits --%>
<c:if test="${empty ssDefinitionEntry.parentEntry}">
<div style="padding-left: 22px">
<%@ include file="/WEB-INF/jsp/definition_elements/popular_view.jsp" %>
</div>

<c:set var="entryIdString" value="<%= title_entry.getId().toString() %>"/>

</c:if>

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${ss_namespace}_${ssDefinitionEntry.id}" 
	linkMenuObjIdx="${ss_namespace}_${ssDefinitionEntry.id}" 
	namespace="${ss_namespace}_${ssDefinitionEntry.id}">
</ssf:menuLink>