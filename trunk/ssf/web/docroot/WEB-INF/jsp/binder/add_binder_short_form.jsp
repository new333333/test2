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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<script type="text/javascript">
var ss_addBinderOperation = "${ssOperation}";
var ss_checkTitleUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="check_binder_title" />
	</ssf:url>";
ss_addValidator("ss_titleCheck", ss_ajax_result_validator);

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

function ss_checkTitleOnSubmit() {
	var formObj = self.document.getElementById("${formName}");
	if (formObj == null) return true;
	var eTitle = formObj["title"];
	if (eTitle == null) return true;
	return ss_validateEntryTitle(eTitle);
}

ss_createOnSubmitObj("ss_checkTitleOnSubmit", "${formName}", ss_checkTitleOnSubmit);

function ss_treeShowIdAddBinder${renderResponse.namespace}(id, obj, action) {
	var binderId = id;
	//See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	//Build a url to go to
	var url = "<ssf:url action="add_binder" actionUrl="true"><ssf:param 
		name="binderId" value="ssBinderIdPlaceHolder"/><ssf:param 
		name="operation" value="add_workspace"/></ssf:url>";
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	self.location.href = url;
	return false;
}

function ss_checkForm(obj) {
	if (ss_buttonSelected == "") return false;
	return ss_onSubmit(obj);
}

</script>

<c:set var="titleTag" value="toolbar.menu.addFolder"/>
<c:if test="${ssDefinitionFamily == 'blog'}"><c:set var="titleTag" value="toolbar.menu.add_blog_folder"/></c:if>
<c:if test="${ssDefinitionFamily == 'photo'}"><c:set var="titleTag" value="toolbar.menu.add_photo_album_folder"/></c:if>
<c:if test="${ssDefinitionFamily == 'wiki'}"><c:set var="titleTag" value="toolbar.menu.add_wiki_folder"/></c:if>
<div class="ss_portlet">
<ssf:form titleTag="${titleTag}">

<form class="ss_style ss_form" 
  action="<ssf:url action="add_binder" actionUrl="true"><ssf:param 
  		name="binderId" value="${ssBinder.id}"/><ssf:param 
  		name="templateName" value="${ssBinderTemplateName}"/><ssf:param 
  		name="shortForm" value="true"/></ssf:url>"
  name="${renderResponse.namespace}fm" 
  id="${renderResponse.namespace}fm" 
  method="post" onSubmit="return ss_checkForm(this);">
  
<fieldset class="ss_fieldset">
	<span class="ss_labelLeft" id="title_label"><label for="title">
	  <ssf:nlt tag="general.title"/>
	</label></span>
    <div class="needed-because-of-ie-bug"><div id="ss_titleCheck" style="display:none; visibility:hidden;" 
      ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
	<input type="text" class="ss_text" size="70" name="title" id="title" 
	  onkeyup="ss_validateEntryTitle(this);"
	  onchange="ss_ajaxValidate(ss_checkTitleUrl, this,'title_label', 'ss_titleCheck');">
	<br/>
	<br/>
	<input type="submit" class="ss_submit"
	 name="addBtn" value="<ssf:nlt tag="button.ok"/>" 
	 onClick="ss_buttonSelect('addBtn');">
	<input type="submit" class="ss_submit" style="padding-left:20px;"
	  name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" 
	   onClick="ss_buttonSelect('cancelBtn');">

</fieldset>
	<sec:csrfInput />

</form>
</ssf:form>
</div>
