<%
// The dashboard "html" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<br/>
<span class="ss_bold"><ssf:nlt tag="dashboard.enterHtml"/></span>
<br/>
<div class="ss_form_color">
<ssf:htmleditor id="data_html" name="data_html"
	initText="${ssDashboard.dashboard.components[ssComponentId].data.html[0]}" />
</div>
<br/>
<script type="text/javascript">
function ss_htmlConfigUnload() {
	//document.getElementById('data_html').value = editorObj.getEditorContent();
}
ss_createEventObj('htmlConfigUnload', 'unload', ss_htmlConfigUnload);
</script>
