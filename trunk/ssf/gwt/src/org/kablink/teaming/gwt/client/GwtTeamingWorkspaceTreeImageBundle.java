/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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


/**
 * Images used by the GWT Teaming 'Workspace Tree' widget.
 * 
 * @author drfoster
 */
public interface GwtTeamingWorkspaceTreeImageBundle extends ClientBundle {
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/entry_blog.png")
	public ImageResource entry_blog();
	
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/entry_file.gif")
	public ImageResource entry_file();
	
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/entry_task.png")
	public ImageResource entry_task();
	
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_calendar.png")
	public ImageResource folder_calendar();
	
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_comment.png")
	public ImageResource folder_comment();
	
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_file.gif")
	public ImageResource folder_file();
	
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_photo.png")
	public ImageResource folder_photo();
	
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_task.gif")
	public ImageResource folder_task();
	
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_trash.png")
	public ImageResource folder_trash();
	
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/tree_closer.png")
	public ImageResource tree_closer();
	
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/tree_opener.png")
	public ImageResource tree_opener();
}
