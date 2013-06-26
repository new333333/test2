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

<c:set var="selection" value="${ss_attachedFileSelection}" />
<jsp:useBean id="selection" type="org.kablink.teaming.domain.FileAttachment" />
<%
	String fn = selection.getFileItem().getName();
	String ext = "";
	if (fn.lastIndexOf(".") >= 0) ext = fn.substring(fn.lastIndexOf("."));
	boolean editInPlaceSupported = false;
	String fnBr = "";
	int cCount = 0;
	for (int i = 0; i < fn.length(); i++) {
		String c = String.valueOf(fn.charAt(i));
		cCount++;
		if (c.matches("[\\W_]?") || cCount > 15) {
			fnBr += c;
			if (!BrowserSniffer.is_ie(request)) {
				fnBr += "<wbr/>";
			}
			cCount = 0;
		} else {
			fnBr += c;
		}
	}
%>
  <ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
<%  editInPlaceSupported = true;  %>
  </ssf:ifSupportsEditInPlace>

  <c:set var="selectionCount" value="${selectionCount + 1}"/>
  <c:set var="versionCount" value="0"/>
  <c:forEach var="fileVersion" items="${selection.fileVersionsUnsorted}">
    <c:set var="versionCount" value="${versionCount + 1}"/>
  </c:forEach>
	  <tr class="${ss_attachedFileRowClass}">		
		<td colspan="2" width="25%" class="ss_att_title"
		  <% if (selection.getFileItem().getName().length() <= 40) { %> style="white-space: nowrap;" <% } %>
		  <% if (selection.getFileItem().getName().length() > 40) { %> style="white-space: normal;" <% } %>
		>
		  <c:if test="${ss_isBinderMirroredFolder || !selection.fileExists}">
		    ${selection.fileItem.name}
		  </c:if>
		  <c:if test="${!ss_isBinderMirroredFolder && selection.fileExists}">
		  <%
			  if (!isIECheck || !ext.equals(".ppt") || !editInPlaceSupported) {
		  %>
		    <a style="text-decoration: none;" 
						href="<ssf:fileUrl file="${selection}"/>" 
					    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${selection.fileItem.name}"/>');"

				    <ssf:title tag="title.open.file">
					    <ssf:param name="value" value="${selection.fileItem.name}" />
				    </ssf:title>
					><%= fnBr %></a>
					<c:if test="${selection.encrypted}">
				        <img src="<html:imagesPath/>pics/encrypted.png" 
				          title="<%= NLT.get("file.encrypted").replaceAll("\"", "&QUOT;") %>" />
					</c:if>
		  <%  }
			if (isIECheck && ext.equals(".ppt") && editInPlaceSupported) {
		  %>
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="applet">
				<ssf:isFileEditorConfiguredForOS relativeFilePath="${selection.fileItem.name}" operatingSystem="<%= operatingSystem %>">
					<a style="text-decoration: none;" href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${selection}"/>" 
						onClick="javascript:ss_openWebDAVFile('${ssDefinitionEntry.parentBinder.id}', 
						    '${ssDefinitionEntry.id}', 
						    '${ss_attachments_namespace}', 
						    '<%= operatingSystem %>', 
							'${selection.id}');
							return false;"
				    	<ssf:title tag="title.open.file">
					      <ssf:param name="value" value="${selection.fileItem.name}" />
				    	</ssf:title>
					><%= fnBr %></a>
					<c:if test="${selection.encrypted}">
				        <img src="<html:imagesPath/>pics/encrypted.png" 
				          title="<%= NLT.get("file.encrypted").replaceAll("\"", "&QUOT;") %>" />
					</c:if>
				</ssf:isFileEditorConfiguredForOS>
			</ssf:editorTypeToUseForEditInPlace>
			
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
				<a href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${selection}"/>"
				><%= fnBr %></a>
					<c:if test="${selection.encrypted}">
				        <img src="<html:imagesPath/>pics/encrypted.png" 
				          title="<%= NLT.get("file.encrypted").replaceAll("\"", "&QUOT;") %>" />
					</c:if>
			</ssf:editorTypeToUseForEditInPlace>
		  <%  }  %>

			<c:if test="${selection.currentlyLocked}">
			  <br/>
			  <img <ssf:alt tag="alt.locked"/> align="absmiddle" src="<html:imagesPath/>pics/sym_s_caution.gif"/>
			  <span class="ss_fineprint"><ssf:nlt tag="entry.lockedBy">
	    		<ssf:param name="value" useBody="true"><ssf:userTitle user="${selection.fileLock.owner}"/></ssf:param>
			  </ssf:nlt></span>
			  <c:if test="${ss_canForceFileUnlock}">
			    <div>
			      <a href="<ssf:url     
				    adapter="true" 
				    portletName="ss_forum" 
				    binderId="${ssDefinitionEntry.parentBinder.id}" 
				    action="view_folder_entry" 
				    entryId="${ssDefinitionEntry.id}" actionUrl="true">
				    <ssf:param name="operation" value="force_unlock_file"/>
				    <ssf:param name="fileId" value="${selection.id}"/></ssf:url>"
				    onclick='if(confirm("<ssf:escapeJavaScript><ssf:nlt tag="entry.forceUnlockFileConfirm"/></ssf:escapeJavaScript>")){ss_postToThisUrl(this.href);return false;}else{return false};'
				    style="padding-left:5px;"
			        >
			        <span class="ss_fineprint"><ssf:nlt tag="entry.forceUnlockFile"/></span>
			      </a>
			    </div>
			  </c:if>
			</c:if>
		  </c:if>
		</td>
		
		<td class="ss_att_meta">
		  <span><ssf:nlt tag="file.versionNumber"><ssf:param
			name="value" value="${selection.fileVersion}"/></ssf:nlt></span>
		</td>

		<td class="ss_att_meta">
          <c:set var="ss_attachedFileIsVersion" value="false" scope="request" />
          <c:set var="ss_attachedFile" value="${selection}" scope="request" />
          <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_status.jsp" />
		</td>
		
		<td><span class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
		     value="${selection.modification.date}" type="date" 
			 dateStyle="medium" /></span> <span class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
		     value="${selection.modification.date}" type="time" 
			 timeStyle="short"/></span>
		</td>
		<td class="ss_att_meta">
		  <fmt:setLocale value="${ssUser.locale}"/>
		  <fmt:formatNumber value="${selection.fileItem.lengthKB}"/> 
		  <ssf:nlt tag="file.sizeKB" text="KB"/>
		</td>
		<td class="ss_att_meta_wrap ss_att_space">
			<ssf:userTitle user="${selection.modification.principal}"/>
		</td>
		<td class="ss_att_meta">
          <c:set var="ss_attachedFileIsVersion" value="false" scope="request" />
          <c:set var="ss_attachedFile" value="${selection}" scope="request" />
          <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_actions.jsp" />
		</td>
		<td class="ss_att_meta" >
          <c:set var="ss_attachedFileIsVersion" value="false" scope="request" />
          <c:set var="ss_attachedFile" value="${selection}" scope="request" />
          <c:set var="ss_attachedFileShowEditButton" value="true" scope="request"/>
          <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_actions_edit_button.jsp" />
          <c:set var="ss_attachedFileShowEditButton" value="false" scope="request"/>
		</td>
		<td style="border: 0px;">&nbsp;</td>
	</tr>
	<tr class="${ss_attachedFileRowClass}">
	  	<td colspan="10" class="ss_att_description" style="padding-left: 38px; white-space: normal">
	    	<ssf:markup type="view" entity="${ssDefinitionEntry}">${selection.fileItem.description.text}</ssf:markup>
	  	</td>
	</tr>
	
	<c:if test="${!empty selection.fileVersions && versionCount > 1 && !ss_isBinderMirroredFolder}">
		  <c:forEach var="fileVersion" items="${selection.fileVersions}" begin="1" varStatus="status">
			<c:if test="${!empty fileVersion.fileItem.name}">
			<jsp:useBean id="fileVersion" type="org.kablink.teaming.domain.FileAttachment" />
<%
	String vfn = fileVersion.getFileItem().getName();
	String vext = "";
	if (vfn.lastIndexOf(".") >= 0) vext = vfn.substring(vfn.lastIndexOf("."));
	String vfnBr = "";
	int vcCount = 0;
	if (vfn.length() > 40) {
		for (int i = 0; i < vfn.length(); i++) {
			String c = String.valueOf(vfn.charAt(i));
			vcCount++;
			if (c.matches("[\\W_]?") || vcCount > 15) {
				vfnBr += c;
				if (!BrowserSniffer.is_ie(request)) {
					vfnBr += "<wbr/>";
				}
				vcCount = 0;
			} else {
				vfnBr += c;
			}
		}
	} else {
		vfnBr = vfn;
	}
%>
			<tr class="${ss_attachedFileRowClass}">
			    <td style="padding-left: 8px; padding-right: 3px">
			     <c:if test="${!ss_pseudoEntity}">
			     <c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['deleteEntry']}">
			      <input type="checkbox" name="delete_version_${fileVersion.id}"/>
			     </c:if>
			     </c:if>
			    </td>
				<td width="25%" class="ss_att_title" style="font-weight: normal; padding-left: 0px;
		  			<% if (fileVersion.getFileItem().getName().length() <= 40) { %> white-space: nowrap; <% } %>
		  			<% if (fileVersion.getFileItem().getName().length() > 40) { %> white-space: normal; <% } %>
				  ">
				<c:if test="<%= !owningBinder.isMirrored() %>">
					<a style="text-decoration: none;"
					  href="<ssf:fileUrl file="${fileVersion}"/>" 
						    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${fileVersion.fileItem.name}"/>');"
						
					    <ssf:title tag="title.open.file">
						    <ssf:param name="value" value="${fileVersion.fileItem.name}" />
					    </ssf:title>
						><%= vfnBr %></a>
					<c:if test="${fileVersion.encrypted}">
				        <img src="<html:imagesPath/>pics/encrypted.png" 
				          title="<%= NLT.get("file.encrypted").replaceAll("\"", "&QUOT;") %>" />
					</c:if>
					<c:if test="${fileVersion.agingEnabled}">
					  <c:if test="${ssBinder.versionAgingEnabled && (!empty ssBinder.versionAgingDays || !empty ss_zoneVersionAgingDays)}">
					    <sup>*</sup>
					    <c:set var="ss_fileVersionAgingEnabledSeen" value="true" scope="request"/>
					  </c:if>
					</c:if>
				</c:if>
				</td>
				
				<td class="ss_att_meta">
				  <span><ssf:nlt tag="file.versionNumber"><ssf:param
					name="value" value="${fileVersion.fileVersion}"/></ssf:nlt></span>
				</td>

				<td class="ss_att_meta">
          		  <c:set var="ss_attachedFileIsVersion" value="true" scope="request" />
          		  <c:set var="ss_attachedFile" value="${fileVersion}" scope="request" />
          		  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_status.jsp" />
				</td>
		
				<td>
				  <span class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${fileVersion.modification.date}" type="date" 
					 dateStyle="medium" /></span> <span class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${fileVersion.modification.date}" type="time" 
					 timeStyle="short" /></span>
				</td>
				<td class="ss_att_meta" >
					<fmt:setLocale value="${ssUser.locale}"/><fmt:formatNumber value="${fileVersion.fileItem.lengthKB}"/> <ssf:nlt tag="file.sizeKB" text="KB"/>
				</td>
				<td class="ss_att_meta_wrap ss_att_space">
					<ssf:userTitle user="${fileVersion.modification.principal}"/>
				</td>
				<td class="ss_att_meta">
          		  <c:set var="ss_attachedFileIsVersion" value="true" scope="request" />
          		  <c:set var="ss_attachedFile" value="${fileVersion}" scope="request" />
          		  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_actions.jsp" />
				</td>
				<td class="ss_att_meta">
				</td>
				<td style="border: 0px;">&nbsp;</td>
			  </tr>	
			  <c:choose>
				<c:when test="${status.count <= 3}">
					<tr class="${ss_attachedFileRowClass}">
				</c:when>	
				<c:otherwise>						
					<tr class="${ss_attachedFileRowClass}" id="${ss_attachments_namespace}att_desc_row${status.count}" style="display: none; ">
				</c:otherwise>
			  </c:choose>
			    <td colspan="10" class="ss_att_description" style="padding-left: 38px; white-space: normal;">
			      <div><ssf:markup type="view" entity="${ssDefinitionEntry}">${fileVersion.fileItem.description.text}</ssf:markup></div>
			    </td>
			  </tr>	
			  </c:if>
 	    	</c:forEach>
	</c:if>
