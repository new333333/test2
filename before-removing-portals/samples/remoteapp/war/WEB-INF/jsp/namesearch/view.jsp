<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<strong>Hey, pay attention everyone!</strong><br>
<pre>
About <c:out value="${count}"/> matches for <c:out value="${title}"/> on Google
</pre>