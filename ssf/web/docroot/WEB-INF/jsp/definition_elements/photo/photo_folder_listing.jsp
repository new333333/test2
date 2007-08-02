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

			<table width="99%"><tr><td>
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
<%
	String _fileId = fileEntry.get("_fileID").toString();
	if (_fileId.contains(",")) _fileId = _fileId.substring(0, _fileId.indexOf(","));
	String _fileTime = "";
	if (fileEntry.containsKey("_fileTime")) _fileTime = fileEntry.get("_fileTime").toString();
	if (_fileTime.contains(",")) _fileTime = _fileTime.substring(0, _fileTime.indexOf(","));
%>			
			    <div>
			    <a onMouseOver="ss_showHoverOver(this, 'ss_photoTitle_${fileEntry._docId}')" 
			      onMouseOut="ss_hideHoverOver(this, 'ss_photoTitle_${fileEntry._docId}')"
			      href="<ssf:url 
				    webPath="viewFile"
				    folderId="${fileEntry._binderId}"
				    entryId="${fileEntry._docId}" >
	    			<ssf:param name="entityType" value="${fileEntry._entityType}"/>
				    <ssf:param name="fileId" value="<%= _fileId %>"/>
				    <ssf:param name="fileTime" value="<%= _fileTime %>"/>
				    </ssf:url>"
					onClick="return ss_openUrlInWindow(this, '_blank');">
			    <img <ssf:alt text="${fileEntry.title}"/> border="0" src="<ssf:url 
			    webPath="viewFile"
			    folderId="${fileEntry._binderId}"
			    entryId="${fileEntry._docId}" >
	    		<ssf:param name="entityType" value="${fileEntry._entityType}"/>
			    <ssf:param name="fileId" value="<%= _fileId %>"/>
			    <ssf:param name="fileTime" value="<%= _fileTime %>"/>
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
			
			  <c:if test="${empty fileEntry._fileID}">
			
			    <div>
			    <img <ssf:alt text="${fileEntry.title}"/> border="0" 
			      src="<html:imagesPath/>thumbnails/NoImage.jpeg"/><br/>
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

<c:forEach var="fileEntry" items="${ssFolderEntries}" >
  <div id="ss_photoTitle_${fileEntry._docId}" class="ss_hover_over" 
    style="visibility:hidden; display:none;">
    <c:if test="${empty fileEntry.title}"
      ><span 
      >--<ssf:nlt tag="entry.noTitle"/>--</span
      ></c:if><span id="folderLine_${fileEntry._docId}"
      ><c:out value="${fileEntry.title}"/></span><br/>
    <span >${fileEntry._desc}</span>
  </div>
</c:forEach>
   