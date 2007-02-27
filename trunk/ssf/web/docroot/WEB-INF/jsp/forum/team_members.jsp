<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<taconite-root xml:space="preserve">

	<taconite-replace contextNodeID="${ss_divId}" parseInBrowser="true">
		<c:forEach var="user" items="${ssTeamMembers}">		
			<li id="ss_findUser_id_<c:out value="${user.id}"/>" >
			<a onClick="${clickRoutine}(<c:out value="${user.id}"/>, '<ssf:escapeJavaScript value="${user.title}"/>');" 
			    href="#"><span style="white-space: nowrap;"><c:out value="${user.title}"/></span></a>
	        </li>
		</c:forEach>
	</taconite-replace>	
	
</taconite-root>
