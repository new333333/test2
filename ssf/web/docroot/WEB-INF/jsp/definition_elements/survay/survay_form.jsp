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
	  id="<%= formName %>" action="" onSubmit="return ss_prepareSubmit(this);">
  
			<c:set var="onClickCancelRoutine" value="ss_cancelButtonCloseWindow();return false;" scope="request"/>
		<div class="ss_survayContainer">
			<div id="ss_survayForm_spacer">&nbsp;</div>
			<div id="ss_content">

				<div id="ss_survayForm_container">
					<div id="ss_survayForm">

						<div id="ss_survayForm_main">
							<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
							  configElement="<%= item %>" 
							  configJspStyle="${ssConfigJspStyle}" />
							
						</div>
					</div>
				</div>
						
			</div>
		</div>
	</form>
	
<script type="text/javascript">
// test only 
	var ss_initialArray = new Array();
	ss_initialArray[0] = new Array();
	ss_initialArray[0].type = "multiple";
	ss_initialArray[0].question = "Bla bla bla - multiple";
	ss_initialArray[0].answers = new Array();
	ss_initialArray[0].answers[0] = "a";
	ss_initialArray[0].answers[1] = "b";
	ss_initialArray[0].answers[2] = "c";
	ss_initialArray[0].answers[3] = "d";
	ss_initialArray[1] = new Array();
	ss_initialArray[1].type = "single";
	ss_initialArray[1].question = "2nd question - single test";
	ss_initialArray[1].answers = new Array();
	ss_initialArray[1].answers[0] = "1";
	ss_initialArray[1].answers[1] = "2";
	ss_initialArray[1].answers[2] = "3";
	ss_initialArray[2] = new Array();
	ss_initialArray[2].type = "input";
	ss_initialArray[2].question = "Your favourites name:";	
	ss_initialArray[3] = new Array();
	ss_initialArray[3].type = "single";
	ss_initialArray[3].question = "last single";	
	ss_initialArray[3].answers = new Array(); 
	ss_initialArray[3].answers[0] = "yes";
	ss_initialArray[3].answers[1] = "no";

	// dojo.addOnLoad(function() {ss_initSurvayQuestions(ss_initialArray)});

</script>
</div>


