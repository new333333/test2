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
%>

<% /* Which skin are we using? */ %>
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%
	String smartGwtSkin = SPropsUtil.getString("ssf.gwt.skin", "Teaming");
%>

<meta name="gwt:property" content="locale=<%= NLT.get( "Teaming.Lang" ) %>">


<% /* Tell SmartGWT where to find its stuff. */ %>
<script type="text/javascript">
	<% /* When we start using SmartGWT, uncomment the following 2 lines. */ %>
	// var isomorphicDir="<html:rootPath />js/gwt/gwtteaming/sc/";
	// var currentSkin= "<%= smartGwtSkin %>";
</script>

<% /* When we start using SmartGWT uncomment the following 6 lines. */ %>
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/initsc.js"                ></script> -->
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/modules/ISC_Core.js"      ></script> -->
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/modules/ISC_Foundation.js"></script> -->
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/modules/ISC_Containers.js"></script> -->
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/modules/ISC_Forms.js"     ></script> -->
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/modules/ISC_Grids.js"     ></script> -->

<% /* Optional SmartGWT components, uncomment as needed. */ %>
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/modules/ISC_Calendar.js"      ></script> -->
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/modules/ISC_DataBinding.js"   ></script> -->
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/modules/ISC_EBay.js"          ></script> -->
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/modules/ISC_History.js"       ></script> -->
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/modules/ISC_Kapow.js"         ></script> -->
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/modules/ISC_RichTextEditor.js"></script> -->
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/modules/ISC_SalesForce.js"    ></script> -->

<% /* When we start using SmartGWT uncomment the following line. */ %>
<!-- <script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/sc/skins/<%= smartGwtSkin %>/load_skin.js"></script> -->
