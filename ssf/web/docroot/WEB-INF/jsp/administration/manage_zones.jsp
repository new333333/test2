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
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.manage.zones") %>' scope="request"/>
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
<ssf:form titleTag="administration.manage.zones">

<script type="text/javascript">

function ${renderResponse.namespace}_onsub(obj) {
	if (obj.name.value == '') {
		alert('<ssf:nlt tag="general.required.name"/>');
		return false;
	}
	return true;
}

var ss_confirmDeleteZoneText = "<ssf:nlt tag="zone.confirmDeleteZone"/>";
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
<ssf:expandableArea title='<%= NLT.get("administration.add.zone") %>'>
<form class="ss_style ss_form" method="post" 
	action="<ssf:url action="manage_zones" actionUrl="true"/>" 
	onSubmit="return(${renderResponse.namespace}_onsub(this))">
		
	<span class="ss_bold"><ssf:nlt tag="administration.zoneName"/></span><br/>
	<input type="text" class="ss_text" size="50" name="zoneName"><br/><br/>
		
	<span class="ss_bold"><ssf:nlt tag="administration.zoneVirtualHost"/></span><br/>
	<input type="text" class="ss_text" size="50" name="virtualHost"><br/><br/>
		
	<input type="submit" class="ss_submit" name="addBtn" onclick="ss_startSpinner();" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>
</ssf:expandableArea>
<br/>
<br/> 

<table>
<tr>
<td valign="top">
<table>
<tr>
<th><span class="ss_bold"><ssf:nlt tag="general.id"/></span></th>
<th><span class="ss_bold"><ssf:nlt tag="administration.zoneName"/></span></th>
<th><span class="ss_bold"><ssf:nlt tag="administration.zoneVirtualHost"/></span></th>
</tr>
  <c:set var="default_zone_name" value="<%= org.kablink.teaming.util.SZoneConfig.getDefaultZoneName() %>"/>
  <c:forEach var="zoneInfo" items="${ss_zoneInfoList}">
  <tr>
	<form class="ss_style ss_form" method="post" 
	action="<ssf:url action="manage_zones" actionUrl="true"/>" 
	onSubmit="return(${renderResponse.namespace}_onsub(this))">
		
    <td align="left" valign="middle">
	<input type="text" class="ss_text" size="20" name="zoneId" value="${zoneInfo.zoneId}" readonly>
	</td>
		
    <td align="left" valign="middle">
	<input type="text" class="ss_text" size="50" name="zoneName" value="${zoneInfo.zoneName}" readonly>
	</td>
		
    <td align="left" valign="middle">
	<input type="text" class="ss_text" size="50" name="virtualHost" <c:if test="${!empty zoneInfo.virtualHost}">value="${zoneInfo.virtualHost}"</c:if> <c:if test="${default_zone_name == zoneInfo.zoneName}">readonly</c:if>>
	</td>
		
	<c:if test="${default_zone_name != zoneInfo.zoneName}">
    <td align="left" valign="middle">
	<input type="submit" class="ss_submit" name="modifyBtn" onclick="ss_startSpinner();" value="<ssf:nlt tag="button.modify" text="Modify"/>">
	</td>	
	
    <td align="left" valign="middle">
	<input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete" text="Delete"/>" 
	  onClick="return ss_confirmDeleteZone();" />
	</td>	
	</c:if>
	</form>
  </tr>
  </c:forEach>
</table>
</td>

</tr>
</table>

<br/>
<br/>
	<form class="ss_portlet_style ss_form" id="${ssNamespace}_btnForm" 
	  name="${ssNamespace}_btnForm" method="post" 
	  action="<ssf:url action="site_administration" actionUrl="false"/>">
		<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="return handleCloseBtn();"/>
	</form>

</div>
</div>

</ssf:form>
</div>
</body>
</html>