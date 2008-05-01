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
<% // Wiki view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />

<script type="text/javascript">
var ss_columnCount = 0;
function ss_loadWikiEntry(obj,id) {
	ss_highlightLineById('folderLine_' + id);
	var iframeDiv = document.getElementById('ss_wikiIframe<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>')
	iframeDiv.src = obj.href;
	return false;
}

function ss_loadWikiEntryInParent(obj,id) {
	self.parent.location.href = obj.href;
}

//Routine called when "find wiki page" is clicked
function ss_loadWikiEntryId${renderResponse.namespace}(id) {
	var urlParams = {binderId:'${ssBinder.id}', entryId:id, namespace:'${renderResponse.namespace}'};
	var iframeDiv = document.getElementById('ss_wikiIframe<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>')
	iframeDiv.src = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "view_folder_entry");
}

var ss_wikiIframeOffset = 60;
function ss_setWikiIframeSize(namespace) {
	var targetDiv = document.getElementById('ss_wikiEntryDiv' + namespace)
	var iframeDiv = document.getElementById('ss_wikiIframe' + namespace)
	if (window.frames['ss_wikiIframe' + namespace] != null) {
		eval("var iframeHeight = parseInt(window.ss_wikiIframe" + namespace + ".document.body.scrollHeight);")
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_wikiIframeOffset + "px"
		}
	}
}

function ss_confirmSetWikiHomepage() {
	return confirm("<ssf:nlt tag="wiki.confirmSetHomepage"/>");
}

</script>

<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">
<% // Entry toolbar %>
<c:if test="${!empty ssEntryToolbar}">
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />
</c:if>
</ssf:toolbar>
<div class="ss_clear"></div>
<%@ include file="/WEB-INF/jsp/forum/page_navigation_bar.jsp" %>
<div class="ss_folder" id="ss_wiki_folder_div">
<%@ include file="/WEB-INF/jsp/definition_elements/description_view.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/wiki/wiki_folder_listing.jsp" %>
</div>
