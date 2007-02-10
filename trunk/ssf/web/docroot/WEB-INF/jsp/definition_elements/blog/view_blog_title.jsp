<% //Blog title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="title_entry" value="${ssDefinitionEntry}"/>
<jsp:useBean id="title_entry" type="com.sitescape.team.domain.FolderEntry" />

<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<%
	//Get a dispalyable number for the replies
	String docNumber = "";
	String fontSize = "ss_largestprint";
	if (title_entry.getTopEntry() != null) fontSize = "ss_largerprint";
	if (title_entry.getTopEntry() != null && title_entry.getDocNumber() != null) {
		docNumber = title_entry.getDocNumber();
		int i = docNumber.indexOf(".");
		if (i > 0) {
			docNumber = docNumber.subSequence(i+1, docNumber.length()) + ". ";
		}
	}
%>

<div class="ss_blog_title">
<div class="ss_header_bar_timestamp">
<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${ssDefinitionEntry.creation.date}" type="both" 
	 timeStyle="short" dateStyle="medium" />
 by <c:out value="${ssDefinitionEntry.creation.principal.title}"/>
</div>
<%
	if (!ssSeenMap.checkIfSeen(title_entry)) {
		ssSeenMap.setSeen(title_entry);
		%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%>
<span class="ss_header_bar_title_text">
 <a class="ss_header_bar_title_link" 
   href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${ssDefinitionEntry.parentFolder.id}"
		    entryId="${ssDefinitionEntry.id}">
		    <ssf:param name="entityType" value="${ssDefinitionEntry.entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>">
<%= docNumber %>
<c:if test="${empty ssDefinitionEntry.title}">
  <span class="ss_light">
    --<ssf:nlt tag="entry.noTitle"/>--
  </span>
</c:if>
<c:out value="${ssDefinitionEntry.title}"/></a>
</span>
</div>

<c:if test="${empty ssDefinitionEntry.parentEntry}">
<table cellspacing="0" cellpadding="0" width="100%">
<tr>
<td valign="top"><%@ include file="/WEB-INF/jsp/definition_elements/popular_view.jsp" %></td>
<td valign="top" align="right">
<c:set var="ssPersonalTags" value="${ssBlogEntries[ss_blog_docId].ssPersonalTags}" scope="request"/>
<c:set var="ssCommunityTags" value="${ssBlogEntries[ss_blog_docId].ssCommunityTags}" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
</td>
</tr>
</table>
</c:if>
