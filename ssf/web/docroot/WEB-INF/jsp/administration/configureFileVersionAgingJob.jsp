<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.configure_file_version_aging") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.configure_file_version_aging">
<br/>

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
		return false;
<%	} %>
	
}// end handleCloseBtn()

function ss_checkIfNumberValid(s) {
	if (ss_trim(s) == '') return true;   //Blank is ok
	
	var pattern1 = new RegExp("^[0-9]+$");
	if (pattern1.test(ss_trim(s))) {
		if (ss_trim(s).length >= 8) {
			alert("<ssf:escapeJavaScript><ssf:nlt tag="error.numberTooBig"/></ssf:escapeJavaScript>");
			return false;
		}
		return true;
	}
	alert("<ssf:escapeJavaScript><ssf:nlt tag="error.mustBeANumber"/></ssf:escapeJavaScript>");
	return false;
}

</script>

<form class="ss_style ss_form" name="form1" method="post" 
	action="<ssf:url action="configure_file_version_aging_job" actionUrl="true"/>">
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="return handleCloseBtn();"/>
</div>
<br/>
<fieldset class="ss_fieldset">
  <legend><ssf:nlt tag="administration.configure_file_version_aging"/></legend>
  <div>
    <span><ssf:nlt tag="administration.configure_file_version_aging.value"/></span>
    <input type="text" name="maxVersionAge" value="${ss_fileVersionMaximumAge}"
    	style="width:80px; text-align:right;"
        onChange="if (!ss_checkIfNumberValid(this.value)){this.value='';}"
    />
    <br/>
	<span class="ss_smallprint" style="padding-left:16px;">
	  <ssf:nlt tag="binder.versions.leaveBlankForNoLimit"/>
	</span>
  </div>
</fieldset>
<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		onClick="return handleCloseBtn();" />
</div>
	<sec:csrfInput />
</form>

</ssf:form>
</div>
</div>
</body>
</html>
