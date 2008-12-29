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
<% // Find a single element %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="org.kablink.util.ParamUtil" %>
<%
	String label = ParamUtil.get(request, "label", "");
%>
<c:set var="prefix" value="${renderResponse.namespace}_${ssFindInstanceCode}_${ssFindInstanceCount}" />
<c:set var="label" value="<%= label %>" />

<ssf:ifaccessible>
	<label for="ss_combobox_autocomplete_${prefix}"><ssf:nlt tag="${accessibilityText}" />&nbsp;</label>
</ssf:ifaccessible>

<!-- textarea's rows and cols attributes are set to 0. -->
<!-- They are overridden by CSS. -->

<textarea 
    class="ss_combobox_autocomplete"
    rows="0"
    cols="0"
    style="width: ${ssFindElementWidth};"
    name="${ssFindFormElement}" 
    id="ss_combobox_autocomplete_${prefix}"
    <c:if test="${!empty label}">
    	title="${label}"
    </c:if>
></textarea>

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
						   	</ssf:url>',
						</c:otherwise>
					</c:choose>    
		      		leaveResultsVisible: '${ssFindLeaveResultsVisible}', 
		      		listType: '${ssFindListType}', 
		      		renderNamespace: '${renderResponse.namespace}',
		      		binderId: '${binderId}',
		      		subFolders: '${ssFindSearchSubFolders}',
		      		foldersOnly: '${ssFindFoldersOnly}',
		      		showFolderTitles: '${ssShowFolderTitles}',
		      		showUserTitleOnly: '${ssShowUserTitleOnly}' 
		})}}); " />    		
