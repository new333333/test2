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

package org.kablink.teaming.gwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;


/**
 * Images used by the GWT Teaming 'Workspace Tree' widget.
 * 
 * @author drfoster
 */
public interface GwtTeamingWorkspaceTreeImageBundle extends ClientBundle {
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/close_x.png")
	public ImageResource breadcrumb_close();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/bucket.png")
	public ImageResource bucket();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/busyanim16b.gif")
	public ImageResource busyAnimation();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_calendar.png")
	public ImageResource folder_calendar();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_microblog.gif")
	public ImageResource folder_comment();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_file.gif")
	public ImageResource folder_file();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder.png")
	public ImageResource folder_generic();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_guestbook.gif")
	public ImageResource folder_guestbook();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_milestone.gif")
	public ImageResource folder_milestone();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_photo.png")
	public ImageResource folder_photo();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_survey.png")
	public ImageResource folder_survey();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_task.gif")
	public ImageResource folder_task();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_trash.png")
	public ImageResource folder_trash();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_wiki.gif")
	public ImageResource folder_wiki();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/workspace_generic.png")
	public ImageResource folder_workspace();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/range.png")
	public ImageResource range();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/range_arrows.gif")
	public ImageResource rangeArrows();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/spacer_1px.png")
	public ImageResource spacer_1px();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/collapser.png")
	public ImageResource tree_closer();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/expander.png")
	public ImageResource tree_opener();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/workspace_discussions.png")
	public ImageResource workspace_discussions();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/workspace_generic.png")
	public ImageResource workspace_generic();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/workspace_global_root.png")
	public ImageResource workspace_global_root();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/workspace_landing_page.png")
	public ImageResource workspace_landing_page();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/workspace_personal.gif")
	public ImageResource workspace_personal();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/workspace_profile_root.png")
	public ImageResource workspace_profile_root();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/workspace_project_management.png")
	public ImageResource workspace_project_management();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/workspace_team.gif")
	public ImageResource workspace_team();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/workspace_team_root.png")
	public ImageResource workspace_team_root();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/workspace_top.png")
	public ImageResource workspace_top();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/workspace_trash.png")
	public ImageResource workspace_trash();
}
