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
<input type="checkbox" name="ss_library" <c:out value="${cb_checked}"/> onClick="if (document.${formName}.ss_library.checked) document.${formName}.library.value='true'; else document.${formName}.library.value='false';">&nbsp;<span class="ss_labelRight"><ssf:nlt tag="folder.isLibrary"/></span></input>
</div>
<input type="hidden" name="library" value="${ssDefinitionEntry.library}"/>
<br/>
<c:set var="cb_checked" value=""/>
<c:if test="${ssDefinitionEntry.uniqueTitles}" >
<c:set var="cb_checked" value="checked"/>
</c:if>
<div style="display:block">
<input type="checkbox" name="ss_unique" <c:out value="${cb_checked}"/> onClick="if (document.${formName}.ss_unique) document.${formName}.uniqueTitles.value='true'; else document.${formName}.uniqueTitles.value='false';">&nbsp;<span class="ss_labelRight"><ssf:nlt tag="folder.isUniqueTitles"/></span></input>
</div>
<input type="hidden" name="uniqueTitles" value="${ssDefinitionEntry.uniqueTitles}"/>
<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" 
  value="<ssf:nlt tag="button.ok" text="  OK  "/>" >&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="cancelBtn" 
  value="<ssf:nlt tag="button.cancel" text="Cancel"/>" >
</div>
</form>
</div>
