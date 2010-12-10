<%@ tag isELIgnored="false" body-content="empty"  %><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><c:set var="ss_color_theme" value="icib" scope="request"/><%--
--%><c:if test="${!empty ssCssTheme}"><%--
    --%><c:set var="ss_color_theme" value="${ssCssTheme}" scope="request"/><%--
--%></c:if><%--
--%><c:if test="${empty ssCssTheme && !empty ssUser.theme}"><%--
    --%><c:set var="ss_color_theme" value="${ssUser.theme}" scope="request"/><%--
--%></c:if><%--
--%>${pageContext.request.contextPath}/i/${ss_color_theme}/