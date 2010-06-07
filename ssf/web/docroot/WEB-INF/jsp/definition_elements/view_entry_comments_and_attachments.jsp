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
<c:if test="${empty ss_viewCommentsAttachmentsDivCount}">
  <c:set var="ss_viewCommentsAttachmentsDivCount" value="0" scope="request"/>
</c:if>
<c:set var="ss_viewCommentsAttachmentsDivCount" value="${ss_viewCommentsAttachmentsDivCount + 1}" scope="request"/>
<script type="text/javascript">
var currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount} = "viewComments${ss_viewCommentsAttachmentsDivCount}";
var currentCommentsAttachmentsHoverOverTab = null;
function ss_showCommentsAttachmentsTab(id) {
	if (currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount} != null) {
		var divObj = self.document.getElementById(currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount} + "Div")
		divObj.style.display = "none";
		var tabObj = self.document.getElementById(currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount} + "Tab");
		tabObj.className = "wg-tab roundcornerSM";
		currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount} = null;
	}
	currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount} = id;
	var divObj = self.document.getElementById(currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount} + "Div");
	var tabObj = self.document.getElementById(currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount} + "Tab");
	divObj.style.display = "block";
	tabObj.className = "wg-tab roundcornerSM on";
	
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

function ss_hoverOverCommentsAttachmentsTab(id) {
	if (currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount} != null) {
		var tabObj = self.document.getElementById(currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount} + "Tab");
		tabObj.className = "wg-tab roundcornerSM on";
	}
	if (currentCommentsAttachmentsHoverOverTab != null && 
			currentCommentsAttachmentsHoverOverTab != currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount}) {
		var tabObj = self.document.getElementById(currentCommentsAttachmentsHoverOverTab + "Tab");
		tabObj.className = "wg-tab roundcornerSM";
		currentCommentsAttachmentsHoverOverTab = null;
	}
	currentCommentsAttachmentsHoverOverTab = id;
	var tabObj = self.document.getElementById(id + "Tab");
	if (currentCommentsAttachmentsHoverOverTab == currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount}) {
		tabObj.className = "wg-tab roundcornerSM selected-menu on";
	} else {
		tabObj.className = "wg-tab roundcornerSM selected-menu";
	}
}

function ss_hoverOverStoppedCommentsAttachmentsTab(id) {
	if (currentCommentsAttachmentsHoverOverTab != null) {
		var tabObj = self.document.getElementById(currentCommentsAttachmentsHoverOverTab + "Tab");
		if (currentCommentsAttachmentsHoverOverTab == currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount}) {
			tabObj.className = "wg-tab roundcornerSM on";
		} else {
			tabObj.className = "wg-tab roundcornerSM";
		}
		currentCommentsAttachmentsHoverOverTab = null;
	}
	if (currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount} != null) {
		var tabObj = self.document.getElementById(currentCommentsAttachmentsTabShowing${ss_viewCommentsAttachmentsDivCount} + "Tab");
		tabObj.className = "wg-tab roundcornerSM on";
	}
}
</script>

<div class="ss_entryContent">
<div style="text-align: left; margin: 0px 10px; border: 0pt none;" 
  class="wg-tabs margintop3 marginbottom2">
  <table cellspacing="0" cellpadding="0">
  <tr>
  <c:if test="${empty ss_pseudoEntity}">
  <td valign="middle">
  <div id="viewComments${ss_viewCommentsAttachmentsDivCount}Tab" class="wg-tab roundcornerSM on" 
    onMouseOver="ss_hoverOverCommentsAttachmentsTab('viewComments${ss_viewCommentsAttachmentsDivCount}');"
    onMouseOut="ss_hoverOverStoppedCommentsAttachmentsTab('viewComments${ss_viewCommentsAttachmentsDivCount}');"
    onClick="ss_showCommentsAttachmentsTab('viewComments${ss_viewCommentsAttachmentsDivCount}');">
    <ssf:nlt tag="__entry_comments"/>
  </div>
  </td>
  </c:if>
  <td valign="middle">
  <div id="viewAttachments${ss_viewCommentsAttachmentsDivCount}Tab" 
    class="wg-tab roundcornerSM <c:if test="${!empty ss_pseudoEntity}">on</c:if>" 
    onMouseOver="ss_hoverOverCommentsAttachmentsTab('viewAttachments${ss_viewCommentsAttachmentsDivCount}');"
    onMouseOut="ss_hoverOverStoppedCommentsAttachmentsTab('viewAttachments${ss_viewCommentsAttachmentsDivCount}');"
    onClick="ss_showCommentsAttachmentsTab('viewAttachments${ss_viewCommentsAttachmentsDivCount}');">
    <ssf:nlt tag="__entry_attachments"/>
  </div>
  </td>
  <td valign="middle">
  <div id="viewFileVersions${ss_viewCommentsAttachmentsDivCount}Tab" 
    class="wg-tab roundcornerSM <c:if test="${!empty ss_pseudoEntity}">on</c:if>" 
    onMouseOver="ss_hoverOverCommentsAttachmentsTab('viewFileVersions${ss_viewCommentsAttachmentsDivCount}');"
    onMouseOut="ss_hoverOverStoppedCommentsAttachmentsTab('viewFileVersions${ss_viewCommentsAttachmentsDivCount}');"
    onClick="ss_showCommentsAttachmentsTab('viewFileVersions${ss_viewCommentsAttachmentsDivCount}');">
    <ssf:nlt tag="__entry_file_versions"/>
  </div>
  </td>
  </tr>
  </table>
</div>

<div id="viewAttachments${ss_viewCommentsAttachmentsDivCount}Div" 
  <c:if test="${empty ss_pseudoEntity}">style="display:none;"</c:if>
  <c:if test="${!empty ss_pseudoEntity}">style="display:block;"</c:if>
  class="wg-tab-content">
  <c:set var="property_caption" value="" scope="request"/>
  <c:set var="ss_showPrimaryFileAttachmentOnly" value="true" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachments.jsp" />
</div>

<div id="viewFileVersions${ss_viewCommentsAttachmentsDivCount}Div" 
  style="display:none;"
  class="wg-tab-content">
  <c:set var="property_caption" value="" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_file_versions.jsp" />
</div>

<c:if test="${empty ss_pseudoEntity}">
<div id="viewComments${ss_viewCommentsAttachmentsDivCount}Div" style="display:block;" class="wg-tab-content">
  <c:set var="property_caption" value="" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_replies.jsp" />
</div>
</c:if>

</div>
