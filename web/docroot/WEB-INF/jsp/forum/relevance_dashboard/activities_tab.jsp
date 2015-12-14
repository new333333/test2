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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_dashboard_content" class="ss_tricolumn">
	<table cellpadding="0" cellspacing="0" class="marginbottom3">
		<tr>
			<td width="35%" style="padding-right: 10px; vertical-align: top;">
				<ssf:canvas id="relevanceDocuments" type="inline" styleId="ss_documents">
					<div id="ss_dashboardDocs${renderResponse.namespace}" style="padding: 5px;">
					  <jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/my_docs.jsp" />
					</div>
				</ssf:canvas>
			</td><!-- end of ss_col 1 -->
			<td width="35%" style="padding-right: 10px; vertical-align: top;">			
				<c:if test="${ssBinder.owner.id == ssUser.id}">
					<ssf:canvas id="relevanceVisitedEntries" type="inline" styleId="ss_documents">
						<div id="ss_dashboardEntriesViewed${renderResponse.namespace}" style="padding: 5px;">
						  <jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/my_visited_entries.jsp" />
						</div>
					</ssf:canvas>
				</c:if>	
			</td><!-- end of col2 -->
			<td width="25%"  style="padding-right: 10px; vertical-align: top;>
				<ssf:canvas id="relevanceVisitors" type="inline" styleId="ss_people">
					<div id="ss_dashboardVisitors${renderResponse.namespace}" style="padding: 5px;">
					  <jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/visitors.jsp" />
					</div>
				</ssf:canvas>
			
				<ssf:canvas id="relevanceTags" type="inline" styleId="ss_people">
				<ssf:param name="title" useBody="true" >
					<div id="ss_title" class="ss_pt_title ss_green" style="padding-top: 10px;"><ssf:nlt tag="relevance.myTags"/></div>
				</ssf:param>
					<div id="ss_dashboardMyTags${renderResponse.namespace}">
					  <jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/my_tags.jsp" />
					</div>
				</ssf:canvas>
      		</td><!-- end of col3 -->
		</td>
	</table>		
</div><!-- end of content -->

