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
<% //view a wiki folder or a wiki entry %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${!ss_wikiFolderList && (!empty ss_wikiHomepageEntry)}">
  <c:set var="ss_showHelpIcon" value="true" scope="request"/>
  <script type="text/javascript">
  var url = "<ssf:url     
      adapter="true" 
          portletName="ss_forum" 
          folderId="${ss_wikiHomepageEntry.parentBinder.id}" 
          action="view_folder_entry" 
          entryId='${ss_wikiHomepageEntry.id}' actionUrl="true"><ssf:param
          name="entryViewStyle" value="popup"/><ssf:param
          name="namespace" value="${renderResponse.namespace}"/></ssf:url>";
  if ((typeof window.top.ss_contextLoaded != "undefined") &&
		((window.name == "gwtContentIframe") || (window.name == "ss_showentryframe"))) {
	  window.top.ss_contextLoaded("${ss_wikiHomepageEntry.parentBinder.id}", "false", "");
  }
  self.location.replace(url);
  </script>
  
</c:if>

<c:if test="${ss_wikiFolderList || empty ss_wikiHomepageEntry}">

<script type="text/javascript">
var ss_showAsWiki = false;
if (self.window.name.indexOf("gwtContentIframe") == 0) {
	ss_showAsWiki = true;  //Indicate that we are showing a wiki folder so wiki links don't pop-up
}

var ss_columnCount = 0;

//Routine called when "find wiki page" is clicked
function ss_loadWikiEntryId${renderResponse.namespace}(id) {
	var urlParams = {entryId:id, namespace:'${renderResponse.namespace}', entryViewStyle:'popup'};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "view_folder_listing");
	self.location.href = url;
}
	
</script>
<% // Show the Wiki Page search widget %>
<%@ include file="/WEB-INF/jsp/definition_elements/wiki/wiki_find_page.jsp" %>

<% // Show the folder or entry %>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}" 
  entry="${item}" />
</c:if>
