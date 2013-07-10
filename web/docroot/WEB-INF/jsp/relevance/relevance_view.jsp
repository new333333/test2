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
<% // Relevance Integration - View. %>
<%@ page import="org.kablink.teaming.relevance.util.RelevanceUtils" %>
<%
	if (RelevanceUtils.isRelevanceEnabled()) {
%>
		<div id="ss_related_files" class="relatedfileslist" style="display: none">
			<input type="hidden" id="ss_related_anchor_id" value="" />
			<div class="ss_related_div">
				<div class="ss_related_head"><ssf:nlt tag="entry.relatedFiles"/><a href="#"><img class="ss_related_close" src="<html:imagesPath/>icons/close_circle16.png" border="0" title="<%= NLT.get("entry.relatedClose").replaceAll("\"", "&QUOT;") %>"></a></div>
				<div id="DIV_relatedFiles">
					<div class="ss_related_item"><a class="ss_related_anchor" href="#">...real files go here...</a></div>
				</div>
		
				<div class="ss_related_head"><ssf:nlt tag="entry.relatedWorkspaces"/></div>
				<div id="DIV_relatedWorkspaces">
					<div class="ss_related_item"><a class="ss_related_anchor" href="#">...real workspaces go here...</a></div>
				</div>
				
				<div class="ss_related_head"><ssf:nlt tag="entry.relatedUsers"/></div>
				<div id="DIV_relatedUsers">
					<div class="ss_related_item"><a class="ss_related_anchor" href="#">...real users go here...</a></div>
				</div>
			</div>
		</div>
<% } %>
