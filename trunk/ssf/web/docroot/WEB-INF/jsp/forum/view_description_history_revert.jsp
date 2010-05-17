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
<%@ page import="org.dom4j.Element" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>

<div class="ss_style ss_portlet" style="padding:10px;">
<ssf:form title='<%= NLT.get("entry.revert") %>'>
<div style="padding:10px 6px;">
<span><ssf:nlt tag="entry.revert.warning"/>
</span>
</div>
<form class="ss_style ss_form" method="post" action="<ssf:url     
		adapter="true" 
		portletName="ss_forum" 
		action="view_editable_history" 
		actionUrl="true">
		<ssf:param name="entityId" value="${ss_entityId}" />
		<ssf:param name="versionId" value="${ss_versionId}" />
		<ssf:param name="operation" value="revert" />
		</ssf:url>"
>
<c:forEach var="change" items="${ss_changeLogList}">
  <table width="100%">
  <tr>
  <td valign="bottom">
    <span style="padding-right:10px;">${change.folderEntry.attributes.logVersion}</span>
    <c:set var="modifyDate"><fmt:formatDate timeZone="${ssUser.timeZone.ID}" type="both" value="${change.changeLog.operationDate}"/></c:set>
    <span>${modifyDate}</span>
  </td>
  <td valign="top" align="right">
    <input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" style="margin:0px 20px 0px 0px;"/>
  </td>
  </tr>
  </table>
  <c:if test="${!empty change.changeLogEntry}">
    <c:set var="changeLogEntry" value="${change.changeLogEntry}"/>
	<jsp:useBean id="changeLogEntry" type="org.kablink.teaming.domain.DefinableEntity" />
	<% 
		Element configEle = (Element)changeLogEntry.getEntryDef().getDefinition().getRootElement().selectSingleNode("//item[@name='entryView']");
	%>
	<c:set var="configEle" value="<%= configEle %>" />
    <div style="margin:20px; padding:10px; border: 1px black solid;">
		<c:if test="${!empty configEle}">
		  <c:set var="ssBinderOriginalFromDescriptionHistory" value="${ssBinder}" />
		  <c:set var="ssBinder" value="${changeLogEntry.parentBinder}" scope="request"/>
		  <c:set var="ss_pseudoEntity" value="true" scope="request"/>
		  <ssf:displayConfiguration 
		    configDefinition="${changeLogEntry.entryDef.definition}" 
		    configElement="<%= configEle %>"
		    configJspStyle="view" 
		    entry="${changeLogEntry}" 
		    processThisItem="true" />
		  <c:set var="ssBinder" value="${ssBinderOriginalFromDescriptionHistory}" scope="request"/>
		</c:if>
    </div>
  </c:if>
</c:forEach>
<div style="margin:10px 0px 0px 0px;">
  <input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" />
  &nbsp;&nbsp;&nbsp;
  <input type="button" value="<ssf:nlt tag="button.cancel"/>" onClick="self.window.close();return false;"/>
</div>
</form>
</ssf:form>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
