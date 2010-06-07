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
<script type="text/javascript">
var currentCommentsAttachmentsTabShowing = "viewComments";
var currentCommentsAttachmentsHoverOverTab = null;
function ss_showCommentsAttachmentsTab(id) {
	if (currentCommentsAttachmentsTabShowing != null) {
		var divObj = self.document.getElementById(currentCommentsAttachmentsTabShowing + "Div")
		divObj.style.display = "none";
		var tabObj = self.document.getElementById(currentCommentsAttachmentsTabShowing + "Tab");
		tabObj.className = "wg-tab roundcornerSM";
		currentCommentsAttachmentsTabShowing = null;
	}
	currentCommentsAttachmentsTabShowing = id;
	var divObj = self.document.getElementById(currentCommentsAttachmentsTabShowing + "Div");
	var tabObj = self.document.getElementById(currentCommentsAttachmentsTabShowing + "Tab");
	divObj.style.display = "block";
	tabObj.className = "wg-tab roundcornerSM on";
	
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

function ss_hoverOverCommentsAttachmentsTab(id) {
	if (currentCommentsAttachmentsTabShowing != null) {
		var tabObj = self.document.getElementById(currentCommentsAttachmentsTabShowing + "Tab");
		tabObj.className = "wg-tab roundcornerSM on";
	}
	if (currentCommentsAttachmentsHoverOverTab != null && 
			currentCommentsAttachmentsHoverOverTab != currentCommentsAttachmentsTabShowing) {
		var tabObj = self.document.getElementById(currentCommentsAttachmentsHoverOverTab + "Tab");
		tabObj.className = "wg-tab roundcornerSM";
		currentCommentsAttachmentsHoverOverTab = null;
	}
	currentCommentsAttachmentsHoverOverTab = id;
	var tabObj = self.document.getElementById(id + "Tab");
	if (currentCommentsAttachmentsHoverOverTab == currentCommentsAttachmentsTabShowing) {
		tabObj.className = "wg-tab roundcornerSM selected-menu on";
	} else {
		tabObj.className = "wg-tab roundcornerSM selected-menu";
	}
}

function ss_hoverOverStoppedCommentsAttachmentsTab(id) {
	if (currentCommentsAttachmentsHoverOverTab != null) {
		var tabObj = self.document.getElementById(currentCommentsAttachmentsHoverOverTab + "Tab");
		if (currentCommentsAttachmentsHoverOverTab == currentCommentsAttachmentsTabShowing) {
			tabObj.className = "wg-tab roundcornerSM on";
		} else {
			tabObj.className = "wg-tab roundcornerSM";
		}
		currentCommentsAttachmentsHoverOverTab = null;
	}
	if (currentCommentsAttachmentsTabShowing != null) {
		var tabObj = self.document.getElementById(currentCommentsAttachmentsTabShowing + "Tab");
		tabObj.className = "wg-tab roundcornerSM on";
	}
}
</script>

<div class="ss_entryContent">
<div style="text-align: left; margin: 0px 10px; border: 0pt none;" 
  class="wg-tabs margintop3 marginbottom2">
  <table cellspacing="0" cellpadding="0">
  <tr>
  <td valign="middle">
  <div id="viewCommentsTab" class="wg-tab roundcornerSM on" 
    onMouseOver="ss_hoverOverCommentsAttachmentsTab('viewComments');"
    onMouseOut="ss_hoverOverStoppedCommentsAttachmentsTab('viewComments');"
    onClick="ss_showCommentsAttachmentsTab('viewComments');">
    <ssf:nlt tag="__entry_comments"/>
  </div>
  </td>
  <td valign="middle">
  <div id="viewAttachmentsTab" class="wg-tab roundcornerSM" 
    onMouseOver="ss_hoverOverCommentsAttachmentsTab('viewAttachments');"
    onMouseOut="ss_hoverOverStoppedCommentsAttachmentsTab('viewAttachments');"
    onClick="ss_showCommentsAttachmentsTab('viewAttachments');">
    <ssf:nlt tag="__entry_attachments"/>
  </div>
  </td>
  </tr>
  </table>
</div>

<div id="viewAttachmentsDiv" style="display:none;" class="wg-tab-content">
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachments.jsp" />
</div>

<div id="viewCommentsDiv" style="display:block;" class="wg-tab-content">
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_replies.jsp" />
</div>

</div>
