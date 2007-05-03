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
<% //View the listing part of a photo folder %>

  <div class="ss_blog">

      <div class="ss_blog_content_container1">
      <div class="ss_blog_content_container2">
        <div class="ss_blog_content">
			<table><tr><td>
			<div class="ss_thumbnail_gallery ss_thumbnail_medium"> 
			<c:forEach var="fileEntry" items="${ssFolderEntries}" >
<jsp:useBean id="fileEntry" type="java.util.HashMap" />
<%
	String folderLineId = "folderLine_" + (String) fileEntry.get("_docId");
	String seenStyle = "";
	String seenStyleFine = "class=\"ss_finePrint\"";
	if (!ssSeenMap.checkIfSeen(fileEntry)) {
		seenStyle = "class=\"ss_unseen\"";
		seenStyleFine = "class=\"ss_unseen ss_fineprint\"";
	}
%>
			  <c:if test="${not empty fileEntry._fileID}">
			
			    <div>
			    <a href="<ssf:url 
				    webPath="viewFile"
				    folderId="${fileEntry._binderId}"
				    entryId="${fileEntry._docId}" >
				    <ssf:param name="fileId" value="${fileEntry._fileID}"/>
				    </ssf:url>"
					onClick="return ss_openUrlInWindow(this, '_blank');">
			    <img <ssf:alt text="${fileEntry.title}"/> border="0" src="<ssf:url 
			    webPath="viewFile"
			    folderId="${fileEntry._binderId}"
			    entryId="${fileEntry._docId}" >
			    <ssf:param name="fileId" value="${fileEntry._fileID}"/>
			    <ssf:param name="viewType" value="thumbnail"/>
			    </ssf:url>"></a><br\>
			    <a 
				    href="<ssf:url     
				    adapter="true" 
				    portletName="ss_forum" 
				    folderId="${ssFolder.id}" 
				    action="view_folder_entry" 
				    entryId="${fileEntry._docId}" actionUrl="true" />" 
				    onClick="ss_loadEntry(this, '${fileEntry._docId}');return false;" 
				    ><c:if test="${empty fileEntry.title}"
				    ><span id="folderLine_${fileEntry._docId}" <%= seenStyleFine %>
				    >--<ssf:nlt tag="entry.noTitle"/>--</span
				    ></c:if><span id="folderLine_${fileEntry._docId}" <%= seenStyle %>
				    ><c:out value="${fileEntry.title}"/></span></a>
			    </div>
			 </c:if>
			
			</c:forEach>
			</div>
			</table>



	    </div>

	  </div>
	  </div>

     <div class="ss_blog_sidebar_container">
	  <div class="ss_blog_sidebar">
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="photo.findPage"/></div>
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
		    clickRoutine="ss_loadPhotoEntryId${renderResponse.namespace}"/> 
	    <input type="hidden" name="searchTitle"/>
	    </form>
	    <br/>
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="blog.archives"/></div>
		<table>
		<c:forEach var="monthYear" items="${ssBlogMonthHits}">
		  <tr>
		  <td><a href="${ssBlogMonthUrls[monthYear.key]}" 
		  class="<c:if test="${!empty selectedYearMonth && selectedYearMonth == ssBlogMonthTitles[monthYear.key]}">ss_bold</c:if>">
		  <c:out value="${ssBlogMonthTitles[monthYear.key]}"/></a></td>
		  <td align="right">(<c:out value="${monthYear.value}"/>)</td>
		  </tr>
		</c:forEach>
		</table>
		
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.community"/></div>
		   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
			   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
					name="action" value="view_folder_listing"/><portlet:param 
					name="binderId" value="${ssBinder.id}"/><portlet:param 
					name="cTag" value="${tag.ssTag}"/></portlet:actionURL>" 
					class="ss_displaytag ${tag.searchResultsRatingCSS} <c:if test="${!empty cTag && cTag == tag.ssTag}">ss_bold</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;
		   </c:forEach>
		
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.personal"/></div>

		   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
		
		   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="view_folder_listing"/><portlet:param 
				name="binderId" value="${ssBinder.id}"/><portlet:param 
				name="pTag" value="${tag.ssTag}"/></portlet:actionURL>" 
				class="ss_displaytag ${tag.searchResultsRatingCSS} <c:if test="${!empty pTag && pTag == tag.ssTag}">ss_bold</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;
						
		   </c:forEach>
	  </div>
     </div>


	  <div class="ss_clear_float"></div>
   </div>
