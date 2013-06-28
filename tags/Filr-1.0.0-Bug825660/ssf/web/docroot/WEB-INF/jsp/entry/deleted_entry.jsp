<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>

<% // Displayed when the user tries to list a moved entry %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ page import="org.kablink.teaming.module.definition.DefinitionUtils" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<%
//Get the folder type of this definition (folder, file, or event)
String folderViewStyle1 = "";
if (ssConfigDefinition != null) folderViewStyle1 = DefinitionUtils.getViewType(ssConfigDefinition);
if (folderViewStyle1 == null || folderViewStyle1.equals("")) folderViewStyle1 = "folder";
%>
<c:set var="ss_folderViewStyle1" value="<%= folderViewStyle1 %>" scope="request" />
<ssf:ifadapter>
<body class="ss_style_body tundra">
</ssf:ifadapter>

<div id="ss_portlet_content" class="ss_style ss_portlet">
	<c:set var="ss_sidebarVisibility" value="${ssUserProperties.sidebarVisibility}" scope="request"/>
	<c:if test="${empty ss_sidebarVisibility}"><c:set var="ss_sidebarVisibility" value="block" scope="request"/></c:if>
	<c:if test="${ss_sidebarVisibility == 'none'}">
	  <c:set var="ss_sidebarVisibilityShow" value="block"/>
	  <c:set var="ss_sidebarVisibilityHide" value="none"/>
	  <c:set var="ss_sidebarTdStyle" value=""/>
	</c:if>
	<c:if test="${ss_sidebarVisibility != 'none'}">
	  <c:set var="ss_sidebarVisibilityShow" value="none"/>
	  <c:set var="ss_sidebarVisibilityHide" value="block"/>
	  <c:set var="ss_sidebarTdStyle" value="ss_view_sidebar"/>
	</c:if>
	
	<c:if test="${empty ss_folderViewStyle1 || ss_folderViewStyle1 != 'wiki'}">
	  <div id="ss_showfolder${renderResponse.namespace}" class="ss_style ss_portlet ss_content_outer">
		<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />
		<jsp:include page="/WEB-INF/jsp/definition_elements/popular_view_init.jsp" />
		<jsp:include page="/WEB-INF/jsp/forum/view_workarea_navbar.jsp" />
		<% if (!(GwtUIHelper.isGwtUIActive(request))) { %>
			<div class="ss_actions_bar1_pane ss_sidebarImage" width="100%">
				<table cellspacing="0" cellpadding="0" width="100%">
					<tr><td valign="middle">
					<a href="javascript: ;" 
					  onClick="ss_showHideSidebar('${renderResponse.namespace}');return false;"
					><span style="padding-left:9px; display:${ss_sidebarVisibilityShow};"
					  id="ss_sidebarHide${renderResponse.namespace}" 
					  class="ss_fineprint ss_sidebarSlidesm"><ssf:nlt tag="toolbar.sidebar.show"/></span><span 
					  style="padding-left:9px; display:${ss_sidebarVisibilityHide};"
					  id="ss_sidebarShow${renderResponse.namespace}" 
					  class="ss_fineprint ss_sidebarSlide"><ssf:nlt tag="toolbar.sidebar.hide"/></span></a>
					</td><td valign="top">
					<jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" />
					</td></tr>
				</table>
			</div>
		<% } %>
	    <table cellpadding="0" cellspacing="0" border="0" width="100%">
	    <tbody>
		    <tr>
			  <td valign="top" class="ss_view_info">
			    <div class="ss_style_color" >
					<p style="text-align:center; padding-top:30px;">
						<c:if test="${!empty entryMoved}">
						<ssf:nlt tag="entry.moved">
						<ssf:param name="value" value="${entryMoved.pathName}"/>
						</ssf:nlt>
						</c:if>
						<c:if test="${empty entryMoved}">
						<ssf:nlt tag="entry.deleted"/>
						</c:if>
					</p>
				</div>
			  </td>
			</tr>
		</tbody>
		</table>
	  </div>
	</c:if>
	<c:if test="${ss_folderViewStyle1 == 'wiki'}">
	    <div class="ss_style_color" >
			<p style="text-align:center; padding-top:30px;">
				<c:if test="${!empty entryMoved}">
				<ssf:nlt tag="entry.moved">
				<ssf:param name="value" value="${entryMoved.pathName}"/>
				</ssf:nlt>
				</c:if>
				<c:if test="${empty entryMoved}">
				<ssf:nlt tag="entry.deleted"/>
				</c:if>
			</p>
		</div>
	</c:if>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

