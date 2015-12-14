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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value="${ssEntry.title}" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>

<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${operation == 'view_photo_in_frame'}">
<script type="text/javascript">
function ss_setImgSize(obj) {
	var ww = ss_getWindowWidth();
	var wh = ss_getWindowHeight();
	var imgW = ss_getObjectWidth(obj);
	var imgH = ss_getObjectHeight(obj);
	var deltaW = parseInt(imgW - ww);
	var deltaH = parseInt(imgH - wh);
	if (deltaW > 0 && deltaW >= deltaH) {
		obj.width = parseInt(ww - 50);
	} else if (deltaH > 0 && deltaH >= deltaW) {
		obj.height = parseInt(wh - 50);
	}
}
</script>
<div id="ss_imageDiv" align="center">
</div>
<script type="text/javascript">
	var divObj = document.getElementById("ss_imageDiv");
	var imgObj = document.createElement("img");
	dojo.connect(imgObj, "onload", function(evt) {
			ss_setImgSize(this);
	    });
	divObj.appendChild(imgObj)
	var url = "<ssf:fileUrl entity="${ssEntry}"/>";
	if (url == "") {
		imgObj.src = "<html:imagesPath/>thumbnails/NoImage.jpeg";
	} else {
		imgObj.src = url;
	}
</script>
</c:if>

<c:if test="${operation != 'view_photo_in_frame'}">
<script type="text/javascript">
function ss_setImgSize(obj) {
	var titleDiv = document.getElementById("ss_photoTitleDiv");
	var ww = ss_getWindowWidth();
	var wh = ss_getWindowHeight();
	var imgW = ss_getObjectWidth(obj);
	var imgH = ss_getObjectHeight(obj);
	var deltaW = parseInt(imgW - ww);
	var deltaH = parseInt(imgH - wh);
	if (deltaW > 0 && deltaW >= deltaH) {
		obj.width = parseInt(ww - 50);
	} else if (deltaH > 0 && deltaH >= deltaW) {
		obj.height = parseInt(wh - ss_getObjectHeight(titleDiv) - 50);
	}
	obj.style.visibility = "visible";
}
</script>

<div class="ss_style ss_portlet_style ss_portlet">
  <div id="ss_photoTitleDiv" align="center">
    <table cellspacing="0" cellpadding="0">
      <tr>
        <td>
          <a href="<ssf:url crawlable="true"    
				    adapter="true" 
				    portletName="ss_forum" 
				    binderId="${ssBinder.id}" 
				    action="view_folder_entry" 
				    entryId="${ssEntry.id}" 
				    operation="view_photo"
				    actionUrl="true" ><ssf:param 
				    name="standalone" value="true"/><ssf:param 
				    name="operation2" value="view_previous"/></ssf:url>" 
			  title="<ssf:nlt tag="general.previousPicture"/>">
            <img border="0" alt="<ssf:nlt tag="general.previousPicture"/>"
              src="<html:rootPath/>images/pics/sym_arrow_left_.png"/>
          </a>
        </td>
        <td style="padding-left:20px;">
          <a href="<ssf:url crawlable="true"    
				    adapter="true" 
				    portletName="ss_forum" 
				    binderId="${ssBinder.id}" 
				    action="view_folder_entry" 
				    entryId="${ssEntry.id}" 
				    operation="view_photo"
				    actionUrl="true" ><ssf:param 
				    name="standalone" value="true"/><ssf:param 
				    name="operation2" value="view_next"/></ssf:url>" 
			  title="<ssf:nlt tag="general.nextPicture"/>">
            <img border="0" alt="<ssf:nlt tag="general.nextPicture"/>"
              src="<html:rootPath/>images/pics/sym_arrow_right_.png"/>
          </a>
        </td>
      </tr>
    </table>
    <br/>
    <a href="<ssf:url crawlable="true"    
				    adapter="true" 
				    portletName="ss_forum" 
				    binderId="${ssBinder.id}" 
				    action="view_folder_entry" 
				    entryId="${ssEntry.id}" 
				    actionUrl="true"><ssf:param 
				    name="entryViewStyle" value="full"/></ssf:url>">
      <span style="font-size:20px; font-weight:400; padding-bottom: 5px;" >${ssEntry.title}</span>
    </a>
  </div>

<div id="ss_imageDiv" align="center">
</div>
<script type="text/javascript">
	var divObj = document.getElementById("ss_imageDiv");
	var imgObj = document.createElement("img");
	imgObj.style.visibility = "hidden";
	dojo.connect(imgObj, "onload", function(evt) {
			ss_setImgSize(this);
	    });
	divObj.appendChild(imgObj)
	var url = "<ssf:fileUrl entity="${ssEntry}"/>";
	if (url == "") {
		imgObj.src = "<html:imagesPath/>thumbnails/NoImage.jpeg";
	} else {
		var pattern = new RegExp("\\.([a-zA-Z]*)$");
		var extArray = url.match(pattern);
		var ext = "";
		if (extArray != null && extArray.length >= 1) ext = extArray[1];
		ext = ext.toLowerCase();
		if (ext == 'jpg' || ext == 'jpeg' || ext == 'gif' || ext == 'png' || ext == 'bmp') {
			//This is an image, show it hon this page
			imgObj.src = url;
		} else {
			//We don't know what this is, show it in its own page
			self.location.href = url;
		}
	}
</script>
</div>

</c:if>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
