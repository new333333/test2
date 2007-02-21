<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<taconite-root xml:space="preserve">
		
	<taconite-replace contextNodeID="${ss_divId}" parseInBrowser="true">
		<c:forEach var="user" items="${ssClipboardPrincipals}">		
			<li id="ss_findUser_id_<c:out value="${user.id}"/>" >
			<% // TODO: escape javascript test %>
			<a onClick="${clickRoutine}(${user.id}, '${user.title}');" 
			    href="#"><span style="white-space: nowrap;"><c:out value="${user.title}"/></span></a>
	        </li>
		</c:forEach>
	</taconite-replace>
	
</taconite-root>