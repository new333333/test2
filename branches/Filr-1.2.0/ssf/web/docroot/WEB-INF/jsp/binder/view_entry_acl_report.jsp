<%
// View a permalink
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
<c:set var="tag" value="administration.report.title.entryAclReport"/>
<jsp:useBean id="tag" type="String" />
<c:set var="ss_windowTitle" value='<%= NLT.get(tag) %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<body class="ss_style_body tundra">
<div class="ss_style ss_portlet">
<ssf:form titleTag="${tag}">

<br/>

<fieldset><legend><ssf:nlt tag="general.summary"/></legend>

<div style="padding:4px;">
<ssf:nlt tag="access.report.totalEntries"><ssf:param name="value" value="${report.totalEntries}"/></ssf:nlt>
</div>
<div style="padding:4px;">
<ssf:nlt tag="access.report.totalEntriesWithAcl"><ssf:param name="value" value="${report.totalEntriesWithAcl}"/></ssf:nlt>
</div>
<div style="padding:4px;">
<ssf:nlt tag="access.report.totalHiddenEntries"><ssf:param name="value" value="${report.totalHiddenEntries}"/></ssf:nlt>
</div>
</fieldset>

<br/>

<fieldset><legend><ssf:nlt tag="access.report.hiddenEntries"/></legend>
<table cellpadding="4" cellspacing="4">
<th><ssf:nlt tag="folder.column.CreationDate"/></th>
<th style="padding-left:20px;"><ssf:nlt tag="folder.column.Author"/></th>
<c:forEach var="entry" items="${report.hiddenEntries}">
<tr>
  <td valign="top">
    <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
      value="${entry.creationDate}" type="both" 
	  timeStyle="short" dateStyle="medium" />
  </td>
  <td valign="top" style="padding-left:20px;">
    <ssf:showUser user="${entry.creator}"/>
  </td>
</tr>
</c:forEach>
</table>
</fieldset>

<br/>

<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
  onClick="ss_cancelButtonCloseWindow();return false;">
</form>
</ssf:form>
</div>

</body>
</html>
