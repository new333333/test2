<%
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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_style ss_portlet">
<div class="ss_form" style="margin:6px;">
<div style="margin:6px;">

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

<c:if test="${ssOperation == 'modify_template'}">
<form method="post" action="<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="configure_configuration"/><portlet:param 
		name="operation" value="modify_template"/><portlet:param 
		name="binderId" value="${ssBinderConfig.id}"/></portlet:actionURL>" >

<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.modify"/>">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
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
</table>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.modify"/>">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</div>

</form>
</c:if>

<c:if test="${ssOperation == 'add'}">
<c:if test="${cfgType == '-1'}">
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
<form class="ss_style ss_form" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" 
    id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" method="post" 
    action="<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="configure_configuration"/><portlet:param 
		name="operation" value="add"/></portlet:actionURL>" >
<input type="hidden" name="cfgType" value="-1"/>
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
<c:if test="${cfgType == '-2'}">
<form class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<portlet:actionURL windowState="maximized"><portlet:param 
		  name="action" value="configure_configuration"/><portlet:param 
		  name="operation" value="add"/></portlet:actionURL>" 
		  name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm">
<div class="ss_style ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="administration.configure_cfg.import" /></span>
<br>
<input type="hidden" name="cfgType" value="-2"/>
<div class="ss_divider"></div>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.selectFiles"/></span>
<br>
<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="95%">
<tr><td>
<input type="file" size="80" class="ss_text" name="template1" ><br>
<input type="file" size="80" class="ss_text" name="template2" ><br>
<input type="file" size="80" class="ss_text" name="template3" ><br>
<input type="file" size="80" class="ss_text" name="template4" ><br>
<input type="file" size="80" class="ss_text" name="template5" ><br>
</td></tr></table>
<div class="ss_divider"></div>

<br/>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>
</div>
</form>

</c:if>
<c:if test="${cfgType != '-1' and cfgType != '-2'}">

<script type="text/javascript">

function <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_onsub(obj) {
	if (obj.title.value == '') {
		alert('<ssf:nlt tag="general.required.title"/>');
		return false;
	}
	return true;
}
</script>
<form method="post" action="<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="configure_configuration"/><portlet:param 
		name="operation" value="add"/></portlet:actionURL>" 
		>
<input type="hidden" name="cfgType" value="${cfgType}"/>
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.add"/>" onClick="return(<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_onsub(this.form))">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
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

</table>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.add"/>" onClick="return(<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_onsub(this.form))">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</div>

</form>
</c:if>
</c:if>
</div>
</div>
</div>

