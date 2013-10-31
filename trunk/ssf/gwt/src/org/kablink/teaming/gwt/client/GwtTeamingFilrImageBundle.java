/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
 * Filr images used by GWT Teaming.
 * 
 * @author drfoster@novell.com
 */
public interface GwtTeamingFilrImageBundle extends ClientBundle {
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/file.png")
	public ImageResource entry();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/file_36.png")
	public ImageResource entry_medium();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/file_48.png")
	public ImageResource entry_large();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/file_folder_transparent_72.png")
	public ImageResource fileFolder_transparent_72();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/Filr_bg.png")
	public ImageResource filrBackground();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/Filr_bg_repeat.png")
	public ImageResource filrBackgroundRepeat();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/filr_folder.png")
	public ImageResource folder();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/filr_folder_36.png")
	public ImageResource folder_medium();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/filr_folder_48.png")
	public ImageResource folder_large();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/folder_home.png")
	public ImageResource folderHome();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/folder_home_36.png")
	public ImageResource folderHome_medium();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/folder_home_48.png")
	public ImageResource folderHome_large();
		
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/android_logo_100.png")
	public ImageResource logoAndroid();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/iOS_logo_100.png")
	public ImageResource logoIOS();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/Mac_mini_apple.png")
	public ImageResource logoMac();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/MSWin-logo.png")
	public ImageResource logoWindows();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/filr_multiple_36.png")
	public ImageResource multipleItems();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/myfiles.png")
	public ImageResource myFiles();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/myfiles_36.png")
	public ImageResource myFiles_medium();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/myfiles_48.png")
	public ImageResource myFiles_large();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/myfiles_transparent_48.png")
	public ImageResource myFiles_transparent_48();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/myfiles_transparent_72.png")
	public ImageResource myFiles_transparent_72();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/netfolder.png")
	public ImageResource netFolder();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/netfolder_36.png")
	public ImageResource netFolder_medium();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/netfolder_48.png")
	public ImageResource netFolder_large();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/netfolders_transparent_48.png")
	public ImageResource netFolders_transparent_48();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/netfolders_transparent_72.png")
	public ImageResource netFolders_transparent_72();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/filr_people_transparent_72.png")
	public ImageResource people_transparent_72();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/group.png")
	public ImageResource profileRoot();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/group_36.png")
	public ImageResource profileRoot_medium();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/group_48.png")
	public ImageResource profileRoot_large();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/sharedbyme.png")
	public ImageResource sharedByMe();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/sharedbyme_36.png")
	public ImageResource sharedByMe_medium();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/sharedbyme_48.png")
	public ImageResource sharedByMe_large();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/shared_by_me_transparent_48.png")
	public ImageResource sharedByMe_transparent_40();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/shared_by_me_transparent_72.png")
	public ImageResource sharedByMe_transparent_72();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/shared.png")
	public ImageResource sharedWithMe();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/shared_36.png")
	public ImageResource sharedWithMe_medium();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/shared_48.png")
	public ImageResource sharedWithMe_large();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/shared_with_me_transparent_48.png")
	public ImageResource sharedWithMe_transparent_48();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/shared_with_me_transparent_72.png")
	public ImageResource sharedWithMe_transparent_72();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/public.png")
	public ImageResource sharedPublic();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/public36.png")
	public ImageResource sharedPublic_medium();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/public48.png")
	public ImageResource sharedPublic_large();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/public_transparent_48.png")
	public ImageResource sharedPublic_transparent_48();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/public_transparent_72.png")
	public ImageResource sharedPublic_transparent_72();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/whatsnew_transparent_48.png")
	public ImageResource whatsNew_transparent_48();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/filr_group_25.png")
	public ImageResource filrGroup25();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/filr_group_36.png")
	public ImageResource filrGroup36();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/filr_group_48.png")
	public ImageResource filrGroup48();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/filr_group_72.png")
	public ImageResource filrGroup72();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/external_user_16.png")
	public ImageResource filrExternalUser16();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/external_user_25.png")
	public ImageResource filrExternalUser25();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/external_user_36.png")
	public ImageResource filrExternalUser36();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/external_user_48.png")
	public ImageResource filrExternalUser48();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/external_user_72.png")
	public ImageResource filrExternalUser72();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Filr/external_user_96.png")
	public ImageResource filrExternalUser96();
	
}
