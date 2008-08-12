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

    <table width="100%" style="margin-left: 7px;"><tbody>
      <tr>
        <td><form name="ss_recordsPerPage_${renderResponse.namespace}" 
                id="ss_recordsPerPage_${renderResponse.namespace}" method="post" 
			    action="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="binderId" value="${ssFolder.id}"/>
				<c:if test="${!empty cTag}"><ssf:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
				name="endDate" value="${endDate}"/></c:if><ssf:param 
				name="operation" value="change_entries_on_page"/></ssf:url>">
		    
		    <input type="hidden" name="ssEntriesPerPage" />
		
			  <ssf:menu title="${ssPageMenuControlTitle}" 
			    titleId="ss_selectEntriesTitle${renderResponse.namespace}" 
			    titleClass="ss_compact" menuClass="ss_actions_bar4 ss_actions_bar_submenu" menuImage="pics/menudown.gif">
			
				<ul class="ss_actions_bar4 ss_actions_bar_submenu" style="width:150px;">
				<li>
					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '5');return false;">
						<ssf:nlt tag="entry.shown"><ssf:param name="value" value="5"/></ssf:nlt>
					</a>
				</li>
				<li>	
					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '10');return false;">
						<ssf:nlt tag="entry.shown"><ssf:param name="value" value="10"/></ssf:nlt>
					</a>
				</li>
				<li>
					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '25');return false;">
						<ssf:nlt tag="entry.shown"><ssf:param name="value" value="25"/></ssf:nlt>
					</a>
				</li>
				<li>
					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '50');return false;">
						<ssf:nlt tag="entry.shown"><ssf:param name="value" value="50"/></ssf:nlt>
					</a>
				</li>
				<li>
					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '100');return false;">
						<ssf:nlt tag="entry.shown"><ssf:param name="value" value="100"/></ssf:nlt>
					</a>
				</li>
				</ul>

			  </ssf:menu>
		</form></td>
      </tr>
    </tbody></table>
