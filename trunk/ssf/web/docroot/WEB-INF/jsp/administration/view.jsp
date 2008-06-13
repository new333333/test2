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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="showAdministrationPage" value="true"/>
<ssf:ifnotadapter>
  <c:set var="showAdministrationPage" value="false"/>
</ssf:ifnotadapter>

<ssf:ifadapter>
<body class="ss_style_body">
<div id="ss_pseudoAdministrationPortalDiv${renderResponse.namespace}">
</ssf:ifadapter>

<c:if test="${showAdministrationPage}">
	<div id="ss_administrationHeader_${renderResponse.namespace}" style="display:none;">
	  <%@ include file="/WEB-INF/jsp/forum/view_workarea_navbar.jsp" %>
	</div>

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
		}
		divObj = self.document.getElementById('ss_administrationHeader_${renderResponse.namespace}');
		if (divObj != null) {
			divObj.style.display = "block"
			divObj.style.visibility = "visible"
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
	frameBorder="0" >xxx</iframe>

</ssf:ifnotadapter>

<c:if test="${showAdministrationPage}">
	<table border="0" width="100%">
	<tr>
	  <td>
  <c:if test="${ss_isSiteAdmin}">
	    <ssHelpSpot helpId="portlets/admin/admin_portlet_site" 
	      title="<ssf:nlt tag="helpSpot.adminPortletSite"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt>" 
	      offsetY="5" offsetX="-13">
	    </ssHelpSpot>
  </c:if>
  <c:if test="${!ss_isSiteAdmin}">
	    <ssHelpSpot helpId="portlets/admin/admin_portlet" 
	      title="<ssf:nlt tag="helpSpot.adminPortlet"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt>" 
	      offsetY="5" offsetX="-13">
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
						<ssf:ifnotaccessible>
						  <ssf:tree treeName="${adminTreeName}" 
						    treeDocument="${ssAdminDomTree}" 
						    rootOpen="true" />
						</ssf:ifnotaccessible>
						<ssf:ifaccessible>
						<ssf:tree treeName="${adminTreeName}" 
						  treeDocument="${ssAdminDomTree}" 
						  flat="true"
						  rootOpen="true" />
						</ssf:ifaccessible>
					</div>
				</td>
			</tr>
			</table>
		</td>
		<td align="right" width="30" valign="top">
		<a href="javascript:;" onClick="ss_helpSystem.run();return false;"><img border="0" 
  		  src="<html:imagesPath/>icons/help.png" 
  		  alt="<ssf:nlt tag="navigation.help" text="Help"/>" /></a>
		</td>
	</tr>
	</table>
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
