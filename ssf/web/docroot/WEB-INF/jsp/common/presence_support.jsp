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
<% //Support routines for showing presence pop-ups %>

<c:if test="${empty ss_presence_support_loaded}">
<script type="text/javascript">
// Presence popup support
var ss_presencePopupGraphics = new Array();
ss_presencePopupGraphics["pres"] = new Image();
ss_presencePopupGraphics["pres"].src = '<html:imagesPath/>pics/sym_m_white_dude.gif';
ss_presencePopupGraphics["preson"] = new Image();
ss_presencePopupGraphics["preson"].src = '<html:imagesPath/>pics/sym_m_green_dude.gif';
ss_presencePopupGraphics["presoff"] = new Image();
ss_presencePopupGraphics["presoff"].src = '<html:imagesPath/>pics/sym_m_gray_dude.gif';
ss_presencePopupGraphics["presaway"] = new Image();
ss_presencePopupGraphics["presaway"].src = '<html:imagesPath/>pics/sym_m_yellow_dude.gif';
ss_presencePopupGraphics["imsg"] = new Image();
ss_presencePopupGraphics["imsg"].src = '<html:imagesPath/>pics/sym_s_message.gif';
ss_presencePopupGraphics["imtg"] = new Image();
ss_presencePopupGraphics["imtg"].src = '<html:brandedImagesPath/>pics/sym_s_imeeting.gif';
ss_presencePopupGraphics["mail"] = new Image();
ss_presencePopupGraphics["mail"].src = '<html:imagesPath/>pics/sym_s_sendmail.gif';
ss_presencePopupGraphics["vcard"] = new Image();
ss_presencePopupGraphics["vcard"].src = '<html:imagesPath/>pics/sym_s_outlook.gif';
ss_presencePopupGraphics["phone"] = new Image();
ss_presencePopupGraphics["phone"].src = '<html:imagesPath/>pics/sym_s_gray_phone.gif';
ss_presencePopupGraphics["sched"] = new Image();
ss_presencePopupGraphics["sched"].src = '<html:imagesPath/>pics/sym_s_sched.gif';
ss_presencePopupGraphics["clipboard"] = new Image();
ss_presencePopupGraphics["clipboard"].src = '<html:imagesPath/>pics/sym_s_clipboard.gif';
ss_presencePopupGraphics["skype"] = new Image();
ss_presencePopupGraphics["skype"].src = '<html:imagesPath/>pics/SkypeBlue_16x16.png';


var ss_ostatus_none = ' <ssf:nlt tag="presence.none"/>'
var ss_ostatus_away = ' <ssf:nlt tag="presence.isAway" text="is away"/>';
var ss_ostatus_online = ' <ssf:nlt tag="presence.isOnline" text="is online"/>';
var ss_ostatus_offline = ' <ssf:nlt tag="presence.isOffline" text="is offline"/>';
var ss_ostatus_at = '<ssf:nlt tag="presence.statusAt" text="status at"/>';
var ss_ostatus_sendIm = '<ssf:nlt tag="presence.sendIM" text="Send instant message..."/>';
var ss_ostatus_startIm = '<ssf:nlt tag="presence.startIM" text="Start instant meeting..."/>';
var ss_ostatus_schedIm = '<ssf:nlt tag="presence.scheduleMeeting" text="Schedule a meeting..."/>';
var ss_ostatus_call = '<ssf:nlt tag="presence.call" text="Call..."/>';

var ss_pagePermalink = "${ssPermalink}"
var ss_ostatus_sendMail = '<ssf:nlt tag="presence.sendMail" text="Send mail"/>';

var ss_ostatus_outlook = '<ssf:nlt tag="presence.addToOutlook" text="Add to Outlook contacts..."/>';
var ss_ostatus_clipboard = '<ssf:nlt tag="presence.addToClipboard" text="Add to clipboard"/>';

var ss_ostatus_skype = '<ssf:nlt tag="presence.callUsingSkype" text="Call using Skype"/>';

</script>
<c:set var="ss_presence_support_loaded" value="1" scope="request"/>
</c:if>

