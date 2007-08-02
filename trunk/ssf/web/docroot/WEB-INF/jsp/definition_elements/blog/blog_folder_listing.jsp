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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<% //View the listing part of a blog folder %>

		  <c:forEach var="entry" items="${ssFolderEntries}" >
			<div class="ss_blog_content">
			  <c:set var="ss_blog_docId" value="${entry._docId}" scope="request"/>
			  <c:set var="ss_blog_workflowStateCaption" value="" scope="request"/>
			  <c:set var="ss_blog_reply_url" value="${ssBlogEntries[entry._docId].replyBlogUrl}" scope="request"/>
			  <c:if test="${!empty entry._workflowStateCaption}">
			  <c:set var="ss_blog_workflowStateCaption" value="${entry._workflowStateCaption}" scope="request"/>
			  </c:if>
			  <ssf:displayConfiguration 
			    configDefinition="${ssBlogEntries[entry._docId].ssConfigDefinition}" 
			    configElement="${ssBlogEntries[entry._docId].ssConfigElement}" 
			    configJspStyle="view"
			    processThisItem="true" 
			    entry="${ssBlogEntries[entry._docId].entry}" />
			</div>
		  </c:forEach>
