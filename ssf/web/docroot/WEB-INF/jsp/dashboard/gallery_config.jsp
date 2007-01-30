<%
// The dashboard "search" component
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
