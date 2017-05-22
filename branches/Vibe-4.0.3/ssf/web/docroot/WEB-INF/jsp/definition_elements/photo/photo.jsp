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
<% // Photo album folder view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />

<script type="text/javascript">
var ss_columnCount = 0;

//Routine called when "find photo" is clicked
function ss_loadPhotoEntryId${renderResponse.namespace}(id) {
	var url = "<ssf:url     
	    adapter="true" 
	    portletName="ss_forum" 
	    folderId="${ssBinder.id}" 
	    action="view_folder_entry" 
	    entryId="ss_entryIdPlaceholder" 
	    actionUrl="true" ><ssf:param 
	    name="operation" value="view_photo"/><ssf:param 
	    name="standalone" value="true"/></ssf:url>";
	url = ss_replaceSubStr(url, 'ss_entryIdPlaceholder', id);
	ss_openUrlInPortlet(url, "_blank");
	return false;
}

var ss_photoIframeOffset = 20;
function ss_setPhotoIframeSize${renderResponse.namespace}() {
	var targetDiv = document.getElementById('ss_photoEntryDiv${renderResponse.namespace}')
	var iframeDiv = document.getElementById('ss_photoIframe${renderResponse.namespace}')
	if (window.frames['ss_photoIframe${renderResponse.namespace}'] != null) {
		eval("var iframeHeight = parseInt(window.ss_photoIframe${renderResponse.namespace}.document.body.scrollHeight);")
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_photoIframeOffset + "px"
		}
	}
}

</script>

<div class="ss_folder_border">
<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar5 ss_actions_bar">
<ssHelpSpot 
  		helpId="workspaces_folders/menus_toolbars/folder_toolbar" offsetX="0" offsetY="0" 
  		title="<ssf:nlt tag="helpSpot.folderControlAndFiltering"/>"></ssHelpSpot>
 <% // Entry toolbar %>
 <ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar5 ss_actions_bar" item="true" />
</ssf:toolbar>
<div class="ss_clear"></div>
</div>
<jsp:include page="/WEB-INF/jsp/forum/add_files_to_folder.jsp" />
<jsp:include page="/WEB-INF/jsp/forum/page_navigation_bar.jsp" />
<c:set var="currentAlbum" value="${ssBinder.id}"/>
<table cellspacing="0" cellpadding="0" width="100%">
  <tbody>
	<tr>
	  <td valign="top">
		<div class="ss_folder" id="ss_photo_folder_div">
		  <jsp:include page="/WEB-INF/jsp/definition_elements/photo/photo_folder_listing.jsp" />
		</div>
	  </td>
	  <td valign="top" width="200">
		<div id="ss_sideNav_wrap" style="padding-top: 20px; padding-bottom: 10px;">
		  <jsp:include page="/WEB-INF/jsp/sidebars/photo.jsp" />
		</div>
	  </td>
	</tr>
  </tbody>
</table>
<jsp:include page="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" />
