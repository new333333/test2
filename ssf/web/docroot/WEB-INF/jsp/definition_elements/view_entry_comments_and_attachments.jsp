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
<% // View entry comments and attachments in tabs %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${ssEntry != ssDefinitionEntry}" >
  <% // This is a reply in the replies list, don't show the tabs %>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_reply_attachments.jsp" />
</c:if>
<c:if test="${ssEntry == ssDefinitionEntry}" >
<c:set var="ss_divCounter" value="${ss_divCounter + 1}" scope="request" />
<script type="text/javascript">
var ss_entryHistoryLoaded${ss_divCounter} = false;
function ss_showHideEntryHistoryDiv${ss_divCounter}(iframeId) {
	var url = "<ssf:url ><ssf:param 
		name="action" value="view_editable_history"/><ssf:param 
		name="actionUrl" value="true"/><ssf:param 
		name="operation" value="view_edit_history"/><ssf:param 
		name="entityId" value="${ssEntry.id}"/></ssf:url>";
	var iframeObj = self.document.getElementById(iframeId);
	if (!ss_entryHistoryLoaded${ss_divCounter}) {
		iframeObj.src = url;
		ss_entryHistoryLoaded${ss_divCounter} = true;
	}
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
	if (self.parent.ssf_onLayoutChange) self.parent.ssf_onLayoutChange();
	if (self.parent.parent.ssf_onLayoutChange) self.parent.parent.ssf_onLayoutChange();
	ss_resizeEntryHistoryIframe(iframeId);
}

var ss_entryHistoryIframeOffset = 50;
function ss_resizeEntryHistoryIframe(iframeId, loadingId) {
	var iframeDiv = document.getElementById(iframeId)
	if (typeof loadingId != "undefined" && iframeDiv.src.indexOf("null.html") < 0) {
		var spanObj = self.document.getElementById(loadingId);
		if (spanObj != null) spanObj.style.display = "none";
	}
	try {
		var frameWindow = window.frames[iframeId];
		if (frameWindow && frameWindow.document && frameWindow.document.body) {
			var iframeHeight = parseInt(frameWindow.document.body.scrollHeight);
			if (typeof iframeDiv.style.height == "undefined" || iframeDiv.style.height == "" || 
					(parseInt(iframeDiv.style.height) != parseInt(iframeHeight) + ss_entryHistoryIframeOffset)) {
				iframeDiv.style.height = parseInt(iframeHeight) + ss_entryHistoryIframeOffset + "px"
				//Signal that the layout changed
				//if (ssf_onLayoutChange) setTimeout("ssf_onLayoutChange();", 300);
				//if (self.parent.ssf_onLayoutChange) setTimeout("self.parent.ssf_onLayoutChange();", 100);
				//if (self.parent.parent.ssf_onLayoutChange) setTimeout("self.parent.parent.ssf_onLayoutChange();", 100);
			}
		}
	} catch(e) {alert(e)}
}
</script>
<%
	//Get the user's desired region view (if set)
	String regionView_commentsAndAttachmentsRegion = "expanded";
	Map userProperties = (Map)  request.getAttribute("ssUserProperties");
	if (userProperties != null && userProperties.containsKey("regionView.commentsAndAttachmentsRegion")) {
		regionView_commentsAndAttachmentsRegion = (String) userProperties.get("regionView.commentsAndAttachmentsRegion");
	}
	if ("collapsed".equals(regionView_commentsAndAttachmentsRegion)) {
		%><c:set var="regionClass" value="wg-tab-content-clipped"/><c:set var="regionImg" value="expand_16_yellow.png"/><%
	} else {
		%><c:set var="regionClass" value="wg-tab-content"/><c:set var="regionImg" value="collapse_16_yellow.png"/><%
	}
%>

<c:if test="${empty ss_tabDivCount}">
  <c:set var="ss_tabDivCount" value="0" scope="request"/>
</c:if>
<c:set var="ss_tabDivCount" value="${ss_tabDivCount + 1}" scope="request"/>
<c:set var="ss_thisCurrentTab" value="viewComments${ss_tabDivCount}"/>
<c:if test="${!empty ss_pseudoEntity}">
  <c:set var="ss_thisCurrentTab" value="viewAttachments${ss_tabDivCount}"/>
</c:if>
<script type="text/javascript">
ss_createOnLoadObj("ss_initThisTab${ss_tabDivCount}", 
		function() {ss_initTab('${ss_thisCurrentTab}', '${ss_tabDivCount}');});
</script>

<div class="ss_entryContent">
	<div style="text-align: left; border: 0px;" class="wg-tabs margintop2">
	  <table cellspacing="0" cellpadding="0" width="100%">
	  <tr>
	  <c:if test="${empty ss_pseudoEntity}">
	  <td valign="middle" width="1%" nowrap>
	  <div id="viewComments${ss_tabDivCount}Tab" class="wg-tab roundcornerSM on" 
		onMouseOver="ss_hoverOverTab('viewComments${ss_tabDivCount}', '${ss_tabDivCount}');"
		onMouseOut="ss_hoverOverTabStopped('viewComments${ss_tabDivCount}', '${ss_tabDivCount}');"
		onClick="ss_showTab('viewComments${ss_tabDivCount}', '${ss_tabDivCount}');">
		<ssf:nlt tag="__entry_comments"/>
		<c:if test="${ssDefinitionEntry.top}">
		  <span class="ss_smallprint">(${fn:length(ssFolderEntryDescendants)})</span>
		</c:if>
	  </div>
	  </td>
	  </c:if>
	  <td valign="middle" width="1%" nowrap>
	  <div id="viewAttachments${ss_tabDivCount}Tab" 
		class="wg-tab roundcornerSM <c:if test="${!empty ss_pseudoEntity}">on</c:if>" 
		onMouseOver="ss_hoverOverTab('viewAttachments${ss_tabDivCount}', '${ss_tabDivCount}');"
		onMouseOut="ss_hoverOverTabStopped('viewAttachments${ss_tabDivCount}', '${ss_tabDivCount}');"
		onClick="ss_showTab('viewAttachments${ss_tabDivCount}', '${ss_tabDivCount}');return false;">
		<ssf:nlt tag="__entry_attachments"/>
		<c:if test="${empty ssPrimaryFileAttribute}">
		  <span class="ss_smallprint">(${fn:length(ssDefinitionEntry.fileAttachments)})</span>
		</c:if>
		<c:if test="${!empty ssPrimaryFileAttribute && !empty ssDefinitionEntry.fileAttachments}">
		  <span class="ss_smallprint">(${fn:length(ssDefinitionEntry.fileAttachments) - 1})</span>
		</c:if>
	  </div>
	  </td>
	  <c:if test="${ssDefinitionEntry.top && empty ss_pseudoEntity}">
	  <td valign="middle" width="1%" nowrap>
	  <div id="viewEntryHistory${ss_tabDivCount}Tab" 
		class="wg-tab roundcornerSM" 
		onMouseOver="ss_hoverOverTab('viewEntryHistory${ss_tabDivCount}', '${ss_tabDivCount}');"
		onMouseOut="ss_hoverOverTabStopped('viewEntryHistory${ss_tabDivCount}', '${ss_tabDivCount}');"
		onClick="ss_showTab('viewEntryHistory${ss_tabDivCount}', '${ss_tabDivCount}');ss_showHideEntryHistoryDiv${ss_divCounter}('viewEntryHistory${ss_tabDivCount}Iframe');return false;">
		<ssf:nlt tag="entry.versionHistory"/>
	  </div>
	  </td>
	  </c:if>
	  <c:if test="${!empty ssDefinitionEntry.fileAttachments}">
	  <td valign="middle" width="1%" nowrap>
	  <div id="viewFileVersions${ss_tabDivCount}Tab" 
		class="wg-tab roundcornerSM" 
		onMouseOver="ss_hoverOverTab('viewFileVersions${ss_tabDivCount}', '${ss_tabDivCount}');"
		onMouseOut="ss_hoverOverTabStopped('viewFileVersions${ss_tabDivCount}', '${ss_tabDivCount}');"
		onClick="ss_showTab('viewFileVersions${ss_tabDivCount}', '${ss_tabDivCount}');return false;">
		<ssf:nlt tag="__entry_file_versions"/>
	  </div>
	  </td>
	  </c:if>
	  <td valign="middle" align="right" width="97%" nowrap>&nbsp;
		<c:if test="${ssDefinitionEntry.top && empty ss_pseudoEntity}">
		<div id="commentsAndAttachmentsRegionImg${ss_divCounter}" style="display:none;">
		<a href="javascript: ;" 
		  onClick="ss_toggleRegion(this, 'commentsAndAttachmentsRegion${ss_divCounter}', 'commentsAndAttachmentsRegion', 'wg-tab-content', 300);return false;" 
		  alt="<ssf:nlt tag="general.expandCollapseRegion"/>" title="<ssf:nlt tag="general.expandCollapseRegion"/>"
		><img src="<html:rootPath/>images/pics/${regionImg}"/></a>
		</div>
		</c:if>
	  </td>
	  </tr>
	</table>
</div>
<c:if test="${ssDefinitionEntry.top && empty ss_pseudoEntity}">
<script type="text/javascript">
ss_createOnLoadObj("commentsAndAttachmentsRegion${ss_divCounter}", function() {
	ss_toggleRegionInit('commentsAndAttachmentsRegion${ss_divCounter}', 'commentsAndAttachmentsRegionImg${ss_divCounter}', 300, '${regionClass}');
});
</script>
</c:if>
<div id="commentsAndAttachmentsRegion${ss_divCounter}" class="wg-tab-content">
<div id="viewAttachments${ss_tabDivCount}Div" 
  <c:if test="${empty ss_pseudoEntity}">style="display:none;"</c:if>
  <c:if test="${!empty ss_pseudoEntity}">style="display:block;"</c:if>
>
  <c:set var="property_caption" value="" scope="request"/>
  <c:set var="ss_showPrimaryFileAttachmentOnly" value="true" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachments_tab.jsp" />
</div>

<c:if test="${ssDefinitionEntry.top && empty ss_pseudoEntity}">
<div id="viewEntryHistory${ss_tabDivCount}Div" style="display:none;">
  <div id="viewEntryHistory${ss_tabDivCount}loading" 
    style="text-align:center;font-weight:bold;display:block;width:100%;"><img src="<html:rootPath/>images/pics/busyanim32b.gif"/></div>
  <iframe id="viewEntryHistory${ss_tabDivCount}Iframe" name="viewEntryHistory${ss_tabDivCount}Iframe" 
    onLoad="ss_resizeEntryHistoryIframe('viewEntryHistory${ss_tabDivCount}Iframe', 'viewEntryHistory${ss_tabDivCount}loading')" 
    src="<html:rootPath/>js/forum/null.html" class="wg-tab-iframe" >xxx</iframe>
</div>
<script type="text/javascript">
ss_createOnLayoutChangeObj('ss_resizeEntryHistoryIframe${ss_divCounter}',
		function() {ss_resizeEntryHistoryIframe('viewEntryHistory${ss_tabDivCount}Iframe');});

</script>
</c:if>

<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
<div id="viewFileVersions${ss_tabDivCount}Div" style="display:none;">
  <c:set var="property_caption" value="" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_file_versions.jsp" />
</div>
</c:if>

<c:if test="${empty ss_pseudoEntity}">
<div id="viewComments${ss_tabDivCount}Div" style="display:block;">
  <c:set var="property_caption" value="" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_replies.jsp" />
</div>
</c:if>
</div>

</div>
</c:if>
