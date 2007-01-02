<% // Rounded box %>
<%@ include file="/WEB-INF/jsp/box/init.jsp" %>

<c:set var="boxClass" value="<%= ParamUtil.get(request, "class", "ss_content_window") %>" />
<c:set var="backgroundClass" value="<%= ParamUtil.get(request, "backgroundClass", "") %>" />

<div class="ss_decor-round-corners-top2 ss_innerContentBegins ${backgroundClass}" ><div><div>
</div></div></div>
<div class="ss_decor-border5 ${backgroundClass}">
  <div class="ss_decor-border6 ${backgroundClass}">
    <div class="${boxClass} ${backgroundClass}">
