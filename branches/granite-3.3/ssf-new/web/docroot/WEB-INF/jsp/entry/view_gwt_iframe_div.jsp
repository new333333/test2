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
<%@ page import="org.kablink.util.BrowserSniffer" %>
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
	function ss_setEntryDivHeightOnload() {
		if (typeof ss_setEntryDivHeight != 'undefined') {
			ss_setEntryDivHeight();
			ss_setCurrentIframeHeight();
		}
		ss_setEntryPopupIframeSize();
	}

</script>
<script type="text/javascript" src="<html:rootPath/>js/forum/view_iframe.js"></script>

<div id="ss_showentrydiv" 
  onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}"
  style="position:absolute; visibility:hidden; display:none; background-color:#fff;">
  <c:if test="${ssUser.displayStyle != 'newpage' && ssUser.displayStyle != 'accessible'}">
	  <ssf:box>
	    <ssf:param name="box_id" value="ss_iframe_box_div" />
	    <ssf:param name="box_title_id" value="ss_showEntryDivTitle" />
	    <ssf:param name="box_width" value="400" />
	    <ssf:param name="box_color" value="${ss_entry_border_color}" />
	    <ssf:param name="box_canvas_color" value="${ss_style_background_color_opaque}" />
	    <ssf:param name="box_show_resize_icon" value="true" />
	    <ssf:param name="box_show_resize_routine" value="ss_startDragDiv('resize')" />
	    <ssf:param name="box_show_resize_gif" value="icons/resize_east_west.gif" />
	    <ssf:param name="box_show_move_icon" value="true" />
	    <ssf:param name="box_show_move_routine" value="ss_startDragDiv('move')" />
	    <ssf:param name="box_show_close_icon" value="true" />
	    <ssf:param name="box_show_close_routine" value="ss_hideEntryDiv()" />
		  <div id="ss_iframe_holder_div" style="padding: 0 4px;">
		  <iframe id="ss_showentryframe"
		  	title="<ssf:nlt tag = "iframe.entry"/>" 
		    name="ss_showentryframe" 
		    style="width:100%; display:block; background-color:#fff;"
		    src="<html:rootPath/>js/forum/null.html" 
		    onLoad="ss_setEntryDivHeightOnload();" 
		    frameBorder="0" >Novell Vibe</iframe>
		  </div>
	  </ssf:box>
  </c:if>
  <c:if test="${ssUser.displayStyle == 'newpage' || ssUser.displayStyle == 'accessible'}">
	  <div class="ss_newpage_box" id="ss_iframe_box_div">
	    <div id="ss_iframe_holder_div" >
	      <div class="ss_newpage_box_header" >
	        <table cellspacing="0" cellpadding="0" 
	          <% if (!BrowserSniffer.is_ie(request)) { %> width="100%" <% } %>
	          <% if (BrowserSniffer.is_ie(request) && !BrowserSniffer.is_ie_6(request)) { %> width="98%" <% } %>
	        >
	        <tr>
	        <td valign="top" nowrap width="90%">
	          <span class="ss_newpage_box_title" id="ss_showEntryDivTitle"></span>
	        </td>
	        <td valign="top" align="right" nowrap width="6%">
	          <span class="ss_newpage_box_next_prev" id="ss_showEntryDivNextPrev"></span>
	        </td>
	        <td valign="top" align="right" nowrap width="4%" >
	          <span class="ss_newpage_box_close ss_close_text">
	            <a href="javascript: ;" onClick="ss_hideEntryDiv();return false;" title="<ssf:nlt tag="button.close"/>"><ssf:nlt tag="button.close"/>&nbsp;<img 
					  	border="0" src="<html:imagesPath/>icons/close_teal16.png" align="absmiddle" /></a>
	          </span>
	        </td>
	        </tr>
	        </table>
	      </div>
	      <iframe id="ss_showentryframe"
	  	    title="<ssf:nlt tag = "iframe.entry"/>" 
	        name="ss_showentryframe" style="width:100%; 
	        display:block; background-color:#fff;"
	        src="<html:rootPath/>js/forum/null.html" 
	        onLoad="ss_setEntryDivHeightOnload();" 
	        frameBorder="0" >Novell Vibe</iframe>
	    </div>
	  </div>
  </c:if>
</div>

<form class="ss_style ss_form" name="ss_saveEntryWidthForm" 
  id="ss_saveEntryWidthForm" >
	<input type="hidden" name="entry_width">
	<input type="hidden" name="entry_top">
	<input type="hidden" name="entry_left">
</form>
<script type="text/javascript">
function gwt_showEntryDivInitialization() {
	ss_showEntryDivInitialization('');
}
ss_createOnLoadObj("gwt_showEntryDivInitialization", gwt_showEntryDivInitialization);
//ss_createOnLayoutChangeObj("gwt_showEntryDiv", ss_setEntryDivHeight);
</script>
