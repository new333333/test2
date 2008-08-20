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
	var iframeDiv = document.getElementById('ss_wikiIframe${renderResponse.namespace}')
	iframeDiv.src = obj.href;
	return false;
}

function ss_loadWikiEntryInParent(obj,id) {
	self.parent.location.href = obj.href;
}

//Routine called when "find wiki page" is clicked
function ss_loadWikiEntryId${renderResponse.namespace}(id) {
	var urlParams = {binderId:'${ssBinder.id}', entryId:id, namespace:'${renderResponse.namespace}', entryViewStyle:'popup'};
	var iframeDiv = document.getElementById('ss_wikiIframe${renderResponse.namespace}')
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

<ssf:ifnotaccessible>
  var ss_wikiAjaxUrl${renderResponse.namespace} = "";
</ssf:ifnotaccessible>
<ssf:ifaccessible>
  var ss_wikiAjaxUrl${renderResponse.namespace} = "<ssf:url 
	action="view_folder_listing" ><ssf:param 
  	name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="type" value="ss_typePlaceHolder" /></ssf:url>";
</ssf:ifaccessible>
</script>

<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">
 <% // Entry toolbar %>
 <c:if test="${!empty ssEntryToolbar}">
  <ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />
 </c:if>
 <ssf:toolbar toolbar="${ss_whatsNewToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />			
</ssf:toolbar>
<div class="ss_clear"></div>
<div id="ss_whatsNewDiv${ss_namespace}">
<c:if test="${!empty ss_whatsNewBinder || ss_pageNumber > '0'}">
<%@ include file="/WEB-INF/jsp/forum/whats_new_page.jsp" %>
</c:if>
</div>

<div id="ss_wrap" align="center">
 <div id="ss_tabsC">
  <ul>
	<!-- CSS Tabs -->
  <c:if test="${empty ssWikiCurrentTab}"><c:set var="ssWikiCurrentTab" value="entries" scope="request"/></c:if>
	<li <c:if test="${ssWikiCurrentTab == 'directory'}">class="ss_tabsCCurrent"</c:if>
	><a id="ss_wikiDirectoryTab${renderResponse.namespace}"
	  href="javascript: ;"
		onClick="ss_selectWikiTab(this, 'directory', '${renderResponse.namespace}');return false;"
		><span><ssf:nlt tag="wiki.tab.directory"/></span></a></li>
	
	<li <c:if test="${ssWikiCurrentTab == 'entries'}">class="ss_tabsCCurrent"</c:if>
	><a id="ss_wikiEntriesTab_${renderResponse.namespace}" 
	  href="javascript: ;"
		onClick="ss_selectWikiTab(this, 'entries', '${renderResponse.namespace}');return false;"
		><span><ssf:nlt tag="wiki.tab.entries"/></span></a></li>

  </ul>
 </div>
</div>
<div class="ss_clear"></div>
<script type="text/javascript">
var ss_wikiTabCurrent_${renderResponse.namespace} = document.getElementById('ss_wikiEntriesTab_${renderResponse.namespace}');
</script>

<div id="ss_wiki_directory_div${renderResponse.namespace}" style="display:none;">
  <%@ include file="/WEB-INF/jsp/definition_elements/description_view.jsp" %>
  <jsp:include page="/WEB-INF/jsp/forum/page_navigation_bar.jsp" />
  <div class="ss_folder" >
       <table cellspacing="0" cellpadding="0">
		  <c:forEach var="entry1" items="${ssFolderEntries}" >
			<jsp:useBean id="entry1" type="java.util.HashMap" />
			<%
				String folderLineId = "folderLine_" + (String) entry1.get("_docId");
				String seenStyle = "";
				String seenStyleFine = "ss_finePrint";
				if (!ssSeenMap.checkIfSeen(entry1)) {
					seenStyle = "ss_unseen";
					seenStyleFine = "ss_unseen ss_fineprint";
				}
			%>
		    <tr><td><div style="padding:0px 4px 4px 8px;">
		    <a 
		    href="<ssf:url     
		    adapter="true" 
		    portletName="ss_forum" 
		    folderId="${ssFolder.id}" 
		    action="view_folder_entry" 
		    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true"><ssf:param
		    name="entryViewStyle" value="popup"/><ssf:param
		    name="namespace" value="${renderResponse.namespace}"/><ssf:ifaccessible><ssf:param name="newTab" value="1" /></ssf:ifaccessible></ssf:url>" 

		    <ssf:title tag="title.open.folderEntry">
			    <ssf:param name="value" useBody="true"><c:choose><c:when test="${!empty entry1.title}">${entry1.title}</c:when><c:otherwise>--<ssf:nlt tag="entry.noTitle"/>--</c:otherwise></c:choose></ssf:param>
		    </ssf:title>

		    <ssf:ifnotaccessible>
		    	onClick="ss_selectWikiTab(document.getElementById('ss_wikiEntriesTab_${renderResponse.namespace}'), 'entries', '${renderResponse.namespace}');ss_loadWikiEntry(this, '${entry1._docId}');return false;" 		    	
		    </ssf:ifnotaccessible>
		    
		    <ssf:ifaccessible>
			    onClick="ss_loadWikiEntryInParent(this, '${entry1._docId}');return false;" 
		    </ssf:ifaccessible>
		    
		    >
		    
   			<% if (!ssSeenMap.checkIfSeen(entry1)) { %>
								    
			  <a id="ss_sunburstDiv${entry1._binderId}_${entry1._docId}" href="javascript: ;" 
			  title="<ssf:nlt tag="sunburst.click"/>"
			  onClick="ss_hideSunburst('${entry1._docId}', '${entry1._binderId}');return false;"
			><span 
			  style="display:${ss_sunburstVisibilityHide};"
			  id="ss_sunburstShow${renderResponse.namespace}" 
			  class="ss_fineprint">
			 	<img src="<html:rootPath/>images/pics/discussion/sunburst.png" align="text-bottom" <ssf:alt tag="alt.new"/> />&nbsp;
			  </span>
			  </a>
							    
			<% } %>
		    
		    <c:if test="${empty entry1.title}"
		    ><span id="folderLine_${entry1._docId}" class="ss_smallprint <%= seenStyleFine %>"
		      >--<ssf:nlt tag="entry.noTitle"/>--</span
		    ></c:if><span id="folderLine_${entry1._docId}" class="ss_smallprint <%= seenStyle %>"
		      ><c:out value="${entry1.title}"/></span></a>
		    </td></tr>
		  </c:forEach>
		</table>
  </div>
</div>

<div id="ss_wiki_entries_div${renderResponse.namespace}" style="display:block;">
  <div class="ss_folder" id="ss_wiki_folder_div">
    <%@ include file="/WEB-INF/jsp/definition_elements/wiki/wiki_folder_listing.jsp" %>
  </div>
</div>
