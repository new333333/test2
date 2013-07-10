<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
%>
#ss_related_files {
	position:absolute; 
	z-index: 500;
}

.ss_related_div {
	border: 1px #135c8f solid;
	position: relative;
	font-weight: normal;
	top: 0px;
	right: 0px;
	padding: 3px;
	background-color: #f6f6f6;
}
	
.ss_related_head img {
	position:absolute; 
	top: 5px;
	right: 8px;
}
.ss_related_head {
	color: #333;
	font-size: .7em;
	text-transform: uppercase;
	padding: .5em;
	background: #ffe085;
}
.ss_related_item {
	font-size: .85em;
	white-space: nowrap;
	padding: 3px 0;
	margin-left: .75em;
	overflow:hidden; 
}

.ss_related_anchor {
	color: #3e6978 !important;
	text-decoration: none !important;
	outline: none !important;
}
