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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="tag" value="folder.manageFolderVersionControls"/>
<jsp:useBean id="tag" type="String" />
<c:set var="ss_windowTitle" value='<%= NLT.get(tag) %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<body class="ss_style_body tundra">

<script type="text/javascript">
<c:forEach var="item" items="${ssAllEntryDefinitions}">
  var def${item.value.id} = "${item.key}";
</c:forEach>

function confirmChangeType() {
	var formObj = document.forms['form1'];
	if (formObj.oldEntryType.value == "" || formObj.newEntryType.value == "") {
		alert("<ssf:nlt tag="binder.changeEntryType.selectOne"/>");
		return false;
	}
	eval("var newDefName = def"+formObj.newEntryType.value);
	var confirmText = '<ssf:escapeJavaScript><ssf:nlt tag="binder.changeEntryType.confirm1"><ssf:param
		name="value" value="__NewDefName"/></ssf:nlt></ssf:escapeJavaScript>';
		confirmText = ss_replaceSubStr(confirmText, "__NewDefName", newDefName);
	return confirm(confirmText);
}
</script>

<div class="ss_style ss_portlet">
	<div style="padding:10px;">
		<br>
		
		<c:if test="${!empty ssException}">
		  <font color="red">
		    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
		  </font>
		  <br/>
		</c:if>
	
<div style="display:block;" class="wg-tab-content marginbottom3">
<form name="form1" class="ss_form" method="post" 
	action="<ssf:url action="manage_folder_entry_types" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="entryId" value="${ssEntry.id}"/><ssf:param
	name="operation" value="change_entry_type_entry"/></ssf:url>"
	onSubmit="return confirmChangeType();"
>

<div class="marginbottom3"><span class="ss_bold"><ssf:nlt tag="binder.changeEntryType" /></span>&nbsp;&nbsp;<span class="ss_largeprint"><ssf:nlt tag="${ssEntry.title}" checkIfTag="true"/></span></div>



<fieldset class="ss_fieldset">
   
  <div style="padding:10px;">
    <span><ssf:nlt tag="binder.changeEntryType.currentType"></span>
	<span>
		<ssf:param
		  name="value" useBody="true">
		  <c:forEach var="item" items="${ssAllEntryDefinitions}">
			  <c:if test="${item.value.id == ssEntry.entryDefId}">
				${item.key}
			  </c:if>
		  </c:forEach>
		  </ssf:param></ssf:nlt>
	</span>
    <div class="ss_bold margintop3"><ssf:nlt tag="binder.changeEntryType.selectNew"/></div>

	<select name="newEntryType">
	  <option value="" selected style="padding-bottom: 2px; padding-top: 2px;"><ssf:nlt tag="binder.changeEntryType.selectType"/></option>
      <c:forEach var="item" items="${ssAllEntryDefinitions}">
	      <option value="${item.value.id}" id="newOption_${item.value.id}" style="padding-bottom: 2px; padding-top: 2px;">
	        ${item.key}<c:if test="${item.value.binderId != -1}"><sup>*</sup></c:if>
	      </option>
      </c:forEach>
    </select>
    <div class="ss_normalprint ss_gray_medium" style="padding: 15px 5px 5px; max-width: 400px;"><ssf:nlt tag="binder.changeEntryType.warning1a"/>&nbsp;<ssf:nlt tag="binder.changeEntryType.warning2"/></div>
    <div class="ss_normalprint margintop2 ss_gray_medium">*&nbsp;<ssf:nlt tag="definition.local"/></div>
  </div>
  
</fieldset>

	<div class="margintop3" style="text-align: right;">
		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />" >
		<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="ss_cancelButtonCloseWindow();return false;">
	</div>
</form>
</div>
</div>
</div>

</body>
</html>
