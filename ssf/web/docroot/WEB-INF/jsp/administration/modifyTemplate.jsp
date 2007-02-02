<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_style ss_portlet">
<div class="ss_form" style="margin:6px;">
<div class="ss_rounded">
<div style="margin:6px;">

<c:if test="${ssOperation == 'modify'}">
<form method="post" action="<portlet:actionURL><portlet:param 
		name="action" value="configure_configuration"/>
		<portlet:param name="operation" value="modify"/><portlet:param 
		name="binderId" value="${ssBinderConfig.id}"/></portlet:actionURL>" >

<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.modify"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
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
<tr><td>
<c:if test="${ssBinderConfig.entityType == 'workspace'}">
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.workspaceTarget.label"/></span>
</c:if>
<c:if test="${ssBinderConfig.entityType != 'workspace'}">
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.folderTarget.label"/></span>
</c:if>
<input type="text" name="title" value="<ssf:nlt tag="${ssBinderConfig.title}" checkIfTag="true"/>"/>
</td></tr>
<tr><td>
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.description"/></span>
    <div align="left">
    <ssf:htmleditor name="description" >
		<c:if test="${!empty ssBinderConfig.description.text}">${ssBinderConfig.description.text}</c:if>
  	</ssf:htmleditor>
  	</div>
</td></tr>
<c:if test="${ssBinderConfig.entityType == 'folder'}">
<tr><td>
<input type="checkbox" name="library" <c:if test="${ssBinderConfig.library}">checked="checked"</c:if>/>
<span class="ss_labelRight"><ssf:nlt tag="administration.configure_cfg.library"/></span>
</td></tr>
<tr><td>
<input type="checkbox" name="uniqueTitles" <c:if test="${ssBinderConfig.uniqueTitles}">checked="checked"</c:if>/>
<span class="ss_labelRight"><ssf:nlt tag="administration.configure_cfg.uniqueTitles"/></span>
</td></tr>
</c:if>
</table>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.modify"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>

</form>
</c:if>

<c:if test="${ssOperation == 'modify_template'}">
<form method="post" action="<portlet:actionURL><portlet:param 
		name="action" value="configure_configuration"/>
		<portlet:param name="operation" value="modify_template"/><portlet:param 
		name="binderId" value="${ssBinderConfig.id}"/></portlet:actionURL>" >

<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.modify"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
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
<tr><td>
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.title"/></span>
<input type="text" name="title" size="50" value="<ssf:nlt tag="${ssBinderConfig.templateTitle}" checkIfTag="true"/>"/>
</td></tr>
<tr><td>
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.description"/></span>
    <div align="left">
    <ssf:htmleditor name="description" >
		<c:if test="${!empty ssBinderConfig.templateDescription.text}">${ssBinderConfig.templateDescription.text}</c:if>
  	</ssf:htmleditor>
  	</div>
</td></tr>
<tr><td>
<c:if test="${ssBinderConfig.entityType == 'workspace'}">
<c:set var="iconListPath" value="icons.workspace" scope="request"/>
</c:if>
<c:if test="${ssBinderConfig.entityType == 'folder'}">
<c:set var="iconListPath" value="icons.folder" scope="request"/>
</c:if>
<c:set var="iconValue" value="${ssBinderConfig.iconName}" scope="request"/>
<c:set var="property_name" value="iconName" scope="request"/>
<c:set var="property_caption" value="<%= NLT.get("__icon") %>" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/iconForm.jsp" %>

</td></tr>
</table>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.modify"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>

</form>
</c:if>

<c:if test="${ssOperation == 'add'}">
<script type="text/javascript">

function <portlet:namespace/>_onsub(obj) {
	if (obj.title.value == '') {
		alert('<ssf:nlt tag="general.required.title"/>');
		return false;
	}
	return true;
}
</script>
<form method="post" action="<portlet:actionURL><portlet:param 
		name="action" value="configure_configuration"/>
		<portlet:param name="operation" value="add"/></portlet:actionURL>" 
		onSubmit="return(<portlet:namespace/>_onsub(this))">
<input type="hidden" name="cfgType" value="${cfgType}"/>
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.add"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
<h2><span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.add"/></span></h2>

<table>
<tr><td>
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.title"/></span>
<input type="text" name="title" size="50" value="" />
</td></tr>
<tr><td>
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.description"/></span>
    <div align="left">
    <ssf:htmleditor name="description" />
  	</div>
</td></tr>
<tr><td>
<c:if test="${cfgType == '5'}">
<c:set var="iconListPath" value="icons.folder" scope="request"/>
</c:if>
<c:if test="${cfgType != '5'}">
<c:set var="iconListPath" value="icons.workspace" scope="request"/>
</c:if>

<c:set var="iconValue" value="" scope="request"/>
<c:set var="property_name" value="iconName" scope="request"/>
<c:set var="property_caption" value="<%= NLT.get("__icon") %>" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/iconForm.jsp" %>

</td></tr>
</table>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.add"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>

</form>
</c:if>

</div>
</div>
</div>
</div>

