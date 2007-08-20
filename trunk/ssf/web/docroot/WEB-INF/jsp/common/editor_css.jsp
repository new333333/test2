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
  font-size: 13px;
}
 
table tr td { border: 1px solid #BBBBBB; }

ul li {
    margin: 2px 0px 0px 25px;
    list-style-type: square;
    list-style-position: outside;
    list-style-image: none;
}

ol li {
    margin: 2px 0px 0px 25px;
    list-style-position: outside;
    list-style-type: decimal;
}




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

.ss_size_8px  { font-size: 8px  !important;}
.ss_size_9px  { font-size: 9px  !important;}
.ss_size_10px { font-size: 10px !important;}
.ss_size_11px { font-size: 11px !important;}
.ss_size_12px { font-size: 12px !important;}
.ss_size_13px { font-size: 13px !important;}
.ss_size_14px { font-size: 14px !important;}
.ss_size_15px { font-size: 15px !important;}
.ss_size_16px { font-size: 16px !important;}
