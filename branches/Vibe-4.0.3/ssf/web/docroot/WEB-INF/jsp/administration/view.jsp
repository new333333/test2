<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.ObjectKeys" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("window.title.siteAdmin") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="showAdministrationPage" value="true"/>
<ssf:ifnotadapter>
  <c:set var="showAdministrationPage" value="false"/>
</ssf:ifnotadapter>

<ssf:ifadapter>
<body class="ss_style_body tundra">
<div id="ss_pseudoAdministrationPortalDiv${renderResponse.namespace}">
</ssf:ifadapter>

<c:if test="${showAdministrationPage}">
	<div class="ss_style ss_portlet" style="border:1px solid #CCC;">
	<div style="border:1px solid #CCC;">
<ssf:skipLink tag='<%= NLT.get("skip.header.toContent") %>' id="headerToContent_${renderResponse.namespace}">
	<div id="ss_administrationHeader_${renderResponse.namespace}" style="display:none;">
		<jsp:include page="/WEB-INF/jsp/forum/view_workarea_navbar.jsp" />
	</div>
</ssf:skipLink>
	

	<c:set var="adminTreeName" value="${renderResponse.namespace}_adminDomTree"/>
	  <div class="ss_portlet_style ss_portlet">
	  <div class="ss_style" style="padding:10px;">
	    <c:out value="${releaseInfo}"/>
	<ssf:ifLicenseExpired><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.expired.warning"/></span></div></ssf:ifLicenseExpired>
	<ssf:ifLicenseExpired invert="true">
	  <ssf:ifLicenseExpired inThisManyDays="30"><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.expiring.soon.warning"/></span></div></ssf:ifLicenseExpired>
	  <ssf:ifLicenseExpired inThisManyDays="30" invert="true">  
		  <ssf:ifLicenseOutOfCompliance><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.out.of.compliance"/></span></div></ssf:ifLicenseOutOfCompliance>
	  </ssf:ifLicenseExpired>
	</ssf:ifLicenseExpired>
</c:if>

<c:if test="${empty ss_portletInitialization}">
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
			divObj.focus();
		}
		divObj = self.document.getElementById('ss_administrationHeader_${renderResponse.namespace}');
		if (divObj != null) {
			divObj.style.display = "block"
			divObj.style.visibility = "visible"
			divObj.focus();
		}
	}
}

function ss_setParentAdministrationIframeSize${renderResponse.namespace}() {
	ss_debug('In routine: ss_setParentAdministrationIframeSize${renderResponse.namespace}')
	if (typeof self.parent != "undefined") {
		var resizeRoutineName = "ss_setAdministrationIframeSize" + ss_parentAdministrationNamespace${renderResponse.namespace};
		eval("var resizeRoutineExists = typeof(self.parent."+resizeRoutineName+")");
		ss_debug('resizeRoutineExists = '+resizeRoutineExists)
		if (resizeRoutineExists != "undefined") {
			ss_debug('namespace = ${renderResponse.namespace}')
			eval("ss_debug(self.parent."+resizeRoutineName+")");
			eval("self.parent."+resizeRoutineName+"()");
		} else {
			//See if there is a common routine to call in case the namespaces don't match
			if (typeof self.parent.ss_setAdministrationIframeSize != "undefined") {
				self.parent.ss_setAdministrationIframeSize();
			}
		}
	}
}

function ss_administration_showPortal${renderResponse.namespace}(obj) {
	//See if we are in an iframe inside a portlet 
	var windowName = self.window.name    
	if (windowName.indexOf("ss_administrationIframe") == 0) {
		//We are running inside a portlet iframe
		if (obj.href != "") self.parent.location.href = obj.href;
	} else {
		self.location.href = obj.href;
	}
}

var ss_administrationIframeOffset = 50;
function ss_setAdministrationIframeSize${renderResponse.namespace}() {
	var iframeDiv = document.getElementById('ss_administrationIframe${renderResponse.namespace}')
	if (window.frames['ss_administrationIframe${renderResponse.namespace}'] != null) {
		eval("var iframeHeight = parseInt(window.ss_administrationIframe${renderResponse.namespace}" + ".document.body.scrollHeight);")
		if (iframeHeight > 100) {
			iframeDiv.style.height = iframeHeight + ss_administrationIframeOffset + "px"
		}
	}
}

function ss_showAdminMenuOption${renderResponse.namespace}(id, obj, action) {
	var features;
	
	//If this is a request to show the error logs, don't pop-up
	if (action == 'get_log_files') return true;

	features = "directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no";

	// If we are opening the "Activity By User" page, give the new window a width and height.
	if ( action == 'activity_report_by_user' ) {
		features += ',width=900px,height=500px';
	}
	
	// If we are opening the "Activity By User" page, give the new window a width and height.
	else if ( action == 'view_credits' ) {
		features += ',width=700px,height=600px';
	}

	self.window.open( obj.href, "_blank", features );
	return false;
}

function ss_returnToBinder${renderResponse.namespace}(binderId, entityType) {
	if (typeof binderId != 'undefined' && typeof entityType != 'undefined') {
		var action = "";
		if (entityType == 'workspace') {
			action = "view_ws_listing"
		} else if (entityType == 'folder') {
			action = "view_folder_listing"
		} else if (entityType == 'profiles') {
			action = "view_profile_listing"
		}
		if (action != '') {
			var url = "<ssf:url action="ssActionPlaceHolder"><ssf:param 
			name="binderId" value="ssBinderIdPlaceHolder"/></ssf:url>"
			url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
			url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
			self.location.href = url;
		}
	}
}

ss_createOnResizeObj('ss_setAdministrationIframeSize${renderResponse.namespace}', ss_setAdministrationIframeSize${renderResponse.namespace});
ss_createOnLayoutChangeObj('ss_setAdministrationIframeSize${renderResponse.namespace}', ss_setAdministrationIframeSize${renderResponse.namespace});
//If this is the first definition of ss_setAdministrationIframeSize, remember its name in case we need to find it later
if (typeof ss_setAdministrationIframeSize == "undefined") 
	var ss_setAdministrationIframeSize = ss_setAdministrationIframeSize${renderResponse.namespace};

</script>
<ssf:ifnotadapter>
<iframe id="ss_administrationIframe${renderResponse.namespace}" 
    name="ss_administrationIframe${renderResponse.namespace}" 
    style="width:100%; height:400px; display:block; position:relative;"
	src="<ssf:url     
    		adapter="true" 
    		portletName="ss_forum" 
    		action="site_administration" 
    		actionUrl="false" >
        <ssf:param name="namespace" value="${renderResponse.namespace}"/>
        </ssf:url>" 
	onLoad="ss_setAdministrationIframeSize${renderResponse.namespace}();" 
	frameBorder="0" title="<ssf:nlt tag="toolbar.menu.siteAdministration"/>">Micro Focus Vibe</iframe>

</ssf:ifnotadapter>

<c:if test="${showAdministrationPage}">
<c:if test="${!empty ssBinderId && !empty ss_entityType}">
<div class="ss_buttonBarRight">
<input type="button" class="ss_submit" name="closeBtn" 
  onClick="ss_returnToBinder${renderResponse.namespace}('${ssBinderId}', '${ss_entityType}');return false;"
  value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
<br/>
</c:if>
<c:set var="adminInternalId" value="<%= ObjectKeys.SUPER_USER_INTERNALID %>"/>
<c:if test="${ss_upgradeVersionCurrent != ss_upgradeVersion}">
<c:if test="${ssUser.internalId == adminInternalId}">
  <div>
    <span class="ss_errorLabel ss_bold"><ssf:nlt tag="administration.upgrade.tasksNotDone"/></span>
  </div>
 <c:if test="${empty ssUserProperties.upgradeDefinitions || 
 		empty ssUserProperties.upgradeTemplates || 
 		empty ssUserProperties.upgradeSearchIndex}">
  <div>
	<ul>
	  <c:if test="${empty ssUserProperties.upgradeDefinitions}">
	    <li><span class="ss_errorLabel"><ssf:nlt tag="administration.upgradeDefinitions"/></span></li>
	  </c:if>
	  <c:if test="${empty ssUserProperties.upgradeTemplates}">
	    <li><span class="ss_errorLabel"><ssf:nlt tag="administration.upgradeTemplates"/></li>
	  </c:if>
	  <c:if test="${empty ssUserProperties.upgradeSearchIndex}">
	    <li><span class="ss_errorLabel"><ssf:nlt tag="administration.upgradeSearchIndex"/></li>
	  </c:if>
	</ul>
  </div>
 </c:if>
</c:if>
<c:if test="${ssUser.internalId != adminInternalId}">
  <div >
    <span class="ss_errorLabel ss_bold"><ssf:nlt tag="administration.upgrade.tasksNotDone"/></span>
    <br/>
    <c:if test="${ss_isSiteAdmin}">
      <span class="ss_errorLabel ss_bold"><ssf:nlt tag="administration.upgrade.tasksNotDoneByAdmin"/></span>
    </c:if>
  </div>
</c:if>
</c:if>

	<table border="0" width="100%">
	<tr>
	  <td>
  <c:if test="${ss_isSiteAdmin}">
	    <ssHelpSpot helpId="portlets/admin/admin_portlet_site" 
	      title="<ssf:nlt tag="helpSpot.adminPortletSite"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt>" 
	      offsetY="9" offsetX="-16">
	    </ssHelpSpot>
  </c:if>
  <c:if test="${!ss_isSiteAdmin}">
	    <ssHelpSpot helpId="portlets/admin/admin_portlet" 
	      title="<ssf:nlt tag="helpSpot.adminPortlet"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt>" 
	      offsetY="9" offsetX="-16">
	    </ssHelpSpot>
  </c:if>
	  </td>
	</tr>
	<tr>
		<td>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<div>
					  <ssf:tree treeName="${adminTreeName}" 
					    treeDocument="${ssAdminDomTree}" 
					    rootOpen="true" 
					    showIdRoutine="ss_showAdminMenuOption${renderResponse.namespace}" />
					</div>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	</table>

<c:if test="${!empty ssBinderId && !empty ss_entityType}">
<br/>
<div class="ss_buttonBarLeft">
<input type="button" class="ss_submit" name="closeBtn" 
  onClick="ss_returnToBinder${renderResponse.namespace}('${ssBinderId}', '${ss_entityType}');return false;"
  value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
</c:if>

  </div>
  </div>
  </div>
<script type="text/javascript">
ss_administration_showPseudoPortal${renderResponse.namespace}();
</script>

</c:if>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
</c:if>

<ssf:ifadapter>
</div>
	</body>
</html>
</ssf:ifadapter>
