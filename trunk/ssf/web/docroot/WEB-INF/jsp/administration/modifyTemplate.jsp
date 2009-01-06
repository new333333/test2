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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.configure_cfg.add") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.configure_cfg.add">

<c:if test="${ssOperation == 'modify'}">
<h2>
<c:if test="${ssBinderConfig.entityType == 'workspace'}">
<ssf:nlt tag="administration.configure_cfg.workspaceTemplate.title"/>
</c:if>
<c:if test="${ssBinderConfig.entityType != 'workspace'}">
<ssf:nlt tag="administration.configure_cfg.folderTemplate.title"/>
</c:if>
<ssf:nlt tag="${ssBinderConfig.templateTitle}" checkIfTag="true"/></h2>
<ssf:nlt tag="administration.configure_cfg.modifyTarget"/>
<%@ include file="/WEB-INF/jsp/binder/modify_binder.jsp" %>

</c:if>
<script type="text/javascript">

function ss_checkForm(obj, binderId) {
    if(ss_buttonSelected == "cancelBtn") {
    	return true;
    }
    
	if (ss_buttonSelected == "") return false;
	if (obj.title.value == '') {
		alert('<ssf:nlt tag="general.required.title"/>');
		ss_buttonSelected="";
		return false;
	}
	if (obj.templateName != undefined && obj.templateName.value == '') {
		alert('<ssf:nlt tag="general.required.name"/>');
		ss_buttonSelected="";
		return false;
	}
	
	if (ss_onSubmit(obj)) return true;
	ss_buttonSelected="";
	return false;

}
</script>
<c:if test="${ssOperation == 'modify_template'}">
<c:if test="${ssBinder.root}">
<script type="text/javascript">
ss_addValidator("ss_nameCheck", ss_ajax_result_validator);
</script>
</c:if>
<form method="post" action="<ssf:url action="configure_configuration" actionUrl="true"><ssf:param 
		name="operation" value="modify_template"/><ssf:param 
		name="binderId" value="${ssBinderConfig.id}"/></ssf:url>" 
		onSubmit="return ss_checkForm(this, '${ssBinderConfig.id}');">

<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.modify"/>" onClick="ss_buttonSelect('okBtn');">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" onClick="ss_buttonSelect('cancelBtn');">
</div>
<h2>
<c:if test="${ssBinderConfig.entityType == 'workspace'}">
<ssf:nlt tag="administration.configure_cfg.workspaceTemplate.title"/>
</c:if>
<c:if test="${ssBinderConfig.entityType != 'workspace'}">
<ssf:nlt tag="administration.configure_cfg.folderTemplate.title"/>
</c:if>
<ssf:nlt tag="${ssBinderConfig.templateTitle}" checkIfTag="true"/></h2>

<table>
<c:if test="${ssBinder.root}">
<tr><td>
<div class="needed-because-of-ie-bug"><div id="ss_nameCheck" style="display:none; visibility:hidden;" 
      ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
<span class="ss_labelLeft" id="ss_nameLabel"><label for="templateName"><ssf:nlt tag="administration.configure_cfg.name"/></label></span>
<input type="text" name="templateName" id="templateName" size="50" value="${ssBinderConfig.name}" onchange="ss_ajaxValidate(ss_buildAdapterUrl(ss_AjaxBaseUrl,{operation:'check_template_name',binderId:'${ssBinderConfig.id}'}), this, 'ss_nameLabel', 'ss_nameCheck');"/>
</td></tr>
</c:if>
<tr><td>
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.title"/></span>
<input type="text" name="title" size="50" value="${ssBinderConfig.templateTitle}"/>
</td></tr>

<tr><td>
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.description"/></span>
    <div align="left">
    <ssf:htmleditor name="description" >
		<c:if test="${!empty ssBinderConfig.templateDescription.text}">${ssBinderConfig.templateDescription.text}</c:if>
  	</ssf:htmleditor>
  	</div>
</td></tr>
</table>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.modify"/>" onClick="ss_buttonSelect('okBtn');">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" onClick="ss_buttonSelect('cancelBtn');">
</div>

</form>
</c:if>

<c:if test="${ssOperation == 'add'}">
<c:if test="${definitionType == '-1'}">
<%
String wsTreeName = "cfg_" + renderResponse.getNamespace();
%>
<script type="text/javascript">
function <%= wsTreeName %>_showId(id, obj, action) {
	return false;
}
</script>
<div class="ss_style ss_portlet">

<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
<form class="ss_style ss_form" name="${renderResponse.namespace}fm" 
    id="${renderResponse.namespace}fm" method="post" 
    action="<ssf:url action="configure_configuration" actionUrl="true"><ssf:param 
		name="operation" value="add"/></ssf:url>" >
<input type="hidden" name="definitionType" value="-1"/>
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</div>
	<table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr align="left"><td><ssf:nlt tag="tree.choose_folder"/></td></tr>
	<tr>
		<td align="left">
			<div>
			<ssf:tree treeName="<%= wsTreeName %>"  
			  treeDocument="${ssWsDomTree}" 
			  rootOpen="true" showImages="true" 
			  singleSelectName="binderId"/>
			</div>
		</td>
	</tr>
	</table>
	<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.add"/>">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</div>
</form>


</c:if>
<c:if test="${definitionType != '-1'}">
<script type="text/javascript">
ss_addValidator("ss_nameCheck", ss_ajax_result_validator);
</script>


<form method="post" action="<ssf:url action="configure_configuration" actionUrl="true"><ssf:param 
		name="operation" value="add"/></ssf:url>" 
		onSubmit="return ss_checkForm(this);">
<input type="hidden" name="definitionType" value="${definitionType}"/>
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.add"/>" onClick="ss_buttonSelect('okBtn');">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" onClick="ss_buttonSelect('cancelBtn');">
</div>
<h2><span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.add"/></span></h2>

<table>
<tr><td>
<div class="needed-because-of-ie-bug"><div id="ss_nameCheck" style="display:none; visibility:hidden;" 
      ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
<span class="ss_labelLeft" id="ss_nameLabel"><label for="templateName"><ssf:nlt tag="administration.configure_cfg.name"/></label></span>
<input type="text" name="templateName" id="templateName" size="50" onchange="ss_ajaxValidate(ss_buildAdapterUrl(ss_AjaxBaseUrl,{operation:'check_template_name'}), this, 'ss_nameLabel', 'ss_nameCheck');"/>
</td></tr><tr><td>
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.title"/></span>
<input type="text" name="title" size="50" value="" />
</td></tr>

<tr><td>
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.description"/></span>
    <div align="left">
    <ssf:htmleditor name="description" />
  	</div>
</td></tr>

</table>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.add"/>" onClick="ss_buttonSelect('okBtn');">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" onClick="ss_buttonSelect('cancelBtn');">
</div>

</form>
</c:if>
</c:if>

</ssf:form>
</div>
</div>
</body>
</html>