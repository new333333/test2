<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% // Rounded box %>
<%@ include file="/WEB-INF/jsp/box/init.jsp" %>

<c:set var="boxClass" value="<%= ParamUtil.get(request, "class", "ss_content_window") %>" />
<c:set var="backgroundClass" value="<%= ParamUtil.get(request, "backgroundClass", "") %>" />

<div class="ss_decor-round-corners-top2 ss_innerContentBegins ${backgroundClass}" ><div><div>
</div></div></div>
<div class="ss_decor-border5 ${backgroundClass}">
  <div class="ss_decor-border6 ${backgroundClass}">
    <div class="${boxClass} ${backgroundClass}">
