<!--<?xml version="1.0" encoding="utf-8" ?>-->
<%@ taglib prefix="blink" uri="http://www.kablink.org/tags" %>
<div class="content">
  <div class="hd" />
  <div class="bd">
    <div id="player">
      <blink:url attr="data" url="swf/FlowPlayerDark.swf">
	<object height="290"
		width="400"
		type="application/x-shockwave-flash">
	  <blink:attachment attr="value"
			    prefix="config={'autoPlay':false,'initialScale':'scale','videoFile':'"
			    suffix="'}">
	    <param name="flashVars" />
	  </blink:attachment>
	  <blink:url attr="src" url="img/no-flash.png" >
	    <img alt="Sorry, Adobe Flash Player is required to view videos" />
	  </blink:url>
	</object>
      </blink:url>
    </div>
  </div>
  <div class="ft" />
</div>
