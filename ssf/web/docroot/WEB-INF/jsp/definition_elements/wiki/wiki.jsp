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
<% // Wiki view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="java.util.Date" %>

<c:if test="${!ss_wikiFolderList && (!empty ss_wikiHomepageEntry)}">
  <c:set var="ssConfigDefinition" value="${ss_wikiHomepageEntryMap.ssConfigDefinition}" scope="request"/>
  <c:set var="item" value="${ss_wikiHomepageEntryMap.ssConfigElement}" scope="request"/>
  <c:set var="ssConfigJspStyle" value="view" scope="request"/>
  <c:set var="ssDefinitionEntry" value="${ss_wikiHomepageEntry}" scope="request"/>
  <c:set var="ssEntry" value="${ss_wikiHomepageEntry}" scope="request"/>
  <c:set var="ss_showHelpIcon" value="true" scope="request"/> 
  
 <script type="text/javascript">
  var url = "<ssf:url     
      adapter="true" 
          portletName="ss_forum" 
          folderId="${ss_wikiHomepageEntry.parentBinder.id}" 
          action="view_folder_entry" 
          entryId='${ss_wikiHomepageEntry.id}' actionUrl="true"><ssf:param
          name="entryViewStyle" value="popup"/><ssf:param
          name="namespace" value="${renderResponse.namespace}"/></ssf:url>";
  self.location.replace(url);
  </script>
  
</c:if>

<c:if test="${ss_wikiFolderList || empty ss_wikiHomepageEntry}">
  <c:if test="${empty ss_wikiHomepageEntry}">
    <div style="text-align:center"><span class="wiki-nohomepage-panel"><ssf:nlt tag="entry.noWikiHomepageSet"/></span></div>
    <c:set var="ss_wikiFolderList" value="true" scope="request"/>
    <c:set var="ss_wikiCurrentTab" value="list" scope="request"/>
  </c:if>

  <%@ include file="/WEB-INF/jsp/definition_elements/wiki/wiki_folder_listing.jsp" %>

</c:if>
