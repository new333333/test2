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

<% //view a folder forum with folder on the left and the entry on the right in an iframe %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="org.kablink.teaming.module.definition.DefinitionUtils" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<%
//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = DefinitionUtils.getViewType(ssConfigDefinition);
if (folderViewStyle == null || folderViewStyle.equals("")) folderViewStyle = "folder";
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />
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

<ssf:skipLink tag='<%= NLT.get("skip.header.toContent") %>' id="headerToContent_${renderResponse.namespace}"
  linkOnly="true"/>
<div id="ss_showfolder${renderResponse.namespace}" class="ss_style ss_portlet ss_content_outer">
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />
<jsp:include page="/WEB-INF/jsp/definition_elements/popular_view_init.jsp" />
<jsp:include page="/WEB-INF/jsp/forum/view_workarea_navbar.jsp" />
<% if (!(GwtUIHelper.isGwtUIActive(request))) { %>
<div class="ss_actions_bar1_pane ss_sidebarImage">
<table cellspacing="0" cellpadding="0">
<tr>
<td valign="middle">
<c:if test="${!ss_mashupHideToolbar}">
  <jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" />
</c:if>
</td></tr>
</table>
</div>
<% } %>
<div width="100%">
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
	<td valign="top" class="ss_view_info">
	  <c:if test="${!ss_mashupHideToolbar}">
	    <c:if test="${ss_folderViewStyle != 'blog'}">
	      <jsp:include page="/WEB-INF/jsp/definition_elements/folder_entry_toolbar.jsp" />
	    </c:if>
	  </c:if>
	  <div align="center">
	    <div id="ss_folder_wrap">
		  <c:if test="${!ss_mashupHideToolbar}">
		    <div id="ss_whatsNewDiv${ss_namespace}">
		    <c:if test="${ss_type == 'whatsNew' || ss_type == 'unseen'}">
		    <jsp:include page="/WEB-INF/jsp/forum/whats_new_page.jsp" />
		    </c:if>
		    </div>
		  </c:if>
<ssf:skipLink tag='<%= NLT.get("skip.header.toContent") %>' id="headerToContent_${renderResponse.namespace}"
  anchorOnly="true"/>
	      <div id="ss_folder_type_${ssDefinitionFamily}" class="ss_style_color" >
			<c:if test="${showTrash}">
				<c:set var="trashMode" value="folder" scope="request"/>
				<%@ include file="/WEB-INF/jsp/binder/view_trash.jsp" %>
			</c:if>
			<c:if test="${!showTrash}">
			  <% // Dispatch based on folder view style %>
			  <c:choose>
				  <c:when test="${ss_folderViewStyle == 'wiki'}">
					<%@ include file="/WEB-INF/jsp/definition_elements/wiki/wiki_folder_view.jsp" %>
				  </c:when>
				  <c:otherwise>
					<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
						  configElement="${ssConfigElement}" 
						  configJspStyle="${ssConfigJspStyle}" 
						  entry="${ssBinder}" />
				  </c:otherwise>
			  </c:choose>
			</c:if>
		  </div>
		  <% // Footer toolbar %>
		  <c:if test="${!ss_mashupHideFooter}">
		    <jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />
		  </c:if>
		  
		  <c:if test="${ss_mashupHideToolbar && ss_mashupShowAlternateToolbar}">
			<div class="ss_actions_bar1_pane ss_sidebarImage">
			  <table cellspacing="0" cellpadding="0">
			    <tr>
			      <td valign="middle">
		            <jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" />
		          </td>
		        </tr>
		      </table>
		    </div>
		    <div id="ss_whatsNewDiv${ss_namespace}">
		    <c:if test="${ss_type == 'whatsNew' || ss_type == 'unseen'}">
		    <jsp:include page="/WEB-INF/jsp/forum/whats_new_page.jsp" />
		    </c:if>
		    </div>
		  </c:if>
		</div>
	  </div>
	</td>
	</tr>
	</tbody>
	</table>
</div>
</div>
<script type="text/javascript">
ss_createOnLoadObj('ss_initShowFolderDiv${renderResponse.namespace}', ss_initShowFolderDiv('${renderResponse.namespace}'));
</script>

<c:if test="${!empty ssEntryIdToBeShown && !empty ss_useDefaultViewEntryPopup}">
	<script type="text/javascript">
		function ss_showEntryToBeShown${renderResponse.namespace}() {
		    var url = "<ssf:url     
				adapter="true" 
				portletName="ss_forum" 
				folderId="${ssBinder.id}" 
				action="view_folder_entry" 
				entryId="${ssEntryIdToBeShown}" 
				actionUrl="true" />"

			var date;

			// To avoid caching problems, add a parameter to the url that will be unique every time.
			date = new Date();
			url += '&ms=' + date.getTime();

			ss_showForumEntryInIframe(url);
		}
		ss_createOnLoadObj('ss_showEntryToBeShown${renderResponse.namespace}', ss_showEntryToBeShown${renderResponse.namespace});
	</script>
</c:if>

