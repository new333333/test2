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

function popupPresenceMenu(x, userId, userTitle, status, screenName, sweepTime, email, vcard, current) {
    var obj
    var m = ''
    var imgid = "ppgpres"
    var ostatus = " <ssf:nlt tag="presence.none"/>"
    obj = self.document.getElementById('ss_presencePopUp')
    m += '<div style="position: relative; background: #666; margin: 4px;">'
    m += '<div style="position: relative; left: -2px; top: -2px; border-top-width:1; border: 1px solid #666666; background-color:white">'

    m += '<table class="ss_style ss_graymenu" border="0" cellspacing="0" cellpadding="3">';
    m += '<tr>';
    if (status >= 0) {
        if (status & 1) {
            if (status & 16) {
                ostatus = ' <ssf:nlt tag="presence.isAway" text="is away"/>';
                imgid = "ppgpresaway"
            } else {
                ostatus = ' <ssf:nlt tag="presence.isOnline" text="is online"/>';
                imgid = "ppgpreson"
            }
        } else {
            ostatus = ' <ssf:nlt tag="presence.isOffline" text="is offline"/>';
            imgid = "ppgpresoff"
        }
    }
    m += '<td class="ss_bglightgray" valign=top><img src="" alt="" id=' +imgid +'></td>';
    m += '<td><span>' + userTitle;
    m += ostatus;
    if (status >= 0) {
        m += '</span><br><span class="ss_fineprint ss_gray">(<ssf:nlt tag="presence.statusAt" text="status at"/> ' + sweepTime + ')</span>';
    }
    m += '</td></tr>';
    if (screenName != '') {
        if (current == '') {
            m += '<tr>';
            m += '<td class="ss_bglightgray"><img alt="" src="" id="ppgimsg"></td>';
            if (status == 0) {
                m += '<td class="ss_fineprint ss_gray"><ssf:nlt tag="presence.sendIM" text="Send instant message..."/></td>';
            } else {
                m += '<td><a class="ss_graymenu" href="iic:im?screenName=' + screenName + '"><ssf:nlt tag="presence.sendIM" text="Send instant message..."/></a></td>';
            }
            m += '</tr>';
        }
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img alt="" src="" id="ppgimtg"></td>';
        m += '<td><a class="ss_graymenu" href="iic:meetone?screenName=' + screenName + '"><ssf:nlt tag="presence.startIM" text="Start instant meeting..."/></a></td></tr>';
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img alt="" src="" id="ppgsched"></td>';
        m += '<td><a class="ss_graymenu" href="javascript:quickMeetingRPC(\'??? addMeeting schedule\',\'' + userId + '\', \'\', \'\', \'\');"><ssf:nlt tag="presence.scheduleMeeting" text="Schedule a meeting..."/></a></td></tr>';
        m += '<tr>';
<c:if test="${ss_presence_zonBridge == 'enabled'}">
        m += '<td class="ss_bglightgray"><img alt="" src="" id="ppgphone"></td>';
        m += '<td><a class="ss_graymenu" href="javascript:quickMeetingRPC(\'??? addMeeting call\',\'' + userId + '\', \'\', \'\', \'\');"><ssf:nlt tag="presence.call" text="Call..."/></a></td></tr>';
</c:if>
	}
	if (userId != '' && current == '') {
        if (email != '') {
            m += '<tr>';
            m += '<td class="ss_bglightgray"><img alt="" src="" id="ppgmail"></td>';
            bodyText = escape(window.location.href);
            m += '<td><a class="ss_graymenu" href="mailto:' + email + '?body=' + bodyText +'"><ssf:nlt tag="presence.sendMail" text="Send mail"/> (' + email + ')...</a></td></tr>';
        }
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img alt="" src="" id="ppgvcard"></td>';
        m += '<td><a class="ss_graymenu" href="' + vcard + '"><ssf:nlt tag="presence.addToOutlook" text="Add to Outlook contacts..."/></a></td></tr>';
    }
    m += '</table>'

    m += '</div>'
    m += '</div>'

    obj.innerHTML = m;

    ss_activateMenuLayer('ss_presencePopUp');
    if (self.document.images["ppgpres"]) {
        self.document.images["ppgpres"].src = ss_presencePopupGraphics["pres"].src;
    }
    if (self.document.images["ppgpreson"]) {
        self.document.images["ppgpreson"].src = ss_presencePopupGraphics["preson"].src;
    }
    if (self.document.images["ppgpresoff"]) {
        self.document.images["ppgpresoff"].src = ss_presencePopupGraphics["presoff"].src;
    }
    if (self.document.images["ppgpresaway"]) {
        self.document.images["ppgpresaway"].src = ss_presencePopupGraphics["presaway"].src;
    }
    if (self.document.images["ppgimsg"]) {
        self.document.images["ppgimsg"].src = ss_presencePopupGraphics["imsg"].src;
    }
    if (self.document.images["ppgimtg"]) {
        self.document.images["ppgimtg"].src = ss_presencePopupGraphics["imtg"].src;
    }
    if (self.document.images["ppgmail"]) {
        self.document.images["ppgmail"].src = ss_presencePopupGraphics["mail"].src;
    }
    if (self.document.images["ppgvcard"]) {
        self.document.images["ppgvcard"].src = ss_presencePopupGraphics["vcard"].src;
    }
    if (self.document.images["ppgphone"]) {
        self.document.images["ppgphone"].src = ss_presencePopupGraphics["phone"].src;
    }
    if (self.document.images["ppgsched"]) {
        self.document.images["ppgsched"].src = ss_presencePopupGraphics["sched"].src;
    }
    // move the div up if it scrolls off the bottom
    var mousePosX = parseInt(ss_getClickPositionX());
    var mousePosY = parseInt(ss_getClickPositionY());
    if (mousePosY != 0) {
        var divHt = ss_getObjectHeight(obj);
        var windowHt = parseInt(ss_getWindowHeight());
        var scrollHt = self.document.body.scrollTop;
        var diff = scrollHt + windowHt - mousePosY;
        if (divHt > 0) {
            if (diff <= divHt) {
               ss_positionDiv('ss_presencePopUp', mousePosX, mousePosY - divHt);
            }
        }
        //See if we need to make the portlet longer to hold the pop-up menu
        var menuObj = document.getElementById('ss_presencePopUp');
        var sizerObj = document.getElementById('ss_presence_sizer_div');
        if (sizerObj) {
        	var menuTop = ss_getDivTop('ss_presencePopUp');
        	var menuHeight = ss_getDivHeight('ss_presencePopUp');
        	var sizerTop = ss_getDivTop('ss_presence_sizer_div');
        	var sizerHeight = ss_getDivHeight('ss_presence_sizer_div');
        	var deltaSizerHeight = parseInt((menuTop + menuHeight) - (sizerTop + sizerHeight));
        	if (deltaSizerHeight > 0) {
        		ss_setObjectHeight(sizerObj, parseInt(sizerHeight + deltaSizerHeight));
        	}
        }
    }
}
</script>
<div id="ss_presencePopUp" style="position:absolute; visibility:hidden; z-index:500;"></div>
<c:set var="ss_presence_support_loaded" value="1" scope="request"/>
</c:if>
