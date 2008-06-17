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
<%@ page import="com.sitescape.team.util.NLT" %>
<ssf:ifadapter>
<body class="ss_style_body">
<div id="ss_pseudoAdministrationPortalDiv${renderResponse.namespace}">
</ssf:ifadapter>


<div class="ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="ldap.title"/></span><br/><br/>
<c:if test="${!empty ssException}">
<span class="ss_largerprint"><ssf:nlt tag="administration.errors"/> (<c:out value="${ssException}"/>)</span></br>
</c:if>

<form class="ss_style ss_form" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" method="post" 
  action="<ssf:url action="configure_ldap" actionUrl="true"/>">
<div class="ss_buttonBarRight">
<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</div>
<table class="ss_style" border ="0" cellspacing="0" cellpadding="3">
<tr><td> 
<input type="checkbox" id="enabled" name="enabled" <c:if test="${ssLdapConfig.enabled}">checked</c:if>/>
<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.enable"/></span><br/>
</td></tr>
<tr><td>
<input type="checkbox" id="runnow" name="runnow" <c:if test="${runnow}"> checked="checked" </c:if>/>
<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.now"/></span><br/>
</td></tr></table>

<br/>
<ssf:expandableArea title="<%= NLT.get("ldap.schedule") %>">
<c:set var="schedule" value="${ssLdapConfig.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
<div class="ss_divider"></div>
</ssf:expandableArea>
<ssf:expandableArea title="<%= NLT.get("ldap.connection") %>">
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
<tr>
	   <td colspan="2"><ssf:nlt tag="ldap.user.url.title"/></td>
	   </tr>
	   <tr><td>
	   <span class="ss_labelLeft ss_normal"><ssf:nlt tag="ldap.user.url"/></span>
	   </td><td><input type="text"  size="100" name="userUrl" value="${ssLdapConfig.userUrl}"/>
	   </td>
	   </tr><tr>
	   <td><span class="ss_labelLeft ss_normal"><ssf:nlt tag="ldap.user.principal"/></span>
	   </td><td><input type="text"  name="userPrincipal" value="${ssLdapConfig.userPrincipal}"/>
	   </td>
	   </tr><tr>
	   <td><span class="ss_labelLeft ss_normal"><ssf:nlt tag="ldap.user.credential"/></span>
	   </td><td><input type="password"  name="userCredentials" value="${ssLdapConfig.userCredential}"/>
	   </td>
	   <tr>
	   <td colspan="2"><ssf:nlt tag="ldap.group.basedn.title"/></td>
	   </tr>
	   <tr><td>
	   <span class="ss_labelLeft ss_normal"><ssf:nlt tag="ldap.group.basedn"/></span>
	   </td><td><input type="text"  size="100" name="groupBasedn" value="${ssLdapConfig.groupsBasedn}"/>
	   </td>
	   </tr></tr>
	   </table>
<div class="ss_divider"></div>
</ssf:expandableArea>
<br/>

<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt tag="ldap.users" /></legend>
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
		<tr><td>
		<span class="ss_labelAbove ss_normal"><ssf:nlt tag="ldap.user.idmapping"/></span>
	  	<input type="text" name="userIdMapping" size="50" value="${ssLdapConfig.userIdMapping}"/>
	 	</td></tr>
		<tr><td>
		<span class="ss_labelAbove ss_normal"><ssf:nlt tag="ldap.user.mappings"/></span>
	  	<c:set var="mappings" value="${ssLdapConfig.userMappings}"/>
<jsp:useBean id="mappings" type="java.util.Map" scope="page" />
	  	<%
			StringBuffer buf = new StringBuffer();
	  		for (java.util.Iterator iter=mappings.entrySet().iterator(); iter.hasNext();) {
				java.util.Map.Entry me = (java.util.Map.Entry)iter.next();
				buf.append(me.getValue() + "=" + me.getKey() + "\n");
	  		}
	  	%>
	  	<textarea name="userMappings" style="height: 100px; width: 500px;" wrap="hard"><%=buf.toString()%></textarea>
		</td></tr>
		<tr><td><input type="checkbox" name="userSync" <c:if test="${ssLdapConfig.userSync}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.sync"/></span></input>
	   	</td></tr>
	   	<tr><td><input type="checkbox" name="userRegister" <c:if test="${ssLdapConfig.userRegister}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.register"/></span></input>
	   	</td></tr>
	   	<tr><td><input type="checkbox" name="userDelete" <c:if test="${ssLdapConfig.userDelete}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.delete"/></span></input>
	   	</td></tr>
	   	<tr><td><input type="checkbox" name="userWorkspaceDelete" <c:if test="${ssLdapConfig.userWorkspaceDelete}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.workspace.delete"/></span></input>
	   	</td></tr>
	 	</td></tr>
</table>
</fieldset>

<br/>
<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt tag="ldap.groups" /></legend>
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
	   <tr>
	   <td><input type="checkbox" name="groupRegister" <c:if test="${ssLdapConfig.groupRegister}">checked</c:if>>
	   <span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.group.register"/></span></input></td>
	   </tr><tr>
	   <td><input type="checkbox" name="membershipSync" <c:if test="${ssLdapConfig.membershipSync}">checked</c:if>>
	   <span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.membership.sync"/></span></input></td>
	   </tr><tr>
	   <td><input type="checkbox" name="groupDelete" <c:if test="${ssLdapConfig.groupDelete}">checked</c:if>>
	   <span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.group.delete"/></span></input></td>
	   </tr>
</table>
</fieldset>

<br/>
<div class="ss_buttonBarLeft">
	<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</div>
</form>
</div>

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
