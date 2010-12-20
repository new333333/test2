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
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;


/**
 * Images used by the GWT Teaming 'Main Menu' widget.
 * 
 * @author drfoster@novell.com
 */
public interface GwtTeamingMainMenuImageBundle extends ClientBundle {
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/browse_hierarchy.png")
	public ImageResource browseHierarchy();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/close_circle16.png")
	public ImageResource closeCircle16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/close_x.png")
	public ImageResource closeX();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/home16.png")
	public ImageResource home16();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/menu_9.gif")
	public ImageResource menu9();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/menu_arrow.png")
	public ImageResource menuArrow();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/newmenu.png")
	public ImageResource newMenu();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/rating_star.gif")
	public ImageResource ratingStar();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/search_adv.png")
	public ImageResource searchOptions();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/search_delete.gif")
	public ImageResource searchDelete();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/search_glass.png")
	public ImageResource searchGlass();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/slide_down.png")
	public ImageResource slideDown();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/slide_left.png")
	public ImageResource slideLeft();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/slide_right.png")
	public ImageResource slideRight();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/slide_up.png")
	public ImageResource slideUp();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/spacer_1px.png")
	public ImageResource spacer1px();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/MainMenu/tag_delete.gif")
	public ImageResource tagDelete();	
}
