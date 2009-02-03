<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<!-- Include the javascript needed to play a tutorial video. -->
<jsp:include page="/WEB-INF/jsp/common/tutorial_support.jsp" />

<%  
	Long ss_mashupTableNumber = (Long) request.getAttribute("ss_mashupTableNumber");
	Long ss_mashupTableDepth = (Long) request.getAttribute("ss_mashupTableDepth");
	Map ss_mashupTableItemCount = (Map) request.getAttribute("ss_mashupTableItemCount");
	ss_mashupTableItemCount.put(ss_mashupTableNumber, "utility");  
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);

	Long ss_mashupListDepth = (Long) request.getAttribute("ss_mashupListDepth");
%>
<% if (ss_mashupListDepth > 0) { %>
<li>
<% } %>

<div>
	<p>
	  <a href="javascript:;" 
	       onClick="ss_helpSystem.showInlineHelpSpotInfo(this, 'print_manuals', '', 200, 230, 'center', 'middle');">
	     <span class="ss_getting_started"><ssf:nlt tag="help.viewBooks.title"/></span>
	   </a>
	</p>
	<div>
		<a 	href="javascript:;"
			title="<ssf:nlt tag="gettingStarted.alt.viewTutorials" />"
			onclick="startTutorial( 'whatIsTeaming' );">
			<span class="ss_getting_started"><ssf:nlt tag="gettingStarted.viewTutorials" /></span>
		</a>
	</div>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />

</div>
<% if (ss_mashupListDepth > 0) { %>
</li>
<% } %>
