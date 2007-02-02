<% //View the listing part of a guestbook folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/guestbook/guestbook_sign.jsp" %>

<table class="ss_blog" width="100%">

	<c:forEach var="entry" items="${ssFolderEntries}" >
		<jsp:useBean id="entry" type="java.util.HashMap" />
		
		<tr>
			<td class="ss_miniBusinessCard">
				<ssf:miniBusinessCard user="<%=(User)entry.get("_principal")%>"/> 
			</td>
			<td class="ss_guestbookContainer">
				<span class="ss_entryTitle"><c:out value="${entry.title}" escapeXml="false"/></span>
				<span class="ss_entrySignature"><fmt:formatDate timeZone="${fileEntry._principal.timeZone.ID}"
				      value="${entry._modificationDate}" type="both" 
					  timeStyle="short" dateStyle="short" /></span>
				
				<c:if test="${!empty entry._desc}">
				<div class="ss_entryContent ss_entryDescription">
					<span><c:out value="${entry._desc}" escapeXml="false"/></span>
				</div>
				</c:if>
			</td>
		</tr>
	</c:forEach>
</table>
