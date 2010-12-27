<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<script type="text/javascript" language="javascript">
	// Save away information such as the binder id and the adapted url for the request we are working with.
	// Through an overlay we will access m_requestInfo from java.
	var m_requestInfo = {
		advancedSearchUrl:					'<ssf:escapeJavaScript><ssf:url action="advanced_search" actionUrl="true" windowState="maximized"><ssf:param name="action" value="advancedSearch"/><ssf:param name="tabTitle" value="SEARCH FORM"/><ssf:param name="newTab" value="0"/></ssf:url></ssf:escapeJavaScript>',
		contentCss:							'<ssf:url webPath="viewCss"><ssf:param name="sheet" value="editor"/></ssf:url>',
		currentUserWorkspaceId:				'${ssUser.workspaceId}',
		deleteUserUrl:						'${ss_deleteEntryAdapter}',
		errMsg:								'<ssf:escapeJavaScript>${errMsg}</ssf:escapeJavaScript>',
		forceSidebarReload:					'false',
		helpUrl:							'<ssf:escapeJavaScript>${ss_helpUrl}</ssf:escapeJavaScript>',
		imagesPath:							'<ssf:escapeJavaScript><html:imagesPath/></ssf:escapeJavaScript>',
		isBinderAdmin:						'${ss_isBinderAdmin}',
		isDiskQuotaHighWaterMarkExceeded:	'${ss_diskQuotaHighWaterMarkExceeded}',
		isModifyAllowed:					'${ss_modifyEntryAllowed}',
		isNovellTeaming:					'${isNovellTeaming}',
		isQuotasDiskQuotaExceeded:			'${ss_diskQuotaExceeded}',
		isQuotasEnabled:					'${ss_quotasEnabled}',
		isUserLoggedIn:						'${isUserLoggedIn}',
		jsPath:								'<ssf:escapeJavaScript>${pageContext.request.contextPath}/js/</ssf:escapeJavaScript>',
		language:							'${ssUser.locale.language}',
		loginError:							'<ssf:escapeJavaScript>${ss_loginError}</ssf:escapeJavaScript>',
		loginPostUrl:						'<ssf:escapeJavaScript>${ss_loginPostUrl}</ssf:escapeJavaScript>',
		loginRefererUrl:					'${loginRefererUrl}',
		modifyUrl:							'${ss_modifyEntryAdapter}',
		productName:						'${productName}',
		promptForLogin:						'${promptForLogin}',
		quotasDiskMessage:					'${ss_quotaMessage}',
		quotasDiskSpacedUsed:				'${ssDiskSpaceUsed}',
		quotasUserMaximum:					'${ssDiskQuota}',
		recentPlaceSearchUrl:				'<ssf:escapeJavaScript><ssf:url action="advanced_search" actionUrl="true"><ssf:param name="operation" value="viewPage"/></ssf:url></ssf:escapeJavaScript>',
		savedSearchUrl:						'<ssf:escapeJavaScript><ssf:url action="advanced_search" actionUrl="true"><ssf:param name="newTab" value="1"/><ssf:param name="operation" value="ss_savedQuery"/></ssf:url></ssf:escapeJavaScript>',
		sessionCaptive:						'${sessionCaptive}',
		showWhatsNewOnLogin:				'${showWhatsNew}',		
		simpleSearchUrl:					'<ssf:escapeJavaScript><ssf:url action="advanced_search" actionUrl="true"><ssf:param name="newTab" value="1"/><ssf:param name="quickSearch" value="true"/><ssf:param name="operation" value="ss_searchResults"/></ssf:url></ssf:escapeJavaScript>',
		teamingFeedUrl:						'<ssf:escapeJavaScript><ssf:url adapter="true" portletName="ss_forum" action="__ajax_mobile" operation="view_teaming_live" actionUrl="false" /></ssf:escapeJavaScript>',
		topWSId:							'${topWSId}',
		userDescription:					'',
		userId:								'${ssUser.id}',
		userLoginId:						'${ssUser.name}',
		vibeProduct:						'${vibeProduct}',
				
		<c:if test="${gwtPage == 'main'}">
			adaptedUrl:						'${adaptedUrl}',
			binderId:						'${binderId}',
			myWSUrl:						'${myWorkspaceUrl}',
			userName:						'<ssf:escapeJavaScript>${userFullName}</ssf:escapeJavaScript>',
		</c:if>
			
		<c:if test="${gwtPage == 'profile'}">
			adaptedUrl:						'<ssf:url crawlable="true" adapter="true" portletName="ss_forum" folderId="${ssBinder.id}"        action="view_ws_listing" ><ssf:param name="profile" value="0" /></ssf:url>',
			binderId:						'${ssBinder.id}',
			myWSUrl:						'<ssf:url crawlable="true" adapter="true" portletName="ss_forum" folderId="${ssUser.workspaceId}" action="view_ws_listing" ><ssf:param name="profile" value="1" /></ssf:url>',			
			userName:						'<ssf:escapeJavaScript><ssf:userTitle user="${ssProfileConfigEntry}"/></ssf:escapeJavaScript>',
		</c:if>
		
		// The following is used by native methods in RequestInfo.java
		// to return a Boolean value from one of the above Strings.
		getBFromS:  function(s) {
			var reply = ( ( null != s ) && ( "true" == ss_trim( s.toLowerCase() ) ) );
			return reply;
		}
	};
</script>
