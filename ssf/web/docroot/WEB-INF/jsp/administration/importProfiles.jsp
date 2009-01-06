<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.import.profiles") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">

<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.import.profiles">
<script type="text/javascript">
function ss_checkForFileSelected() {
	var formObj = document.forms['form1']
	if (formObj.profiles.value == '') {
		alert("<ssf:nlt tag="administration.import.profiles.selectFile"/>")
		return false;
	}
	return true;
}

</script>

<form name="form1" class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<ssf:url adapter="true" 
			portletName="ss_administration" 
			action="import_profiles" 
			actionUrl="true" ><ssf:param 
		    name="binderId" value="${ssBinder.id}"/></ssf:url>" >
<span class="ss_titlebold"><ssf:nlt tag="administration.import.profiles" /></span>
<br>

<div class="ss_divider"></div>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.profiles.file"/></span>
<br>
<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="95%">
<tr><td>
<input type="file" size="80" class="ss_text" name="profiles" ><br>
</td></tr></table>
<div class="ss_divider"></div>

<br/>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"
  onclick="return ss_checkForFileSelected();"
/>

<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
onClick="window.close();return false;"/>

</div>
</div>
</form>
</ssf:form>
</div>
</div>
</body>
</html>
