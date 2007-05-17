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
%><%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/css" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
body {
  font-family: Lucida Sans Unicode,Arial,sans-serif;
  font-size: 12px;
}
 
table tr td { border: 1px solid #BBBBBB; }

.ss_largestprint {
font-size:15px !important;
}
.ss_largerprint {
font-size:14px !important;
}
.ss_largeprint {
font-size:13px !important;
}
.ss_normalprint {
font-size:12px !important;
}
.ss_smallprint {
font-size:11px !important;
}
.ss_fineprint {
font-size:10px !important;
}
.ss_finestprint {
font-size:9px !important;
}
