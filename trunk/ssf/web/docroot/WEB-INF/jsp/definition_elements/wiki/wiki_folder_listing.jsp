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

<% //View the listing part of a wiki folder %>
        <div id="ss_wikiEntryDiv${renderResponse.namespace}">
          <iframe id="ss_wikiIframe${renderResponse.namespace}" 
            name="ss_wikiIframe${renderResponse.namespace}" 
            style="width:99%; display:inline; position:relative;"
    		<c:if test="${empty ss_wikiHomepageEntryId && empty ssEntryIdToBeShown}">
    		  src="<ssf:url     
		    		adapter="true" 
		    		portletName="ss_forum" 
		    		folderId="${ssFolder.id}" 
		    		action="view_folder_entry" 
		    		actionUrl="false" ><ssf:param 
		    		name="operation" value="show_no_entries" /><ssf:param
					name="binderType" value="wiki" /><ssf:param 
		    		name="entryViewStyle" value="popup" /><ssf:param 
		    		name="namespace" value="${renderResponse.namespace}" /></ssf:url>" 
    		</c:if>
    		<c:if test="${!empty ss_wikiHomepageEntryId || !empty ssEntryIdToBeShown}">
    		  <c:set var="entryId" value="${ss_wikiHomepageEntryId}"/>
    		  <c:if test="${!empty ssEntryIdToBeShown}">
    		    <c:set var="entryId" value="${ssEntryIdToBeShown}"/>
    		  </c:if>
    		  src="<ssf:url     
		    		adapter="true" 
		    		portletName="ss_forum" 
		    		folderId="${ssFolder.id}" 
		    		action="view_folder_entry" 
		    		entryId="${entryId}" 
		    		actionUrl="true" ><ssf:param 
		    		name="entryViewStyle" value="popup" /><ssf:param 
		    		name="namespace" value="${renderResponse.namespace}" /></ssf:url>" 
    		</c:if>
    		height="95%" width="100%" 
    		onLoad="ss_setWikiIframeSize('${renderResponse.namespace}');" 
    		frameBorder="0" >xxx</iframe>
        </div>
   