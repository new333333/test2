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

var ss_ostatus_none = ' <ssf:nlt tag="presence.none"/>'
var ss_ostatus_away = ' <ssf:nlt tag="presence.isAway" text="is away"/>';
var ss_ostatus_online = ' <ssf:nlt tag="presence.isOnline" text="is online"/>';
var ss_ostatus_offline = ' <ssf:nlt tag="presence.isOffline" text="is offline"/>';
var ss_ostatus_at = '<ssf:nlt tag="presence.statusAt" text="status at"/>';
var ss_ostatus_sendIm = '<ssf:nlt tag="presence.sendIM" text="Send instant message..."/>';
var ss_ostatus_startIm = '<ssf:nlt tag="presence.startIM" text="Start instant meeting..."/>';
var ss_ostatus_schedIm = '<ssf:nlt tag="presence.scheduleMeeting" text="Schedule a meeting..."/>';
var ss_ostatus_call = '<ssf:nlt tag="presence.call" text="Call..."/>';
var ss_ostatus_sendMail = '<ssf:nlt tag="presence.sendMail" text="Send mail"/>';
var ss_ostatus_outlook = '<ssf:nlt tag="presence.addToOutlook" text="Add to Outlook contacts..."/>';

</script>
<c:set var="ss_presence_support_loaded" value="1" scope="request"/>
</c:if>
<script type="text/javascript">

function ss_popupPresenceMenu${ssDashboardId}(x, userId, userTitle, status, screenName, sweepTime, email, vcard, current) {
	var ssDashboardId = '${ssDashboardId}';
	var ssPresenceZonBridge = '${ss_presence_zonBridge}';
	ss_popupPresenceMenu_common(x, userId, userTitle, status, screenName, sweepTime, email, vcard, current, ssDashboardId, ssPresenceZonBridge);
}
</script>
<div id="ss_presencePopUp${ssDashboardId}" style="position:absolute; visibility:hidden;"></div>
