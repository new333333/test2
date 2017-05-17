<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.onSubmit="return(ss_checkForFileSelected())"
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ include file="/WEB-INF/jsp/common/initializeGWT.jsp"     %>

<c:set var="ss_windowTitle" value='<%= NLT.get("administration.manage.extensions") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

<script type="text/javascript">
	/**
	 * 
	 */
	function handleCloseBtn()
	{
	<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			// Tell the Teaming GWT ui to close the administration content panel.
			if ( window.parent.ss_closeAdministrationContentPanel ) {
				window.parent.ss_closeAdministrationContentPanel();
			} else {
				ss_cancelButtonCloseWindow();
			}

			return false;
	<% 	}
		else { %>
			ss_cancelButtonCloseWindow();
			return true;
	<%	} %>
	
	}// end handleCloseBtn()
</script>

<div class="ss_pseudoPortal">
<ssf:form titleTag="administration.manage.extensions">

<script type="text/javascript">

function ${renderResponse.namespace}_onsub(obj) {
	if (obj.name.value == '') {
		alert('<ssf:nlt tag="general.required.name"/>');
		return false;
	}
	return true;
}

var ss_confirmDeleteZoneText = "<ssf:nlt tag="administration.extensions.confirmDeleteExtension"/>";
function ss_confirmDeleteZone() {
	if (confirm(ss_confirmDeleteZoneText)) {
		ss_startSpinner();
		return true;
	} else {
		return false;
	}
}

</script>

<div class="ss_style ss_portlet">
<div style="padding:10px;" id="ss_manageZones">
<br>
<ssf:expandableArea title='<%= NLT.get("administration.extensions.add") %>'>


<script type="text/javascript">

//Create an onload handler that will look for errors passed to this page from a previous request.
ss_createOnLoadObj( 'onloadCheckForErrors', onloadCheckForErrors );

/**
 * This function gets called when the page is loaded.  It checks to see if an error returned from a previous
 * request to import a profile.
 */
function onloadCheckForErrors()
{
	// Did an error happen while importing a profile?
	<c:if test="${!empty ssException}">
		var errMsg;

		// Yes, tell the user about it.
		errMsg = '<ssf:escapeJavaScript><ssf:nlt tag="administration.import.profiles.error"/>${ssException}</ssf:escapeJavaScript>';
		alert( errMsg );
	</c:if>
	 
}// end onloadCheckForErrors()


function ss_checkForFileSelected() {
	var formObj = document.forms['form1']
	if (formObj.uploadFormElement.value == '') {
		alert("<ssf:nlt tag="administration.import.profiles.selectFile"/>")
		return false;
	}

	ss_startSpinner();
	
	return true;
}

</script>

<form name="form1" class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<ssf:url adapter="true" 
			action="manage_extensions" 
			actionUrl="true"></ssf:url>" >
<br>
<div class="ss_divider"></div>
<br>
		
<br>
<label for="profiles"><span class="ss_bold"><ssf:nlt tag="administration.profiles.file"/></span></label>
<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="95%">
<tr><td>
<input type="file" size="80" class="ss_text" name="uploadFormElement" id="uploadFormElement"><br>
</td></tr>
</table>
<br/>

<!-- 
<label for="zoneName"><span class="ss_bold"><ssf:nlt tag="administration.extensions.zones"/></span></label>
<select name="zoneName" id="zoneName">
<c:set var="default_zone_name" value="<%= org.kablink.teaming.util.SZoneConfig.getDefaultZoneName() %>"/>
<c:forEach var="zoneInfo" items="${ss_zoneInfoList}">
<option value="${zoneInfo.zoneName}" <c:if test="${default_zone_name == zoneInfo.zoneName}">selected</c:if> >${zoneInfo.zoneName}</option>
</c:forEach>
</select>
<br/>
<br/>
 -->

<div class="ss_formBreak"></div>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="addBtn" onclick="return ss_checkForFileSelected();" value="<ssf:nlt tag="button.add" text="Add"/>">
</div>
</div>
	<sec:csrfInput />
</form>

</ssf:expandableArea>
<br/>
<br/> 

<script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/gwtteaming.nocache.js"></script>
<div id="gwtExtensionsConfigDiv">
</div>

<br/>
<br/>
	<form class="ss_portlet_style ss_form" id="${ssNamespace}_btnForm" 
	  name="${ssNamespace}_btnForm" method="post" 
	  action="<ssf:url action="site_administration" actionUrl="false"/>">
		<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="return handleCloseBtn();"/>
		<sec:csrfInput />
	</form>
</div>
</div>

</ssf:form>
</div>
</body>
</html>