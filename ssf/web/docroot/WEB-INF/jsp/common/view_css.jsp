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
 
%><%--

--%>
<c:if test="${empty ssf_support_files_loaded}"><%--
--%><c:set var="ssf_support_files_loaded" value="1" scope="request"/><%--
--%>
<script type="text/javascript">
var undefined;
var ss_urlBase = self.location.protocol + "//" + self.location.host;
var ss_rootPath = "<html:rootPath/>";
var ss_imagesPath = "<html:imagesPath/>";
var ss_isAdapter="true";
var ss_tagSearchResultUrl = "<ssf:url windowState="maximized" 
    action="advanced_search" actionUrl="true"><ssf:param 
	name="searchTags" value="ss_tagPlaceHolder"/><ssf:param 
	name="operation" value="ss_searchResults"/><ssf:param 
	name="tabTitle" value="ss_tagPlaceHolder"/><ssf:param 
	name="newTab" value="1"/><ssf:param 
	name="searchItemType" value="workspace"/><ssf:param 
	name="searchItemType" value="folder"/><ssf:param 
	name="searchItemType" value="user"/><ssf:param 
	name="searchItemType" value="entry"/><ssf:param 
	name="searchItemType" value="reply"/></ssf:url>";
var ss_tagSearchResultUrlNoWS = "<ssf:url action="advanced_search" 
	actionUrl="true"><ssf:param 
	name="searchTags" value="ss_tagPlaceHolder"/><ssf:param 
	name="operation" value="ss_searchResults"/><ssf:param 
	name="tabTitle" value="ss_tagPlaceHolder"/><ssf:param 
	name="newTab" value="1"/><ssf:param 
	name="searchItemType" value="workspace"/><ssf:param 
	name="searchItemType" value="folder"/><ssf:param 
	name="searchItemType" value="user"/><ssf:param 
	name="searchItemType" value="entry"/><ssf:param 
	name="searchItemType" value="reply"/></ssf:url>";

var ss_tagSearchResultUrl = "<ssf:url windowState="maximized" 
	action="advanced_search" actionUrl="true"><ssf:param 
	name="searchTags" value="ss_tagPlaceHolder"/><ssf:param 
	name="operation" value="ss_searchResults"/><ssf:param 
	name="tabTitle" value="ss_tagPlaceHolder"/><ssf:param 
	name="newTab" value="1"/><ssf:param 
	name="searchItemType" value="workspace"/><ssf:param 
	name="searchItemType" value="folder"/><ssf:param 
	name="searchItemType" value="user"/><ssf:param 
	name="searchItemType" value="entry"/><ssf:param 
	name="searchItemType" value="reply"/></ssf:url>";
var ss_baseEntryUrl${renderResponse.namespace} = '<ssf:url windowState="maximized" 
	action="ssActionPlaceHolder"><ssf:param 
	name="binderId" value="ssBinderIdPlaceHolder"/><ssf:param 
	name="entryId" value="ssEntryIdPlaceHolder"/><ssf:param 
	name="newTab" value="ssNewTabPlaceHolder"/></ssf:url>';
var ss_baseEntryUrl = ss_baseEntryUrl${renderResponse.namespace};
	
var ss_baseEntryUrlNoWS${renderResponse.namespace} = '<ssf:url><ssf:param 
	name="action" value="ssActionPlaceHolder"/><ssf:param 
	name="binderId" value="ssBinderIdPlaceHolder"/><ssf:param 
	name="entryId" value="ssEntryIdPlaceHolder"/><ssf:param 
	name="newTab" value="ssNewTabPlaceHolder"/></ssf:url>';
var ss_baseEntryUrlNoWS = ss_baseEntryUrlNoWS${renderResponse.namespace};
	
var ss_baseBinderUrl${renderResponse.namespace} = '<ssf:url windowState="maximized"><ssf:param 
	name="action" value="ssActionPlaceHolder"/><ssf:param 
	name="binderId" value="ssBinderIdPlaceHolder"/><ssf:param 
	name="newTab" value="ssNewTabPlaceHolder"/></ssf:url>';
var ss_baseBinderUrl = ss_baseBinderUrl${renderResponse.namespace};
var ss_baseBinderUrlNoWS${renderResponse.namespace} = '<ssf:url><ssf:param 
	name="action" value="ssActionPlaceHolder"/><ssf:param 
	name="binderId" value="ssBinderIdPlaceHolder"/><ssf:param 
	name="newTab" value="ssNewTabPlaceHolder"/></ssf:url>';
var ss_baseBinderUrlNoWS = ss_baseBinderUrlNoWS${renderResponse.namespace};

var ss_baseRootPathUrl = '<html:rootPath/>';

var ss_userDisplayStyle = "${ssUser.displayStyle}";
<c:if test="${empty ssUser.displayStyle || ssUser.displayStyle == ''}">
	ss_userDisplayStyle = "iframe";	
</c:if>

var ss_1pix = ss_imagesPath + "pics/1pix.gif";
var ss_forumColorsCssUrl = "<ssf:url webPath="viewCss"><ssf:param 
		name="theme" value="${ssUser.theme}"/></ssf:url>";

var ss_AjaxBaseUrl = "<ssf:url adapter="true" portletName="ss_forum" actionUrl="true" />";

//Not logged in message
var ss_not_logged_in = "<ssf:nlt tag="general.notLoggedIn"/>";
	
// RTC client not installed
var ss_rtc_not_configured = "<ssf:nlt tag="rtc.client.not.configured"/>";
	
//Clipboard text
var ss_clipboardTitleText = "<ssf:nlt tag="clipboard.title"/>";
var ss_addContributesToClipboardText = "<ssf:nlt tag="button.add_contributes_to_clipboard"/>";
var ss_addTeamMembersToClipboardText = "<ssf:nlt tag="button.sdd_team_members_to_clipboard"/>";
var ss_clearClipboardText = "<ssf:nlt tag="button.clear_clipboard"/>";
var ss_noUsersOnClipboardText = "<ssf:nlt tag="clipboard.noUsers"/>";
var ss_closeButtonText = "<ssf:nlt tag="button.close"/>";
var ss_selectAllBtnText = "<ssf:nlt tag="button.selectAll"/>";
var ss_clearAllBtnText = "<ssf:nlt tag="button.clearAll"/>";
									
var ss_findButtonClose = "<ssf:nlt tag="button.close"/>";
var ss_validationErrorMessage = "<ssf:nlt tag="validation.errorMessage"/>";

</script>
<%
	boolean isIE = com.sitescape.util.BrowserSniffer.is_ie(request);
	%><%--

--%>
<script type="text/javascript" src="<html:rootPath/>js/dojo/dojo/dojo.js" 
  djConfig="isDebug: false, locale: '<ssf:convertLocaleToDojoStyle />', parseOnLoad: true"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/ss_common.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/taconite-client.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/taconite-parser.js"></script>
<script type="text/javascript">
</script>

<link href="<html:rootPath/>css/forum.css" rel="stylesheet" type="text/css" >
<link href="<html:rootPath/>css/slider_swing.css" rel="stylesheet" type="text/css" >
<link href="<ssf:url webPath="viewCss"> <ssf:param name="theme" value="${ssUser.theme}"/>
	    </ssf:url>" rel="stylesheet" type="text/css" >

<script type="text/javascript">

<c:set var="ss_loadCssStylesInline" value="true" scope="request"/>
<c:set var="ss_skipCssStyles" value="true" scope="request"/>
<jsp:include page="/WEB-INF/jsp/common/ssf_css.jsp" />

function ss_defineColorValues() {
	ss_style_background_color = '${ss_style_background_color}';
	ss_dashboard_table_border_color = '${ss_dashboard_table_border_color}';
}

</script>

<%--
* The following line is used to call customer supplied customizations.
* This jsp will be called at least once per page. 
* (It can be called more than once on portlet pages, so be prepared for that.)
* This jsp is called before any other Teaming content is displayed. 
* However, it is not graranteed to be in the <head> section.
* Jsp files added to the custom_jsps directory will not be overwritten during upgrades
--%><jsp:include page="/WEB-INF/jsp/custom_jsps/ss_call_out_css_init.jsp" /><%--

--%></c:if>


