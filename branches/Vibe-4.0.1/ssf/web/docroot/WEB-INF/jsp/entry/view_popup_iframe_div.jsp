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
<script type="text/javascript">
	//Define the variables needed by the javascript routines
	var ss_popup_iframe_box_div_name = 'ss_popup_iframe_box_div';
	
	var ss_forumRefreshUrl = "<html:rootPath/>js/forum/refresh.html";
	var ss_popupBackgroundColor = "${ss_style_background_color}";

	function ss_popupFrameLoaded() {
		//ss_debug("**** "+ss_debugTrace());
		var frameObj = self.document.getElementById("ss_showpopupframe");
		if (frameObj != null && !(frameObj.src.indexOf("null.html") >= 0)) {
			var entryContentDiv = self.document.getElementById("ss_entryContentDiv");
			if (entryContentDiv != null) {
				entryContentDiv.style.display = "none";
			}
			setTimeout("ss_resizePopupDiv();", 50);
			//Signal that the layout changed
			if (ssf_onLayoutChange) {
				setTimeout("ssf_onLayoutChange();", 100);
			}
		}
	}
</script>
<script type="text/javascript" src="<html:rootPath/>js/forum/view_iframe.js"></script>

<div id="ss_showpopupdiv" 
  onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}"
  style="visibility:hidden; display:none;">
    <iframe id="ss_showpopupframe" name="ss_showpopupframe"
  	  title="<ssf:nlt tag = "iframe.popup"/>" 
      name="ss_showpopupframe" style="display:block;"
      src="<html:rootPath/>js/forum/null.html" 
      onLoad="ss_popupFrameLoaded();"
      frameBorder="0" >Novell Vibe</iframe>
</div>
<script type="text/javascript">
ss_createOnResizeObj("ss_showpopupdiv", function(){setTimeout("ss_resizePopupDiv();", 100);});
ss_createOnLayoutChangeObj("ss_showpopupdiv", function(){setTimeout("ss_resizePopupDiv();", 150);});
</script>
