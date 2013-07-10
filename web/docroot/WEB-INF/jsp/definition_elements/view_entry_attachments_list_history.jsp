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
<c:set var="owningBinder" value="${ssBinder}"/>
<jsp:useBean id="owningBinder" type="org.kablink.teaming.domain.Binder" />

<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<div>

<table class="ss_attachments_list" cellpadding="0" cellspacing="0" width="100%">
<tbody>
<c:set var="selectionCount" value="0"/>
<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
  <jsp:useBean id="selection" type="org.kablink.teaming.domain.FileAttachment" />
<%
	String fn = selection.getFileItem().getName();
	String ext = "";
	if (fn.lastIndexOf(".") >= 0) ext = fn.substring(fn.lastIndexOf("."));
	boolean editInPlaceSupported = false;
%>
  <c:set var="selectionCount" value="${selectionCount + 1}"/>
  <c:set var="versionCount" value="0"/>
  <c:forEach var="fileVersion" items="${selection.fileVersionsUnsorted}">
    <c:set var="versionCount" value="${versionCount + 1}"/>
  </c:forEach>
  <c:set var="thumbRowSpan" value="2"/>
  <c:if test="${versionCount >= 1}">
    <c:set var="thumbRowSpan" value="2"/>
  </c:if>
     <tr><td valign="top" colspan="9"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
	  <tr>
	    <td valign="top" class="ss_att_meta" nowrap width="5%">
	      <c:if test="${selection.fileExists && !empty ss_pseudoEntityRevert && !ss_isBinderMirroredFolder}">
	        <input type="checkbox" name="file_revert_${selection.id}" onChange="saveFileId(this);" checked />
	      </c:if>
	    </td>
		<td valign="top" width="80" rowspan="${thumbRowSpan}">
		<div class="ss_thumbnail_gallery ss_thumbnail_tiny"> 
          <c:set var="ss_attachedFile" value="${selection}" scope="request" />
          <c:if test="${selection.fileExists && !ss_isBinderMirroredFolder}">
            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_thumbnail.jsp" />
          </c:if>
          <c:if test="${selection.fileExists && ss_isBinderMirroredFolder}">
            <img border="0" <ssf:alt text="${selection.fileItem.name}"/> 
			       src="<ssf:fileUrl webPath="readThumbnail" file="${selection}"/>"/>
          </c:if>
          <c:if test="${!selection.fileExists}">
            <span class="ss_fineprint"><ssf:nlt tag="milestone.folder.deleted"/></span>
          </c:if>
	    </div>
		</td>
		
		<td valign="top" style="height:20px;" class="ss_att_title" width="25%">
          <c:set var="ss_attachedFile" value="${selection}" scope="request" />
          <c:if test="${selection.fileExists && !ss_isBinderMirroredFolder}">
            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_title.jsp" />
          </c:if>
          <c:if test="${!selection.fileExists || ss_isBinderMirroredFolder}">
            ${selection.fileItem.name}
          </c:if>
		</td>

		<td valign="top" class="ss_att_meta" nowrap width="5%">
		  <ssf:nlt tag="file.versionNumber"><ssf:param name="value" value="${selection.fileVersion}"/></ssf:nlt>
		</td>

		<td valign="top" class="ss_att_meta" nowrap width="5%">
		  <div>
		    <span>${selection.fileStatusText}</span>
		  </div>
		</td>
		
		<td valign="top" width="15%"><span class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${selection.modification.date}" type="date" 
					 dateStyle="medium" /></span> <span class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${selection.modification.date}" type="time" 
					 timeStyle="short"/></span></td>
		<td valign="top" class="ss_att_meta" nowrap width="5%">
		  <fmt:setLocale value="${ssUser.locale}"/>
		  <fmt:formatNumber value="${selection.fileItem.lengthKB}"/> 
		  <ssf:nlt tag="file.sizeKB" text="KB"/>
		</td>
		
		<td valign="top" class="ss_att_meta_wrap ss_att_space" width="25%"
		><ssf:userTitle user="${selection.modification.principal}"/></td>
		<td valign="top" class="ss_att_meta" width="20%">
		</td>
	</tr>
	<tr>
	  <td></td>
	  <td valign="top" colspan="8" class="ss_att_description" width="100%">
	    <div><ssf:markup type="view" entity="${ssDefinitionEntry}">${selection.fileItem.description.text}</ssf:markup></div>
	  </td>
	</tr>	
</c:forEach>
<c:if test="${selectionCount > 0}">
     <tr><td valign="top" colspan="9"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
</c:if>
</tbody>
</table>

</div>
