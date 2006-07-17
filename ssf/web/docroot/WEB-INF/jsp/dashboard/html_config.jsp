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
<script type="text/javascript">dojo.require("dojo.widget.Editor");</script>
<br/>
<span class="ss_bold"><ssf:nlt tag="dashboard.enterHtml"/></span>
<br/>
<div class="ss_form_color" style="border:1px solid #CECECE; height:150px;">
<textarea id="data_html" name="data_html" dojoType="Editor"
  items="textGroup;|;colorGroup;|;listGroup;|;indentGroup;|;justifyGroup;|;linkGroup;"
><c:out
value="${ssDashboard.dashboard.components[ssDashboardId].data.html[0]}"/></textarea>
</div>
<br/>
<script type="text/javascript">
	var editorObj = dojo.widget.createWidget('data_html');

function ss_htmlConfigUnload() {
	document.getElementById('data_html').value = editorObj.getEditorContent();
}
ss_createEventObj('htmlConfigUnload', 'unload', ss_htmlConfigUnload);
</script>
