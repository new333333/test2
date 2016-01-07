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
<% // Find a single element %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="org.kablink.util.ParamUtil" %>
<c:set var="prefix" value="${renderResponse.namespace}_${ssFindInstanceCode}_${ssFindInstanceCount}" />
<c:set var="accessibilityTextNltized"><ssf:nlt tag="${accessibilityText}"/></c:set>

<ssf:ifaccessible simple_ui="true">
	<label for="ss_combobox_autocomplete_${prefix}"><span style="display:none;">${accessibilityTextNltized}</span></label>
</ssf:ifaccessible>

<!-- textarea's rows and cols attributes are set to 1. -->
<!-- They are overridden by CSS. -->

<input
	type="text" 
	autocomplete="off"
    class="ss_combobox_autocomplete"
    style="width: ${ssFindElementWidth}; white-space:nowrap; "
    name="${ssFindFormElement}" 
    id="ss_combobox_autocomplete_${prefix}"
    <c:if test="${!empty accessibilityTextNltized}">
    	title="${accessibilityTextNltized}"
    </c:if>
/>

<img src="<html:imagesPath/>pics/1pix.gif" <ssf:alt/>
onload="dojo.addOnLoad(function(){
	if (!window['findSingle${prefix}']) {<%-- prevents FF problem - img.onload called sometimes twice --%>
		window['findSingle${prefix}'] = ssFind.configSingle({
		inputId: 'ss_combobox_autocomplete_${prefix}',
		findMultipleObj: '${ssFindMultipleObj}', 
		prefix: '${prefix}', 
		displayValue: '${ssDisplayValue}',
		displayValueOnly: '${ssDisplayValueOnly}',
		clickRoutineObj: '${ssFindClickRoutineObj}', 
		clickRoutine: '${ssFindClickRoutine}',
		<c:choose>
			<c:when test="${ssFindListType == 'places'}">
				viewUrl: '<ssf:url adapter="true" portletName="ss_forum" 
				    action="view_permalink"
				    binderId="ss_binderIdPlaceholder">
				    <ssf:param name="entityType" value="ss_entityTypePlaceholder" />
				</ssf:url>',
				viewAccesibleUrl: '',
				searchUrl: '<ssf:url adapter="true" portletName="ss_forum" action="__ajax_find" actionUrl="false">
					<ssf:param name="operation" value="find_places_search" />
					<ssf:param name="sendingEmail" value="${sendingEmail}" />
				</ssf:url>',
			</c:when>
			<c:when test="${ssFindListType == 'tags' || ssFindListType == 'personalTags' || ssFindListType == 'communityTags'}">
				<ssf:ifnotadapter>
					viewUrl: '<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
						name="searchTags" value="ss_tagPlaceHolder"/><ssf:param 
						name="operation" value="ss_searchResults"/><ssf:param 
						name="tabTitle" value="ss_tagPlaceHolder"/><ssf:param 
						name="searchItemType" value="workspace"/><ssf:param 
						name="searchItemType" value="folder"/><ssf:param 
						name="searchItemType" value="user"/><ssf:param 
						name="searchItemType" value="entry"/><ssf:param 
						name="searchItemType" value="reply"/>
					</ssf:url>'
				</ssf:ifnotadapter>
				<ssf:ifadapter>
					viewUrl: window.ss_tagSearchResultUrl?window.ss_tagSearchResultUrl:''
				</ssf:ifadapter>,
				viewAccesibleUrl: '',
				searchUrl: '<ssf:url adapter="true" portletName="ss_forum" action="__ajax_find" actionUrl="false">
					<ssf:param name="operation" value="find_tag_search" />
					<ssf:param name="sendingEmail" value="${sendingEmail}" />
				</ssf:url>',
			</c:when>	
			<c:when test="${ssFindListType == 'entries'}">
				viewUrl: '<ssf:url adapter="true" portletName="ss_forum" 
				    action="view_permalink"
				    binderId="${ssBinder.id}"
				    entryId="ss_entryIdPlaceholder">
					    <ssf:param name="entityType" value="folderEntry" />
				</ssf:url>',
				viewAccesibleUrl: '<ssf:url adapter="false" portletName="ss_forum" 
				    folderId="${ssFolder.id}" action="view_folder_entry" 
				    entryId="ss_entryIdPlaceholder" actionUrl="true">
				</ssf:url>',
				searchUrl: '<ssf:url adapter="true" portletName="ss_forum" action="__ajax_find" actionUrl="false">
					<ssf:param name="operation" value="find_entries_search" />
					<ssf:param name="sendingEmail" value="${sendingEmail}" />
				</ssf:url>',					
			</c:when>
			<c:otherwise>
				<%-- find user --%>
				viewUrl: '<ssf:url action="view_ws_listing">
					<ssf:param name="binderId" value="${ssUser.parentBinder.id}"/>
					<ssf:param name="entryId" value="ss_entryIdPlaceholder"/>
				</ssf:url>',
				viewAccesibleUrl: '',
				searchUrl: '<ssf:url adapter="true" portletName="ss_forum" action="__ajax_find" actionUrl="false">
					<ssf:param name="operation" value="find_user_search" />
					<ssf:param name="addCurrentUser" value="${ssFindAddCurrentUser}" />
					<ssf:param name="sendingEmail" value="${sendingEmail}" />
			   	</ssf:url>',
			</c:otherwise>
		</c:choose>    
  		leaveResultsVisible: '${ssFindLeaveResultsVisible}',
  		sendingEmail: '${sendingEmail}',
  		listType: '${ssFindListType}', 
  		renderNamespace: '${renderResponse.namespace}',
  		binderId: '${binderId}',
  		subFolders: '${ssFindSearchSubFolders}',
  		foldersOnly: '${ssFindFoldersOnly}',
  		showFolderTitles: '${ssShowFolderTitles}',
  		showUserTitleOnly: '${ssShowUserTitleOnly}' 
	})}}); " />    		
