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
<% // Form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String enctype = "application/x-www-form-urlencoded";
	if (item.selectSingleNode(".//item[@name='file']") != null || 
			item.selectSingleNode(".//item[@name='fileEntryTitle']") != null || 
			item.selectSingleNode(".//item[@name='graphic']") != null || 
			item.selectSingleNode(".//item[@name='profileEntryPicture']") != null || 
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


<div id="ss_tab_content">
		
	<form method="<%= methodName %>" enctype="<%= enctype %>" name="<%= formName %>" 
	  id="<%= formName %>" action="" onSubmit="return ssSurvey.prepareSubmit(this);">
  
			<c:set var="onClickCancelRoutine" value="ss_cancelButtonCloseWindow();return false;" scope="request"/>
		<div class="ss_surveyContainer">
			<div id="ss_surveyForm_spacer">&nbsp;</div>
			<div id="ss_content">

				<div id="ss_surveyForm_container">
					<div id="ss_surveyForm">

						<div id="ss_surveyForm_main">
							<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
							  configElement="<%= item %>" 
							  configJspStyle="${ssConfigJspStyle}" />
							
						</div>
					</div>
				</div>
						
			</div>
		</div>
	</form>
	
</div>


