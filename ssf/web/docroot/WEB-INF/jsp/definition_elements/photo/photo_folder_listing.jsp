<% //View the listing part of a photo folder %>
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
	  <ssf:expandableArea title="<%= NLT.get("photo.findPage") %>" action="wipe" initOpen="false">
	    <form method="post" 
	    	action="<portlet:actionURL 
	                windowState="maximized" portletMode="view"><portlet:param 
					name="action" value="view_folder_listing"/><portlet:param 
					name="binderId" value="${ssBinder.id}"/></portlet:actionURL>">
	     <input type="text" size="20" name="searchTitle"/>
	     <input type="submit" name="findPage" value="<ssf:nlt tag="button.ok"/>"/>
	    </form>
	  </ssf:expandableArea>
	  <br/>
	  <ssf:expandableArea title="<%= NLT.get("photo.pages") %>" action="wipe" initOpen="true">
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
		    onClick="ss_loadEntry(this, '${entry1._docId}');return false;" 
		    ><c:if test="${empty entry1.title}"
		    ><span id="folderLine_${entry1._docId}" style="margin:-8px;" <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span
		    ></c:if><span id="folderLine_${entry1._docId}" style="margin:-8px;" <%= seenStyle %>><c:out value="${entry1.title}"/></span></a>
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
					<c:if test="${!empty cTag && cTag == tag.ssTag}">ss_bold</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;
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
				<c:if test="${!empty pTag && pTag == tag.ssTag}">ss_bold</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;
						
		   </c:forEach>
		   </c:if>
      </ssf:expandableArea>
	  </td>
      <td class="ss_photo_content" width="80%" valign="top">
        <div id="ss_photoEntryDiv<portlet:namespace/>">
          <iframe id="ss_photoIframe<portlet:namespace/>" name="ss_photoIframe<portlet:namespace/>" style="width:100%; 
    		display:block; position:relative; left:5px;"
    		src="<html:rootPath/>js/forum/null.html" height="95%" width="100%" 
    		onLoad="ss_setPhotoIframeSize<portlet:namespace/>();" frameBorder="0" >xxx</iframe>
        </div>
	  </td>
    </tr>
  </table>
