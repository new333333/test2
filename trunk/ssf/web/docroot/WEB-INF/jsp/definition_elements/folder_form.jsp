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

<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="folder.type" 
    text="Folder type"/></legend>
  <span class="ss_bold"><ssf:nlt tag="folder.selectFolderType" 
  text="Select the type of folder:"/></span>
  <br/>
  <c:forEach var="item" items="${ssPublicFolderDefinitions}">
      <c:choose>
        <c:when test="${ssDefaultFolderDefinitionId == item.value.id}">
          <input type="radio" name="binderDefinition" value="${item.value.id}" checked>
          <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
        </c:when>
        <c:otherwise>
          <input type="radio" name="binderDefinition" value="${item.value.id}">
          <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
        </c:otherwise>
      </c:choose>
  </c:forEach>

</fieldset>

<br/>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" />

<br>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" 
  value="<ssf:nlt tag="button.ok" text="  OK  "/>" >&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="cancelBtn" 
  value="<ssf:nlt tag="button.cancel" text="Cancel"/>" >
</div>
</form>
</div>
