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
<% //View the listing part of a wiki folder %>
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />
<%
	boolean useAdaptor = true;
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
		useAdaptor = false;
	}
%>


<div class="ss_blog">

  <div class="ss_blog_content_container1">
    <div class="ss_blog_content_container2">
	  <div class="ss_blog_content">

        <div id="ss_wikiEntryDiv<portlet:namespace/>">
          <iframe id="ss_wikiIframe<portlet:namespace/>" name="ss_wikiIframe<portlet:namespace/>" style="width:100%; 
    		display:block; position:relative;"
    		<c:if test="${empty ss_wikiHomepageEntryId}">
    		  src="<html:rootPath/>js/forum/null.html" 
    		</c:if>
    		<c:if test="${!empty ss_wikiHomepageEntryId || !empty ssEntryIdToBeShown}">
    		  <c:set var="entryId" value="${ss_wikiHomepageEntryId}"/>
    		  <c:if test="${!empty ssEntryIdToBeShown}">
    		    <c:set var="entryId" value="${ssEntryIdToBeShown}"/>
    		  </c:if>
    		  src="<ssf:url     
		    		adapter="<%= useAdaptor %>" 
		    		portletName="ss_forum" 
		    		folderId="${ssFolder.id}" 
		    		action="view_folder_entry" 
		    		entryId="${entryId}" 
		    		actionUrl="true" />" 
    		</c:if>
    		height="95%" width="100%" 
    		onLoad="ss_setWikiIframeSize<portlet:namespace/>();" frameBorder="0" >xxx</iframe>
        </div>
        
     </div>
  </div>
</div>
<div class="ss_blog_sidebar_container">
	  <div class="ss_blog_sidebar">
	  <ssHelpSpot helpId="tools/wiki_controls" offsetX="0" 
	    title="<ssf:nlt tag="helpSpot.wikiControls"/>">

	    <c:if test="${!empty ss_wikiHomepageEntryId}">
	    <span class="ss_bold">
	    <a href="<ssf:url     
		    adapter="<%= useAdaptor %>" 
		    portletName="ss_forum" 
		    folderId="${ssFolder.id}" 
		    action="view_folder_entry" 
		    entryId="${ss_wikiHomepageEntryId}" 
		    actionUrl="true" />" 
		    onClick="ss_loadWikiEntry(this, '${ss_wikiHomepageEntryId}');return false;" 
		><ssf:nlt tag="wiki.homePage"/></a>
	    </span>
	    <br/>
	    <br/>
	    </c:if>

	    <span class="ss_bold"><ssf:nlt tag="wiki.findPage"/></span>
	    <br/>
	    <c:if test="${ssConfigJspStyle != 'template'}">
	    <form method="post" name="ss_findWikiPageForm<portlet:namespace/>"
	    	action="<portlet:actionURL 
	                windowState="maximized" portletMode="view"><portlet:param 
					name="action" value="view_folder_listing"/><portlet:param 
					name="binderId" value="${ssBinder.id}"/></portlet:actionURL>">
		 <ssf:find formName="ss_findWikiPageForm${renderResponse.namespace}" 
		    formElement="searchTitle" 
		    type="entries"
		    width="140px" 
		    binderId="${ssBinder.id}"
		    searchSubFolders="false"
		    singleItem="true"
		    clickRoutine="ss_loadWikiEntryId${renderResponse.namespace}"/> 
	    <input type="hidden" name="searchTitle"/>
	    </form>
		</c:if>
	  <br/>

	  <span class="ss_bold">
	    <c:if test="${ssConfigJspStyle != 'template'}">
	  <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssBinder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_sortTitle"/>
		<portlet:param name="ssFolderSortDescend" value="false"/>
		</portlet:actionURL>"
	  ><ssf:nlt tag="wiki.showAll"/></a>
		</c:if>
	    <c:if test="${ssConfigJspStyle == 'template'}">
	    <ssf:nlt tag="wiki.showAll"/>
		</c:if>
	  </span>
	  <br/>
	  
	  <span class="ss_bold">
	    <c:if test="${ssConfigJspStyle != 'template'}">
	  <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssBinder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_modificationDate"/>
		<portlet:param name="ssFolderSortDescend" value="true"/>
		</portlet:actionURL>"
	  ><ssf:nlt tag="wiki.showRecent"/></a>
		</c:if>
	    <c:if test="${ssConfigJspStyle == 'template'}">
		<ssf:nlt tag="wiki.showRecent"/>
		</c:if>
	  </span>
	  <br/>
	  <br/>

	  <ssf:expandableArea title="<%= NLT.get("wiki.pages") %>" action="wipe" initOpen="true">
		<table cellspacing="0" cellpadding="0">
		  <c:forEach var="entry1" items="${ssFolderEntries}" >
<jsp:useBean id="entry1" type="java.util.HashMap" />
<%
	String folderLineId = "folderLine_" + (String) entry1.get("_docId");
	String seenStyle = "";
	String seenStyleFine = "class=\"ss_finePrint\"";
	if (!ssSeenMap.checkIfSeen(entry1)) {
		seenStyle = "class=\"ss_unseen\"";
		seenStyleFine = "class=\"ss_unseen ss_fineprint\"";
	}
%>
		    <tr><td><div style="margin:0px 4px 4px 8px;">
		    <a 
		    href="<ssf:url     
		    adapter="<%= useAdaptor %>" 
		    portletName="ss_forum" 
		    folderId="${ssFolder.id}" 
		    action="view_folder_entry" 
		    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
		    onClick="ss_loadWikiEntry(this, '${entry1._docId}');return false;" 
		    ><c:if test="${empty entry1.title}"
		    ><span id="folderLine_${entry1._docId}" class="ss_normal"
		      style="margin:-8px;" <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span
		    ></c:if><span id="folderLine_${entry1._docId}" class="ss_normal"
		      style="margin:-8px;" <%= seenStyle %>><c:out value="${entry1.title}"/></span></a>
		    </td></tr>
		  </c:forEach>
		</table>
      </ssf:expandableArea>

	<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.community"/></div>
		
		   <c:if test="${!empty ssFolderEntryCommunityTags}">
		   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
			   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
					name="action" value="view_folder_listing"/><portlet:param 
					name="binderId" value="${ssBinder.id}"/><portlet:param 
					name="cTag" value="${tag.ssTag}"/></portlet:actionURL>" 
					class="ss_displaytag  ${tag.searchResultsRatingCSS} 
					<c:if test="${!empty cTag && cTag == tag.ssTag}">ss_bold</c:if>
					<c:if test="${empty cTag || cTag != tag.ssTag}">ss_normal</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;
		   </c:forEach>
		   </c:if>
		
	<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.personal"/></div>
		   <c:if test="${!empty ssFolderEntryPersonalTags}">
		   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
		   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="view_folder_listing"/><portlet:param 
				name="binderId" value="${ssBinder.id}"/><portlet:param 
				name="pTag" value="${tag.ssTag}"/></portlet:actionURL>" 
				class="ss_displaytag  ${tag.searchResultsRatingCSS} 
				<c:if test="${!empty pTag && pTag == tag.ssTag}">ss_bold</c:if>
				<c:if test="${empty pTag || pTag != tag.ssTag}">ss_normal</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;
						
		   </c:forEach>
		   </c:if>


	  </div>
     </div>


	  <div class="ss_clear_float"></div>
   </div>
   