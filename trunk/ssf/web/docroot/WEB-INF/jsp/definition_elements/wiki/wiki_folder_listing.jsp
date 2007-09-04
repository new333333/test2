<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% //View the listing part of a wiki folder %>
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />
<%
	boolean useAdaptor = true;
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
		useAdaptor = false;
	}
%>


        <div id="ss_wikiEntryDiv<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>">
          <iframe id="ss_wikiIframe<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>" 
            name="ss_wikiIframe<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>" 
            style="width:100%; display:block; position:relative;"
    		<c:if test="${empty ss_wikiHomepageEntryId && empty ssEntryIdToBeShown}">
    		  src="<html:rootPath/>js/forum/null.html" 
    		</c:if>
    		<c:if test="${!empty ss_wikiHomepageEntryId || !empty ssEntryIdToBeShown}">
    		  <c:set var="entryId" value="${ss_wikiHomepageEntryId}"/>
    		  <c:if test="${!empty ssEntryIdToBeShown}">
    		    <c:set var="entryId" value="${ssEntryIdToBeShown}"/>
    		  </c:if>
    		  src="<ssf:url     
		    		adapter="<%= useAdaptor %>" 
		    		portletName="ss_forum" 
		    		folderId="${ssFolder.id}" 
		    		action="view_folder_entry" 
		    		entryId="${entryId}" 
		    		actionUrl="true" ><ssf:param name="namespace" value="${renderResponse.namespace}" /></ssf:url>" 
    		</c:if>
    		height="95%" width="100%" 
    		onLoad="ss_setWikiIframeSize('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>');" 
    		frameBorder="0" >xxx</iframe>
        </div>
   