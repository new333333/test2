<%@ include file="/WEB-INF/jsp/common.jsp" %>
<c:set var="ss_portalUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}"/>
<c:set var="ss_portalSignalUrl" value="${ss_portalUrl}/signal.html"/>

<script type="text/javascript">
//Random number seed (for building urls that are unique)
var ssf_now = new Date();
var ssf_random = Math.round(Math.random()*ssf_now.getTime());

function ss_commFrameLoaded(obj) {
	return;
	var url = "${ssTeamingUrl}/ssf/a/do?p_name=ss_forum&p_action=1&action=__ajax_request&operation=get_window_height&random=" + ssf_random++;
	var script = document.createElement("script");
	script.src = url;
	document.getElementsByTagName("head")[0].appendChild(script);
}
function ssf_setFrameHeight(height) {
	if (typeof height == "undefined") return;
	var obj = document.getElementById('ss_workareaIframe${renderResponse.namespace}')
	obj.style.height = parseInt(height + 50) + "px";
}

var ss_workareaIframeOffset = 50;
function ss_setWorkareaIframeSize${renderResponse.namespace}() {
	//If possible, try to directly set the size of the iframe
	//This may fail if the iframe is showing something in another domain
	//If so, the alternate method (via ss_communicationFrame) is used to set the window height
	try {
		var iframeDiv = document.getElementById('ss_workareaIframe${renderResponse.namespace}')
		if (window.frames['ss_workareaIframe${renderResponse.namespace}'] != null) {
			eval("var iframeHeight = parseInt(window.ss_workareaIframe${renderResponse.namespace}" + ".document.body.scrollHeight);")
			if (iframeHeight > 100) {
				iframeDiv.style.height = iframeHeight + ss_workareaIframeOffset + "px"
			}
		}
	} catch(e) {
		//Try it the slower way
		commObj = document.getElementById('ss_communicationFrame${renderResponse.namespace}');
		commObj.src = "${ssTeamingUrl}/ssf/a/do?p_name=ss_forum&p_action=0&action=__ajax_request&operation=set_portal_signal_url&operation2=${ss_portalSignalUrl}";
	}
}

function ss_setTeamingIframeHeight${renderResponse.namespace}(height) {
	var iframeObj = document.getElementById('ss_workareaIframe${renderResponse.namespace}');
	iframeObj.style.height = parseInt(parseInt(height) + parseInt(ss_workareaIframeOffset)) + "px";
}

//If this is the first definition of ss_setWorkareaIframeSize, remember its name in case we need to find it later
if (typeof ss_setWorkareaIframeSize == "undefined") 
	var ss_setWorkareaIframeSize = ss_setWorkareaIframeSize${renderResponse.namespace};

var ss_portal_view_normal_url${renderResponse.namespace} = '<portlet:renderURL windowState="normal"/>';
var ss_portal_view_maximized_url${renderResponse.namespace} = '<portlet:renderURL windowState="maximized"/>';
var ss_portal_view_window_state${renderResponse.namespace} = "${ss_windowState}"
</script>
<iframe id="ss_workareaIframe${renderResponse.namespace}" 
    name="ss_workareaIframe${renderResponse.namespace}" 
    style="width:100%; height:400px; display:block; position:relative;"
	src="${ssTeamingUrl}?portalSignalUrl=${ss_portalSignalUrl}" 
	onLoad="ss_setWorkareaIframeSize${renderResponse.namespace}();"
	frameBorder="0" >xxx</iframe>
<div style="display:none;">
<iframe id="ss_communicationFrame${renderResponse.namespace}" 
  name="ss_communicationFrame${renderResponse.namespace}" 
  onload="ss_commFrameLoaded(this);" frameBorder="0"
  src="${ssTeamingUrl}/ssf/js/forum/null.html"
>xxx</iframe>
</div>

<!-- portlet iframe div -->
<% // @ include file="/WEB-INF/jsp/entry/view_iframe_div.jsp" %>
<!-- portlet iframe div -->	
