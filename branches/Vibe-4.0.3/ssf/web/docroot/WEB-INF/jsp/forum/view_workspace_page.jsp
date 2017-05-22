<%
  /**
   * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
   * (c) 1998-2014 Novell, Inc. All Rights Reserved.
   *
   * Attribution Information:
   * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request"/>
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request"/>
<script type="text/javascript">
  var ss_reloadUrl = "${ss_reloadUrl}";
  var ss_reloadUrl${ssBinder.id} = ss_reloadUrl;
</script>

<ssf:skipLink tag='<%= NLT.get("skip.header.toContent") %>' id="headerToContent_${renderResponse.namespace}"
              linkOnly="true"/>
<div id="ss_showfolder${renderResponse.namespace}" class="ss_style ss_portlet ss_content_outer">
  <jsp:include page="/WEB-INF/jsp/forum/view_workarea_navbar.jsp"/>
  <c:set var="ss_sidebarVisibility" value="${ssUserProperties.sidebarVisibility}" scope="request"/>
  <c:if test="${empty ss_sidebarVisibility}"><c:set var="ss_sidebarVisibility" value="block"
                                                    scope="request"/></c:if>
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
  <% if (!(GwtUIHelper.isGwtUIActive(request))) { %>
  <div class="ss_actions_bar1_pane ss_sidebarImage">
    <table cellspacing="0" cellpadding="0">
      <tbody>
      <tr>
        <c:if test="${!ss_mashupHideSidebar && (empty ss_captive || !ss_captive)}">
          <td valign="middle">
            <a href="javascript: ;"
               onClick="ss_showHideSidebar('${renderResponse.namespace}');return false;"
                ><span style="padding-left:12px; display:${ss_sidebarVisibilityShow};"
                       id="ss_sidebarHide${renderResponse.namespace}"
                       class="ss_fineprint ss_sidebarSlidesm ss_sidebarSlidetext"><ssf:nlt
                tag="toolbar.sidebar.show"/></span>
<span style="padding-left:12px; display:${ss_sidebarVisibilityHide};"
id="ss_sidebarShow${renderResponse.namespace}"
class="ss_fineprint ss_sidebarSlide ss_sidebarSlidetext"><ssf:nlt tag="toolbar.sidebar.hide"/></span></a>
          </td>
        </c:if>

        <c:if test="${!ss_mashupHideToolbar}">
          <td valign="middle">
            <ssf:toolbar style="ss_actions_bar1 ss_actions_bar">
              <% // Workspace toolbar %>
              <jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp"/>
            </ssf:toolbar>
          </td>
        </c:if>
      </tr>
      </tbody>
    </table>
  </div>
  <% } %>
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
      <td valign="top" class="ss_view_info">
        <c:if test="${!showTrash}">
          <!-- Include "Video Tutorials" ui. -->
          <jsp:include page="/WEB-INF/jsp/common/tutorial_support.jsp"/>
        </c:if>

        <ssf:skipLink tag='<%= NLT.get("skip.header.toContent") %>'
                      id="headerToContent_${renderResponse.namespace}"
                      anchorOnly="true"/>
        <div class="ss_tab_canvas">
          <c:if test="${showTrash}">
            <div class="ss_style_color">
              <div class="ss_content_inner">
                <div id="ss_whatsNewDiv${ss_namespace}">
                  <c:if test="${ss_type == 'whatsNew' || ss_type == 'unseen'}">
                    <jsp:include page="/WEB-INF/jsp/forum/whats_new_page.jsp"/>
                  </c:if>
                </div>
              </div>
            </div>
            <c:set var="trashMode" value="workspace" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/binder/view_trash.jsp" />
          </c:if>

          <c:if test="${!showTrash}">
            <!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
            <div class="ss_style_color">
              <div class="ss_content_inner">
                <div id="ss_whatsNewDiv${ss_namespace}">
                  <c:if test="${ss_type == 'whatsNew' || ss_type == 'unseen'}">
                    <jsp:include page="/WEB-INF/jsp/forum/whats_new_page.jsp"/>
                  </c:if>
                </div>
                <c:choose>
                  <c:when test="${ss_showTeamMembers}">
                    <c:if test="${!empty ss_reloadUrl}">
                      <div style="text-align: right; ">
                        <input type="button"
                               onClick="window.top.ss_gotoContentUrl('${ss_reloadUrl}');return false;"
                               value='<%= StringEscapeUtils.escapeXml(NLT.get("__return_to").concat(" ").concat(NLT.get("__workspace_view"))) %>'>
                      </div>
                    </c:if>

                    <jsp:include page="/WEB-INF/jsp/forum/list_team_members.jsp" />

                    <c:if test="${!empty ss_reloadUrl && ssTeamMembersCount > 10}">
                      <div style="text-align: right; ">
                        <input type="button"
                               onClick="window.top.ss_gotoContentUrl('${ss_reloadUrl}');return false;"
                               value='<%= StringEscapeUtils.escapeXml(NLT.get("__return_to").concat(" ").concat(NLT.get("__workspace_view"))) %>'>
                      </div>
                    </c:if>

                  </c:when>
                  <c:otherwise>

                    <% // Show the workspace according to its definition %>
                    <ssf:displayConfiguration configDefinition="${ssConfigDefinition}"
                                              processThisItem="true"
                                              configElement="${ssConfigElement}"
                                              configJspStyle="${ssConfigJspStyle}"
                                              entry="${ssBinder}"/>
                  </c:otherwise>
                </c:choose>
              </div>

            </div>
          </c:if>

          <% // Footer toolbar %>
          <c:if test="${!ss_mashupHideFooter}">
            <jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp"/>
          </c:if>
          <% if (!(GwtUIHelper.isGwtUIActive(request))) { %>
          <c:if test="${ss_mashupHideToolbar && ss_mashupShowAlternateToolbar}">
            <div class="ss_actions_bar1_pane ss_sidebarImage">
              <table cellspacing="0" cellpadding="0">
                <tbody>
                <tr>
                  <td valign="middle">
                    <ssf:toolbar style="ss_actions_bar1 ss_actions_bar">
                      <% // Workspace toolbar %>
                      <jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp"/>
                    </ssf:toolbar>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </c:if>
          <% } %>

        </div>
      </td>
    </tr>
    </tbody>
  </table>
</div>
<script type="text/javascript">
  ss_createOnLoadObj('ss_initShowFolderDiv${renderResponse.namespace}', ss_initShowFolderDiv('${renderResponse.namespace}'));

  <c:if test="${!showTrash}">
  // Add initTutorial() as a function to be called when the page is loaded.
  ss_createOnLoadObj('initTutorial', initTutorial);
  </c:if>
</script>
