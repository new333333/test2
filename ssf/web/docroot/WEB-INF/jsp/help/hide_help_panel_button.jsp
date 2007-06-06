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
 
 /** NOTE: THIS code is shared by more than the HELP system.  It should
  ** be both renamed and somewhere else.  It is a common way to get a close
  ** button!
  **/
 
%>
<div align="right">
  <a href="javascript: ;" 
    onClick="ss_helpSystem.hideHelpPanel(this);return false;"><img 
    border="0" style="padding-left: 10px;" 
    <ssf:alt tag="alt.exitHelp"/> src="<html:imagesPath/>pics/sym_s_delete.gif"/></a>
</div>
