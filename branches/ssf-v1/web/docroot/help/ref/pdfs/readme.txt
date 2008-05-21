Novell Teaming 1.0 
Support Pack 3
May 1, 2008


1  Product Overview

Novell® Teaming is an enterprise collaboration tool that integrates 
with GroupWise®. Novell Teaming can increase individual productivity, 
team effectiveness, and organizational success by providing the right 
set of tools. Key teaming features include:
 - Team workspaces, calendars, task lists, and milestone tracking
 - Easy document management and document sharing
 - Discussion threads, wikis, blogs, and RSS feeds
 - Workflow automation with customized forms
 - Global searches


2  Teaming Installation Instructions

System requirements and installation instructions are available in 
the Teaming 1.0 Installation and Configuration Guide on the Novell 
Teaming 1.0 Documentation Web site 
(http://www.novell.com/documentation/team_plus_conf).

On the Teaming Installer Welcome Screen:
 1	If you are updating an existing Teaming installation with 
	Support Pack 3, select the Upgrade option.
	This option preserves your existing databases and other 
	configuration information of your existing system.
	or
	If you are installing Novell Teaming Support Pack 3 as your 
	initial Teaming installation, select the New installation 
	option.
	This option creates new databases and walks you through the 
	configuration of Novell Teaming.
 2	Click Next to continue the installation or upgrade.

2.1  Selecting a Server for Novell Teaming

Novell Teaming is designed to be installed on a different server 
from the Conferencing component of the Teaming + Conferencing 
product. However, it is possible, though somewhat more difficult, 
to install both components on the same server. Production deployment 
typically uses two or more separate servers. 

2.2  Multiple License Key Files

If you purchased a number of Novell Teaming licenses in such a way 
that you received multiple license key files, or if you purchased 
licenses to enable additional Teaming features, you must consolidate 
the license information.
 1	Use any one of the license key files when you install 
	Novell Teaming.
 2	After Novell Teaming is installed and running, copy 
	all your license key files to the following directory:
	../icecore/liferay-portal-tomcat-5.5-jdk5-4.3.0/webapps/
	ssf/WEB-INF/license
 3	Log in to Novell Teaming as the Teaming Site Manager. 
 4	In the Teaming Administration portlet, click Manage License.
	Your current license information is displayed.
 5	Scroll to the bottom of the license information, 
	then click Reload License File.
	The Teaming server reads all the license key files in 
	the license subdirectory and displays your total license 
	information.
 6	Restart the server to put the new licenses into effect.

2.3  Reindexing the Search Index

If you are updating an existing Teaming system to Support Pack 3, 
you must completely reindex the search index in order to resolve
existing index problems that are now fixed.

After installing Support Pack 3:
 1	In the Teaming Administration portlet, 
	click Manage the Search Index.
 2	Select the top workspace, then click OK.

	Depending on the size of your Novell Teaming site, the 
	reindexing process might be lengthy. If you have a large
	site, you might want to perform this process when the site 
	is not busy. The reindexing process might prevent users
	from accessing workspaces and folders on the site.


3  Teaming Known Issues for Support Pack 3

 - Section 3.1, “WebDAV / Edit-in-Place Issues with Microsoft 
   Windows Vista”
 - Section 3.2, “Updated JRE Required on SLED for Drag-and-Drop 
   Functionality"
 - Section 3.3, “Authentication by E-Mail Address Not Supported 
   by Default"
 - Section 3.4, “Folder Summary Update Failure When Tasks Are 
   Modified In Place"
 - Section 3.5, “Extra Directory in the FileRepository Tree"
 - Section 3.6, “Novell Teaming Error Log on Internet Explorer” 
 - Section 3.7, “Liferay Session Timeout Box Obscured”
 - Section 3.8, “Benign Warnings in the Liferay Log File"
 - Section 3.9, “Benign Error at Shutdown”
 - Section 3.10, “Logout/Login Required for Password Change”
 - Section 3.11, “Chinese Characters in Activity Logs"

To review the known issues for Support Pack 1 or Support Pack 2, 
see the Teaming 1.0 Support Pack 1 or Support Pack 2 Readme 
(http://www.novell.com/documentation/team_plus_conf/).

3.1  WebDAV / Edit-in-Place Issues with Microsoft Windows Vista

Microsoft* Windows Vista* has numerous problems with WebDAV access 
that affect all WebDAV interactions. There is also a Vista-specific 
issue with applets that can prevent the Edit-in-Place feature from 
working properly. Be sure you are running the latest version of 
Vista. Vista users who are using Internet Explorer might see a Java* 
warning when they try to use Edit in Place. 
Firefox users do not see this error.

To configure Internet Explorer to support Edit-in-Place:
 1	Click Tools > Internet Options.
 2	Click the Security tab, select Trusted Sites, 
	then click Sites.
 3	In the Add this website to the zone field, 
	specify the URL of your Teaming server, 
	then click Add.
 4	Select or deselect Require server verification (https:) 
	for all sites in this zone as appropriate for your 
	Teaming server.
 5	Click Close, then click OK to save the security settings.
	
To get Edit-in-Place to work in Novell Teaming with Office 2003, 
Office 2007, and Vista, complete Step 1 through Step 3 in any order:
 1	If you are using Internet Explorer, set your Internet 
	security options as described above.
	Firefox users do not need to change their Internet 
	security settings.
 2	Install the latest Microsoft Web Folder update.
	This is available from the Microsoft Knowledgebase article 
	907306 (http://support.microsoft.com/kb/907306).
 3	Add new keys to the registry for each MS Office 
	application:
	3a	In Windows Explorer, navigate to Program 
		Files/Microsoft Office/Office12.
	3b	Scroll down to each MS Office .exe in turn:
		excel.exe
		powerpnt.exe
		winword.exe
		...
	3c	Right-click each executable, then click 
		Properties.
	3d	Click the Compatibility tab.
	3e	Select Run this program in compatibility mode for, 
		then select Windows XP (Service Pack 2) from 		the pull-down list.
	3f	 Reboot the computer.

	You should now be able to use Edit-in-Place for 
	MS Office files.

	NOTE: 
	Although these steps enable Edit-in-Place for Novell Teaming, 
	they do not fix Vista’s inability to attach via WebDAV 
	in Novell Teaming.

	For additional information on applets, view the following 
	Sun* bulletins:
	- Bug 6440902 
	  (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6440902)
	- Bug 6432317 
	  (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6432317)

3.2  Updated JRE Required on SLED for Drag-and-Drop Functionality

At release time, the current version of SUSE® Linux Enterprise Desktop 
(SLED) ships with an older 1.4.x version of the Java Runtime Environment. 
That version has a bug that prevents drag-and-drop from operating properly 
with the Novell Teaming file upload applet. However, copy-and-paste still 
works. Updating the JRE* to 1.5.x on the SLED client system removes this 
restriction.

3.3  Authentication by E-Mail Address Not Supported by Default

The Liferay* portal allows authentication by IDs other than screen name 
(for example, e-mail address). Enabling this feature requires a number 
of prerequisites and has some side effects that might not be desirable:
 - All e-mail addresses must be unique.
 - Liferay’s LDAP search filter must be changed from cn=@screen_name@ 
   to mail=@email_address@
 - Unique screen names must still be defined for each user.
 - WebDAV and Web Services only support authentication by username 
   (screen name), not the e-mail address.
   Alternatively, you can use the e-mail address as the screen name 
   (so that the screen name and the e-mail address fields are identical). 
   However, this is a decision that cannot be changed; it must be part 
   of the initial population of the database from LDAP.

3.4  Folder Summary Update Failure When Tasks Are Modified “In Place”

If you modify a task “in place,” the summary page underneath does 
not update to reflect the change. You must refresh the page. 
A similar behavior can be seen in calendars.

3.5  Extra Directory in the FileRepository Tree

A directory tree named jackrabbit is created in the FileRepository 
root directory. This is the result of initialization of software 
components that are not yet supported in the current release.

3.6  Novell Teaming Error Log on Internet Explorer

If you try to download the Novell Teaming system error log by using 
the Teaming Administration portlet Reports menu with Internet Explorer*, 
you must have security set to Medium or less for the download to work. 
Firefox* works in all cases.

3.7  Liferay Session Timeout Box Obscured
If your Teaming session times out while you are viewing a folder with 
a table view, the Liferay pop-up box is obscured. 
This will be fixed in a future update.

3.8  Benign Warnings in the Liferay Log File

Five warnings appear in the Liferay startup log file (catalina.out). 
These are benign and can safely be ignored:
 - service.impl.PortletLocalServiceImpl - "Portal with name EXT_1..."
 - deploy.hot.PluginPackageHotDeployListener
 - org.hibernate.imple.SessionFactoryObjectFactory - "Initial context...
 - No appenders for log4j
 - net.sf.ehcache.config.Configurator - "No configuration found..."

3.9  Benign Error at Shutdown

When you shut down the product, the log file contains a message 
about a NullPointerException. This error is benign.

3.10  Logout/Login Required for Password Change

If you change your password through the portal or by other means, 
you might need to log out and log in again in order for WebDAV 
access to work properly.

3.11  Chinese Characters in Activity Logs

When a report.csv file for an activity report is opened in Excel, 
Chinese characters do not display correctly, even though the 
report.csv file has been created correctly by default because Excel 
always reads the file using the ISO Latin character set.

As a workaround:
 1	Import the report.csv file into Excel using 
	Data > Import External Data > Import Data.
 2	Select the report.csv file, then click Open.
 3	Select Delimited, select UTF-8, then click Next.
 4	Select Comma as the delimiter, click Next, then click Finish.

	Excel should now display the Chinese characters correctly.


4  Teaming Bug Fixes in Support Pack 3

 - Section 4.1, “Performance Improvements”
 - Section 4.2, “User Interface Fixes”
 - Section 4.3, “Search Fixes”
 - Section 4.4, “Administration Fixes”
 - Section 4.5, “Documentation Fixes”
 - Section 4.6, “E-Mail Fixes”
 - Section 4.7, “New Web Service APIs”
 - Section 4.8, “Other Fixes”

To review the bug fixes in Support Pack 1 or Support Pack 2, 
see the Teaming 1.0 Support Pack 1 or Support Pack 2 Readme 
(http://www.novell.com/documentation/team_plus_conf/).

4.1  	Performance Improvements

 - Minimized database flushes during index operation.
 - Sped up ACL changes in the search index.

4.2  User Interface Fixes

 - Use brackets in folder tree if there are more than 25 
   sub-folders in a folder or workspace.
 - Fixed display problems when showing a deleted user account.
 - Show the team member list properly sorted.
 - Fix tree buckets when a folder name includes the “&” symbol.
 - Allow users to right-click and save attached JPG, TXT 
   and PDF files.
 - Changed the name of the Conferencing product from “Zon” to 
   “Conferencing.”
 - Added a way to specify that date elements in entry definitions 
   should not be initialized to today’s date.
 - If you delete an attached file from a file entry, 
   the title is left unchanged.
 - Fixed “bleed through” problem of selectbox elements on IE6.
 - iFRAMEs works better in IE6 (eliminated error message if the 
   site is running through https).
 - Provide better error message if a document cannot 
   be shown in HTML.
 - Display file names correctly when the file name 
   has UTF-8 characters.
 - Allow text elements to be columns in the discussion table view.

4.3  Search Fixes

 - Fixed searching within a folder and all of its sub-folders 
   to include the starting folder.

   IMPORTANT: 
   To take advantage of these fixes, make sure you follow the 
   instructions in Section 2.3, “Reindexing the Search Index” 
   after you install Support Pack 3.

4.4  Administration Fixes

 - Logout/Login no longer equired for Language Change.
 - Added configuration option for using different hostname for 
   all generated webdav URLs. This allows iChain to work with 
   WebDAV better.
 - When a user changes language or time zone settings from the 
   Liferay My account form, propagate those changes to Teaming 
   immediately. This eliminates the need for the user to log out 
   and log in to see the changes.
 - Added Hungarian as a supported language.
 - When creating new user accounts via LDAP Synchronization, use 
   the system’s Locale as the default Locale for the new accounts.
 - Fixed the tree widget to work in Italian.

4.5  Documentation Fixes

 - New Installer process added to installation guide.
 - Post installation configuration information moved to the 
   admininistration guide.

4.6  E-Mail Fixes

 - Better handling of attached files when mailing entries 
   into a folder.
 - No longer send mail notifications if there is nothing new.
 - Do not send empty alternate content in email notification 
   messages.

4.7  New Web Service APIs

 - Get all principals.
 - Start a workflow on an entry 
   (including specifying the desired starting state).
 - Web services for setDefinitions, setTeamMembers, 
   getDefinitionListAsXML and add name to principals.
 - Add a comment to an entry.
 - Web service to perform a synchronize operation on 
   a mirrored folder.
 - Other Web Service calls to support the migration from 
   SiteScape Forum to Teaming.

4.8  Other Fixes

 - Properly start the workflow timer if a workflow specifies 
   a timer in the first state of the workflow process.
 - Correctly copy binder attributes when building templates.
 - No longer crashes in hibernate mode if a user double clicks 
   on an entry that has never been viewed.
 - Allow HTML elements in the definition builder to be nested.
 - Allow height to be set for an IFRAME accessory.
 - Fix Move operation to work when the “saved location” 
   is changed.
 - Fixed applets (file upload, edit in place, and workflow) 
   to work when iChain is being.
 - Conferencing names now work in Teaming even though there 
   are spaces in the name or there are uppercase characters 
   in the name.
 - Added SXI as an extension that can be viewed as html.


5  Teaming Documentation

The following sources provide information about 
Novell Teaming 1.0:
 - Installation: The following files are available after you 
   extract the software from the .exe file and before you 
   install the software:
   - readmeen.html
   - TeamingInstallationGuide.pdf 
     (the Novell Teaming 1.0 Installation and Configuration Guide)
 - Product documentation included in Novell Teaming: 
   - To access the Teaming Help system, log in to Novell Teaming, 
     then click the Help link.
   - To access the Teaming guides from within Novell Teaming, 
     click the Getting Started link on the Teaming Home page.
   - Online product documentation: Novell Teaming 1.0 
     Documentation Web site       (http://www.novell.com/documentation/team_plus_conf)


6  Documentation Conventions

In this documentation, a greater-than symbol (>) is used to 
separate actions within a step and items in a cross-reference 
path.
A trademark symbol (®, TM, etc.) denotes a Novell trademark; 
an asterisk (*) denotes a third-party trademark


7  Legal Notices

Novell, Inc. makes no representations or warranties with 
respect to the contents or use of this documentation, and 
specifically disclaims any express or implied warranties of 
merchantability or fitness for any particular purpose. 
Further, Novell, Inc. reserves the right to revise this 
publication and to make changes to its content, at any time, 
without obligation to notify any person or entity of such 
revisions or changes. 
Further, Novell, Inc. makes no representations or warranties 
with respect to any software, and specifically disclaims any 
express or implied warranties of merchantability or fitness 
for any particular purpose. 
Further, Novell, Inc. reserves the right to make changes to 
any and all parts of Novell software, at any time, without 
any obligation to notify any person or entity of such changes.
Any products or technical information provided under this 
Agreement may be subject to U.S. export controls and the trade laws of other countries. You agree to comply with all 
export control regulations and to obtain any required licenses or classification to export, re-export, or import 
deliverables. You agree not to export or re-export to 
entities on the current U.S. export exclusion lists or to 
any embargoed or terrorist countries as specified in the U.S. export laws. You agree to not use deliverables for 
prohibited nuclear, missile, or chemical biological 
weaponry end uses. Please refer to the Novell International 
Trade Services Web page (http://www.novell.com/info/exports/) 
for more information on exporting Novell software. 
Novell assumes no responsibility for your failure to obtain 
any necessary export approvals.
Copyright © 2008 Novell, Inc. All rights reserved. 
No part of this publication may be reproduced, photocopied, stored on a retrieval system, or transmitted without the 
express written consent of the publisher.
Novell, Inc. has intellectual property rights relating to 
technology embodied in the product that is described in this document. In particular, and without limitation, these 
intellectual property rights may include one or more of the U.S. patents listed on the Novell Legal Patents Web page 
(http://www.novell.com/company/legal/patents/) and one or more 
additional patents or pending patent applications in the U.S. 
and in other countries.
For Novell trademarks, see the Novell Trademark and Service Mark 
list (http://www.novell.com/company/legal/trademarks/tmlist.html).
All third-party trademarks are the property of their 
respective owners.