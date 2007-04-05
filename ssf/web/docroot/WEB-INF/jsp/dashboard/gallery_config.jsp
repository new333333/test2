<%
// The dashboard "gallery" component
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
<br/>
<span class="ss_bold"><ssf:nlt tag="dashboard.galleryImageSize"/></span>
<input type="radio" id="galleryImageSizeHandle" name="data_galleryImageSize" value="big">Big
<input type="radio" id="galleryImageSizeHandle" name="data_galleryImageSize" value="small">Small

<br/>
<br/>
<ssf:nlt tag="dashboard.config.search"/>
<br/>
<br/>

<ssf:searchForm form="${ss_dashboard_config_form_name}" element="data.query" 
  data="${ssDashboard.beans[ssComponentId].ssSearchFormData}" />
<input type="hidden" name="data_ssMaxHits" value="24"/>
<br/>

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
ss_setRadioCheckedByValue('galleryImageSizeHandle','${ssDashboard.dashboard.components[ssComponentId].data.galleryImageSize[0]}');
</script>
