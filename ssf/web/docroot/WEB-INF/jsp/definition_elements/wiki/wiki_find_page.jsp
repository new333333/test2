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
<c:if test="${!ss_wikiFindPageSeen}">
<c:set var="ss_wikiFindPageSeen" value="true" scope="request"/>

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
<table class="ss_wiki_search_bar" width="100%">
  <tr>
    <td align="right" width="95%" valign="top">        
      <c:if test="${ssConfigJspStyle != 'template'}">
	    <form method="post" 
	        name="ss_findWikiPageForm${renderResponse.namespace}"
	        id="ss_findWikiPageForm${renderResponse.namespace}"
	    	action="<ssf:url action="view_folder_listing" actionUrl="true"><ssf:param 
					name="binderId" value="${ssBinder.id}"/></ssf:url>">
         	 <span><ssf:nlt tag="wiki.findPage"/></span>
		 <ssf:find formName="ss_findWikiPageForm${renderResponse.namespace}" 
		    formElement="searchTitle" 
		    type="entries"
		    width="140px" 
		    binderId="${ssBinder.id}"
		    searchSubFolders="true"
		    showFolderTitles="true"
		    singleItem="true"
		    clickRoutine="ss_loadWikiEntryId${renderResponse.namespace}"
		    accessibilityText="wiki.findPage"
		    /> 
	    </form>
	  </c:if>
	</td>
	<td width="5%" valign="top" nowrap>
	  	<a class="ss_actions_bar13_pane_none" href="javascript: window.print();"><img border="0" 
	      alt="<ssf:nlt tag="navigation.print"/>" title="<ssf:nlt tag="navigation.print"/>"
	      src="<html:rootPath/>images/pics/masthead/masthead_printer.png" width="23" height="21"/></a>&nbsp;&nbsp;
	    <a class="ss_actions_bar13_pane_none"
	      href="javascript: ss_helpSystem.run();"><img border="0"
	      <ssf:alt tag="navigation.help"/> src="<html:imagesPath/>icons/help.png" />
	    </a>&nbsp;&nbsp;
	</td>
  </tr>
</table>
</c:if>
