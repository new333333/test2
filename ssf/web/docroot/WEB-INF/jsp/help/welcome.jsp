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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<script type="text/javascript" src="<html:rootPath/>js/common/common.js"></script>
<div class="ss_portlet_style ss_portlet">
<div class="ss_style" style="padding:4px;">
<h1><ssf:nlt tag="welcome.header"/></h1>

<ul style="list-style: outside;">
  <li style="list-style-type: disc;">
    <a href="<html:rootPath/>help/${ssUser.locale.language}/pdfs/ICEcore Quick Tips.pdf"
      onClick="javascript:return ss_openUrlInWindow(this,'help')">
      <span><ssf:nlt tag="welcome.quickTipsOnUsage"/></span></a>
  </li>
  <li style="list-style-type: disc;">
    <a href="<html:rootPath/>help/${ssUser.locale.language}/pdfs/ICEcore QuickStart User Guide.pdf"
      onClick="javascript:return ss_openUrlInWindow(this,'help')">
      <span><ssf:nlt tag="welcome.quickStartUserGuide"/></span></a>
  </li>
<c:if test="${ss_showInstallationGuide}">
  <li style="list-style-type: disc;">
    <a href="<html:rootPath/>help/${ssUser.locale.language}/pdfs/ICEcore Installation Guide.pdf"
      onClick="javascript:return ss_openUrlInWindow(this,'help')">
      <span><ssf:nlt tag="welcome.installationGuide"/></span></a>
  </li>
</c:if>
</ul>

<div align="right">
<a href="<html:rootPath/>help/${ssUser.locale.language}/welcome.html" 
  onClick="javascript:return ss_openUrlInWindow(this,'help')">[<ssf:nlt tag="general.more"/>]</a>
</div>

</div>
</div>

