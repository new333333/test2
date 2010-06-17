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
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.ssfs.util.SsfsUtil" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_quotaMessage" value="" />
<c:if test="${ss_diskQuotaHighWaterMarkExceeded && !ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.nearLimit"><ssf:param name="value" useBody="true"
	    ><fmt:formatNumber value="${(ss_diskQuotaUserMaximum - ssUser.diskSpaceUsed)/1048576}" 
	    maxFractionDigits="2"/></ssf:param></ssf:nlt></c:set>
</c:if>
<c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.diskQuotaExceeded"/></c:set>
</c:if>
<c:set var="owningBinder" value="${ssBinder}"/>
<jsp:useBean id="owningBinder" type="org.kablink.teaming.domain.Binder" />

<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<%
boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "nonie";
if (isIECheck) strBrowserType = "ie";
boolean isAppletSupportedCheck = SsfsUtil.supportApplets(request);
String operatingSystem = BrowserSniffer.getOSInfo(request);
%>

<table class="ss_attachments_list" cellpadding="0" cellspacing="0" width="100%">
<tbody>
<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
<tr>
  <th style="padding-right:4px;"><ssf:nlt tag="file.name"/></th>
  <th align="center" style="padding:0px 4px;"><ssf:nlt tag="entry.Version"/></th>
  <th style="padding:0px 4px;"><ssf:nlt tag="file.status"/></th>
  <th style="padding:0px 4px;"><ssf:nlt tag="file.date"/></th>
  <th align="center" style="padding:0px 4px;"><ssf:nlt tag="file.size"/></th>
  <th style="padding:0px 4px;"><ssf:nlt tag="entry.modifiedBy"/></th>
  <th><ssf:nlt tag="toolbar.actions"/></th>
</tr>
</c:if>

<c:set var="selectionCount" value="0"/>
<c:set var="primaryFileId" value=""/>
<c:if test="${!empty ssPrimaryFileAttribute}">
  <% //There is a primary file, so show it first %>
  <c:set var="selections" value="${ssDefinitionEntry.customAttributes[ssPrimaryFileAttribute].valueSet}" />
  <c:forEach var="selection" items="${selections}" varStatus="status">
    <c:if test="${status.first}">
      <c:set var="primaryFileId" value="${selection.id}"/>
    </c:if>
  </c:forEach>
</c:if>
<c:if test="${!empty primaryFileId}">
  <c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
    <c:if test="${selection.id == primaryFileId}">
      <c:set var="ss_attachedFileSelection" value="${selection}" scope="request"/>
      <c:set var="ss_attachedFileRowClass" value="ss_attachments_list_primary_file" scope="request"/>
      <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_file_versions_item.jsp" />
    </c:if>
  </c:forEach>
</c:if>

<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
  <c:if test="${selection.id != primaryFileId}">
    <c:set var="ss_attachedFileSelection" value="${selection}" scope="request"/>
    <c:set var="ss_attachedFileRowClass" value="" scope="request"/>
    <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_file_versions_item.jsp" />
  </c:if>
</c:forEach>
<c:if test="${selectionCount > 0}">
     <tr><td valign="top" colspan="7"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
</c:if>
</tbody>
</table>
