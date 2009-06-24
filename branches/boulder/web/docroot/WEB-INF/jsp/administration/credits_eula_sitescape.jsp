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
		<style>
		<!--
		 /* Font Definitions */
		 @font-face
			{font-family:Wingdings;
			panose-1:5 0 0 0 0 0 0 0 0 0;}
		@font-face
			{font-family:Tahoma;
			panose-1:2 11 6 4 3 5 4 4 2 4;}
		@font-face
			{font-family:"Century Schoolbook";
			panose-1:2 4 6 4 5 5 5 2 3 4;}
		@font-face
			{font-family:"Times New Roman Bold";
			panose-1:0 0 0 0 0 0 0 0 0 0;}
		 /* Style Definitions */
		 p.MsoNormal, li.MsoNormal, div.MsoNormal
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		h1
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-indent:-.5in;
			page-break-after:avoid;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		h2
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:1.0in;
			text-indent:-.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-weight:normal;}
		h3
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:1.5in;
			text-indent:-.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-weight:normal;}
		h4
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			layout-grid-mode:line;
			font-weight:normal;}
		h5
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:2.0in;
			text-indent:-.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			layout-grid-mode:line;
			font-weight:normal;}
		h6
			{margin-top:12.0pt;
			margin-right:0in;
			margin-bottom:3.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-weight:normal;
			font-style:italic;}
		p.MsoHeading7, li.MsoHeading7, div.MsoHeading7
			{margin-top:12.0pt;
			margin-right:0in;
			margin-bottom:3.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoHeading8, li.MsoHeading8, div.MsoHeading8
			{margin-top:12.0pt;
			margin-right:0in;
			margin-bottom:3.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-style:italic;}
		p.MsoHeading9, li.MsoHeading9, div.MsoHeading9
			{margin-top:12.0pt;
			margin-right:0in;
			margin-bottom:3.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-weight:bold;
			font-style:italic;}
		p.MsoToc1, li.MsoToc1, div.MsoToc1
			{margin-top:6.0pt;
			margin-right:.5in;
			margin-bottom:6.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			text-transform:uppercase;}
		p.MsoToc2, li.MsoToc2, div.MsoToc2
			{margin-top:0in;
			margin-right:.5in;
			margin-bottom:6.0pt;
			margin-left:12.25pt;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-variant:small-caps;}
		p.MsoToc3, li.MsoToc3, div.MsoToc3
			{margin-top:0in;
			margin-right:.5in;
			margin-bottom:6.0pt;
			margin-left:23.75pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoToc4, li.MsoToc4, div.MsoToc4
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoToc5, li.MsoToc5, div.MsoToc5
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:48.0pt;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoToc6, li.MsoToc6, div.MsoToc6
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:60.0pt;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoToc7, li.MsoToc7, div.MsoToc7
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:1.0in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoToc8, li.MsoToc8, div.MsoToc8
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:84.0pt;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoToc9, li.MsoToc9, div.MsoToc9
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:96.0pt;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoCommentText, li.MsoCommentText, div.MsoCommentText
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:10.0pt;
			font-family:"Times New Roman";}
		p.MsoHeader, li.MsoHeader, div.MsoHeader
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoFooter, li.MsoFooter, div.MsoFooter
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoEnvelopeAddress, li.MsoEnvelopeAddress, div.MsoEnvelopeAddress
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:2.0in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoEnvelopeReturn, li.MsoEnvelopeReturn, div.MsoEnvelopeReturn
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:10.0pt;
			font-family:"Times New Roman";}
		p.MsoToaHeading, li.MsoToaHeading, div.MsoToaHeading
			{margin-top:12.0pt;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-weight:bold;}
		p.MsoTitle, li.MsoTitle, div.MsoTitle
			{margin-top:12.0pt;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:center;
			font-size:16.0pt;
			font-family:"Times New Roman";
			font-weight:bold;}
		a:link, span.MsoHyperlink
			{color:blue;
			text-decoration:underline;}
		a:visited, span.MsoHyperlinkFollowed
			{color:purple;
			text-decoration:underline;}
		p.MsoCommentSubject, li.MsoCommentSubject, div.MsoCommentSubject
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:10.0pt;
			font-family:"Times New Roman";
			font-weight:bold;}
		p.MsoAcetate, li.MsoAcetate, div.MsoAcetate
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:8.0pt;
			font-family:Tahoma;}
		p.1-2-3Bold, li.1-2-3Bold, div.1-2-3Bold
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:justify;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.1-2-3NoBold, li.1-2-3NoBold, div.1-2-3NoBold
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:justify;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.1-2-3NoBoldD, li.1-2-3NoBoldD, div.1-2-3NoBoldD
			{margin:0in;
			margin-bottom:.0001pt;
			text-align:justify;
			text-indent:.5in;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.1-2-3NoBoldL, li.1-2-3NoBoldL, div.1-2-3NoBoldL
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-indent:-.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.a-b-c-Bold, li.a-b-c-Bold, div.a-b-c-Bold
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.a-b-c-NoBold, li.a-b-c-NoBold, div.a-b-c-NoBold
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.A-B-CBold, li.A-B-CBold, div.A-B-CBold
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:justify;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.A-B-CNoBold, li.A-B-CNoBold, div.A-B-CNoBold
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:justify;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlock, li.hkBlock, div.hkBlock
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlock1, li.hkBlock1, div.hkBlock1
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlock1D, li.hkBlock1D, div.hkBlock1D
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlock1J, li.hkBlock1J, div.hkBlock1J
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-align:justify;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlock1JD, li.hkBlock1JD, div.hkBlock1JD
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			text-align:justify;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlockD, li.hkBlockD, div.hkBlockD
			{margin:0in;
			margin-bottom:.0001pt;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlockJ, li.hkBlockJ, div.hkBlockJ
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:justify;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlockJD, li.hkBlockJD, div.hkBlockJD
			{margin:0in;
			margin-bottom:.0001pt;
			text-align:justify;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBod, li.hkBod, div.hkBod
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBodD, li.hkBodD, div.hkBodD
			{margin:0in;
			margin-bottom:.0001pt;
			text-indent:.5in;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBodJ, li.hkBodJ, div.hkBodJ
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:justify;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBodJD, li.hkBodJD, div.hkBodJD
			{margin:0in;
			margin-bottom:.0001pt;
			text-align:justify;
			text-indent:.5in;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkHang, li.hkHang, div.hkHang
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-indent:-.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkHangD, li.hkHangD, div.hkHangD
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			text-indent:-.5in;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkHangJ, li.hkHangJ, div.hkHangJ
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-align:justify;
			text-indent:-.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkHangJD, li.hkHangJD, div.hkHangJD
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			text-align:justify;
			text-indent:-.5in;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hknotaryblk, li.hknotaryblk, div.hknotaryblk
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:3.0in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkQuote, li.hkQuote, div.hkQuote
			{margin-top:0in;
			margin-right:.5in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkQuote1, li.hkQuote1, div.hkQuote1
			{margin-top:0in;
			margin-right:1.0in;
			margin-bottom:12.0pt;
			margin-left:1.0in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkQuoteD, li.hkQuoteD, div.hkQuoteD
			{margin-top:0in;
			margin-right:.5in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkQuoteJ, li.hkQuoteJ, div.hkQuoteJ
			{margin-top:0in;
			margin-right:.5in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-align:justify;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkQuoteJD, li.hkQuoteJD, div.hkQuoteJD
			{margin-top:0in;
			margin-right:.5in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			text-align:justify;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkSign, li.hkSign, div.hkSign
			{margin-top:30.0pt;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:3.0in;
			margin-bottom:.0001pt;
			page-break-after:avoid;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkSigTxt, li.hkSigTxt, div.hkSigTxt
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:3.0in;
			margin-bottom:.0001pt;
			page-break-after:avoid;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkTitleC, li.hkTitleC, div.hkTitleC
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:center;
			page-break-after:avoid;
			font-size:12.0pt;
			font-family:"Times New Roman Bold";
			font-weight:bold;
			text-decoration:underline;}
		p.hkTitleL, li.hkTitleL, div.hkTitleL
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			page-break-after:avoid;
			font-size:12.0pt;
			font-family:"Times New Roman";
			text-decoration:underline;}
		 /* Page Definitions */
		 @page Section1
			{size:8.5in 11.0in;
			margin:1.0in 1.0in 1.0in 1.0in;}
		div.Section1
			{page:Section1;}
		 /* List Definitions */
		 ol
			{margin-bottom:0in;}
		ul
			{margin-bottom:0in;}
		-->
		</style>
		
		<div class=Section1>
		
		
		<p class=MsoNormal align=center style='text-align:center'><b>&nbsp;</b></p>
		
		<p class=MsoNormal align=center style='text-align:center'><b><span
		style='font-size:11.0pt;font-family:Arial'>(Open) SITESCAPE, INC.  </span></b></p>
		
		<p class=MsoNormal align=center style='text-align:center'><b><span
		style='font-size:11.0pt;font-family:Arial'>END USER LICENSE AGREEMENT</span></b></p>
		
		<p class=MsoNormal><span style='font-size:11.0pt;font-family:Arial'>&nbsp;</span></p>
		
		<p class=MsoNormal align=center style='text-align:center'><b><i><span
		style='font-size:11.0pt;font-family:Arial'>This is a legal document - retain
		for your records</span></i></b></p>
		
		<p class=hkBod><span style='font-size:11.0pt;font-family:Arial'>&nbsp;</span></p>
		
		<p class=hkTitleC align=left style='text-align:left'><span style='font-size:
		11.0pt;font-family:Arial'>Important Notice to User:  Read Carefully </span></p>
		
		<p class=hkTitleC><span style='font-size:11.0pt;font-family:Arial;font-weight:
		normal;text-decoration:none'>&nbsp;</span></p>
		
		<p class=hkTitleC align=left style='text-align:left'><span style='font-size:
		11.0pt;font-family:Arial;font-weight:normal;text-decoration:none'>This End User
		License Agreement (&quot;EULA&quot;) is a legal document between the user
		("you" or "Customer") and SiteScape, Inc. (&quot;SiteScape&quot;). It is
		important that you read this document before installing or using the SiteScape software
		(the &quot;Software,&quot; as further defined below) and any accompanying
		documentation, including, without limitation printed materials, online files,
		or electronic documentation (&quot;Documentation&quot;). By clicking the
		&quot;I accept&quot; and &quot;Next&quot; buttons below, or by installing, or
		otherwise using the Software, you agree to be bound by the terms of this EULA as
		well as the SiteScape Privacy Policy (&quot;Privacy Policy&quot;) including,
		without limitation, the warranty disclaimers, limitation of liability, data use
		and termination provisions below. You agree that this agreement is enforceable
		like any written agreement negotiated and signed by you. If you do not agree, you
		are not licensed to use the Software, and you must destroy any downloaded
		copies of the Software in your possession or control. Please go to our Web site
		at http://www.sitescape.com/eula to download and print a copy of this EULA for
		your files and http://www.sitescape.com/privacy to review the privacy policy.</span></p>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;text-transform:uppercase'>1.<span
		style='font:7.0pt "Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>BACKGROUND</span></h1>
		
		<p class=hkBod><span style='font-size:11.0pt;font-family:Arial'>SiteScape holds
		the copyright to ICEcore, an open source software application, which is
		governed by a separate license agreement even though it may be delivered to
		Customers as part of a package that includes the Software governed by this
		Agreement.  SiteScape has developed, for use in conjunction with ICEcore, certain
		proprietary Software that provides additional features and capabilities not
		available with ICEcore.  Your rights and obligations relating to the Software are
		governed by this EULA.</span></p>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;text-transform:uppercase'>2.<span
		style='font:7.0pt "Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Definitions.</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>&quot;<i>ICEcore</i>&quot;
		has the meaning set out in Section </span><span
		style='font-size:11.0pt;font-family:Arial'>5</span><span style='font-size:11.0pt;
		font-family:Arial'> (ICEcore; Open Source).</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Documentation</i>"
		means the manuals and other material, whether in hard copy or electronic form,
		that are delivered with the Software or provided to Customer by SiteScape and that
		include technical information about the Software and its functional
		specifications.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Major
		Release</i>" means a release of the Software with modified or added functionality,
		features, and/or error correction that is designated by SiteScape, in its sole
		discretion, as a major release.  Without limiting the foregoing, Major Releases
		are generally, but not always, identified by means of a new integer before the
		decimal point in the version number.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>d.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Nonconformity</i>"</span><span
		style='font-size:11.0pt;font-family:Arial'> shall mean a material error that is
		attributable to SiteScape which prevents substantial performance of a principal
		function of the Software, as set forth in the Documentation in effect for the
		applicable version of the Software, and which error is reproducible by
		SiteScape at its facility. </span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>e.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Permitted
		Number</i>" means the number of Users authorized to Use the Software in
		accordance with this EULA, as specified at the time that you purchased licenses
		to the Software.  </span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>f.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>&quot;<i>Premium</i><i> Module</i>&quot; means additional component(s) of the Software
		that SiteScape may make available to Customer and which provide premium
		features or functionality.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>g.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>&quot;<i>Common
		Public Attribution License</i>&quot; has the meaning set out in Section 5 (ICEcore;
		Open Source).</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>h.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Software</i>"
		means the SiteScape software product delivered in connection with this EULA, in
		its standard, unmodified object code form; any accompanying database(s); all
		related Documentation and technical information; and any Updates,
		modifications, enhancements and additional components that SiteScape may
		provide to Customer and which become part of, or interoperate with, the software. 
		Software shall also include: (i) any accompanying security device and may
		include sublicensed software that SiteScape has obtained under license, (ii)
		any Premium Module(s) that are (a) delivered to Customer with the Software, (b)
		later acquired by Customer that become a part of, or interoperate with, the
		Software.  "Software" shall not include ICEcore.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>i.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Update</i>"
		means a patch, work-around, error correction, or minor release for the Software
		that is made generally available by SiteScape, but does not include any new
		Major Release.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>j.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Use</i>"
		means copying, storing, loading, installing, executing, or displaying the
		Software in the course of Customers normal business operations.  Use is
		limited to the type of operations described in the Documentation, solely to
		process Customers own work.  Use specifically excludes any ASP, service bureau,
		internet or web-hosting, or time-share services to third parties without
		SiteScapes prior written consent, which consent may be withheld or denied in
		SiteScapes sole and absolute discretion, and which is subject to payment of
		such additional license fees and maintenance fees as SiteScape may require.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>k.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>User</i>" shall
		mean any specific, identifiable person or device that is authorized or
		authenticated to Use the Software by means of a username or user ID.  </span></h2>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;
		text-transform:uppercase'>3.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Grant</span><span
		style='font-size:11.0pt;font-family:Arial'> of Rights.</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>License
		grant.</span></b><span style='font-size:11.0pt;font-family:Arial'>  Upon your
		acceptance of this EULA, and subject to its terms and conditions, SiteScape
		grants you a non-exclusive, non-transferable, non-sublicenseable, perpetual
		(except in the event of termination of this EULA) license to Use the Software only
		in object code form on any number of workstations or file servers for local or
		wide area computer networks at any of Customers locations in the United States
		(but not including its subsidiaries or related or affiliated companies), but
		only by the Permitted Number of Users.  Customer has no right to Use the source
		code for the Software.  Customer is expressly prohibited from allowing or
		permitting any third-party usage of the Software without the prior written
		consent of SiteScape, which consent may be conditioned upon Customers advance
		payment to SiteScape for any additional license fees for such expanded use.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Server use.</span></b><span
		style='font-size:11.0pt;font-family:Arial'>  You may install one copy of the
		Software on your computer file server for the purpose of downloading and
		installing the Software onto other computers within your internal network up to
		the Permitted Number of computers. No other network use is permitted, including
		without limitation using the Software either directly or through commands, data
		or instructions from or to a computer not part of your internal network, for
		Internet or Web-hosting services or by any user not licensed to use this copy
		of the Software through a valid license from SiteScape.  If you have purchased
		Concurrent User Licenses as defined in Section 3.(c) you may install a copy of the
		Software on a terminal server within your internal network for the sole and
		exclusive purpose of permitting individual users within your organization to
		access and use the Software through a terminal server session from another
		computer on the network provided that the total number of users that access or
		use the Software on such network or terminal server does not exceed the
		Permitted Number. </span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Concurrent
		Use.</span></b><span style='font-size:11.0pt;font-family:Arial'> If you have
		licensed a &quot;Concurrent-User&quot; version of the Software, you may install
		the Software on any compatible computers, up to three (3) times the Permitted
		Number of users, <b>provided</b> that only the Permitted Number of users
		actually use the Software at the same time. The Permitted Number of concurrent
		users shall be specified at such time as you purchase the Software licenses.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>d.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Updates.</span></b><span
		style='font-size:11.0pt;font-family:Arial'>  Customer acknowledges and agrees
		that the continued integrity of the Software and SiteScapes obligations under
		this EULA are dependent upon and subject to the proper Use and maintenance of
		the Software by Customer and its Users.  Proper Use and maintenance means that Customer
		will install, maintain, and Use the Software according to the Documentation
		supplied by SiteScape, follow SiteScapes instructions for installing Updates and
		for correcting and circumventing bugs, and abide by all of the terms of this EULA.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>e.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Restrictions.</span></b><span
		style='font-size:11.0pt;font-family:Arial'>  Customer is prohibited from:</span></h2>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>i.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>removing or
		altering any notices of intellectual property and/or proprietary rights that
		are embedded in or otherwise provided with the Software or any copy thereof;  </span></h3>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>ii.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>copying (except
		as specifically permitted in this EULA), distributing, renting, leasing, exporting,
		granting a security interest in, sublicensing or otherwise transferring all or
		any portion of the Software or the rights in or to the Software;</span></h3>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>iii.<span
		style='font:7.0pt "Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>modifying, adapting,
		translating, reverse engineering, decompiling, disabling any control feature,
		or creating derivative works based on the Software, or otherwise creating the
		source code from the object code of the Software, except as expressly authorized
		by SiteScape in writing; or</span></h3>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>iv.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>permitting any
		Use of the Software that is not expressly authorized by this EULA, including Use
		by any third parties or Users not authorized by this EULA to Use the Software.</span></h3>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>f.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer may
		copy the Documentation only to the extent necessary to Use the Software,
		provided that you reproduce SiteScapes copyright notices and other notices of
		intellectual property and/or proprietary rights, and further provided that all
		such copies shall be subject to all terms, conditions, and obligations of this EULA. 
		Customer shall not modify or alter the Documentation in any manner.</span></h2>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;
		text-transform:uppercase'>4.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Ownership</span><span
		style='font-size:11.0pt;font-family:Arial;font-weight:normal'>.</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer
		acknowledges that SiteScape (together with its third party licensors) owns all
		right and title to the Software and to all intellectual property rights (including,
		without limitation, all applicable rights to patents, copyrights, trademarks,
		and trade secrets in and related to the Software) in the Software (including
		without limitation any images, data, animations, video, audio, music, and text
		incorporated into the Software).  </span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer
		understands and acknowledges that the Software is protected by U.S. patent 
		and copyright laws and international treaties.  Nothing in this EULA constitutes
		a waiver of SiteScapes rights under U.S. or international copyright or patent law
		or any other federal or state law.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer
		understands and acknowledges that this EULA is for a license of limited rights
		to the Software, and is not a sale.  SiteScape owns all title to the Software
		and to any copies of the Software delivered to Customer.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>d.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer
		understands and acknowledges that its rights to Use the Software are specified
		in this EULA and SiteScape retains all rights that are not expressly granted to
		Customer in this Agreement.  </span></h2>
		
		<h1><span style='font-size:
		11.0pt;font-family:Arial;text-transform:uppercase'>5.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>ICEcore; Open
		Source</span><span style='font-size:11.0pt;font-family:Arial'>.</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>The Software may
		be bundled with, and designed to interoperate with, software made available by
		SiteScape under the Common Public Attribution License v.1.0 (available at <a
		href="http://www.opensource.org/licenses/cpal_1.0">http://www.opensource.org/licenses/cpal_1.0</a>)
		(&quot;ICEcore&quot;).  Customer's rights and obligations with respect to the
		Software are governed by this EULA.  Customer's rights and obligations with
		respect to ICEcore are governed by the Common Public Attribution License.</span></h2>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;text-transform:uppercase'>6.<span
		style='font:7.0pt "Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>DISCLAIMER OF WARRANTIES.  </span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>THE SOFTWARE
		IS PROVIDED "AS IS" AND SITESCAPE MAKES NO REPRESENTATIONS OR WARRANTIES AS TO
		ITS USE OR PERFORMANCE.  SITESCAPE AND ITS SUPPLIERS DO NOT AND CANNOT WARRANT
		THE PERFORMANCE OR RESULTS YOU MAY OBTAIN BY USING THE SOFTWARE, AND TO THE MAXIMUM
		EXTENT PERMITTED BY LAW SITESCAPE SPECIFICALLY DISCLAIMS ALL WARRANTIES,
		EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO ANY WARRANTIES OF
		MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, AND ALL WARRANTIES OF
		TITLE, NONINTERFERENCE, AND NONINFRINGEMENT.</span></b></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>This EULA does <b>not</b>
		include technical support by SiteScape. Customer may purchase support services
		from SiteScape under a separate agreement.  Customer is solely responsible for providing
		support to Users.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Parties other
		than SiteScape may provide modifications, enhancements, and additional
		components (Third Party Additions&quot;) designed to become part of, or
		interoperate with, the Software or ICEcore.  SiteScape is not responsible for
		any Third Party Additions and expressly disclaims all warranties with respect
		to Third Party Additions.  </span></h2>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;
		text-transform:uppercase'>7.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>LIMITATION OF
		LIABILITY</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>IN NO EVENT
		WILL SITESCAPE BE LIABLE FOR ANY INDIRECT, SPECIAL, INCIDENTAL, CONSEQUENTIAL, EXEMPLARY
		OR PUNITIVE DAMAGES, OR ANY DAMAGES RELATED TO LOSS OF USE, DATA, SOFTWARE,
		BUSINESS, PROFITS OR GOODWILL, WORK STOPPAGE, OR ANY OTHER COMMERCIAL DAMAGES
		OR LOSSES, ARISING IN CONTRACT, TORT OR OTHERWISE, EVEN IF SITESCAPE HAS
		KNOWLEDGE OF THE POTENTIAL LOSS OR DAMAGE.  SITESCAPE SHALL NOT BE LIABLE FOR
		DAMAGES, CLAIMS BY THIRD PARTIES, OR ANY OTHER CLAIM FOR ANY CAUSE WHATSOEVER, REGARDLESS
		OF THE FORM OF ACTION, ARISING MORE THAN ONE (1) YEAR AFTER THE DISCOVERY OF
		THE NONCONFORMITY RESULTING IN ANY SUCH DAMAGE.  </span></b></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>IN NO EVENT
		SHALL SITESCAPES LIABILITY TO CUSTOMER, IF ANY, EXCEED THE FEES PAID TO
		SITESCAPE UNDER THIS AGREEMENT FOR THE PARTICULAR SOFTWARE OR SERVICE THAT IS
		THE SUBJECT OF THE CLAIM.  </span></b></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>CUSTOMER ACCEPTS
		THE TERMS AND CONDITIONS OF THIS LICENSE AGREEMENT WITH THE UNDERSTANDING THAT
		SITESCAPES LIABILITY IS LIMITED, THE FEES PAYABLE HEREUNDER HAVE BEEN AND WILL
		BE CALCULATED ACCORDINGLY, AND THAT CUSTOMER MAY REDUCE ITS RISK FURTHER BY
		MAKING APPROPRIATE PROVISION FOR INSURANCE.  CUSTOMER FURTHER AGREE TO MITIGATE
		ALL LOSSES OR DAMAGES.</span></b></h2>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;text-transform:uppercase'>8.<span
		style='font:7.0pt "Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Term and
		Termination. </span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>This EULA is
		effective until terminated.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer may
		terminate this EULA at any time by providing written notice to SiteScape and destroying
		the Software and documentation together with all copies.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>This EULA will
		terminate immediately and automatically if Customer fails to pay any sum of
		money past due or owing to SiteScape within 15 days after written notice from
		SiteScape, or fails to comply with or breaches any other term or condition of
		this EULA or any Support Services Agreement.  Termination of this EULA will be
		without prejudice to SiteScapes other rights and remedies under this EULA, and
		SiteScape will further have the right to pursue any other remedy available to
		it at law or in equity or under any statute, and shall be entitled to recover
		its costs and attorneys fees incurred in connection with any legal action or
		proceeding relating to this EULA.  The termination of this Agreement will not
		relieve Customer of any obligation under this Agreement that that arose prior
		to such termination or which is identified as continuing thereafter, nor shall
		it operate as a cancellation of any indebtedness owed or accruing to
		SiteScape.  </span></h2>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;
		text-transform:uppercase'>9.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>SOFTWARE
		ACTIVATION, UPDATES, AND AUDIT RIGHTS</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer is
		hereby notified that the Software will contain license keys intended to ensure
		that the use limits of a particular license will not be exceeded.  SiteScape
		may use your internal network for license metering between installed versions
		of the Software.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Software
		Activation. The Software may use your internal network and Internet connection
		for the purpose of transmitting license-related data at the time of
		installation, registration, use, or update to a SiteScape-operated license
		server and validating the authenticity of the license-related data in order to
		protect SiteScape against unlicensed or illegal use of the Software and to
		improve customer service. Activation is based on the exchange of license
		related data between your computer and the SiteScape license server. You agree
		that SiteScape may use these measures and you agree to follow, and not attempt
		to circumvent, any applicable requirements.</span></b></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>LiveUpdate. </span></b><span
		style='font-size:11.0pt;font-family:Arial'> SiteScape may provide a LiveUpdate
		notification service to you. SiteScape may use your internal network and
		Internet connection for the purpose of transmitting license-related data to a
		SiteScape-operated LiveUpdate server to validate your license at appropriate
		intervals and determine if there is any update available for you.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>d.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Use of Data.</span></b><span
		style='font-size:11.0pt;font-family:Arial'> The terms and conditions of the
		Privacy Policy are set out in full at http://www.sitescape.com/privacy and are
		incorporated by reference into this EULA. By your acceptance of the terms of
		this EULA or use of the Software, you authorize the collection, use and
		disclosure of information collected by SiteScape for the purposes provided for
		in this EULA and/or the Privacy Policy as revised from time to time. European
		users understand and consent to the processing of personal information in the 
		United States for the purposes described herein. SiteScape has the right in its sole
		discretion to amend this provision of the EULA and/or Privacy Policy at any
		time. You are encouraged to review the terms of the Privacy Policy as posted on
		the SiteScape Web site from time to time.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>e.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer agrees
		that:</span></h2>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>i.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Upon request
		from SiteScape or SiteScapes authorized representative, Customer will conduct
		a self-audit of its Use of the Software by running a script provided by
		SiteScape (the "Authorized Script") on the database associated with the
		Software.  Customer shall provide to SiteScape the raw, unmodified output
		generated by the Authorized Script in electronic form within thirty (30)
		business days of receiving the request from SiteScape.  Customer shall pay
		license fees (and any fees for Support Services) for any Use of the Software in
		excess of the Permitted Number revealed by such self-audit within 30 days of
		the date of any invoice issued by SiteScape.</span></h3>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>ii.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>In addition, SiteScape
		may, not more than once every year, direct an independent firm of its choosing
		to audit, upon seven (7) days notice and during normal business hours, the
		number of Users of the Software.  If the number of Users identified by the
		auditors exceeds the Permitted Number, Customer will be invoiced for the
		license fees attributable to the number of Users in excess of the Permitted
		Number plus the costs associated with the audit, which invoice shall be payable
		within fifteen (15) days of the date of the invoice.  </span></h3>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;
		text-transform:uppercase'>10.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>MISCELLANEOUS</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>No Misuse or
		Modification</span></b><span style='font-size:11.0pt;font-family:Arial'>.  The
		Software is intended for Use as specified in this Agreement and in accordance
		with the Documentation.  Customer agrees to hold harmless, indemnify, and
		defend SiteScape, its officers, directors, employees, and agents, from and
		against any loss, claim or damages (including reasonable attorneys fees)
		arising out of or relating to any claim</span></h2>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>i.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>that Customer
		has encoded, compressed, copied, or transmitted any materials (other than the
		materials provided by SiteScape) in connection with the Software in violation
		of another partys rights or in violation of any law;</span></h3>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>ii.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>that Customer
		has misused, allowed to be misused, or modified the Software in any manner not
		expressly permitted by this EULA; or</span></h3>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>iii.<span
		style='font:7.0pt "Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>that any content
		added to or incorporated into the Software by Customer either independently of,
		or pursuant to, the Common Public Attribution License, infringes any
		intellectual property rights of any third party.</span></h3>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Government
		End Users</span></b><span style='font-size:11.0pt;font-family:Arial'>.  The
		license granted under this EULA does not constitute a response by SiteScape to
		any request for proposals, bid solicitation or other invitation or offer to
		contract by any governmental authority but instead constitutes an offer to
		enter into a license agreement only upon the terms and conditions set forth
		herein.  If the United States Government or any other governmental authority
		shall seek to acquire the Software and its acquisition of such Software would
		result in the U.S. Government or such other governmental authority having
		rights in any software that are at variance with the terms and conditions of
		this EULA, SiteScape shall not be bound by any such rights unless it shall have
		expressly entered into an amendment of this EULA that shall set forth such
		rights in accordance with any applicable governmental rules or regulations,
		including the Federal Acquisition Regulation and the Defense Federal
		Acquisition Regulation Supplement.  The Software is a "commercial item," as
		that term is defined in 48 C.F.R. §2.101.  The contractor/manufacturer is
		SiteScape, Inc., 12 Clock Tower Place, Suite 210, Maynard, MA 01754.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Export
		Controls</span></b><span style='font-size:11.0pt;font-family:Arial'>.  Customer
		agrees that the Software will not be shipped, transferred or exported into any
		country or used in any manner prohibited by the United States Export
		Administration Act or any other export laws, restrictions or regulations.  In
		addition, if the Software is identified as an export controlled item under any
		such export laws, Customer represents and warrants that it is not a citizen, or
		otherwise located within, an embargoed nation and that Customer is not
		otherwise prohibited from receiving the Software.  Customer is responsible for
		obtaining any and all required governmental authorizations required in
		connection with Customers Use of the Software, including, without limitation,
		any export or import licenses and foreign exchange permits.  SiteScape shall
		not be liable if any such authorization is delayed, denied, revoked, restricted
		or not renewed and you shall bear all risks and costs associated with such
		activities.  If Customer is importing or exporting the Software outside of the 
		United States, Customer agrees to indemnify and hold SiteScape harmless from and against
		any import and export duties or other claims arising from such importation or
		exportation.  All rights to Use the Software are granted on condition that such
		rights are forfeited if Customer fails to comply with the terms of this EULA.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>d.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Installation</span></b><span
		style='font-size:11.0pt;font-family:Arial'>.  Except as otherwise provided
		herein or as agreed between the parties, Customer is responsible for
		installation, management, and operation of the Software.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>e.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Survival</span></b><span
		style='font-size:11.0pt;font-family:Arial'>.  The terms of Sections 4, 6, 7, 9,
		and 10, but not any grant of rights to Use the Software, shall survive the
		termination of this EULA.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>f.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Governing
		Law; Venue</span></b><span style='font-size:11.0pt;font-family:Arial'>.  This EULA
		is governed by the internal laws of the Commonwealth of Massachusetts without
		regard to its conflict of laws principles.  By executing and accepting this EULA,
		Customer consents irrevocably to jurisdiction and venue in the state and
		federal courts sitting in the Commonwealth of Massachusetts.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>g.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Equitable
		Relief</span></b><span style='font-size:11.0pt;font-family:Arial'>.  Customer acknowledges
		that its breach of the terms and restrictions of this EULA will cause SiteScape
		irreparable harm for which the recovery of money damages would be inadequate. 
		Therefore, Customer agrees that SiteScape will be entitled to obtain injunctive
		relief to enforce the terms of this EULA and to protect its rights in the
		Software, in addition to any other remedies that may be available to it at law,
		in equity, or under any statute.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>h.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Waiver</span></b><span
		style='font-size:11.0pt;font-family:Arial'>.  No failure or delay on the part
		of SiteScape in exercising any right or remedy provided in this EULA shall
		operate as a waiver of such right nor shall any single or partial exercise of
		or failure to exercise any such right or remedy preclude SiteScape from further
		exercising such right or remedy under this EULA.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>i.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Assignment</span></b><span
		style='font-size:11.0pt;font-family:Arial'>.  This EULA may not be assigned
		(whether in whole or in part, and whether by merger, operation of law or
		otherwise) by Customer without SiteScapes prior written consent.  SiteScape
		may assign its rights and obligations under this EULA without notice or
		consent.  This EULA shall be binding upon any permitted successors and assigns.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>j.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Severability</span></b><span
		style='font-size:11.0pt;font-family:Arial'>.  If any provision of this EULA is
		held by a court of competent jurisdiction to be illegal, invalid, or
		unenforceable, the remaining provisions shall remain in full force and effect.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>k.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Entire
		Agreement; Amendment</span></b><span style='font-size:11.0pt;font-family:Arial'>. 
		This EULA, together with all exhibits or other documents attached to, delivered
		with, or expressly referenced by it, constitutes the entire agreement of the
		parties with respect to its subject matter and supersedes all previous and
		contemporaneous communications, presentations, quotations, or agreements
		regarding its subject matter,  No waiver, alteration, modification, or
		cancellation of any of the provisions of this EULA shall be binding unless made
		in writing and signed by an authorized officer of SiteScape.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>l.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Headings</span></b><span
		style='font-size:11.0pt;font-family:Arial'>.  The headings in this EULA are for
		purposes of reference only and shall not in any way limit or affect the meaning
		or interpretation of any of the terms hereof.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>m.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Software Licensed From 
		Third Parties</span></b><span style='font-size:11.0pt;font-family:Arial'>. 
		The Software may contain modules or components licensed from third parties, as 
		identified in the accompanying Documentation (collectively referred to herein 
		as "Third Party Components"), the use of which must comply with the terms of the 
		respective licenses listed for each such Third Party Component (the licenses 
		applicable to the Third Party Components are collectively referred to herein 
		as "Third Party Licenses").   The license granted pursuant to this Agreement 
		does not limit your rights under, or grant you rights that supersede, the terms 
		of the Third Party Licenses.  You agree to defend, indemnify and hold SiteScape, 
		its affiliates and their respective officers, directors, employees, agents and 
		representatives harmless from and against any liabilities, losses, expenses, 
		costs or damages (including attorneys fees) arising from or in any manner relating 
		to any claim or action based upon your breach of the terms and conditions of the 
		Third Party Licenses associated with the Software licensed under this Agreement. </span></h2>

		<p class=MsoNormal style='margin-top:6.0pt'><span style='font-size:8.0pt'>#
		4771339_v1a</span></p>
		
		</div>

