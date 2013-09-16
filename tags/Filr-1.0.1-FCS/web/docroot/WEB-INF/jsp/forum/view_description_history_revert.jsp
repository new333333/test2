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
<%@ page import="org.dom4j.Element" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="ss_useExplicitFileVersionNumbers" value="true" scope="request" />
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
var ss_viewing_entry_history = true;

function ss_history_revert_init() {
	//Called at onLoad to initialize the fileId checkboxes
	var inputList = self.document.getElementsByTagName("input");
	for (var i = 0; i < inputList.length; i++) {
		if (inputList[i].type == "checkbox" && inputList[i].name && inputList[i].name.indexOf("file_revert_") == 0) {
			try {
				saveFileId(inputList[i]);
			} catch(e) {}
		}
	}
}

function saveFileId(obj) {
	var formName = "<%= org.kablink.teaming.web.WebKeys.DEFINITION_DEFAULT_FORM_NAME %>";
	var formObj = self.document.forms[formName];
	if (formObj == null && self.document.forms.length > 0) {
		formObj = self.document.forms[self.document.forms.length-1];
	}
	if (formObj != null) {
		if (typeof formObj[obj.name] == "undefined") {
			var hiddenObj = self.document.createElement("input");
			hiddenObj.setAttribute("type", "hidden");
			hiddenObj.setAttribute("id", obj.name);
			hiddenObj.setAttribute("name", obj.name);
			formObj.appendChild(hiddenObj);
			hiddenObj.value = obj.checked;      //Set the value in hiddenObj here because IE can't find objects added this way
		} else {
			formObj[obj.name].value = obj.checked;
		}
		var objs = self.document.getElementById(obj.name);
		for (var i = 0; i < objs.length; i += 1) {
			//Make sure the hidden obj is correctly set in IE
			objs[i].value = obj.checked;
		}
	}
}

function submitRevertForm() {
	var actionUrl = "<ssf:url     
		adapter="true" 
		portletName="ss_forum" 
		action="view_editable_history" 
		actionUrl="true">
		<ssf:param name="entityId" value="${ss_entityId}" />
		<ssf:param name="versionId" value="${ss_versionId}" />
		<ssf:param name="operation" value="revert" />
		<ssf:param name="operation2" value="okBtn" />
		</ssf:url>";
	var formName = "<%= org.kablink.teaming.web.WebKeys.DEFINITION_DEFAULT_FORM_NAME %>";
	var formObj = self.document.forms[formName];
	if (formObj == null && self.document.forms.length > 0) {
		formObj = self.document.forms[self.document.forms.length-1];
	}
	if (formObj != null) {
		formObj.action = actionUrl;
		if (formObj.onsubmit != null && formObj.onsubmit != "") formObj.onsubmit();
		formObj.submit();
	}
}

ss_createOnLoadObj("ss_history_revert_init", ss_history_revert_init);
</script>

<div class="ss_style ss_portlet" style="padding:10px;">
<ssf:form title='<%= NLT.get("entry.revert") %>'>
<div style="padding:10px 6px;">
  <span class="ss_bold"><ssf:nlt tag="entry.revert.warning"/></span>
  <div>
    <ul style="margin:2px; padding:0px 6px;">
      <li><span><ssf:nlt tag="entry.revert.warning1"/></span></li>
      <li><span><ssf:nlt tag="entry.revert.warning2"/></span></li>
      <li><span><ssf:nlt tag="entry.revert.warning3"/></span></li>
    </ul>
  </div>
</div>
<c:if test="${fn:length(ss_changeLogList) > 0}">
  <c:set var="change" value="${ss_changeLogList[0]}"/>
  <table width="100%">
  <tr>
  <td valign="bottom">
    <span class="ss_bold" style="padding-right:10px;">
      <ssf:nlt tag="entry.revert.version"><ssf:param name="value" value="${change.folderEntry.attributes.logVersion}"/></ssf:nlt>
    </span>
    <c:set var="modifyDate"><fmt:formatDate timeZone="${ssUser.timeZone.ID}" type="both" value="${change.changeLog.operationDate}"/></c:set>
    <span>${modifyDate}</span>
  </td>
  <td valign="top" align="right">
    <input type="button" value="<ssf:nlt tag="button.ok"/>" style="margin:0px 20px 0px 0px;" 
      onClick="ss_buttonSelect('okBtn');submitRevertForm();return false;"/>
  </td>
  </tr>
  </table>
  <c:if test="${!empty change.changeLogEntry}">
    <c:set var="changeLogEntry" value="${change.changeLogEntry}"/>
	<jsp:useBean id="changeLogEntry" type="org.kablink.teaming.domain.DefinableEntity" />
	<% 
		Element configEle = (Element)changeLogEntry.getEntryDefDoc().getRootElement().selectSingleNode("//item[@name='entryView']");
	%>
	<c:set var="configEle" value="<%= configEle %>" />
    <div style="margin:20px; padding:10px; border: 1px black solid;">
		<c:if test="${!empty configEle}">
		  <c:set var="ssBinderOriginalFromDescriptionHistory" value="${ssBinder}" />
		  <c:set var="ssBinder" value="${changeLogEntry.parentBinder}" scope="request"/>
		  <c:set var="ssEntryOriginalFromDescriptionHistory" value="${ssEntry}" />
		  <c:set var="ssEntry" value="${changeLogEntry}" scope="request"/>
		  <c:set var="ss_pseudoEntity" value="true" scope="request"/>
		  <c:set var="ss_pseudoEntityRevert" value="true" scope="request"/>
		  <ssf:displayConfiguration 
		    configDefinition="${changeLogEntry.entryDefDoc}" 
		    configElement="<%= configEle %>"
		    configJspStyle="view" 
		    entry="${changeLogEntry}" 
		    processThisItem="true" />
		  <c:set var="ssBinder" value="${ssBinderOriginalFromDescriptionHistory}" scope="request"/>
		  <c:set var="ssEntry" value="${ssEntryOriginalFromDescriptionHistory}" scope="request"/>
		</c:if>
    </div>
  </c:if>
</c:if>
<div style="margin:10px 0px 0px 0px;">
  <input type="button" value="<ssf:nlt tag="button.ok"/>" 
    onClick="ss_buttonSelect('okBtn');submitRevertForm();return false;"/>
  &nbsp;&nbsp;&nbsp;
  <input type="button" value="<ssf:nlt tag="button.cancel"/>" onClick="self.window.history.back();return false;"/>
</div>
</ssf:form>
</div>

<c:if test="${!empty change}">
  <c:if test="${!empty change.changeLogEntry}">
    <c:set var="changeLogEntry2" value="${change.changeLogEntry}"/>
	<jsp:useBean id="changeLogEntry2" type="org.kablink.teaming.domain.DefinableEntity" />
<% 
  Element configEle2 = (Element)changeLogEntry2.getEntryDefDoc().getRootElement().selectSingleNode("//item[@name='entryForm']");
%>
   <div style="display:none;">
	<c:if test="${!empty configEle}">
	  <c:set var="ssBinderOriginalFromDescriptionHistory" value="${ssBinder}" />
	  <c:set var="ssBinder" value="${changeLogEntry2.parentBinder}" scope="request"/>
	  <c:set var="ssEntryOriginalFromDescriptionHistory" value="${ssEntry}" />
	  <c:set var="ssEntry" value="${changeLogEntry}" scope="request"/>
	  <c:set var="ss_pseudoEntity" value="true" scope="request"/>
	  <c:set var="ss_pseudoEntityRevert" value="true" scope="request"/>
	  <ssf:displayConfiguration 
	    configDefinition="${changeLogEntry2.entryDefDoc}" 
	    configElement="<%= configEle2 %>"
	    configJspStyle="form" 
	    entry="${changeLogEntry2}" 
	    processThisItem="true" />
	  <c:set var="ssBinder" value="${ssBinderOriginalFromDescriptionHistory}" scope="request"/>
	  <c:set var="ssEntry" value="${ssEntryOriginalFromDescriptionHistory}" scope="request"/>
	</c:if>
   </div>
  </c:if>
</c:if>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
