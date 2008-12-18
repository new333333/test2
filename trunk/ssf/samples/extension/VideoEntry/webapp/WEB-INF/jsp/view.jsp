<!--<?xml version="1.0" encoding="utf-8" ?>-->
<%@ page isELIgnored="false" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<div class="content">
  <div class="hd" />
  <div class="bd">
    <div id="player">
	<object height="290" width="400"
		type="application/x-shockwave-flash" data="<ssf:extensionUrl url='swf/FlowPlayerDark.swf'/>">
	    <param name="flashVars" value="config={'autoPlay':false,'initialScale':'scale','videoFile':'<ssf:fileUrl entity='${ssDefinitionEntry}'/>'}"/>
	    <img alt="Sorry, Adobe Flash Player is required to view videos" src="<ssf:extensionUrl url='img/no-flash.png'/>" />
	</object>
    </div>
  </div>
  <div class="ft">
  	<style type="text/css">
  		.bd #player {
  			background-color: red;
  		}
  	</style>
  </div>
</div>
