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
<% //view a folder entry in an iframe %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<script type="text/javascript">
	//Define the variables needed by the javascript routines
	var ss_iframe_box_div_name = 'ss_iframe_box_div';
	
	<c:if test="${!empty ss_entryWindowTop && !empty ss_entryWindowLeft}">
		var ss_entryWindowTopOriginal = ${ss_entryWindowTop};
		var ss_entryWindowTop = ${ss_entryWindowTop};
		var ss_entryWindowLeft = ${ss_entryWindowLeft};
	</c:if>
	<c:if test="${empty ss_entryWindowTop || empty ss_entryWindowLeft}">
		var ss_entryWindowTopOriginal = -1;
		var ss_entryWindowTop = -1;
		var ss_entryWindowLeft = -1;
	</c:if>
	
	
	var ss_forumRefreshUrl = "<html:rootPath/>js/forum/refresh.html";
	<c:if test="${empty ss_entryWindowWidth}">
		var ss_entryWindowWidth = 0;
	</c:if>
	<c:if test="${!empty ss_entryWindowWidth}">
		var ss_entryWindowWidth = "${ss_entryWindowWidth}";
	</c:if>
	var ss_entryBackgroundColor = "${ss_style_background_color}";
</script>
<script type="text/javascript" src="<html:rootPath/>js/forum/view_iframe.js"></script>

<div id="ss_showentrydiv${renderResponse.namespace}" 
  onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}"
  style="position:absolute; visibility:hidden;
  width:600px; height:80%; display:none;">
  <ssf:box>
    <ssf:param name="box_id" value="ss_iframe_box_div${renderResponse.namespace}" />
    <ssf:param name="box_width" value="400" />
    <ssf:param name="box_color" value="${ss_entry_border_color}" />
    <ssf:param name="box_canvas_color" value="${ss_style_background_color}" />
    <ssf:param name="box_title" useBody="true">
      <div style="position:relative;">
      <c:set var="ss_history_bar_table_class" value="ss_title_bar_history_bar" scope="request"/>
      <jsp:include page="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" />
      </div>
    </ssf:param>
    <ssf:param name="box_show_resize_icon" value="true" />
    <ssf:param name="box_show_resize_routine" value="ss_startDragDiv('resize')" />
    <ssf:param name="box_show_resize_gif" value="icons/resize_east_west.gif" />
    <ssf:param name="box_show_move_icon" value="true" />
    <ssf:param name="box_show_move_routine" value="ss_startDragDiv('move')" />
    <ssf:param name="box_show_close_icon" value="true" />
    <ssf:param name="box_show_close_routine" value="ss_hideEntryDiv()" />
  <iframe id="ss_showentryframe${renderResponse.namespace}" 
    name="ss_showentryframe${renderResponse.namespace}" style="width:99%; 
    display:block; position:relative; left:5px;"
    src="<html:rootPath/>js/forum/null.html" 
    height="99%" width="99%" 
    onLoad="if (self.ss_setEntryDivHeight && self.document.getElementById('ss_showentrydiv') && self.document.getElementById('ss_showentrydiv').style.display != 'none') ss_setEntryDivHeight();" 
    frameBorder="0" >xxx</iframe>
  </ssf:box>
</div>

<form class="ss_style ss_form" name="ss_saveEntryWidthForm" 
  id="ss_saveEntryWidthForm${renderResponse.namespace}" >
	<input type="hidden" name="entry_width">
	<input type="hidden" name="entry_top">
	<input type="hidden" name="entry_left">
</form>
<script type="text/javascript">
ss_createOnLoadObj("${renderResponse.namespace}_showEntryDivInitialization", ss_showEntryDivInitialization${renderResponse.namespace});
function ss_showEntryDivInitialization${renderResponse.namespace}() {
	ss_showEntryDivInitialization('${renderResponse.namespace}');
}
</script>
