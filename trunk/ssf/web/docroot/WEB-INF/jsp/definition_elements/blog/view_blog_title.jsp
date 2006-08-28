<% //Blog title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="title_entry" value="${ssDefinitionEntry}"/>
<jsp:useBean id="title_entry" type="com.sitescape.ef.domain.FolderEntry" />

<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<%
	if (!ssSeenMap.checkIfSeen(title_entry)) {
		ssSeenMap.setSeen(title_entry);
		%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
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
<span class="ss_bold <%= fontSize %>">
 <a style="text-decoration: none;" href="<ssf:url 
    folderId="${ssDefinitionEntry.parentFolder.id}" 
    action="view_folder_entry"
    entryId="${ssDefinitionEntry.id}"/>">
<%= docNumber %>
<c:if test="${empty ssDefinitionEntry.title}">
  <span class="ss_light">
    --<ssf:nlt tag="entry.noTitle"/>--
  </span>
</c:if>
<c:out value="${ssDefinitionEntry.title}"/></a>
</span>
</div>
<%@ include file="/WEB-INF/jsp/definition_elements/popular_view.jsp" %>
<br>
<span class="ss_italic ss_smallprint">
<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${ssDefinitionEntry.creation.date}" type="both" 
	 timeStyle="short" dateStyle="medium" />
 by <c:out value="${ssDefinitionEntry.creation.principal.title}"/>
</span>
<br>
<br>
