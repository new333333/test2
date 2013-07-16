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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<% // History and entry navigation bar %>
<c:if test="${!empty ssFolder.id && !empty ssFolderEntries}">
<c:if test="${empty ss_history_bar_loaded}">
<c:set var="ss_history_bar_imageId" value="0" scope="request"/>
<c:set var="ss_history_bar_loaded" value="1" scope="request"/>
<script type="text/javascript">
if (!ss_history_bar_loaded || ss_history_bar_loaded == "undefined" ) {
var ss_entriesSeen = new Array();
var ss_entryList = new Array();
// var ss_entryList2 = new Array();
// var ss_entryList3 = new Array();
var ss_entryCount = 0;
<c:forEach var="entry" items="${ssFolderEntries}" >
  <c:if test="${entry._entityType != 'folder' && entry._entityType != 'workspace' && entry._entityType != 'group'}">
    if (typeof ss_entriesSeen['docId${entry._docId}'] == "undefined") {
    	ss_entryList[ss_entryCount++] = { 
    		index : '${entry._docId}',
    		entryId : '${entry._docId}',
    		binderId : '${entry._binderId}',
    		entityType : '${entry._entityType}'
		};
    	ss_entriesSeen['docId${entry._docId}'] = 1;
    }
  </c:if>
</c:forEach>

var left_end = "<html:imagesPath/>pics/sym_s_left_end.gif";
var left = "<html:imagesPath/>pics/sym_s_arrow_down.gif";
var right_end = "<html:imagesPath/>pics/sym_s_right_end.gif";
var right = "<html:imagesPath/>pics/sym_s_arrow_up.gif";
var left_end_g = "<html:imagesPath/>pics/sym_s_left_end_g.gif";
var left_g = "<html:imagesPath/>pics/sym_s_arrow_down_g.gif";
var right_end_g = "<html:imagesPath/>pics/sym_s_right_end_g.gif";
var right_g = "<html:imagesPath/>pics/sym_s_arrow_up_g.gif";
var g_alt = "<ssf:nlt tag="nav.noEntries" text="No more entries"/>";
var left_alt = "<ssf:nlt tag="nav.prevEntry" text="Previous entry"/>"
var left_end_alt = "<ssf:nlt tag="nav.firstEntry" text="First entry"/>"
var right_alt = "<ssf:nlt tag="nav.nextEntry" text="Next entry"/>"
var right_end_alt = "<ssf:nlt tag="nav.lastEntry" text="Last entry"/>"

var ss_baseHistoryUrl = '<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="ssBinderIdPlaceHolder"
          entryId="ssEntryIdPlaceHolder"
          action="ssActionPlaceHolder"
          operation="view_entry"
          actionUrl="true"
          />';
          
}
var ss_history_bar_loaded = 1;

</script>
</c:if>
<c:set var="ss_history_bar_imageId" value="${ss_history_bar_imageId + 1}" scope="request"/>
<c:if test="${empty ss_history_bar_table_class}">
  <c:set var="ss_history_bar_table_class" value="ss_actions_bar_background" scope="request"/>
</c:if>
<table class="${ss_history_bar_table_class}" cellspacing="0" cellpadding="0">
  <tr>

<c:if test="${ssConfigJspStyle != 'template'}">
     <c:if test="${!empty ssFolder.id && !empty ssFolderEntries}">
     <td><a  
          href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="${ssFolder.id}"
          action="view_folder_entry"        
          operation="entry_previous"
          actionUrl="true"
          />"
          onClick="if (self.ss_getNextEntryId) ss_getNextEntryId('${ss_history_bar_imageId}'); return false;" ><img
          alt="<ssf:nlt tag="nav.prevEntry" text="Previous entry"/>" id="ss_prev"
          border="0" src="<html:imagesPath/>pics/sym_s_arrow_down.gif"></a></td>
          
     <td nowrap><span style="padding:0px 6px;"><ssf:nlt tag="nav.view"/></span></td>
     
     <td><a href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="${ssFolder.id}"
          action="view_folder_entry"
          operation="entry_next"
          actionUrl="true"
          />"
          onClick="if (self.ss_getPreviousEntryId) ss_getPreviousEntryId('${ss_history_bar_imageId}');return false;" ><img
          alt="<ssf:nlt tag="nav.nextEntry" text="Next entry"/>" id="ss_next"
          border="0" src="<html:imagesPath/>pics/sym_s_arrow_up.gif"></a></td>
	</c:if>
</c:if>
<c:if test="${ssConfigJspStyle == 'template'}">

     <td><img alt="" src="<html:imagesPath/>pics/sym_s_arrow_down.gif"/></td>
    <td nowrap><span style="padding:0px 6px;"><ssf:nlt tag="nav.view"/></span></td>
     <td><img alt="" src="<html:imagesPath/>pics/sym_s_arrow_up.gif"/></td>
</c:if>
         
  </tr>
</table>
<div id="ss_historyNoMoreEntries" style="position:absolute; display:none; visibility:hidden;
  border:1px solid black; background-color:#fff; x-index:1000;">
  <span><ssf:nlt tag="nav.noEntries"/></span>
</div>
</c:if>
