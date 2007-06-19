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
	    title="<ssf:nlt tag="helpSpot.wikiControls"/>"></ssHelpSpot>

	    <c:if test="${!empty ss_wikiHomepageEntryId}">
	    <a class="ss_linkButton" href="<ssf:url     
		    adapter="<%= useAdaptor %>" 
		    portletName="ss_forum" 
		    folderId="${ssFolder.id}" 
		    action="view_folder_entry" 
		    entryId="${ss_wikiHomepageEntryId}" 
		    actionUrl="true"><ssf:ifaccessible><ssf:param name="newTab" value="1" /></ssf:ifaccessible></ssf:url>" 

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
        <div class="ss_blog_sidebar_box">		
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
		    clickRoutine="ss_loadWikiEntryId${renderResponse.namespace}"
		    accessibilityText="wiki.findPage"
		    /> 
	    <input type="hidden" name="searchTitle"/>
	    </form>
		</c:if>
		</div>
	  <br/>


	  <ssf:expandableArea title="<%= NLT.get("wiki.pages") %>" titleClass="ss_blog_sidebar_subhead" action="wipe" initOpen="true">
	  <div class="ss_blog_sidebar_box">
       <div style="padding-bottom: 10px;">
	    <c:if test="${ssConfigJspStyle != 'template'}">
	  <a class="ss_linkButton" href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssBinder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_sortTitle"/>
		<portlet:param name="ssFolderSortDescend" value="false"/>
		</portlet:actionURL>" <ssf:title tag="wiki.showAll" />
	  ><ssf:nlt tag="wiki.showAll"/></a>
		</c:if>
	  
	    <c:if test="${ssConfigJspStyle != 'template'}">
	  <a class="ss_linkButton" href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssBinder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_modificationDate"/>
		<portlet:param name="ssFolderSortDescend" value="true"/>
		</portlet:actionURL>" <ssf:title tag="wiki.showRecent" />
	  ><ssf:nlt tag="wiki.showRecent"/></a>
		</c:if>
       </div>
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
		    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true"><ssf:ifaccessible><ssf:param name="newTab" value="1" /></ssf:ifaccessible></ssf:url>" 

		    <ssf:title tag="title.open.folderEntry">
			    <ssf:param name="value" value="${entry1.title}" />
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
       </div>
      </ssf:expandableArea>

	<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.community"/></div>
    <div class="ss_blog_sidebar_box">		
		   <c:if test="${!empty ssFolderEntryCommunityTags}">
		   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
			   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
					name="action" value="view_folder_listing"/><portlet:param 
					name="binderId" value="${ssBinder.id}"/><portlet:param 
					name="cTag" value="${tag.ssTag}"/></portlet:actionURL>" 
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
	<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.personal"/></div>
    <div class="ss_blog_sidebar_box">		
		   <c:if test="${!empty ssFolderEntryPersonalTags}">
		   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
		   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="view_folder_listing"/><portlet:param 
				name="binderId" value="${ssBinder.id}"/><portlet:param 
				name="pTag" value="${tag.ssTag}"/></portlet:actionURL>" 
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


   </div>
  </div>
  <div class="ss_clear_float">
 </div>
</div>
   