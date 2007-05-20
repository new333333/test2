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
<% //Add/modify a folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String enctype = "application/x-www-form-urlencoded";
	if (item.selectSingleNode(".//item[@name='file']") != null || 
			item.selectSingleNode(".//item[@name='graphic']") != null || 
			item.selectSingleNode(".//item[@name='attachFiles']") != null) {
		enctype = "multipart/form-data";
	}
	String formName = (String) request.getAttribute("property_name");
	if (formName == null || formName.equals("")) {
		formName = WebKeys.DEFINITION_DEFAULT_FORM_NAME;
	}
	request.setAttribute("formName", formName);
	String methodName = (String) request.getAttribute("property_method");
	if (methodName == null || methodName.equals("")) {
		methodName = "post";
	}
%>
<div class="ss_style ss_portlet" width="100%">
<form class="ss_form" method="<%= methodName %>" 
  enctype="<%= enctype %>" name="<%= formName %>" 
  id="<%= formName %>" action="" onSubmit="return ss_onSubmit(this);">

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" />

<br>
<c:set var="cb_checked" value=""/>
<c:if test="${ssDefinitionEntry.library}" >
<c:set var="cb_checked" value="checked"/>
</c:if>
<div style="display:block">
<input type="checkbox" name="ss_library" <c:if test="${ssDefinitionEntry.mirrored}">disabled</c:if> <c:out value="${cb_checked}"/> onClick="if (document.${formName}.ss_library.checked) document.${formName}.library.value='true'; else document.${formName}.library.value='false';">&nbsp;<span class="ss_labelRight"><ssf:nlt tag="folder.isLibrary"/></span></input>
</div>
<input type="hidden" name="library" value="${ssDefinitionEntry.library}"/>
<br/>

<c:set var="cb_checked" value=""/>
<c:if test="${ssDefinitionEntry.uniqueTitles}" >
<c:set var="cb_checked" value="checked"/>
</c:if>
<div style="display:block">
<input type="checkbox" name="ss_unique" <c:out value="${cb_checked}"/> onClick="if (document.${formName}.ss_unique.checked) document.${formName}.uniqueTitles.value='true'; else document.${formName}.uniqueTitles.value='false';">&nbsp;<span class="ss_labelRight"><ssf:nlt tag="folder.isUniqueTitles"/></span></input>
</div>
<input type="hidden" name="uniqueTitles" value="${ssDefinitionEntry.uniqueTitles}"/>
<br/>

<c:if test="${ssDefinitionEntry.mirroredAllowed}">
<c:set var="cb_checked" value=""/>
<c:if test="${ssDefinitionEntry.mirrored}" >
<c:set var="cb_checked" value="checked"/>
</c:if>
<c:set var="resourceDrivers" value="<%= com.sitescape.team.fi.connection.ResourceDriverManagerUtil.getAllowedResourceDrivers() %>"/>
<div style="display:block">
<input type="checkbox" name="ss_mirrored" <c:if test="${ssDefinitionEntry.mirrored || empty resourceDrivers}">disabled</c:if> <c:out value="${cb_checked}"/> onClick="if (document.${formName}.ss_mirrored.checked) document.${formName}.mirrored.value='true'; else document.${formName}.mirrored.value='false';">&nbsp;<span class="ss_labelRight"><ssf:nlt tag="folder.isMirrored"/></span></input>
</div>
<input type="hidden" name="mirrored" value="${ssDefinitionEntry.mirrored}"/>
<br/>

<c:if test="${ssDefinitionEntry.mirrored}" >
<span class="ss_labelLeft"><ssf:nlt tag="folder.resource.driver.label"/></span>
<input type="text" class="ss_text" size="30" name="resourceDriver" value="${ssDefinitionEntry.resourceDriverName}" disabled/>
</c:if>
<c:if test="${!ssDefinitionEntry.mirrored && !empty resourceDrivers}" >
<span class="ss_labelLeft"><ssf:nlt tag="folder.resource.driver.label"/></span>
<select name="resourceDriverName" <c:if test="${ssDefinitionEntry.mirrored}">disabled</c:if>>
<c:forEach var="driver" items="${resourceDrivers}">
<option value="${driver.name}" <c:if test="${driver.name == ssDefinitionEntry.resourceDriverName}">selected</c:if>>${driver.title}</option>
</c:forEach>
</select>
</c:if>
<br/>

<c:if test="${ssDefinitionEntry.mirrored || !empty resourceDrivers}" >
<span class="ss_labelLeft"><ssf:nlt tag="folder.resource.path.label"/></span>
<input type="text" class="ss_text" size="80" name="resourcePath" value="${ssDefinitionEntry.resourcePath}" <c:if test="${ssDefinitionEntry.mirrored}">disabled</c:if>></br/>
</c:if>
<br/>
</c:if>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" 
  value="<ssf:nlt tag="button.ok" text="  OK  "/>" >&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="cancelBtn" 
  value="<ssf:nlt tag="button.cancel" text="Cancel"/>" >
</div>
</form>
</div>
