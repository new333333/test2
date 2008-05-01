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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<% //View the listing part of a wiki folder %>
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />
<%
	boolean useAdaptor = true;
	if (ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(ssUser.getDisplayStyle()) &&
			!ObjectKeys.GUEST_USER_INTERNALID.equals(ssUser.getInternalId())) {
		useAdaptor = false;
	}

%>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<ssf:sidebarPanel title="__definition_default_folder_wiki" id="ss_folder_sidebar" divClass="ss_blog_sidebar"
    initOpen="true" sticky="true">
   
	  <ssHelpSpot helpId="workspaces_folders/misc_tools/wiki_controls" offsetX="0" 
	    title="<ssf:nlt tag="helpSpot.wikiControls"/>"></ssHelpSpot>

	    <c:if test="${!empty ss_wikiHomepageEntryId}">
	    <a class="ss_linkButton" href="<ssf:url     
		    adapter="<%= useAdaptor %>" 
		    portletName="ss_forum" 
		    folderId="${ssFolder.id}" 
		    action="view_folder_entry" 
		    entryId="${ss_wikiHomepageEntryId}" 
		    actionUrl="true"><ssf:param
		    name="namespace" value="${renderResponse.namespace}"/><ssf:ifaccessible><ssf:param name="newTab" value="1" /></ssf:ifaccessible></ssf:url>" 

		    <ssf:title tag="title.open.folderEntrySimple" />
		    
		    <ssf:ifnotaccessible>
		    	onClick="ss_loadWikiEntry(this, '${ss_wikiHomepageEntryId}');return false;" 
		    </ssf:ifnotaccessible>
		    
		    <ssf:ifaccessible>
		    	onClick="ss_loadWikiEntryInParent(this, '${ss_wikiHomepageEntryId}');return false;" 		    	
		    </ssf:ifaccessible>
		    
		><ssf:nlt tag="wiki.homePage"/></a>
	    <br/>
	    </c:if>

        <div class="ss_blog_sidebar_subhead"><ssf:nlt tag="wiki.findPage"/></div>
	    <c:if test="${ssConfigJspStyle != 'template'}">
	    <form method="post" name="ss_findWikiPageForm<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>"
	    	action="<ssf:url action="view_folder_listing" actionUrl="true"><ssf:param 
					name="binderId" value="${ssBinder.id}"/></ssf:url>">
		 <ssf:find formName="ss_findWikiPageForm${renderResponse.namespace}" 
		    formElement="searchTitle" 
		    type="entries"
		    width="140px" 
		    binderId="${ssBinder.id}"
		    searchSubFolders="false"
		    singleItem="true"
		    clickRoutine="ss_loadWikiEntryId${renderResponse.namespace}"
		    accessibilityText="wiki.findPage"
		    /> 
	    <input type="hidden" name="searchTitle"/>
	    </form>
		</c:if>
	  <br/>


	  <ssf:expandableArea title="<%= NLT.get("wiki.pages") %>" titleClass="ss_blog_sidebar_subhead" action="wipe" initOpen="true">
	  <div class="ss_blog_sidebar_box">
       <table cellspacing="0" cellpadding="0">
		  <c:forEach var="entry1" items="${ssFolderEntries}" >
<jsp:useBean id="entry1" type="java.util.HashMap" />
<%
	String folderLineId = "folderLine_" + (String) entry1.get("_docId");
	String seenStyle = "";
	String seenStyleFine = "ss_finePrint";
	if (!ssSeenMap.checkIfSeen(entry1)) {
		seenStyle = "ss_unseen";
		seenStyleFine = "ss_unseen ss_fineprint";
	}
%>
		    <tr><td><div style="padding:0px 4px 4px 8px;">
		    <a 
		    href="<ssf:url     
		    adapter="<%= useAdaptor %>" 
		    portletName="ss_forum" 
		    folderId="${ssFolder.id}" 
		    action="view_folder_entry" 
		    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true"><ssf:param
		    name="namespace" value="${renderResponse.namespace}"/><ssf:ifaccessible><ssf:param name="newTab" value="1" /></ssf:ifaccessible></ssf:url>" 

		    <ssf:title tag="title.open.folderEntry">
			    <ssf:param name="value" useBody="true"><c:choose><c:when test="${!empty entry1.title}">${entry1.title}</c:when><c:otherwise>--<ssf:nlt tag="entry.noTitle"/>--</c:otherwise></c:choose></ssf:param>
		    </ssf:title>

		    <ssf:ifnotaccessible>
		    	onClick="ss_loadWikiEntry(this, '${entry1._docId}');return false;" 		    	
		    </ssf:ifnotaccessible>
		    
		    <ssf:ifaccessible>
			    onClick="ss_loadWikiEntryInParent(this, '${entry1._docId}');return false;" 
		    </ssf:ifaccessible>
		    
		    ><c:if test="${empty entry1.title}"
		    ><span id="folderLine_${entry1._docId}" class="ss_smallprint <%= seenStyleFine %>"
		      >--<ssf:nlt tag="entry.noTitle"/>--</span
		    ></c:if><span id="folderLine_${entry1._docId}" class="ss_smallprint <%= seenStyle %>"
		      ><c:out value="${entry1.title}"/></span></a>
		    </td></tr>
		  </c:forEach>
		</table>
		<table cellspacing="0" cellpadding="0" width="100%">
		<tr>
		<td width="50%">
		<c:choose>
		  <c:when test="${ssPagePrevious.ssPageNoLink == 'true'}"></c:when>
		  <c:otherwise>
			<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="operation" value="save_folder_page_info"/><ssf:param 
				name="binderId" value="${ssFolder.id}"/><ssf:param 
				name="ssPageStartIndex" value="${ssPagePrevious.ssPageInternalValue}"/><c:if test="${!empty cTag}"><ssf:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
				name="endDate" value="${endDate}"/></c:if></ssf:url>" 
				<ssf:title tag="title.goto.prev.page" /> >&lt;&lt;
			</a>
		  </c:otherwise>
		</c:choose>
		</td><td align="right" width="50%">
		<c:choose>
		  <c:when test="${ssPageNext.ssPageNoLink == 'true'}">
			
		  </c:when>
		  <c:otherwise>
			<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="operation" value="save_folder_page_info"/><ssf:param 
				name="binderId" value="${ssFolder.id}"/><ssf:param 
				name="ssPageStartIndex" value="${ssPageNext.ssPageInternalValue}"/><c:if test="${!empty cTag}"><ssf:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
				name="endDate" value="${endDate}"/></c:if></ssf:url>" 
				<ssf:title tag="title.goto.next.page" />>&gt;&gt;
			</a>
		  </c:otherwise>
		</c:choose>
		</td>
		</tr>
		</table>
       </div>
      </ssf:expandableArea>

    <c:if test="${!empty ssFolderEntryCommunityTags}"> 
	<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.community"/></div>
    <div class="ss_blog_sidebar_box">		
		   <c:if test="${!empty ssFolderEntryCommunityTags}">
		   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
			   	<a href="<ssf:url action="view_folder_listing" actionUrl="true"><ssf:param 
					name="binderId" value="${ssBinder.id}"/><ssf:param 
					name="cTag" value="${tag.ssTag}"/></ssf:url>" 
					class="ss_displaytag  ${tag.searchResultsRatingCSS} 
					<c:if test="${!empty cTag && cTag == tag.ssTag}">ss_bold</c:if>
					<c:if test="${empty cTag || cTag != tag.ssTag}">ss_normal</c:if>"
					  <ssf:title tag="title.search.entries.in.folder.for.community.tag">
					  	<ssf:param name="value" value="${tag.ssTag}" />
					  </ssf:title>
					>${tag.ssTag}</a>&nbsp;&nbsp;
		   </c:forEach>
		   </c:if>
    </div>
    </c:if>
    <c:if test="${!empty ssFolderEntryPersonalTags}"> 
	<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.personal"/></div>
    <div class="ss_blog_sidebar_box">		
		   <c:if test="${!empty ssFolderEntryPersonalTags}">
		   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
		   	<a href="<ssf:url action="view_folder_listing" actionUrl="true"><ssf:param 
				name="binderId" value="${ssBinder.id}"/><ssf:param 
				name="pTag" value="${tag.ssTag}"/></ssf:url>" 
				class="ss_displaytag  ${tag.searchResultsRatingCSS} 
				<c:if test="${!empty pTag && pTag == tag.ssTag}">ss_bold</c:if>
				<c:if test="${empty pTag || pTag != tag.ssTag}">ss_normal</c:if>"
				  <ssf:title tag="title.search.entries.in.folder.for.personal.tag">
				  	<ssf:param name="value" value="${tag.ssTag}" />
				  </ssf:title>
				>${tag.ssTag}</a>&nbsp;&nbsp;
						
		   </c:forEach>
		   </c:if>
    </div>		
	</c:if>
		
</ssf:sidebarPanel>
<%@ include file="/WEB-INF/jsp/sidebars/folder_tags.jsp" %>
   