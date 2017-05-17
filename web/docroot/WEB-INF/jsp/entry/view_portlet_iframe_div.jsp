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
<% //view a folder entry in an iframe %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
	String portletOverlayDiv = renderResponse.getNamespace() + "_portlet_overlay_div";
	String portletOverlayInnerDiv = renderResponse.getNamespace() + "_portlet_overlay_inner_div";
	String portletOverlayInnerIframe = renderResponse.getNamespace() + "_portlet_overlay_inner_iframe";
	String portletOverlayForm = renderResponse.getNamespace() + "_ss_saveEntryWidthForm";

	String strDivCloseFunction = "ss_hideEntryDiv('" + portletOverlayDiv + "')";
	String strDivResizeFunction = "ss_startDragDiv('resize', '" + portletOverlayDiv + "')";
	String strDivMoveFunction = "ss_startDragDiv('move', '" + portletOverlayDiv + "')";
%>

<br/><br/>

<script type="text/javascript">
	//Define the variables needed by the javascript routines
	//var ss_iframe_box_div_name = 'ss_iframe_box_div';
	//var ss_box_iframe_name = '${renderResponse.namespace}_box_iframe';	
	
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
<script type="text/javascript" src="<html:rootPath/>js/forum/view_portlet_iframe.js"></script>


<div id="<%= portletOverlayDiv %>" name="<%= portletOverlayDiv %>" 
  onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}"
  style="position:absolute; visibility:hidden;
  width:600px; height:80%; display:none;">
  <ssf:box>
    <ssf:param name="box_id" value="<%= portletOverlayInnerDiv %>" />
    <ssf:param name="box_title_id" value="ss_showEntryDivTitle" />
    <ssf:param name="box_class" value="ss_box_top_rounded ss_class_showentryinternaldiv" />
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
    <ssf:param name="box_show_resize_routine" value="<%= strDivResizeFunction %>" />
    <ssf:param name="box_show_resize_gif" value="icons/resize_east_west.gif" />
    <ssf:param name="box_show_move_icon" value="true" />
    <ssf:param name="box_show_move_routine" value="<%= strDivMoveFunction %>" />
    <ssf:param name="box_show_close_icon" value="true" />
    <ssf:param name="box_show_close_routine" value="<%= strDivCloseFunction %>" />
  <iframe id="<%= portletOverlayInnerIframe %>" name="<%= portletOverlayInnerIframe %>" 
    class="ss_class_showentryframe" style="width:100%; 
    display:block; position:relative; left:5px;"
    src="<html:rootPath/>js/forum/null.html" 
    height="95%" width="100%" 
    onLoad="if (self.ss_setEntryDivHeight && ss_selectedDiv != null && ss_selectedDiv.style.display != 'none') ss_setEntryDivHeight();" 
    frameBorder="0" title="<ssf:nlt tag = "iframe.entry"/>" >Micro Focus Vibe</iframe>
  </ssf:box>
</div>

<form class="ss_style ss_form" name="<%= portletOverlayForm %>" id="<%= portletOverlayForm %>" >
	<input type="hidden" name="entry_width">
	<input type="hidden" name="entry_top">
	<input type="hidden" name="entry_left">
  <sec:csrfInput />
</form>
