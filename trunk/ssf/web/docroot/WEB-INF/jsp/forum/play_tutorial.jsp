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
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
try
{
%>
<html>
	<head>
		<title><ssf:nlt tag="playtutorial.title" /></title>

		<script type="text/javascript">
			/**
			 * This function will play the given tutorial.
			 */
			function playTutorial( tutorialName )
			{
				var	ctrl;

				ctrl = document.getElementById( 'moviePlayer' );
				ctrl.focus();
			}// end playTutorial()
		</script>
	</head>

	<body>
		<table width="100%" style="padding: 5px;">
			<tr>
				<td valign="middle" colspan="2" nowrap>
					<div style="border: solid thin #000000;">
						<span style="margin: 15px;"><ssf:nlt tag="playtutorial.heading" /></span>
					</div>
				</td>
			</tr>
			<tr>
				<td nowrap width="1%">
					<div>
						<span><ssf:nlt tag="tutorial.whatsTeaming" /></span>
					</div>
					<div>
						<span><ssf:nlt tag="tutorial.gettingAround" /></span>
					</div>
					<div>
						<span><ssf:nlt tag="tutorial.importingFiles" /></span>
					</div>
					<div>
						<span><ssf:nlt tag="tutorial.customization" /></span>
					</div>
					<div>
						<span><ssf:nlt tag="tutorial.bestPractices" /></span>
					</div>
				</td>
				<td width="*">
					<embed	id="moviePlayer"
							allowscriptaccess="always"
							allowfullscreen="true"
							quality="high"
							bgcolor="#000000"
							name="moviePlayer"
							style="" src="http://s.ytimg.com/yt/swf/watch-vfl71976.swf"
							type="application/x-shockwave-flash"/>
				</td>
			</tr>
		</table>
	</body>
</html>
<%
}
catch (Exception e)
{
	e.printStackTrace();
%>
	An exception occured processing play_tutorial.jsp.
<%
}// end catch
%>

						<% /* * * * * * * */ %>
						<% /* End of File */ %>
						<% /* * * * * * * */ %>

