<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


You have the following tasks to do...<br/><br/>

<c:forEach var="task" items="${taskList}">
  <a href='${task.href}'
    onClick="window.open(this.href,'_blank','width=600,height=500,directories=no,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no');return false;"
  >${task.title}</a><br/>
</c:forEach>
