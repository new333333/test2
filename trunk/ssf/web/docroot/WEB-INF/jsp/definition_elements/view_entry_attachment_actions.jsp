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
<jsp:useBean id="ss_attachedFile" type="org.kablink.teaming.domain.FileAttachment" scope="request"/>
<c:set var="ss_divCounter" value="${ss_divCounter + 1}" scope="request" />
<c:set var="ss_quotaMessage" value="" />
<c:if test="${ss_diskQuotaHighWaterMarkExceeded && !ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.nearLimit"><ssf:param name="value" useBody="true"
	    ><fmt:formatNumber value="${(ss_diskQuotaUserMaximum - ssUser.diskSpaceUsed)/1048576}" 
	    maxFractionDigits="2"/></ssf:param></ssf:nlt></c:set>
</c:if>
<c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.diskQuotaExceeded"/></c:set>
</c:if>

<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<%
	boolean isIECheck = BrowserSniffer.is_ie(request);
	String strBrowserType = "nonie";
	if (isIECheck) strBrowserType = "ie";
	boolean isAppletSupportedCheck = SsfsUtil.supportApplets(request);
	String operatingSystem = BrowserSniffer.getOSInfo(request);

	String fn = ss_attachedFile.getFileItem().getName();
	String ext = "";
	if (fn.lastIndexOf(".") >= 0) ext = fn.substring(fn.lastIndexOf("."));
	boolean editInPlaceSupported = false;
	String fnBr = "";
	int cCount = 0;
	for (int i = 0; i < fn.length(); i++) {
		String c = String.valueOf(fn.charAt(i));
		cCount++;
		if (c.matches("[\\W_]?") || cCount > 15) {
			fnBr += c + "<wbr/>";
			cCount = 0;
		} else {
			fnBr += c;
		}
	}
%>
  <ssf:ifSupportsEditInPlace relativeFilePath="${ss_attachedFile.fileItem.name}" browserType="<%=strBrowserType%>">
<%  editInPlaceSupported = true;  %>
  </ssf:ifSupportsEditInPlace>

  <a href="javascript: ;" 
    onClick="ss_showHide('ss_fileActionsMenu${ss_divCounter}_${ss_attachedFile.id}');return false;"
  ><ssf:nlt tag="file.actions"/>
  <img style="vertical-align: bottom;" src="<html:rootPath/>images/pics/menu_arrow.png"/></a>
  <div id="ss_fileActionsMenu${ss_divCounter}_${ss_attachedFile.id}" 
    style="position:absolute; display:none; background:#fff; border:1px #ccc solid;">
    <ul style="margin:0px;padding:0px 10px 0px 10px;">
	  <li>
		<%
			if (!isIECheck || !ext.equals(".ppt") || !editInPlaceSupported) {
		%>
			<a style="text-decoration: none;" href="<ssf:fileUrl file="${ss_attachedFile}"/>" 
			  onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${ss_attachedFile.fileItem.name}"/>');"
			><span><ssf:nlt tag="file.view"/></span></a>

		<%  }
			if (isIECheck && ext.equals(".ppt") && editInPlaceSupported) {
		%>
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="applet">
				<ssf:isFileEditorConfiguredForOS relativeFilePath="${ss_attachedFile.fileItem.name}" 
				  operatingSystem="<%= operatingSystem %>">
					<a style="text-decoration: none;" href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${ss_attachedFile}"/>" 
						onClick="javascript:ss_openWebDAVFile('${ssDefinitionEntry.parentBinder.id}', 
						    '${ssDefinitionEntry.id}', 
						    '${ss_attachments_namespace}', 
						    '<%= operatingSystem %>', 
							'${ss_attachedFile.id}');
							return false;"
					><span><ssf:nlt tag="file.view"/></span></a>
				</ssf:isFileEditorConfiguredForOS>
			</ssf:editorTypeToUseForEditInPlace>
			
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
				<a href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${ss_attachedFile}"/>"
				><span><ssf:nlt tag="file.view"/></span></a>
			</ssf:editorTypeToUseForEditInPlace>
		<%  }  %>
	  </li>

	  <ssf:ifSupportsViewAsHtml relativeFilePath="${ss_attachedFile.fileItem.name}" browserType="<%=strBrowserType%>">
		<li>
		  <a target="_blank" style="text-decoration: none;" href="<ssf:url 
		    webPath="viewFile"
		    folderId="${ssDefinitionEntry.parentBinder.id}"
	   	 	entryId="${ssDefinitionEntry.id}"
		    entityType="${ssDefinitionEntry.entityType}" >
	    	<ssf:param name="fileId" value="${ss_attachedFile.id}"/>
	    	<ssf:param name="fileTime" value="${ss_attachedFile.modification.date.time}"/>
	    	<ssf:param name="viewType" value="html"/>
	    	</ssf:url>" title="<ssf:nlt tag="title.open.file.in.html.format" />" 
	       ><span><ssf:nlt tag="file.viewAsHtml" /></span></a>
	    </li>
	  </ssf:ifSupportsViewAsHtml>

		<c:if test="${!ss_attachedFileIsVersion}">
		<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
			<ssf:ifSupportsEditInPlace relativeFilePath="${ss_attachedFile.fileItem.name}" browserType="<%=strBrowserType%>">
				<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="applet">
					<ssf:isFileEditorConfiguredForOS relativeFilePath="${ss_attachedFile.fileItem.name}" operatingSystem="<%= operatingSystem %>">
						<c:if test="${!ss_diskQuotaExceeded || ss_isBinderMirroredFolder}">
						  <li>
						    <a href="javascript: ;" 
							  onClick='javascript:<c:if test="${!empty ss_quotaMessage}">alert("${ss_quotaMessage}");</c:if>
							    ss_openWebDAVFile("${ssDefinitionEntry.parentBinder.id}", "${ssDefinitionEntry.id}", "${ss_attachments_namespace}", "<%= operatingSystem %>", 
								"${ss_attachedFile.id}");
								return false;'
						    ><span><ssf:nlt tag="file.editFile"/></span></a>
						  </li>
						</c:if>
						<c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
						  <li>
						  <a href="javascript: ;" 
							onClick='alert("${ss_quotaMessage}");return false;'
						  ><span><ssf:nlt tag="file.editFile"/></span></a>
						  </li>
						</c:if>
					</ssf:isFileEditorConfiguredForOS>
				</ssf:editorTypeToUseForEditInPlace>
					
				<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
					  <c:if test="${!ss_diskQuotaExceeded || ss_isBinderMirroredFolder}">
					    <li>
					      <a href="<ssf:ssfsInternalAttachmentUrl 
							binder="${ssDefinitionEntry.parentBinder}"
							entity="${ssDefinitionEntry}"
							fileAttachment="${ss_attachedFile}"/>"
						  <c:if test="${!empty ss_quotaMessage}">onClick='alert("${ss_quotaMessage}");'</c:if>
						  ><span><ssf:nlt tag="file.editFile"/></span></a>
						</li>
					  </c:if>
					  <c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
					    <li>
						  <a href="javascript: ;" 
							onClick='alert("${ss_quotaMessage}");return false;'
						  ><span><ssf:nlt tag="file.editFile"/></span></a>
						</li>
					  </c:if>
				</ssf:editorTypeToUseForEditInPlace>
			
			</ssf:ifSupportsEditInPlace>
		  </c:if>
		  </c:if>
		
		<li>
		  <a target="_blank" style="text-decoration: none;" 
		    href="<ssf:fileUrl zipUrl="true" entity="${ssDefinitionEntry}" fileId="${ss_attachedFile.id}" />" 
	       ><span><ssf:nlt tag="file.downloadAsZip" /></span></a>
		</li>
		
		<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
		  <li>
		    <a href="<ssf:url
			    adapter="true" 
			    portletName="ss_forum" 
			    action="modify_file" 
			    actionUrl="false" 
			    ><ssf:param name="entityId" value="${ssDefinitionEntry.id}"/><ssf:param 
			    name="entityType" value="${ssDefinitionEntry.entityType}"/><ssf:param 
			    name="fileId" value="${ss_attachedFile.id}"/><ssf:param 
			    name="operation" value="modify_file_description"/></ssf:url>"
		      onClick="ss_openUrlInPortlet(this.href, true, '500', '400');return false;"
			><span><ssf:nlt tag="file.addComment"/></span></a>
		  </li>
		</c:if>

		<c:if test="${!ss_attachedFileIsVersion}">
		<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
		  <li>
		    <a href="<ssf:url
			    adapter="true" 
			    portletName="ss_forum" 
			    action="modify_file" 
			    actionUrl="false" 
			    ><ssf:param name="entityId" value="${ssDefinitionEntry.id}"/><ssf:param 
			    name="entityType" value="${ssDefinitionEntry.entityType}"/><ssf:param 
			    name="fileId" value="${ss_attachedFile.id}"/><ssf:param 
			    name="operation" value="modify_file_major_version"/></ssf:url>"
		      onClick="ss_openUrlInPortlet(this.href, true, '500', '400');return false;"
			><span><ssf:nlt tag="file.command.incrementMajorVersion"/></span></a>
		  </li>
		</c:if>
		</c:if>
		
		<c:if test="${ss_attachedFileIsVersion}">
		<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
		  <li>
		    <a href="<ssf:url
			    adapter="true" 
			    portletName="ss_forum" 
			    action="modify_file" 
			    actionUrl="false" 
			    ><ssf:param name="entityId" value="${ssDefinitionEntry.id}"/><ssf:param 
			    name="entityType" value="${ssDefinitionEntry.entityType}"/><ssf:param 
			    name="fileId" value="${ss_attachedFile.id}"/><ssf:param 
			    name="operation" value="modify_file_revert"/></ssf:url>"
		      onClick="ss_openUrlInPortlet(this.href, true, '500', '400');return false;"
			><span><ssf:nlt tag="file.command.revertVersion"/></span></a>
		  </li>
		</c:if>
		</c:if>

		<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['deleteEntry']}">
		  <li>
		    <a href="<ssf:url
			    adapter="true" 
			    portletName="ss_forum" 
			    action="modify_file" 
			    actionUrl="false" 
			    ><ssf:param name="entityId" value="${ssDefinitionEntry.id}"/><ssf:param 
			    name="entityType" value="${ssDefinitionEntry.entityType}"/><ssf:param 
			    name="fileId" value="${ss_attachedFile.id}"/><ssf:param 
			    name="operation" value="delete"/></ssf:url>"
		      onClick="ss_openUrlInPortlet(this.href, true, '500', '400');return false;"
			><span><ssf:nlt tag="file.command.deleteVersion"/></span></a>
		  </li>
		</c:if>

	</ul>
  </div>
