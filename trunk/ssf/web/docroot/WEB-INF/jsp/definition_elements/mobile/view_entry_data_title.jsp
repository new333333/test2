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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUserFolderProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssBinder" type="org.kablink.teaming.domain.Binder" scope="request" />
<%
	Map ssFolderColumns = (Map) ssUserFolderProperties.get("userFolderColumns");
	if (ssFolderColumns == null) ssFolderColumns = (Map)ssBinder.getProperty("folderColumns");
	if (ssFolderColumns == null) {
		ssFolderColumns = new java.util.HashMap();
	}
%>
<c:set var="ssFolderColumns" value="<%= ssFolderColumns %>" scope="request"/>
<c:set var="title_entry" value="${ssDefinitionEntry}"/>
<jsp:useBean id="title_entry" type="org.kablink.teaming.domain.FolderEntry" />
<% //Title view %>
  <c:if test="${empty ss_title_breadcrumbs_seen && 
                    ssDefinitionEntry.entityType == 'folderEntry' && 
                    !empty ssDefinitionEntry.parentEntry}">
    <div style="padding-bottom:10px;">
	    <c:set var="parentEntry" value="${ssDefinitionEntry.parentEntry}"/>
	    <jsp:useBean id="parentEntry" type="java.lang.Object" />
	    <%
		    Stack parentEntryTree = new Stack();
		    while (parentEntry != null) {
				parentEntryTree.push(parentEntry);
				parentEntry = ((FolderEntry)parentEntry).getParentEntry();
			}
			while (!parentEntryTree.empty()) {
				FolderEntry nextEntry = (FolderEntry) parentEntryTree.pop();
				%>
				<c:set var="nextEntry" value="<%= nextEntry %>"/>
				<div style="padding-left: 15px">
				  <span class="ss_mobile_small ss_mobile_light">
					<a
			  			href="<ssf:url adapter="true" portletName="ss_forum" 
								folderId="${ssDefinitionEntry.parentBinder.id}" 
								entryId="${nextEntry.id}"
								action="__ajax_mobile" 
								operation="mobile_show_entry" 
								actionUrl="false" />"
					>
					<c:if test="${!empty nextEntry.docNumber && !empty ssFolderColumns['number']}">
						${nextEntry.docNumber}.
					</c:if>
					<c:if test="${empty nextEntry.title}" >
						--<ssf:nlt tag="entry.noTitle" />--
					</c:if>
					<c:out value="${nextEntry.title}" escapeXml="true"/><img border="0" <ssf:alt/>
			  		  style="width:1px;height:14px;" src="<html:imagesPath/>pics/1pix.gif"/></a>
				  </span>
				  <br/>
				</div>
		<%
			}
		%>
	</div>
  </c:if>
  <c:set var="ss_title_breadcrumbs_seen" value="1" scope="request"/>

<div class="entry-title">
<c:if test="${!ssSeenEntries[title_entry.id]}">
  <img border="0" align="absbottom" <ssf:alt tag="alt.unseen"/> src="<html:imagesPath/>pics/sym_s_unseen.png">
</c:if>
  <c:if test="${!empty ssDefinitionEntry.docNumber && !empty ssFolderColumns['number']}">
	<div class="doc-number">${ssDefinitionEntry.docNumber}.</div>
  </c:if>
  <a href="<ssf:url adapter="true" portletName="ss_forum" 
					folderId="${ssBinder.id}" 
					entryId="${ssDefinitionEntry.id}"
					action="__ajax_mobile" 
					operation="mobile_show_entry" 
					actionUrl="false" />"
  ><span><c:if test="${empty ssDefinitionEntry.title}" >
  (<ssf:nlt tag="entry.noTitle" />)</c:if>
  <c:out value="${ssDefinitionEntry.title}" escapeXml="true" /></span></a>

</div>
<c:if test="${ss_showSignatureAfterTitle && !ss_signatureShown}">
  <%@ include file="/WEB-INF/jsp/definition_elements/mobile/view_entry_signature2.jsp" %>
  <c:set var="ss_signatureShown" value="true" scope="request"/>
</c:if>
