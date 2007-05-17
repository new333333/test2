<%
// The dashboard "html" component
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<br/>
<span class="ss_bold"><ssf:nlt tag="dashboard.enterHtml"/></span>
<br/>
<div class="ss_form_color">
<ssf:htmleditor id="data_html" name="data_html"
	initText="${ssDashboard.dashboard.components[ssComponentId].data.html}" />
</div>
<br/>
<script type="text/javascript">
function ss_htmlConfigUnload() {
	//document.getElementById('data_html').value = editorObj.getEditorContent();
}
ss_createEventObj('htmlConfigUnload', 'unload', ss_htmlConfigUnload);
</script>
