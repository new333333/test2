<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% //View the listing part of a photo folder %>
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request"/>
<script type="text/javascript">
var ss_showingFolder = true;
</script>

			<table width="99%"><tr><td>
			<div class="ss_thumbnail_gallery ss_thumbnail_medium round"> 
			<c:forEach var="fileEntry" items="${ssFolderEntries}" >
<jsp:useBean id="fileEntry" type="java.util.HashMap" />

<%
boolean useAdaptor = true;
if (ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(ssUser.getDisplayStyle()) &&
		!ObjectKeys.GUEST_USER_INTERNALID.equals(ssUser.getInternalId())) {
	useAdaptor = false;
}	
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
			    <a href="<ssf:url crawlable="true"    
				    adapter="true" 
				    portletName="ss_forum" 
				    binderId="${ssBinder.id}" 
				    action="view_folder_entry" 
				    entryId="${fileEntry._docId}" 
				    operation="view_photo"
				    actionUrl="true" 
				    ><ssf:param name="standalone" value="true"/>
				    </ssf:url>"
					onClick="return ss_openUrlInWindow(this, '_blank');">
			    <img <ssf:alt text="${fileEntry.title}"/> border="0" 
			      onMouseOver="ss_showHoverOver(this, 'ss_photoTitle_${fileEntry._docId}', null, -10, 24)" 
			      onMouseOut="ss_hideHoverOver('ss_photoTitle_${fileEntry._docId}')"
			      src="<ssf:fileUrl webPath="readThumbnail" search="${fileEntry}"/>"></a><br/>
			    
			    <% if (!ssSeenMap.checkIfSeen(fileEntry)) { %>
								    
				 <a id="ss_sunburstDiv${fileEntry._binderId}_${fileEntry._docId}" href="javascript: ;" 
				 title="<ssf:nlt tag="sunburst.click"/>"
				 onClick="ss_hideSunburst('${fileEntry._docId}', '${fileEntry._binderId}');return false;"
				><span 
				  style="display:${ss_sunburstVisibilityHide};"
				  id="ss_sunburstShow${renderResponse.namespace}" 
				  class="ss_fineprint">
				  	<img src="<html:rootPath/>images/pics/discussion/sunburst.png" align="absmiddle" border="0"
				  		style="width:12px;height:12px;" <ssf:alt tag="alt.new"/> />
				  </span>
				  </a>
					    
				<% } %>			    
			    
			    <a 
				    href="<ssf:url crawlable="true"    
				    adapter="true" 
				    portletName="ss_forum" 
				    binderId="${ssBinder.id}" 
				    action="view_folder_entry" 
				    entryId="${fileEntry._docId}" actionUrl="true" />" 
				    onClick="ss_loadEntry(this, '${fileEntry._docId}', '${ssBinder.id}', '${fileEntry._entityType}', '${renderResponse.namespace}', 'no');return false;" 
				    title="<c:out value="${fileEntry.title}"/>"
			    ><c:if test="${empty fileEntry.title}"
			    ><span id="folderLineSeen_${fileEntry._docId}" <%= seenStyleFine %>
			    >--<ssf:nlt tag="entry.noTitle"/>--</span
			    ></c:if><span id="folderLineSeen_${fileEntry._docId}" <%= seenStyle %>
			    ><ssf:makeWrapable>${fileEntry.title}</ssf:makeWrapable></span></a>
			    </div>
			 </c:if>
			
			  <c:if test="${empty fileEntry._fileID}">
			
			    <div>			    			    
			    <img <ssf:alt text="${fileEntry.title}"/> border="0" 
			      src="<html:imagesPath/>thumbnails/NoImage.jpeg"/><br/>

			    <% if (!ssSeenMap.checkIfSeen(fileEntry)) { %>
								    
				 <a id="ss_sunburstDiv${fileEntry._binderId}_${fileEntry._docId}" href="javascript: ;" 
				 title="<ssf:nlt tag="sunburst.click"/>"
				 onClick="ss_hideSunburst('${fileEntry._docId}', '${fileEntry._binderId}');return false;"
				><span 
				  style="display:${ss_sunburstVisibilityHide};"
				  id="ss_sunburstShow${renderResponse.namespace}" 
				  class="ss_fineprint">
				  	<img src="<html:rootPath/>images/pics/discussion/sunburst.png" align="absmiddle" border="0"
				  		style="width:12px;height:12px;" <ssf:alt tag="alt.new"/> />
				  </span>
				  </a>
					    
				<% } %>

			    <a 
				    href="<ssf:url crawlable="true"    
  					adapter="true" 
				    portletName="ss_forum" 
				    folderId="${ssFolder.id}" 
				    action="view_folder_entry" 
				    entryId="${fileEntry._docId}" actionUrl="true" />" 
				    onClick="ss_loadEntry(this, '${fileEntry._docId}', '${ssBinder.id}', '${fileEntry._entityType}', '${renderResponse.namespace}', 'no');return false;" 
				    ><c:if test="${empty fileEntry.title}"
				    ><span id="folderLineSeen_${fileEntry._docId}" <%= seenStyleFine %>
				    >--<ssf:nlt tag="entry.noTitle"/>--</span
				    ></c:if><span id="folderLineSeen_${fileEntry._docId}" <%= seenStyle %>
				    ><c:out value="${fileEntry.title}"/></span></a>
			    </div>
			 </c:if>
			
			</c:forEach>
			</div>
			</td></tr></table>

   </div>

	<c:if test="${empty ssFolderEntries}">
		<jsp:include page="/WEB-INF/jsp/forum/view_no_entries.jsp" />
	</c:if>
	<c:if test="${!empty ssFolderEntries}">
		<c:forEach var="fileEntry" items="${ssFolderEntries}" >
		  <div id="ss_photoTitle_${fileEntry._docId}" class="ss_hover_over" 
		    style="visibility:hidden; display:none;">
		    <c:if test="${empty fileEntry.title}">
			  <span>--<ssf:nlt tag="entry.noTitle"/>--</span>
			</c:if>
			<span id="folderLineSeen_${fileEntry._docId}">
			  <c:out value="${fileEntry.title}"/></span>
			  <c:if test="${!empty fileEntry._desc}">
			  	<div style="border-top: 1px solid #BABDB6; height: 10px;" /></div>
			  </c:if>
		    <span >
		    		<ssf:textFormat formatAction="limitedDescription" textMaxWords="100">
		    		<ssf:markup search="${fileEntry}">${fileEntry._desc}</ssf:markup>
		    		</ssf:textFormat></span>
		    <div class="ss_clear"></div>
		  </div>
		</c:forEach>
	</c:if>

<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>
