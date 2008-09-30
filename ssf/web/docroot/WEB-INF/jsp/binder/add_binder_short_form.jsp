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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.util.NLT" %>
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

<div class="ss_portlet">
<br/>

<form class="ss_style ss_form" 
  action="<ssf:url action="add_binder" actionUrl="true"><ssf:param 
  		name="binderId" value="${ssBinder.id}"/><ssf:param 
  		name="templateName" value="${ssBinderTemplateName}"/></ssf:url>"
  name="${renderResponse.namespace}fm" 
  id="${renderResponse.namespace}fm" 
  method="post" onSubmit="return ss_checkForm(this);">
<span class="ss_bold">
<ssf:nlt tag="toolbar.menu.addFolder"><ssf:param name="value" value="${ssBinder.pathName}"/></ssf:nlt>
</span></br></br>
  
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="general.title" /></legend>
	<span class="ss_labelLeft" id="title_label"><label for="title">
	  <ssf:nlt tag="folder.title"/>
	</label></span>
    <div class="needed-because-of-ie-bug"><div id="ss_titleCheck" style="display:none; visibility:hidden;" 
      ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
	<input type="text" class="ss_text" size="70" name="title" id="title" 
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

</form>
</div>
