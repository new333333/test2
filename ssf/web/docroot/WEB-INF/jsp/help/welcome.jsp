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

<h1><ssf:nlt tag="welcome.header"/></h1>

<a href="<html:rootPath/>help/${ssUser.locale.language}/welcome.html" 
  onClick="javascript:return ss_openUrlInWindow(this,'help')"><ssf:nlt tag="welcome.learnMore"/></a>

