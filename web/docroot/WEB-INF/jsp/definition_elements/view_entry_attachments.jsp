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
<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.ssfs.util.SsfsUtil" %>
<%
	boolean presence_service_enabled = org.kablink.teaming.util.SPropsUtil.getBoolean("presence.service.enable", false);
	String webdavSuffix = org.kablink.teaming.util.SPropsUtil.getString("webdav.folder.url.suffix", "");
%>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${empty ss_tabDivCount}">
  <c:set var="ss_tabDivCount" value="0" scope="request"/>
</c:if>
<c:set var="ss_tabDivCount" value="${ss_tabDivCount + 1}" scope="request"/>
<script type="text/javascript">
ss_createOnLoadObj("ss_initThisTab${ss_tabDivCount}", 
		function() {ss_initTab('viewAttachments${ss_tabDivCount}', '${ss_tabDivCount}');});
</script>

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

<div class="ss_entryContent">
<div style="text-align: left; margin: 0px 10px; border: 0pt none;" 
  class="wg-tabs margintop3 marginbottom2">
  <table cellspacing="0" cellpadding="0" width="100%">
  <tr>
  <td valign="middle" width="1%" nowrap>
  <div id="viewAttachments${ss_tabDivCount}Tab" 
    class="wg-tab roundcornerSM on" 
    onMouseOver="ss_hoverOverTab('viewAttachments${ss_tabDivCount}', '${ss_tabDivCount}');"
    onMouseOut="ss_hoverOverTabStopped('viewAttachments${ss_tabDivCount}', '${ss_tabDivCount}');"
    onClick="ss_showTab('viewAttachments${ss_tabDivCount}', '${ss_tabDivCount}');">
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
    onClick="ss_showTab('viewFileVersions${ss_tabDivCount}', '${ss_tabDivCount}');">
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
    onClick="ss_showTab('viewTags${ss_tabDivCount}', '${ss_tabDivCount}');ss_showHideEntryHistoryDiv${ss_divCounter}('viewTags${ss_tabDivCount}Iframe');return false;">
    <ssf:nlt tag="tags.tags"/>
  </div>
  </td>
  </c:if>
  
  <td valign="middle" width="98%" nowrap>&nbsp;</td>
  </tr>
  </table>
</div>

<div id="attachmentsRegion${ss_tabDivCount}" class="wg-tab-content">
<div id="viewAttachments${ss_tabDivCount}Div" 
  style="display:block;"
>
  <c:set var="property_caption" value="" scope="request"/>
  <c:set var="ss_showPrimaryFileAttachmentOnly" value="true" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachments_tab.jsp" />
</div>

<c:if test="${empty ss_pseudoEntity}">
<div id="viewTags${ss_tabDivCount}Div" class="ss_entryContent" style="display:none;" >
  <jsp:include page="/WEB-INF/jsp/definition_elements/tag_view.jsp" />
</div>
</c:if>

<c:if test="${empty ss_seenHistoryTab && empty ss_pseudoEntity}">
<div id="viewEntryHistory${ss_tabDivCount}Div" style="display:none;">
  <div id="viewEntryHistory${ss_tabDivCount}loading" 
    style="text-align:center;font-weight:bold;display:block;width:100%;"><ssf:nlt tag="Loading"/></div>
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

</div>

</div>
