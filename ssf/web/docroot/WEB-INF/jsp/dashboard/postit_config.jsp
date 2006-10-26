<%
// The dashboard "post-it note" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript">dojo.require("dojo.widget.Editor");</script>
<span class="ss_bold"><ssf:nlt tag="dashboard.enterNoteText"/></span>
<br/>
<div class="ss_form_color" style="border:1px solid #CECECE; height:150px;">
<textarea id="data_note" name="data_note" dojoType="Editor"
  items="textGroup;"
  minHeight="150px"
><c:out
value="${ssDashboard.dashboard.components[ssComponentId].data.note[0]}"/></textarea>
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



