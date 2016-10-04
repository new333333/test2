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
<c:set var="ss_commentsAndAttachmentsSectionRequested" value="true" scope="request"/>
<c:if test="${!ss_delayShowingCommentsAndAttachments}">
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
<c:set var="ss_thisCurrentTab" value="viewComments"/>
<c:if test="${!empty ss_pseudoEntity}">
  <c:set var="ss_thisCurrentTab" value="viewAttachments"/>
</c:if>
<c:if test="${!empty ss_showCommentsAttachmentsTab}">
  <c:set var="ss_thisCurrentTab" value="${ss_showCommentsAttachmentsTab}"/>
</c:if>


<script type="text/javascript">
ss_createOnLoadObj("ss_initThisTab${ss_tabDivCount}", 
		function() {ss_initTab('${ss_thisCurrentTab}${ss_tabDivCount}', '${ss_tabDivCount}');});
</script>

<div style="text-align: left; border: 0px;" class="wg-tabs margintop2">
	<table cellspacing="0" cellpadding="0" width="100%">
	  <tr>
	  <c:if test="${empty ss_pseudoEntity}">
	  <td valign="middle" width="1%" nowrap>
	  <div id="viewComments${ss_tabDivCount}Tab" class="wg-tab roundcornerSM <c:if test="${ss_thisCurrentTab == 'viewComments'}">on</c:if>" 
		onMouseOver="ss_hoverOverTab('viewComments${ss_tabDivCount}', '${ss_tabDivCount}');"
		onMouseOut="ss_hoverOverTabStopped('viewComments${ss_tabDivCount}', '${ss_tabDivCount}');"
		onClick="ss_showTab('viewComments${ss_tabDivCount}', '${ss_tabDivCount}');">
		<c:if test="${propertyValues_tabTitle[0] != 'replies'}">
		  <ssf:nlt tag="__entry_comments"/>
		</c:if>
		<c:if test="${propertyValues_tabTitle[0] == 'replies'}">
		  <ssf:nlt tag="__entry_replies"/>
		</c:if>
		<c:if test="${ssEntry == ssDefinitionEntry}">
		  <span class="ss_smallprint">(${fn:length(ssFolderEntryDescendants)})</span>
		</c:if>
	  </div>
	  </td>
	  </c:if>
	  <td valign="middle" width="1%" nowrap>
	  <div id="viewAttachments${ss_tabDivCount}Tab" 
		class="wg-tab roundcornerSM <c:if test="${!empty ss_pseudoEntity || ss_thisCurrentTab == 'viewAttachments'}">on</c:if>" 
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
	  <c:if test="${empty ss_seenHistoryTab && empty ss_pseudoEntity}">
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
	  <c:if test="${!empty ssDefinitionEntry.fileAttachments && !ss_isBinderMirroredFolder}">
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
	  
	  <c:if test="${empty ss_pseudoEntity}">
	  <td valign="middle" width="1%" nowrap>
	  <div id="viewTags${ss_tabDivCount}Tab" 
		class="wg-tab roundcornerSM" 
		onMouseOver="ss_hoverOverTab('viewTags${ss_tabDivCount}', '${ss_tabDivCount}');"
		onMouseOut="ss_hoverOverTabStopped('viewTags${ss_tabDivCount}', '${ss_tabDivCount}');"
		onClick="ss_showTab('viewTags${ss_tabDivCount}', '${ss_tabDivCount}');return false;">
		<ssf:nlt tag="tags.tags"/>
	  </div>
	  </td>
	  </c:if>
	  
	  <td align="right" width="97%" >&nbsp;
		<c:if test="${ssDefinitionEntry.top && empty ss_pseudoEntity}">
		<div id="commentsAndAttachmentsRegionImg${ss_divCounter}" style="display:none;">
		<a href="javascript: ;" 
		  onClick="ss_toggleRegion(this, 'commentsAndAttachmentsRegion${ss_divCounter}', 'commentsAndAttachmentsRegion', 'wg-tab-content', 300);return false;" 
		  alt="<ssf:nlt tag="general.expandCollapseRegion"/>" title="<ssf:nlt tag="general.expandCollapseRegion"/>"
		><img border="0" style="padding-bottom: 1px;" src="<html:rootPath/>images/pics/${regionImg}" /></a>
		</div>
		</c:if>
	  </td>
	  <td align="right" width="97%" >&nbsp;
		<c:if test="${ssDefinitionEntry.top && empty ss_pseudoEntity}">
		<div id="commentsAndAttachmentsRegionImg${ss_divCounter}" style="display:none;">
		<a href="javascript: ;" 
		  onClick="ss_toggleRegion(this, 'commentsAndAttachmentsRegion${ss_divCounter}', 'commentsAndAttachmentsRegion', 'wg-tab-content', 300);return false;" 
		  alt="<ssf:nlt tag="general.expandCollapseRegion"/>" title="<ssf:nlt tag="general.expandCollapseRegion"/>"
		><img border="0" style="padding-bottom: 1px;" src="<html:rootPath/>images/pics/${regionImg}"/></a>
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
  <c:if test="${empty ss_pseudoEntity || ss_thisCurrentTab == 'viewAttachments'}">style="display:none;"</c:if>
  <c:if test="${!empty ss_pseudoEntity && ss_thisCurrentTab == 'viewAttachments'}">style="display:block;"</c:if>
>
  <c:set var="property_caption" value="" scope="request"/>
  <c:set var="ss_showPrimaryFileAttachmentOnly" value="true" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachments_tab.jsp" />
</div>

<c:if test="${empty ss_seenHistoryTab && empty ss_pseudoEntity}">
<div id="viewEntryHistory${ss_tabDivCount}Div" style="display:none;">
  <div id="viewEntryHistory${ss_tabDivCount}loading" 
    style="text-align:center;font-weight:bold;display:block;width:100%;"><img src="<html:rootPath/>images/pics/busyanim32b.gif"/></div>
  <iframe id="viewEntryHistory${ss_tabDivCount}Iframe" name="viewEntryHistory${ss_tabDivCount}Iframe" 
    onLoad="ss_resizeEntryHistoryIframe('viewEntryHistory${ss_tabDivCount}Iframe', 'viewEntryHistory${ss_tabDivCount}loading')" 
    src="<html:rootPath/>js/forum/null.html" class="wg-tab-iframe" >Micro Focus Vibe</iframe>
</div>
<script type="text/javascript">
ss_createOnLayoutChangeObj('ss_resizeEntryHistoryIframe${ss_divCounter}',
		function() {ss_resizeEntryHistoryIframe('viewEntryHistory${ss_tabDivCount}Iframe');});

</script>
<c:set var="ss_seenHistoryTab" value="true" scope="request"/>
</c:if>

<c:if test="${!empty ssDefinitionEntry.fileAttachments && !ss_isBinderMirroredFolder}">
<div id="viewFileVersions${ss_tabDivCount}Div" style="display:none;">
  <c:set var="property_caption" value="" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_file_versions.jsp" />
</div>
</c:if>

<c:if test="${empty ss_pseudoEntity}">
<div id="viewTags${ss_tabDivCount}Div" class="ss_entryContent" style="display:none;" >
  <jsp:include page="/WEB-INF/jsp/definition_elements/tag_view.jsp" />
</div>
</c:if>

<c:if test="${empty ss_pseudoEntity}">
<div id="viewComments${ss_tabDivCount}Div" 
  <c:if test="${empty ss_pseudoEntity || ss_thisCurrentTab == 'viewComments'}">style="display:none;"</c:if>
  <c:if test="${!empty ss_pseudoEntity && ss_thisCurrentTab == 'viewComments'}">style="display:block;"</c:if>
>
  <c:set var="property_caption" value="" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_replies.jsp" />
</div>
</c:if>
</div>

</c:if>
</c:if>
