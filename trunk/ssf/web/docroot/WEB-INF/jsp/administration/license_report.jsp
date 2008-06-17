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

<c:set var="formName"><ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm</c:set>

<table class="ss_style" width="100%"><tr><td>
<form class="ss_style ss_form" 
	action="<ssf:url action="license_report" actionUrl="true"><ssf:param 
		name="binderId" value="${ssBinder.id}"/><ssf:param 
		name="binderType" value="${ssBinder.entityType}"/></ssf:url>" 
	method="post" 
	name="${formName}">
<input type="hidden" name="ss_reportType" value="license"/>
<div class="ss_buttonBarRight">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</div>
   <br/>
   <br/>
   <span class="ss_bold"><ssf:nlt tag="administration.report.license"/></span>
   <br/>
   <br/>
   <ssf:nlt tag="administration.report.dates"/>
   <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_startPopup" id="ss_startDate" initDate="${startDate}"
				 immediateMode="false" altText="<%= com.sitescape.team.util.NLT.get("calendar.view.popupAltText") %>"
				 />
   </div>
   <div id="ss_startPopup" class="ss_calPopupDiv"></div>
   <ssf:nlt tag="smallWords.and"/>
   <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
				 immediateMode="false" altText="<%= com.sitescape.team.util.NLT.get("calendar.view.popupAltText") %>"
				 />
   </div>
   <br/>
   <div id="ss_endPopup" class="ss_calPopupDiv"></div>
   <div class="ss_buttonBarLeft">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
   </div>
</form>
<c:if test="${not empty ssLicenseData}">
<hr>
<p class="ss_bold">
ICEcore Enterprise Version 1.0<br/>
License Audit Report  -- <fmt:formatDate value="${ssCurrentDate}" pattern="yyyy-MM-dd HH:mm:ss z" timeZone="${ssUser.timeZone.ID}"/>
</p>
<p>
License Information<br/>
  Key id:    ${ssLicenseKey}<br/>
  Issued:    ${ssLicenseIssued}<br/>
  Effective: ${ssLicenseEffective}<br/>
  Users:     ${ssLicenseUsers}<br/>
</p><%--
--%><c:set var="highWater" value="-1"/><%--
--%><c:set var="highWaterDate" value=""/><%--
--%><c:set var="currentUser" value="0"/><%--
	--%><c:forEach var="datum" items="${ssLicenseData}" ><%--
	--%><c:if test="${datum.internalUserCount > highWater}"><%--
	    --%><c:set var="highWater" value="${datum.internalUserCount}"/><%--
	    --%><c:set var="highWaterDate" value="${datum.snapshotDate}"/><%--
	--%></c:if><%--
	--%><c:set var="currentUser" value="${datum.internalUserCount}"/><%--
--%></c:forEach>
<p>
Users highwater mark: ${highWater} (${highWaterDate})<br/>
Current user count: ${currentUser}
</p>
<p class="ss_bold">
Usage History
</p>
<table syle="border-spacing: 2px;"><tbody>
<tr>
<th>Date</th><th>Registered</th><th>External</th><th>Check</th>
</tr>
<c:forEach var="datum" items="${ssLicenseData}" >
<tr>
<td><fmt:formatDate value="${datum.snapshotDate}" pattern="yyyy-MM-dd" timeZone="${ssUser.timeZone.ID}"/></td>
<td style="text-align: right">${datum.internalUserCount}</td>
<td style="text-align: right">${datum.externalUserCount}</td>
<td style="text-align: right">${datum.checksum}</td>
</tr>
</c:forEach>
</table>
<p>  
Report checksum: 41fec13e30afd9cffe48a20ba5ce55982f95c0cf
</p>
<p>
${ssLicenseContact}
</p>   
</c:if>
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
