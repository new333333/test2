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

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.export_import") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<ssf:form titleTag="administration.export_import">
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<script type="text/javascript">
function ss_saveExportImportBinderId(id) {
	var formObj = document.getElementById('ss_exportImportForm')
	formObj['binderId'].value = id;
	var urlParams = {operation:"get_export_import_entry_form", binderId:id, random:ss_random++};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
	ifObj = document.getElementById('ss_exportImportIframe');
	ifObj.src = url;
}

function ss_saveExportImportEntryId(id) {
	var formObj = document.getElementById('ss_exportImportForm')
	formObj['entityId'].value = id;
}

//Create an onload handler that will look for errors passed to this page from a previous request.
ss_createOnLoadObj( 'onloadCheckForErrors', onloadCheckForErrors );

/**
 * This function gets called when the page is loaded.  It checks to see if an error returned from a previous
 * request to import a file.
 */
function onloadCheckForErrors()
{
	// Did an error happen while importing a file?
	<c:if test="${!empty ssException}">
		var errMsg;

		// Yes, tell the user about it.
		errMsg = '<ssf:escapeJavaScript><ssf:nlt tag="administration.export_import.error"/>${ssException}</ssf:escapeJavaScript>';
		alert( errMsg );
	</c:if>
	 
}// end onloadCheckForErrors()


function ss_checkForFileSelected() {
	var formObj = document.forms['form1']
	//var formObj = document.form1;
	if (formObj.imports.value == '') {
		//alert("hi");
		//alert(formObj.ssOperation);
		//if (formObj.ssOperation == 'import'){
			alert("<ssf:nlt tag="administration.export_import.selectFile"/>")
		//	return false;
		//}
	}
	return true;
}

</script>

<div class="ss_style ss_portlet">

<%--
<form class="ss_portlet_style ss_form" 
  id="ss_exportImportForm" 
  name="ss_exportImportForm" method="post" 
  action="<ssf:url action="export_import" actionUrl="true"/>">
  --%>

  <form name="form1" class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<ssf:url  
			action="export_import" 
			actionUrl="true" ><ssf:param 
		    name="binderId" value="${binderId}"/></ssf:url>" >
  <span class="ss_titlebold"><ssf:nlt tag="administration.export_import" /></span>

<br/>
<br/>

<input type="radio" name="ssOperation" id="ssOperation" value="export" checked><ssf:nlt tag="button.exportFolder"/>
<br/>
<input type="radio" name="ssOperation" id="ssOperation" value="import" ><ssf:nlt tag="button.importFolder"/>

<br/>
<div class="ss_buttonBarLeft">
<br/>
<%--<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>" 
	  onClick="if (${ssNamespace}_getChanges) {${ssNamespace}_getChanges()};return false;">--%>

<%--<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
	onClick="self.window.close();return false;"/>--%>
</div>
<br>

<div class="ss_divider"></div>
<br>
<label for="imports"><span class="ss_bold"><ssf:nlt tag="administration.export_import.file"/></span></label>
<br>
<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="95%">
<tr><td>
<input type="file" size="80" class="ss_text" name="imports" id="imports"><br>
</td></tr></table>
<div class="ss_divider"></div>

<br/>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"
  onclick="return ss_checkForFileSelected();"
/>

<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
  onClick="window.close();return false;"/>

</div>
</div>
</form>

</div>
<script type="text/javascript">
var rn = Math.round(Math.random()*999999);

</script>

</ssf:form>
</div>
</div>
</body>
</html>