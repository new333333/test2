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
<%@ page import="java.util.ArrayList" %>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body class="ss_style_body">
<div id="ss_pseudoAdministrationPortalDiv${renderResponse.namespace}">
</ssf:ifadapter>

<script type="text/javascript">
function ss_numbers_only(evt)
  {
     var charCode = (evt.which) ? evt.which : event.keyCode
     if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;

     return true;
  }
</script>
<c:set var="formName"><ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm</c:set>
<script type="text/javascript">
var ssReportURL="<ssf:url action="quota_report" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="binderType" value="${ssBinder.entityType}"/></ssf:url>";
</script>

<table class="ss_style" width="100%"><tr><td>
<form class="ss_style ss_form" 
	action="<ssf:url webPath="reportDownload"/>" 
	method="post" 
	name="${formName}">
<input type="hidden" name="ss_reportType" value="quota"/>
<div class="ss_buttonBarRight">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</div>
   <br/>
   <br/>
   <span class="ss_bold"><ssf:nlt tag="administration.report.quota"/></span>
   <br/>
   <br/>
	<div>
	<input type="radio" name="ssQuotaOption" value="UsersOnly" id="usersOnlyQuota" checked="checked"/><label for="usersOnlyQuota"><span><ssf:nlt tag="administration.report.quota.option.usersOnly"/></span></label><br/>
	<input type="radio" name="ssQuotaOption" value="WorkspacesOnly" id="workspacesOnlyQuota"/><label for="workspacesOnlyQuota"><span><ssf:nlt tag="administration.report.quota.option.workspacesOnly"/></span></label><br/>
	<input type="radio" name="ssQuotaOption" value="UsersAndWorkspaces" id="bothQuota"/><label for="bothQuota"><span><ssf:nlt tag="administration.report.quota.option.both"/></span></label><br/>
	</div>
	<div>
	<label for="quotaThreshold"><span><ssf:nlt tag="administration.report.quota.threshold"/></span></label><input type="text" name="ssQuotaThreshold" id="quotaThreshold" value="0" onkeypress="return ss_numbers_only(event)"/>
	</div>
   <div class="ss_buttonBarLeft">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
   </div>
</form>
<br>
</td></tr></table>

<ssf:ifadapter>
</div>
<script type="text/javascript">
var ss_parentAdministrationNamespace${renderResponse.namespace} = "";
function ss_administration_showPseudoPortal${renderResponse.namespace}(obj) {
	//See if we are in an iframe inside a portlet 
	var windowName = self.window.name    
	if (windowName.indexOf("ss_administrationIframe") == 0) {
		//We are running inside a portlet iframe; set up for layout changes
		ss_parentAdministrationNamespace${renderResponse.namespace} = windowName.substr("ss_administrationIframe".length)
		ss_createOnResizeObj('ss_setParentAdministrationIframeSize${renderResponse.namespace}', ss_setParentAdministrationIframeSize${renderResponse.namespace});
		ss_createOnLayoutChangeObj('ss_setParentAdministrationIframeSize${renderResponse.namespace}', ss_setParentAdministrationIframeSize${renderResponse.namespace});
	} else {
		//Show the pseudo portal
		var divObj = self.document.getElementById('ss_pseudoAdministrationPortalDiv${renderResponse.namespace}');
		if (divObj != null) {
			divObj.className = "ss_pseudoPortal"
		}
		divObj = self.document.getElementById('ss_upperRightToolbar${renderResponse.namespace}');
		if (divObj != null) {
			divObj.style.display = "block"
			divObj.style.visibility = "visible"
		}
		divObj = self.document.getElementById('ss_administrationHeader_${renderResponse.namespace}');
		if (divObj != null) {
			divObj.style.display = "block"
			divObj.style.visibility = "visible"
		}
	}
}
ss_administration_showPseudoPortal${renderResponse.namespace}();
</script>
	</body>
</html>
</ssf:ifadapter>
