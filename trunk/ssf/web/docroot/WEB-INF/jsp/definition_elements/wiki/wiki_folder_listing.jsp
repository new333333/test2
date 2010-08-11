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

<% //View the listing part of a wiki folder %>

<% //View the listing part of a wiki folder %>
<%@ page import="java.util.Date" %>

<div class="ss_wiki_folder">
   
	<ssHelpSpot helpId="workspaces_folders/misc_tools/wiki_controls" offsetX="-14" offsetY="8" 
	   			title="<ssf:nlt tag="helpSpot.wikiControls"/>">
	</ssHelpSpot>

	<table class="ss_wiki_search_bar">
	  <tr>
	    <td valign="baseline">
	      <c:if test="${!empty ss_wikiHomepageEntryId}">
		    <a class="ss_linkButton" href="<ssf:url     
			    adapter="true" 
			    portletName="ss_forum" 
			    folderId="${ssFolder.id}" 
			    action="view_folder_entry" 
			    entryId="${ss_wikiHomepageEntryId}" 
			    actionUrl="true"><ssf:param
			    name="entryViewStyle" value="popup"/><ssf:param
			    name="namespace" value="${renderResponse.namespace}"/><ssf:ifaccessible><ssf:param 
			    name="newTab" value="1" /></ssf:ifaccessible></ssf:url>" 
			    <ssf:title tag="title.open.folderEntrySimple" />
			    onclick="ss_loadEntry(this, '${ss_wikiHomepageEntryId}', '${ssFolder.id}', 'folderEntry', '${renderResponse.namespace}', 'no');return false;" 
			><ssf:nlt tag="wiki.homePage"/></a>
	      </c:if>
	    </td>
	    
	    <td valign="baseline">        
	      <c:if test="${ssConfigJspStyle != 'template'}">
		    <form method="post" name="ss_findWikiPageForm${renderResponse.namespace}"
		    	action="<ssf:url action="view_folder_listing" actionUrl="true"><ssf:param 
						name="binderId" value="${ssBinder.id}"/></ssf:url>">
          	 <span><ssf:nlt tag="wiki.findPage"/></span>
			 <ssf:find formName="ss_findWikiPageForm${renderResponse.namespace}" 
			    formElement="searchTitle" 
			    type="entries"
			    width="140px" 
			    binderId="${ssBlogSetBinder.id}"
			    searchSubFolders="true"
			    showFolderTitles="true"
			    singleItem="true"
			    clickRoutine="ss_loadWikiEntryId${renderResponse.namespace}"
			    accessibilityText="wiki.findPage"
			    /> 
		     <input type="hidden" name="searchTitle"/>
		    </form>
		  </c:if>
		</td>

	  </tr>
	</table>
	
	
	<div style="padding:10px 0px 10px 0px;">
	<table cellspacing="0" cellpadding="0">
	  <tbody>
	    <tr>
	      <th align="left">
		    <span>
		      <ssf:nlt tag="wiki.topics"/>
		    </span>
		  </td>
		</tr>
		
		<tr>
		  <td>
			 <div class="ss_navbar_inline">
				<ul>
			     <c:forEach var="blogPage" items="${ssBlogPages}">
		 		   <li>
			           <a class="<c:if test="${blogPage.id == ssBinder.id}"> ss_navbar_current</c:if>
							   <c:if test="${blogPage.id != ssBinder.id}"></c:if>" 
						  href="<ssf:url action="view_folder_listing" binderId="${blogPage.id}"/>"
			           >${blogPage.title}</a>
			       </li>
			     </c:forEach>
			    </ul>
		    </div>
		  </td>
		</tr>
		
	  </tbody>
	</table>
	</div>

	<table cellspacing="0" cellpadding="0" width="100%">
	  <tbody>
	    <tr>
	      <th align="left">
		    <span>
		      <ssf:nlt tag="wiki.pages"/>
		    </span>
		  </td>
		</tr>
		
		<tr>
		  <td>
			<div id="ss_wikiFolderList${renderResponse.namespace}" class="ss_wiki_folder_list">
		      <%@ include file="/WEB-INF/jsp/definition_elements/wiki/wiki_folder_page.jsp" %>
		    </div>
		  </td>
		</tr>
	  </tbody>
	</table>
		

    <c:if test="${0 == 1}">
    <c:if test="${!empty ssFolderEntryCommunityTags}"> 
		<div class="ss_wiki_sidebar_subhead"><ssf:nlt tag="tags.community"/></div>
	    <div class="ss_wiki_sidebar_box">		
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
		<div class="ss_wiki_sidebar_subhead"><ssf:nlt tag="tags.personal"/></div>
	    <div class="ss_wiki_sidebar_box">		
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
	</c:if>
		
</div>
    