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
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>

<% // The main workspace view  %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ include file="/WEB-INF/jsp/common/initializeGWT.jsp" %>
<c:set var="ss_windowTitle" value="${ssBinder.title}" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="showWorkspacePage" value="true"/>

<body class="ss_style_body tundra">
<div id="ss_pseudoPortalDiv${renderResponse.namespace}">
  <script type="text/javascript">
    function ss_resizeTopDiv_${renderResponse.namespace}() {
      ss_resizeTopDiv('${renderResponse.namespace}');
    }
    ss_createOnResizeObj("ss_resizeTopDiv", ss_resizeTopDiv_${renderResponse.namespace});
    ss_createOnLayoutChangeObj("ss_resizeTopDiv", ss_resizeTopDiv_${renderResponse.namespace});
  </script>
  <table width="100%" cellpadding="0" cellspacing="0">
    <tr>
      <td>
        <ssf:ifLoggedIn><c:if
            test="${empty ss_noEnableAccessibleLink && !empty ss_accessibleUrl && (empty ss_displayStyle || ss_displayStyle != 'accessible')}">
          <a class="ss_skiplink" href="${ss_accessibleUrl}"><img border="0"
              <ssf:alt tag="accessible.enableAccessibleMode"/>
                                                                 src="<html:imagesPath/>pics/1pix.gif"/></a><%--
		--%></c:if></ssf:ifLoggedIn>
        <jsp:include page="/WEB-INF/jsp/common/presence_support.jsp"/>

        <c:if test="${!empty ssReloadUrl}">
          <script type="text/javascript">
            //Open the current url in the opener window
            ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
          </script>
        </c:if>

        <c:if test="${empty ssReloadUrl}">
          <script type="text/javascript">
            if (self.parent) {
              //We are in an iframe inside a portlet (maybe?)
              var windowName = self.window.name
              if (windowName.indexOf("ss_workareaIframe") == 0) {
                //We are running inside an iframe, get the namespace name of that iframe's owning portlet
                var namespace = windowName.substr("ss_workareaIframe".length)
                //alert('namespace = '+namespace+', binderId = ${ssBinder.id}, entityType = ${ssBinder.entityType}')
                var url = "
                <ssf:url
                          adapter="true"
                          portletName="ss_forum"
                          action="__ajax_request"
                          binderId="${ssBinder.id}">
                <ssf:param name="entityType" value="${ssBinder.entityType}"/>
                <ssf:param name="namespace" value="ss_namespacePlaceholder" />
                <ssf:param name="operation" value="set_last_viewed_binder" />
                <ssf:param name="rn" value="ss_randomNumberPlaceholder"/>
                </ssf:url>"
                url = ss_replaceSubStr(url, 'ss_namespacePlaceholder', namespace);
                url = ss_replaceSubStr(url, 'ss_randomNumberPlaceholder', ss_random++);
                ss_fetch_url(url);
              }
            }

            function ss_workarea_showId${renderResponse.namespace}(id, action, entryId) {
              if (typeof entryId == "undefined") entryId = "";
              //Build a url to go to
              var url = "
              <ssf:url
                          adapter="true"
                          portletName="ss_forum"
                          binderId="ssBinderIdPlaceHolder"
                          entryId="ssEntryIdPlaceHolder"
                          action="ssActionPlaceHolder"
                          actionUrl="false" >
              <ssf:param name="namespace" value="${renderResponse.namespace}"/>
              </ssf:url>"
              url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
              url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
              url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
              if (typeof window.top.ss_gotoContentUrl != "undefined") {
                window.top.ss_gotoContentUrl(url);
              }
              else {
                setTimeout("self.location.href = '" + url + "';", 100);
              }
              return false;
            }
            if (typeof ss_workarea_showId == "undefined")
              ss_workarea_showId = ss_workarea_showId${renderResponse.namespace};
          </script>

          <c:if test="${!showTrash}">
            <!-- Include the javascript needed to play a tutorial video. -->
            <jsp:include page="/WEB-INF/jsp/common/tutorial_support_js.jsp"/>
          </c:if>

          <c:if test="${showWorkspacePage}">
            <jsp:include page="/WEB-INF/jsp/forum/view_workspace_page.jsp"/>
          </c:if>

        </c:if>

      </td>
    </tr>
  </table>
</div>
</body>
</html>
