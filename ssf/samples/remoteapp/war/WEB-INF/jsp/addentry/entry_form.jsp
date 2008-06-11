<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<form method="post" action="/remoteapp/addentry/submit">
Title:
<input type="text" name="title"/>

<br/>
<br/>

Description:
<textarea name="description"></textarea>

<br/>
<input type="submit" name="okBtn" value="OK"/>

<input type="hidden" name="ss_access_token" value="${ss_access_token}"/>
<input type="hidden" name="binderId" value="${binderId}"/>
<input type="hidden" name="definitionId" value="${definitionId}"/>

</form>
