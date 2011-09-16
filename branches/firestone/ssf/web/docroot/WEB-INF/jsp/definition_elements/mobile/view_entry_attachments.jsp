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
<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%
boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "nonie";
if (isIECheck) strBrowserType = "ie";
%>
<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
  <div class="entry-content">
	<c:if test="${!empty property_caption}">
	  <div class="entry-caption">${property_caption}</div>
	</c:if>
	
	<div class="entry-element">
	<table cellpadding="0" cellspacing="0">
	  <tbody>
		<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
			  <tr>
				<td class="ss_att_title">
				  <a style="text-decoration: none;" 
					href="<ssf:fileUrl file="${selection}"/>" 
				    <ssf:title tag="title.open.file">
					    <ssf:param name="value" value="${selection.fileItem.name}" />
				    </ssf:title>
					><c:out value="${selection.fileItem.name} "/></a>
					<c:if test="${selection.encrypted}">
				        <img src="<html:imagesPath/>pics/encrypted.png" 
				          title="<%= NLT.get("file.encrypted").replaceAll("\"", "&QUOT;") %>" />
					</c:if>
					<c:if test="${selection.currentlyLocked}">
					  <br/>
					  <img <ssf:alt tag="alt.locked"/> src="<html:imagesPath/>pics/sym_s_caution.gif"/>
					  <span class="ss_mobile_small"><ssf:nlt tag="entry.lockedBy">
			    		<ssf:param name="value" useBody="true"><ssf:userTitle user="${selection.fileLock.owner}"/></ssf:param>
					  </ssf:nlt></span>
					</c:if>
				  <ssf:ifSupportsViewAsHtml relativeFilePath="${selection.fileItem.name}" 
				    browserType="<%=strBrowserType%>">
						<div style="display: inline" class="entry-type">
						   <a style="text-decoration: none;" href="<ssf:url 
						    webPath="viewFile"
						    folderId="${ssDefinitionEntry.parentBinder.id}"
					   	 	entryId="${ssDefinitionEntry.id}"
						    entityType="${ssDefinitionEntry.entityType}" >
					    	<ssf:param name="fileId" value="${selection.id}"/>
					    	<ssf:param name="fileTime" value="${selection.modification.date.time}"/>
					    	<ssf:param name="viewType" value="html"/>
					    	</ssf:url>"><span style="padding: 2px 10px"><ssf:nlt tag="entry.HTML" /></span></a>
						</div>	
				  </ssf:ifSupportsViewAsHtml>
				</td>
			</tr>
		</c:forEach>
	  </tbody>
	</table>
	</div>
  </div>
</c:if>

