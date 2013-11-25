<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>

<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.credits") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

<script type="text/javascript">
	/**
	 * 
	 */
	function handleCloseBtn()
	{
	<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			// Tell the Teaming GWT ui to close the administration content panel.
			if ( window.parent.ss_closeAdministrationContentPanel ) {
				window.parent.ss_closeAdministrationContentPanel();
			} else {
				ss_cancelButtonCloseWindow();
			}

			return false;
	<% 	}
		else { %>
			ss_cancelButtonCloseWindow();
			return false;
	<%	} %>
	
}// end handleCloseBtn()

</script>

<div class="ss_pseudoPortal">

<ssf:form titleTag="administration.credits">

<style>
.ss_credits_title {
	padding-left:4px;
}
a:link {
	text-decoration:none !important;
}
a:hover {
	text-decoration:underline !important;
}
</style>

<div class="ss_style ss_portlet">
  <div class="ss_style">
  
  <div align="right">
	<form>
		<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="return handleCloseBtn();"/>
	</form>
  </div>

<% boolean openEdition = (!(org.kablink.teaming.util.ReleaseInfo.isLicenseRequiredEdition())); %>
<c:set var="openEdition" value="<%= !org.kablink.teaming.util.ReleaseInfo.isLicenseRequiredEdition() %>"/>
<% if (openEdition) { %>
	<c:set var="creditProduct" value="Kablink"/>
<% } else { %>
	<c:set var="creditProduct" value="Novell"/>
<% } %>
<span>
	<ssf:nlt tag="credits.broughtBy">
		<ssf:param name="value" value="${creditProduct}"/>
	</ssf:nlt>
</span>

<br/><br/>
<span>
	<ssf:nlt tag="credits.subjectTo"/> <ssf:nlt tag="credits.licenses"/>
</span>

<br/><br/>
<span>
	<ssf:nlt tag="credits.apache" />
</span>

<br/>
<br/><span>addressing</span>
<br/><span>Ant</span>
<br/><span>AntContrib</span>
<br/><span>Axis</span>
<br/><span>Commons</span>
<br/><span>cglib</span>
<br/><span>easyconf</span>
<br/><span>Ehcache</span>
<br/><span>ezMorph</span>
<br/><span>Google Web Toolkit</span>
<br/><span>Groovy</span>
<br/><span>Jackrabbit</span>
<br/><span>Jakarta Taglibs</span>
<br/><span>Jasypt</span>
<br/><span>JDOM</span>
<br/><span>Joda-Time</span>
<br/><span>JSon-lib</span>
<br/><span>JSTL</span>
<br/><span>Kaptcha</span>
<br/><span>Log4j</span>
<br/><span>Lucene</span>
<br/><span>MINA</span>
<br/><span>opensaml</span>
<br/><span>Oro</span>
<br/><span>POI</span>
<br/><span>Portal bridges</span>
<br/><span>Quartz</span>
<br/><span>Slide</span>
<br/><span>Spring</span>
<br/><span>Struts</span>
<br/><span>SubEthaSMTP</span>
<br/><span>Tomcat</span>
<br/><span>Velocity</span>
<br/><span>WSS$J</span>
<br/><span>Xalan</span>
<br/><span>Xerces</span>
<br/><span>xmlrpc</span>
<br/><span>xmlsec</span>

<br/><br/>
<span>
	<ssf:nlt tag="credits.gnu"/>
</span>

<br/>
<br/><span>bsh</span>
<br/><span>crypt</span>
<br/><span>Colt</span>
<br/><span>Dbunit</span>
<br/><span>Hibernate</span>
<br/><span>JBoss</span>
<br/><span>JBPM</span>
<br/><span>JTDS</span>
<br/><span>juniversalchardet</span>
<br/><span>Open Office Libraries</span>
<br/><span>Trove</span>

<br/><br/><br/>
<span>
	<ssf:nlt tag="credits.other"/>
</span>

<br/><br/><span class="ss_bold"><a href="http://www.antlr.org/license.html">antlr#</a></span>
<br/><br/><span class="ss_bold"><a href="http://aopalliance.sourceforge.net">aopalliance</a></span>
<br/><br/><span class="ss_bold"><a href="http://asm.ow2.org/license.html">ASM</a></span>
<br/><br/><span class="ss_bold"><a href="http://sourceforge.net/projects/backport-jsr166/">backport-concurrent.jar</a></span>
<br/><br/><span class="ss_bold"><a href="http://www.bouncycastle.org/licence.html">Bouncy Castle</a></span>
<br/><br/><span class="ss_bold"><a href="http://g.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html">concurrent library</a></span>
<br/><br/><span class="ss_bold"><a href="http://dojotoolkit.org/license">dojo</a></span>
<br/><br/><span class="ss_bold"><a href="http://dom4j.sourceforge.net/dom4j-1.6.1/license.html">dom4j</a></span>
<br/><br/><span class="ss_bold"><a href="http://easymock.org/License.html">EasyMock</a></span>
<br/><br/><span class="ss_bold"><a href="https://addons.mozilla.org/af/firefox/addon/firebug/license/1.5.0">Firebug</a></span>
<br/><br/><span class="ss_bold"><a href="http://m2.modularity.net.au/projects/ical4j/license.html">iCal4j</a></span>
<br/><br/><span class="ss_bold"><a href="http://www.xom.nu/lib/normalizer_license.html">icu4j</a></span>
<br/><br/><span class="ss_bold"><a href="http://glassfish.java.net/public/CDDL+GPL_1_1.html">JAXB</a></span>
<br/><br/><span class="ss_bold"><a href="http://jaxen.codehaus.org/license.html">Jaxen</a></span>
<br/><br/><span class="ss_bold"><a href="http://www.day.com/specs/jcr/2.0/license.html">JCR</a></span>
<br/><br/><span class="ss_bold"><a href="http://glassfish.java.net/public/CDDL+GPL_1_1.html">Jersey</a></span>
<br/><br/><span class="ss_bold"><a href="http://jtidy.sourceforge.net/license.html">JTidy</a></span>
<br/><br/><span class="ss_bold"><a href="http://sourceforge.net/projects/jung/">jung#</a></span>
<br/><br/><span class="ss_bold"><a href="http://contraintes.inria.fr/OADymPPaC/sourceforge/cvs/tra4cp/src/cp-infovis/license-junit.html">Junit</a></span>
<br/><br/><span class="ss_bold"><a href="http://jquery.org/license/">jQuery</a></span>
<br/><br/><span class="ss_bold"><a href="http://www.installjammer.com/docs/">locateJavaRuntime.tcl</a></span>
<br/><br/><span class="ss_bold"><a href="http://www.freebsd.org/cgi/cvsweb.cgi/~checkout~/src/lib/libcrypt/crypt.c?rev=1.2">md5crypt</a></span>
<br/><br/><span class="ss_bold"><a href="http://www.gnu.org/copyleft/gpl.html">MySQL Connector/J</a></span>
<br/><br/><span class="ss_bold"><a href="https://glassfish.dev.java.net/public/CDDL+GPL.html">SAAJ</a></span>
<br/><br/><span class="ss_bold"><a href="http://sourceforge.net/projects/saxpath/files/">saxpath</a></span>
<br/><br/><span class="ss_bold"><a href="http://snowball.tartarus.org/license.php">Snowball</a></span>
<br/><br/><span class="ss_bold"><a href="http://www.slf4j.org/license.html">SLF4J</a></span>
<br/><br/><span class="ss_bold"><a href="http://www.opensource.org/licenses/mit-license.php">swfobject</a></span>
<br/><br/><span class="ss_bold"><a href="http://www.tcl.tk/software/tcltk/license.html">tclkit</a></span>
<br/><br/><span class="ss_bold"><a href="http://www.tcl.tk/software/tcltk/license.html">tcllib</a></span>
<br/><br/><span class="ss_bold"><a href="http://www.eclipse.org/legal/cpl-v10.html">wsdl4j</a></span>
<br/><br/><span class="ss_bold"><a href="http://xstream.codehaus.org/license.html">xstream</a></span>


</ssf:form>
</div>
</body>
</html>
