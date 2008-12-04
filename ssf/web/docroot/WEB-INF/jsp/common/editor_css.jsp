<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%><%@ page import="org.kablink.util.BrowserSniffer" %>
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
    margin: 2px 0px 0px 15px;
    list-style-type: square;
    list-style-position: outside;
    list-style-image: none;
}

ol li {
    margin: 2px 0px 0px 15px;
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
.ss_size_18px { font-size: 18px !important;}
.ss_size_20px { font-size: 20px !important;}
.ss_size_24px { font-size: 24px !important;}
.ss_size_28px { font-size: 28px !important;}
.ss_size_32px { font-size: 32px !important;}
