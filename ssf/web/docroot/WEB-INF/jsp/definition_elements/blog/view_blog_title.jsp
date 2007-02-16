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
 <ssf:nlt tag="general.title.timestamp.by"/> <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    action="view_permalink"
	    binderId="${ssDefinitionEntry.creation.principal.parentBinder.id}"
	    entryId="${ssDefinitionEntry.creation.principal.id}">
	    <ssf:param name="entityType" value="workspace" />
	    <ssf:param name="newTab" value="1" />
		</ssf:url>"><c:out value="${ssDefinitionEntry.creation.principal.title}"/></a>
</div>
<div class="ss_header_bar_burst"><img src="<html:imagesPath/>icons/new_burst.gif"></div>
<%
	if (!ssSeenMap.checkIfSeen(title_entry)) {
		ssSeenMap.setSeen(title_entry);
		%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%>
<div class="ss_header_bar_title_text">
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
</div>
<%-- Subscribe, Ratings bar, visits --%>
<c:if test="${empty ssDefinitionEntry.parentEntry}">
<div style="height: 20px;">
<div style="float:right; padding-top: 2px;">
<a href="javascript: ;" 
  onClick="return false;"
><div class="ss_iconed_label ss_send_friend"><ssf:nlt tag="entry.sendtofriend"/></div></a>
<a href="javascript: ;" 
  onClick="return false;"
><div class="ss_iconed_label ss_subscribe"><ssf:nlt tag="entry.subscribe"/></div></a>
</div>
</div>
<div>
<%@ include file="/WEB-INF/jsp/definition_elements/popular_view.jsp" %>
</div>
</c:if>
