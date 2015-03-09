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
<% //view the wiki tabs %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<div id="wiki-tabset" class="wiki-tabs margintop3" style="text-align: left;">
	<table cellpadding="0" cellspacing="0" style="white-space: nowrap;">
		<tr>
			<td align="right" style="padding-right: 7px;" nowrap>
		      <c:if test="${!empty ss_wikiEntryBeingShown}">
				<span class="wiki-tab <c:if test="${ss_wikiCurrentTab == 'page'}">on</c:if>">
				  <a href="<ssf:url     
			          adapter="true" 
			          portletName="ss_forum" 
			          folderId="${ss_wikiEntryBeingShown.parentBinder.id}" 
			          action="view_folder_entry" 
			          entryId='${ss_wikiEntryBeingShown.id}' 
			          actionUrl="true"><ssf:param
			          name="entryViewStyle" value="popup"/><ssf:param
			          name="namespace" value="${renderResponse.namespace}"/></ssf:url>"
			          onclick="if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this, true);"
			      >
			      <c:if test="${ss_wikiHomepageEntryId == ss_wikiEntryBeingShown.id}">
			        <ssf:nlt tag="wiki.homePage"/>
			      </c:if>
			      <c:if test="${ss_wikiHomepageEntryId != ss_wikiEntryBeingShown.id}">
			        <ssf:nlt tag="wiki.page"/>
			      </c:if>
			      </a>
			    </span>
			  </c:if>
			</td>
			<td align="right" nowrap>
				<span class="wiki-tab <c:if test="${ss_wikiCurrentTab == 'list'}">on</c:if>">
				  <a href="<ssf:url 
					action="view_folder_listing" 
					binderId="${ssBinder.id}"
					entryId="${ss_wikiEntryBeingShown.id}"
					><ssf:param name="wiki_folder_list" value="1"/></ssf:url>"
					onclick="if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this, true);"
				  ><ssf:nlt tag="wiki.topicsAndPages"/></a>
				</span>
			</td>
			<td width="100%"></td>
		</tr>	
	</table>	
</div>
