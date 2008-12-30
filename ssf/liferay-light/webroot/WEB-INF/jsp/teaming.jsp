<%@ include file="/WEB-INF/jsp/common.jsp" %>

<script type="text/javascript">
var ss_workareaIframeOffset = 50;
function ss_setWorkareaIframeSize${renderResponse.namespace}() {
	return;
	var iframeDiv = document.getElementById('ss_workareaIframe${renderResponse.namespace}')
	if (window.frames['ss_workareaIframe${renderResponse.namespace}'] != null) {
		eval("var iframeHeight = parseInt(window.ss_workareaIframe${renderResponse.namespace}" + ".document.body.scrollHeight);")
		if (iframeHeight > 100) {
			iframeDiv.style.height = iframeHeight + ss_workareaIframeOffset + "px"
		}
	}
}
//ss_createOnResizeObj('ss_setWorkareaIframeSize${renderResponse.namespace}', ss_setWorkareaIframeSize${renderResponse.namespace});
//ss_createOnLayoutChangeObj('ss_setWorkareaIframeSize${renderResponse.namespace}', ss_setWorkareaIframeSize${renderResponse.namespace});

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
	src="${ssTeamingUrl}" 
	onLoad="ss_setWorkareaIframeSize${renderResponse.namespace}();" 
	frameBorder="0" >xxx</iframe>

<!-- portlet iframe div -->
<% // @ include file="/WEB-INF/jsp/entry/view_iframe_div.jsp" %>
<!-- portlet iframe div -->	
