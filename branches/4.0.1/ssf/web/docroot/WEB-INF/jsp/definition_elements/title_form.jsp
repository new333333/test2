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
<% //Title form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_fieldModifyDisabled" value=""/>
<c:set var="ss_fieldModifyStyle" value=""/>
<c:if test="${ss_accessControlMap['ss_modifyEntryRightsSet']}">
  <c:if test="${(!ss_accessControlMap['ss_modifyEntryFieldsAllowed'] && !ss_accessControlMap['ss_modifyEntryAllowed']) || 
			(!ss_accessControlMap['ss_modifyEntryAllowed'] && !ss_fieldModificationsAllowed == 'true')}">
    <c:set var="ss_fieldModifyStyle" value="ss_modifyDisabled"/>
    <c:set var="ss_fieldModifyInputAttribute" value=" disabled='disabled' "/>
    <c:set var="ss_fieldModifyDisabled" value="true"/>
  </c:if>
</c:if>
<c:if test="${property_required}"><c:set var="ss_someFieldsRequired" value="true" scope="request"/></c:if>
<%
	String caption1 = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption1});
%>
<c:if test="${!property_generated && !ssBinder.mirrored}">

<c:if test='${ssBinderMarker}'>
 <script type="text/javascript">
var ss_checkTitleUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="check_binder_title" />
	</ssf:url>";
ss_addValidator("ss_titleCheck", ss_ajax_result_validator);
 </script>
</c:if>

<c:choose>
  <c:when test="${ss_formViewStyle == 'guestbook'}">
		<input type="hidden" name="title" value="<c:out value="${ssUser.firstName}"/> <c:out value="${ssUser.lastName}"/> wrote" />
  </c:when>

  <c:otherwise>
		<%
			String caption = (String) request.getAttribute("property_caption");
			if (caption == null) {caption = "";}
		
			String width = (String) request.getAttribute("property_width");
			if (width == null || width.equals("")) {
				width = "";
			} else {
				width = "size=\""+width+"\"";
			}
		%>
		<div class="ss_entryContent ${ss_fieldModifyStyle}">
		<div class="ss_labelAbove" id="${property_name}_label">
		<label for="title">
		  ${property_caption}<c:if test="${property_required}"><span 
			  id="ss_required_${property_name}" title="<%= caption2 %>" class="ss_required">*</span></c:if>
		</label>
		</div>
<c:if test='${ssBinderMarker}'>
  <div class="needed-because-of-ie-bug"><div id="ss_titleCheck" style="display:none; visibility:hidden;" 
    ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
</c:if>
		<input type="text" class="ss_text" name="title" id="title" <%= width %> ${ss_fieldModifyInputAttribute}
		  onkeyup="ss_validateEntryTitle(this);"
		  <c:if test='${ssBinderMarker}'>
		    onchange="ss_ajaxValidate(ss_checkTitleUrl, this,'${property_name}_label', 'ss_titleCheck');"
		  </c:if>
		  <c:if test="${empty ssEntryTitle && !empty ssEntry && empty ssDefinitionEntry}">
		      value="<ssf:escapeQuotes><ssf:nlt tag="reply.re.title"><ssf:param 
		        name="value" useBody="true">${ssEntry.title}</ssf:param></ssf:nlt></ssf:escapeQuotes>"
		  </c:if>
		  <c:if test="${!empty ssEntryTitle}">
		      value="<ssf:escapeQuotes><c:out value="${ssEntryTitle}"/></ssf:escapeQuotes>"
		  </c:if>
		  <c:if test="${empty ssEntryTitle && !empty ssDefinitionEntry.title}">
		     value="<ssf:escapeQuotes><c:out value="${ssDefinitionEntry.title}"/></ssf:escapeQuotes>"
		  </c:if>
		>
	</div>
<script type="text/javascript">
function ss_validateEntryTitle(eTitle) {
	var sTitle = ss_validateEntryTextFieldLength(eTitle.value);
	if (sTitle != eTitle.value) {
		eTitle.value = sTitle;
		alert("<ssf:nlt tag="error.titleTooLong"/>");
		window.setTimeout(
			function() {
				input_setCaretToEnd(eTitle);
			}, 100 );
		return false;
	}
	return true;
}
function ss_focusOnTitle() {
	var formObj = self.document.getElementById('${ss_form_form_formName}')
	if (formObj != null) {
		if (typeof formObj.title != 'undefined' && 
				typeof formObj.title.type != 'undefined' && 
				formObj.title.type == 'text') {
			if (ss_checkIfVisible(formObj.title)) formObj.title.focus();
		}
	}
}
function ss_checkTitleOnSubmit() {
	var formObj = self.document.getElementById("${formName}");
	if (formObj == null) return true;
	var eTitle = formObj["title"];
	if (eTitle == null) return true;
	return ss_validateEntryTitle(eTitle);
}
ss_createOnSubmitObj("ss_checkTitleOnSubmit", "${formName}", ss_checkTitleOnSubmit);
<c:if test="${empty ss_fieldModifyDisabled || ss_fieldModificationsAllowed}">
var titleObj = document.getElementById("title");
if (titleObj != null && !ss_isIE) {
	try {
		titleObj.focus();
	} catch(e) {
		ss_createOnLoadObj("ss_focusOnTitle", ss_focusOnTitle);
	}
} else {
	ss_createOnLoadObj("ss_focusOnTitle", ss_focusOnTitle);
}
</c:if>
</script>
  </c:otherwise>
</c:choose>

</c:if>