<% // htmlarea editor %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.lang.String" %>
<script type="text/javascript">dojo.require("dojo.widget.Editor2");</script>
<div style="border:1px solid black; 
<c:if test="${!empty element_color}">
background-color:${element_color};
</c:if>
<c:if test="${!empty element_height}">
height:${element_height};
</c:if>
">
<textarea dojoType="Editor2" 
  items="textGroup;|;colorGroup;|;listGroup;|;indentGroup;|;justifyGroup;"
<c:if test="${!empty element_color}">
  style="background-color:${element_color};"
</c:if>
  id="${element_id}" name="${element_id}" ><c:out value="${init_text}"/></textarea>
</div>

