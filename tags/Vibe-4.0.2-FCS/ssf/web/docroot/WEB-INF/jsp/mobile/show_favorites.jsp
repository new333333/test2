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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("navigation.favorites") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>

<c:set var="ss_pageTitle" value='<%= NLT.get("navigation.myFavorites") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/masthead.jsp" %>

<div class="content">

<c:set var="ss_hideMiniBlog" value="true" scope="request" />

  <div class="folders">
    <div class="folder-content">
	  <c:forEach var="favorite" items="${ss_mobileFavoritesList}">
		 <jsp:useBean id="favorite" type="net.sf.json.JSONObject" />
		 <% try { %><c:set var="f_eletype" value='<%= favorite.get("eletype") %>'/><% } catch(Exception e) {} %>
		 <% try { %><c:set var="f_type" value='<%= favorite.get("type") %>'/><% } catch(Exception e) {} %>
		 <% try { %><c:set var="f_id" value='<%= favorite.get("id") %>'/><% } catch(Exception e) {} %>
		 <% try { %><c:set var="f_action" value='<%= favorite.get("action") %>'/><% } catch(Exception e) {} %>
		 <% try { %><c:set var="f_name" value='<%= favorite.get("name") %>'/><% } catch(Exception e) {} %>
		 <% try { %><c:set var="f_value" value='<%= favorite.get("value") %>'/><% } catch(Exception e) {} %>
		 <c:if test="${f_eletype == 'favorite'}">
			<c:if test="${f_type == 'binder' && (f_action == 'view_folder_listing' || f_action == 'view_profile_listing')}">
			  	<a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${f_value}" 
							action="__ajax_mobile" actionUrl="false" 
							operation="mobile_show_folder" />">
					<div class="folder-item">
	             		<img class="margin5r" src="<html:rootPath/>images/mobile/favorite_25.png" align="absmiddle" />
						<span>${f_name}</span>
					</div>
				</a>
			</c:if>
			<c:if test="${f_type == 'binder' && empty f_action}">
				<a href="<ssf:url adapter="true" portletName="ss_forum" 
			    			folderId="${f_value}" 
							action="__ajax_mobile" actionUrl="false" 
							operation="mobile_show_workspace" />">
					<div class="folder-item">
	             		<img class="margin5r" src="<html:rootPath/>images/mobile/favorite_25.png" align="absmiddle" />
						<span>${f_name}</span>
					</div>	
				</a>
			</c:if>
		 </c:if>
	  </c:forEach>
	  <c:if test="${empty ss_mobileFavoritesList}">
	    <div class="entry-content margintop2"><ssf:nlt tag="mobile.noFavorites"/></div>
	  </c:if>
    </div>
  </div>
</div>

</body>
</html>
