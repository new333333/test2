<% //Blog title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="title_entry" value="${ssDefinitionEntry}"/>
<jsp:useBean id="title_entry" type="com.sitescape.ef.domain.Entry" />

<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<%
	if (!ssSeenMap.checkIfSeen(title_entry)) {
		ssSeenMap.setSeen(title_entry);
		%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%>

<div style="margin:0px; padding:0px;">
<span class="ss_bold ss_largestprint" style="margin:0px; padding:0px;">
 <a style="text-decoration: none; margin:0px; padding:0px;" href="<ssf:url 
    folderId="${ssDefinitionEntry.parentFolder.id}" 
    action="view_folder_entry"
    entryId="${ssDefinitionEntry.id}"/>">
<c:if test="${empty ssDefinitionEntry.title}">
  <span class="ss_gray" style="margin:0px; padding:0px;">
    --<ssf:nlt tag="entry.noTitle"/>--
  </span>
</c:if>
<c:out value="${ssDefinitionEntry.title}"/></a>
</span>
</div>
<hr size="1" color="#cecece" style="margin:0px; padding:0px;">
<%@ include file="/WEB-INF/jsp/definition_elements/popular_view.jsp" %>
<br>
<span class="ss_italic ss_smallprint" style="margin:0px; padding:0px;">
<fmt:formatDate 
     value="${ssDefinitionEntry.creation.date}" type="both" 
	 pattern="dd MMMM yyyy, HH:mm" />
 by <c:out value="${ssDefinitionEntry.creation.principal.title}"/>
</span>
<br>
<br>
