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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<script type="text/javascript">
if (self.parent) {
	//We are in an iframe inside a portlet (maybe?)
	var windowName = self.window.name    
	if (windowName.indexOf("ss_workareaIframe") == 0) {
		//We are running inside an iframe, get the namespace name of that iframe's owning portlet
		var namespace = windowName.substr("ss_workareaIframe".length)
		//alert('namespace = '+namespace+', binderId = ${ssBinder.id}, entityType = ${ssBinder.entityType}')
		var url = "<ssf:url
					adapter="true"
					portletName="ss_forum" 
					action="__ajax_request" 
					binderId="${ssBinder.id}">
				  <ssf:param name="entityType" value="${ssBinder.entityType}"/>
				  <ssf:param name="namespace" value="ss_namespacePlaceholder" />
				  <ssf:param name="operation" value="set_last_viewed_binder" />
				  <ssf:param name="rn" value="ss_randomNumberPlaceholder"/>
				  </ssf:url>"
		url = ss_replaceSubStr(url, 'ss_namespacePlaceholder', namespace);
		url = ss_replaceSubStr(url, 'ss_randomNumberPlaceholder', ss_random++);
		//Save the last binder viewed by calling this url (it returns nothing)
		ss_fetch_url(url);
	}
}
function ss_workarea_showId${renderResponse.namespace}(id, action, entryId) {
	if (typeof entryId == "undefined") entryId = "";
	//Build a url to go to
<ssf:ifnotadapter>
	var url = "<ssf:url     
	    		  adapter="true" 
	    		  portletName="ss_forum" 
	    		  binderId="ssBinderIdPlaceHolder" 
    			  entryId="ssEntryIdPlaceHolder" 
	    		  action="ssActionPlaceHolder" 
	    		  actionUrl="false" >
	    	   <ssf:param name="namespace" value="${renderResponse.namespace}"/>
	           </ssf:url>"
</ssf:ifnotadapter>
<ssf:ifadapter>
	var url = "<ssf:url     
	    		  adapter="true" 
	    		  portletName="ss_forum" 
	    		  binderId="ssBinderIdPlaceHolder" 
    			  entryId="ssEntryIdPlaceHolder" 
	    		  action="ssActionPlaceHolder" 
	    		  actionUrl="false" >
	           </ssf:url>"
</ssf:ifadapter>
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
<ssf:ifnotadapter>
	var iframeDivObj = document.getElementById('ss_workareaIframe${renderResponse.namespace}')
	if (iframeDivObj != null) {
		iframeDivObj.src = url;
	} else {
		return true;
	}
</ssf:ifnotadapter>
<ssf:ifadapter>
	self.location.href = url;
</ssf:ifadapter>
	return false;
}
if (typeof ss_workarea_showId == "undefined") 
	ss_workarea_showId = ss_workarea_showId${renderResponse.namespace};
</script>
<ssf:ifnotadapter>
<script type="text/javascript">
var ss_workareaIframeOffset = 50;
function ss_setWorkareaIframeSize${renderResponse.namespace}() {
	var iframeDiv = document.getElementById('ss_workareaIframe${renderResponse.namespace}')
	if (window.frames['ss_workareaIframe${renderResponse.namespace}'] != null) {
		eval("var iframeHeight = parseInt(window.ss_workareaIframe${renderResponse.namespace}" + ".document.body.scrollHeight);")
		if (iframeHeight > 100) {
			iframeDiv.style.height = iframeHeight + ss_workareaIframeOffset + "px"
		}
	}
}
//If this is the first definition of ss_setWorkareaIframeSize, remember its name in case we need to find it later
if (typeof ss_setWorkareaIframeSize == "undefined") 
	var ss_setWorkareaIframeSize = ss_setWorkareaIframeSize${renderResponse.namespace};

var ss_portal_view_normal_url${renderResponse.namespace} = "<ssf:url windowState="normal"/>";
var ss_portal_view_maximized_url${renderResponse.namespace} = "<ssf:url windowState="maximized"/>";
var ss_portal_view_window_state${renderResponse.namespace} = "${ss_windowState}"
</script>
<div id="${renderResponse.namespace}">
<iframe id="ss_workareaIframe${renderResponse.namespace}" 
    name="ss_workareaIframe${renderResponse.namespace}" 
    style="width:100%; height:400px; display:block; position:relative;"
	src="<ssf:url     
    		adapter="true" 
    		portletName="ss_forum" 
    		binderId="${ssBinderId}" 
    		action="view_folder_listing" 
    		entryId="${ssEntryIdToBeShown}" 
    		actionUrl="false" >
        <ssf:param name="namespace" value="${renderResponse.namespace}"/>
        </ssf:url>" 
	onLoad="ss_setWorkareaIframeSize${renderResponse.namespace}();" 
	frameBorder="0" >xxx</iframe>
</div>
<%
	//Define the z-index offset for table columns to handle the overlays
	int slidingTableColumnZ = 11;
	int slidingTableInfoZ = 40;
%>
<script type="text/javascript" src="<html:rootPath/>js/sliding_table/sliding_table_common.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/sliding_table/sliding_table.js"></script>
<div id="ss_info_popup" class="ss_style ss_sliding_table_info_popup" style="z-index: <%= slidingTableInfoZ %>;"></div>
<div id="ss_info_popup_sizer" style="position:absolute; visibility:hidden;"></div>


<!-- portlet iframe div -->
<%@ include file="/WEB-INF/jsp/entry/view_iframe_div.jsp" %>
<!-- portlet iframe div -->	

</ssf:ifnotadapter>
