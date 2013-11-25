<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%><%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/css" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
body, td, pre, .mceContentBody {
  font-family: Arial, sans-serif;
  font-size: 13px;
  color:#000;
  margin:8px;
  background:#FFF;
}

p { margin:8px 0px 8px 0px; }
table tr td { border: 1px solid #BBBBBB; }

ul li {
    margin: 2px 0px 0px 0px;
    list-style-type: disc;
    list-style-position: outside;
    list-style-image: none;
}

ol li {
    margin: 2px 0px 0px 0px;
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

/* styles used in the editor for wikilinks */
a.ss_icecore_link {
  	color: #666666 !important;
  	text-decoration:underline;
	}
a:hover.ss_icecore_link {
  	color: #AFC8E3 !important;
  	text-decoration:underline;
}

