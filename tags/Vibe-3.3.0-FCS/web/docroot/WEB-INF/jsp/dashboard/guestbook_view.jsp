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
  //this is used by penlets and portlets
  //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

<c:set var="ss_pageNumber" value="0"/>

<c:if test="${ssConfigJspStyle != 'template'}">
<c:if test="${!empty ssDashboard.beans[componentId].ssFolderList}">
<table class="ss_style" cellspacing="0" cellpadding="0" width="100%">
<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}">
<tr>
  <td>
    <a href="javascript: ;"
		onclick="return ss_gotoPermalink('${folder.parentBinder.id}', '${folder.parentBinder.id}', '${folder.parentBinder.entityIdentifier.entityType}', '${ss_namespace}', 'yes');"
		>${folder.parentBinder.title}</a> // 
    <a href="javascript: ;"
		onclick="return ss_gotoPermalink('${folder.id}', '${folder.id}', 'folder', '${ss_namespace}', 'yes');"
		><span class="ss_bold">${folder.title}</span></a>
  </td>
  <td valign="top" align="right">
	<div style="text-align: right; margin: 5px; ">
		<c:if test="${!empty ssDashboard.beans[componentId].ssBinder}">
			<c:set var="entryDefs" value="${ssDashboard.beans[componentId].ssBinder.entryDefinitions}"/>
			<c:forEach var="entryDefsScan" items="${entryDefs}">
				<c:if test="${entryDefsScan.name == '_guestbookEntry'}">
					<c:set var="entryDef" value="${entryDefsScan}"/>
				</c:if>
			</c:forEach>
			<c:if test="${empty entryDef}">
				<c:set var="entryDef" value="${ssDashboard.beans[componentId].ssBinder.entryDefinitions[0]}"/>
			</c:if>
			<c:if test="${!empty entryDef}">
				<a class="ss_linkButton" href="<ssf:url adapter="true" portletName="ss_forum" 
						    action="add_folder_entry"
						    binderId="${ssDashboard.beans[componentId].ssBinder.id}">
						    <ssf:param name="entryType" value="${entryDef.id}" />
				    	    <ssf:param name="newTab" value="1"/>
				    	    <ssf:param name="addEntryFromIFrame" value="1"/>
				    	    <ssf:param name="namespace" value="${ss_namespace}"/>    	        	    
							</ssf:url>" onclick="ss_signGuestbook('${ss_namespace}', this);return false;">
				<span class="ss_bold"><ssf:nlt tag="guestbook.addEntry"/></span>
				</a>
			</c:if>
		</c:if>
	</div>
  </td>
</tr>
</c:forEach>
</table>
</c:if>
<div id="${ss_namespace}_add_entry_from_iframe" style="display:none; visibility:hidden;">
<iframe id="${ss_namespace}_new_guestbook_entry_iframe"
  name="${ss_namespace}_new_guestbook_entry_iframe"
  src="<html:rootPath/>js/forum/null.html" 
  onLoad="ss_showSignGuestbookIframe('${ss_namespace}', this);" 
  width="100%" frameBorder="0"
  title="<ssf:nlt tag="guestbook.addEntry"/>">Novell Vibe</iframe>
</div>

<div id="${ss_divId}">
<%@ include file="/WEB-INF/jsp/dashboard/guestbook_view2.jsp" %>
</div>

</c:if>

<c:if test="${ssConfigJspStyle == 'template'}">
<table class="ss_style" cellspacing="0" cellpadding="0">
<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}">
<tr>
  <td>
    ${folder.parentBinder.title} // <span class="ss_bold">${folder.title}</span>
   </td>
</tr>
</c:forEach>
</table>
<br/>
</c:if>
