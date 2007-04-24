<%
// The dashboard "post-it note" component
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<span class="ss_bold"><ssf:nlt tag="dashboard.enterNoteText"/></span>
<br/>
<div class="ss_form_color" style="border:1px solid #CECECE;">
<ssf:htmleditor id="data_note" name="data_note" 
><c:out
value="${ssDashboard.dashboard.components[ssComponentId].data.note[0]}"/></ssf:htmleditor>
</div>
<br/>
<span class="ss_bold"><ssf:nlt tag="dashboard.postitNoteColor"/></span>
<br/>
<input type="radio" id="noteColorHandle" name="data_noteColor" value="#FFD0D0">
<div style="display:inline; padding: 3px; background-color: #FFD0D0"><ssf:nlt tag="__color_Red"/></div>
<input type="radio" id="noteColor2" name="data_noteColor" value="#D0FFD0">
<div style="display:inline; padding: 3px; background-color: #D0FFD0"><ssf:nlt tag="__color_Green"/></div>
<input type="radio" id="noteColor3" name="data_noteColor" value="#FFFFD9">
<div style="display:inline; padding: 3px; background-color: #FFFFD0"><ssf:nlt tag="__color_Yellow"/></div>
<input type="radio" id="noteColor4" name="data_noteColor" value="transparent">
<div style="display:inline; padding: 3px; background-color: transparent"><ssf:nlt tag="__color_Transparent"/></div>

<script type="text/javascript">
function ss_setRadioCheckedByValue(id, value) {
  var rbHandle = document.getElementById(id);
  var formObj, buttonName, radioGroup;
  if (rbHandle) {
  	formObj = rbHandle.form;
  	buttonName = rbHandle.name;
  	radioGroup = formObj.elements[buttonName];
  	for (var i = 0; i < radioGroup.length; i++) {
  		if (radioGroup[i].value == value) {
  			radioGroup[i].checked = true;
  		}
  	}
  }
}
ss_setRadioCheckedByValue('noteColorHandle','${ssDashboard.dashboard.components[ssComponentId].data.noteColor[0]}');
</script>



