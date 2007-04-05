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
ss_presencePopupGraphics["imtg"].src = '<html:imagesPath/>pics/sym_s_imeeting.gif';
ss_presencePopupGraphics["mail"] = new Image();
ss_presencePopupGraphics["mail"].src = '<html:imagesPath/>pics/sym_s_sendmail.gif';
ss_presencePopupGraphics["vcard"] = new Image();
ss_presencePopupGraphics["vcard"].src = '<html:imagesPath/>pics/sym_s_outlook.gif';
ss_presencePopupGraphics["phone"] = new Image();
ss_presencePopupGraphics["phone"].src = '<html:imagesPath/>pics/sym_s_gray_phone.gif';
ss_presencePopupGraphics["sched"] = new Image();
ss_presencePopupGraphics["sched"].src = '<html:imagesPath/>pics/sym_s_sched.gif';
ss_presencePopupGraphics["clipboard"] = new Image();
ss_presencePopupGraphics["clipboard"].src = '<html:imagesPath/>icons/liveclipboard-icon-16x16.jpg';

var ss_ostatus_none = ' <ssf:nlt tag="presence.none"/>'
var ss_ostatus_away = ' <ssf:nlt tag="presence.isAway" text="is away"/>';
var ss_ostatus_online = ' <ssf:nlt tag="presence.isOnline" text="is online"/>';
var ss_ostatus_offline = ' <ssf:nlt tag="presence.isOffline" text="is offline"/>';
var ss_ostatus_at = '<ssf:nlt tag="presence.statusAt" text="status at"/>';
var ss_ostatus_sendIm = '<ssf:nlt tag="presence.sendIM" text="Send instant message..."/>';
var ss_ostatus_start_meeting_url = '<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true" ><ssf:param name="operation" value="start_meeting" /></ssf:url>';
var ss_ostatus_schedule_meeting_url = '<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true" ><ssf:param name="operation" value="schedule_meeting" /></ssf:url>';
var ss_ostatus_startIm = '<ssf:nlt tag="presence.startIM" text="Start instant meeting..."/>';
var ss_ostatus_schedIm = '<ssf:nlt tag="presence.scheduleMeeting" text="Schedule a meeting..."/>';
var ss_ostatus_call = '<ssf:nlt tag="presence.call" text="Call..."/>';
var ss_ostatus_sendMail = '<ssf:nlt tag="presence.sendMail" text="Send mail"/>';
var ss_ostatus_outlook = '<ssf:nlt tag="presence.addToOutlook" text="Add to Outlook contacts..."/>';
var ss_ostatus_clipboard = '<ssf:nlt tag="presence.addToClipboard" text="Add to clipboard"/>';

</script>
<c:set var="ss_presence_support_loaded" value="1" scope="request"/>
</c:if>
<script type="text/javascript">


</script>

