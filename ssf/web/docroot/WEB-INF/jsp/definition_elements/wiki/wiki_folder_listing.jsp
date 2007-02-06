<% //View the listing part of a wiki folder %>
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />
<%
	boolean useAdaptor = true;
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
		useAdaptor = false;
	}
%>

  <table class="ss_blog" width="100%">
    <tr>
	  <td class="ss_blog_sidebar" width="20%" valign="top">
	    <c:if test="${!empty ss_wikiHomepageEntryId}">
	    <span class="ss_bold">
	    <a href="<ssf:url     
		    adapter="<%= useAdaptor %>" 
		    portletName="ss_forum" 
		    folderId="${ssFolder.id}" 
		    action="view_folder_entry" 
		    entryId="${ss_wikiHomepageEntryId}" 
		    actionUrl="true" />" 
		    onClick="ss_loadEntry(this, '${ss_wikiHomepageEntryId}');return false;" 
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
		    width="70px" 
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
		seenStyle = "class=\"ss_bold\"";
		seenStyleFine = "class=\"ss_bold ss_fineprint\"";
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
		    onClick="ss_loadEntry(this, '${entry1._docId}');return false;" 
		    ><c:if test="${empty entry1.title}"
		    ><span id="folderLine_${entry1._docId}" class="ss_normal"
		      style="margin:-8px;" <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span
		    ></c:if><span id="folderLine_${entry1._docId}" class="ss_normal"
		      style="margin:-8px;" <%= seenStyle %>><c:out value="${entry1.title}"/></span></a>
		    </td></tr>
		  </c:forEach>
		</table>
      </ssf:expandableArea>
	  <br/>
		
	  <ssf:expandableArea title="<%= NLT.get("tags.community") %>" action="wipe" initOpen="true">
		   <c:if test="${!empty ssFolderEntryCommunityTags}">
		   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
			   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
					name="action" value="view_folder_listing"/><portlet:param 
					name="binderId" value="${ssBinder.id}"/><portlet:param 
					name="cTag" value="${tag.ssTag}"/></portlet:actionURL>" 
					class="${tag.searchResultsRatingCSS} 
					<c:if test="${!empty cTag && cTag == tag.ssTag}">ss_bold</c:if>
					<c:if test="${empty cTag || cTag != tag.ssTag}">ss_normal</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;
		   </c:forEach>
		   </c:if>
      </ssf:expandableArea>
		
      <br/>
	  <ssf:expandableArea title="<%= NLT.get("tags.personal") %>" action="wipe" initOpen="true">
		   <c:if test="${!empty ssFolderEntryPersonalTags}">
		   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
		   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="view_folder_listing"/><portlet:param 
				name="binderId" value="${ssBinder.id}"/><portlet:param 
				name="pTag" value="${tag.ssTag}"/></portlet:actionURL>" 
				class="${tag.searchResultsRatingCSS} 
				<c:if test="${!empty pTag && pTag == tag.ssTag}">ss_bold</c:if>
				<c:if test="${empty pTag || pTag != tag.ssTag}">ss_normal</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;
						
		   </c:forEach>
		   </c:if>
      </ssf:expandableArea>
	  </td>
      <td class="ss_wiki_content" width="80%" valign="top">
        <div id="ss_wikiEntryDiv<portlet:namespace/>">
          <iframe id="ss_wikiIframe<portlet:namespace/>" name="ss_wikiIframe<portlet:namespace/>" style="width:100%; 
    		display:block; position:relative; left:5px;"
    		src="<html:rootPath/>js/forum/null.html" height="95%" width="100%" 
    		onLoad="ss_setWikiIframeSize<portlet:namespace/>();" frameBorder="0" >xxx</iframe>
        </div>
	  </td>
    </tr>
  </table>
