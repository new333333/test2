<%@ tag isELIgnored="false" body-content="empty" dynamic-attributes="options" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="object" required="true"%>
<spring:bind path="${object}">
	<input type="text" name="${status.expression}"  value="${status.value}"
		<c:forEach items="${options}" var="option">
			${option.key}="${option.value}"
		</c:forEach>/>
	<span style="color:#A00000">${status.errorMessage}</span>
</spring:bind>