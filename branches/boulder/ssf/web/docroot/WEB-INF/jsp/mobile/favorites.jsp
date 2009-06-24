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
<%@ page import="org.kablink.teaming.ObjectKeys" %>

<c:if test="${!empty ss_mobileFavoritesList}">
	<div class="pagebody">
	  <div id="favorites">
	    <span><ssf:nlt tag="favorites"/></span>
	  </div>
	  <div class="pagebody_border">
		<ul>
		<c:forEach var="favorite" items="${ss_mobileFavoritesList}">
		 <jsp:useBean id="favorite" type="net.sf.json.JSONObject" />
		 <% try { %><c:set var="f_eletype" value='<%= favorite.get("eletype") %>'/><% } catch(Exception e) {} %>
		 <% try { %><c:set var="f_type" value='<%= favorite.get("type") %>'/><% } catch(Exception e) {} %>
		 <% try { %><c:set var="f_id" value='<%= favorite.get("id") %>'/><% } catch(Exception e) {} %>
		 <% try { %><c:set var="f_action" value='<%= favorite.get("action") %>'/><% } catch(Exception e) {} %>
		 <% try { %><c:set var="f_name" value='<%= favorite.get("name") %>'/><% } catch(Exception e) {} %>
		 <% try { %><c:set var="f_value" value='<%= favorite.get("value") %>'/><% } catch(Exception e) {} %>
		 <c:if test="${f_eletype == 'favorite'}">
		  <li>
			<c:if test="${f_type == 'binder' && f_action == 'view_folder_listing'}">
			  <a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${f_value}" 
							action="__ajax_mobile" actionUrl="false" 
							operation="mobile_show_folder" />"><span>${f_name}</span></a>
			</c:if>
			<c:if test="${f_type == 'binder' && empty f_action}">
			  <a href="<ssf:url adapter="true" portletName="ss_forum" 
			    			folderId="${f_value}" 
							action="__ajax_mobile" actionUrl="false" 
							operation="mobile_show_workspace" />"><span>${f_name}</span></a>
			</c:if>
		  </li>
		 </c:if>
		</c:forEach>
		</ul>
	  </div>
	</div>
</c:if>
