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

<%@ page import="java.util.ArrayList" %>
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

function ss_checkForImportOperation() {
	var operObj = document.forms['form1'].ssOperation;
	var importClicked = false;
	for (var i_oper = 0; i_oper < operObj.length; i_oper++) {
		if (operObj[i_oper].value == "import") {
			if (operObj[i_oper].checked) {
				importClicked = true;
			}
			
			return importClicked;
		}
	}
}

function ss_checkForFileNotSelected() {
	var formObj = document.forms['form1'];
	if (formObj.imports.value == '') {
		return true;
	}
	return false;
}

function ss_checkForm() {
	if (ss_checkForImportOperation() && ss_checkForFileNotSelected()){
			alert("<ssf:nlt tag="administration.export_import.selectFile"/>");
		return false;
	}
}

function toggleFileInput() {
	var el = document.getElementById('fileInput');
	el.style.visibility = (ss_checkForImportOperation() ? 'visible' : 'hidden' );
	el.style.display = (ss_checkForImportOperation() ? 'block' : 'none' );
	var el2 = document.getElementById('treeSelection');
	el2.style.visibility = (ss_checkForImportOperation() ? 'hidden' : 'visible' );
	el2.style.display = (ss_checkForImportOperation() ? 'none' : 'block' );
}

</script>

<div class="ss_style ss_portlet">

  <form name="form1" class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<ssf:url  
			action="export_import" 
			actionUrl="true" ><ssf:param 
		    name="binderId" value="${binderId}"/></ssf:url>" >

<br/>
<br/>

<input type="radio" name="ssOperation" id="ssOperation" value="export" checked 
  onClick="toggleFileInput()"><ssf:nlt tag="button.exportBinder"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

<input type="radio" name="ssOperation" id="ssOperation" value="import" 
  onClick="toggleFileInput()"><ssf:nlt tag="button.importBinder"/>

<br/>
<div class="ss_buttonBarLeft">
<br/>
</div>
<br>

<div class="ss_divider"></div>
<br>
<div id="treeSelection" style="visibility:visibile; display:block;">
<span class="ss_bold"><ssf:nlt tag="administration.export_import.tree"/></span>
<br>
<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="95%">
<tr><td>
  <ssf:tree treeName="ss_folderTree" treeDocument="${ssFolderDomTree}" 
    rootOpen="false" 
    nodeOpen="${ssFolder.parentFolder.id}" 
    highlightNode="${ssFolder.id}" 
    multiSelect="<%= new ArrayList() %>" 
    multiSelectPrefix="id" />
</td></tr></table>
</div>

<div id="fileInput" style="visibility:hidden; display:none;">
<label for="imports"><span class="ss_bold"><ssf:nlt tag="administration.export_import.file"/></span></label>
<br>
<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="95%">
<tr><td>
<input type="file" size="80" class="ss_text" name="imports" id="imports"><br>
</td></tr></table>
</div>

<div class="ss_divider"></div>

<br/>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"
  onclick="return ss_checkForm();"
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