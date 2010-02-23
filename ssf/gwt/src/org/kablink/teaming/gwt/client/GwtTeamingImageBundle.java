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
 * Images used by GWT Teaming.
 * 
 * @author jwootton
 */
public interface GwtTeamingImageBundle extends ClientBundle
{
	@Source("org/kablink/teaming/gwt/public/images/browse_hierarchy.png")
	public ImageResource browseHierarchy();

	@Source("org/kablink/teaming/gwt/public/images/edit_10.png")
	public ImageResource edit10();

	@Source("org/kablink/teaming/gwt/public/images/edit_16.gif")
	public ImageResource edit16();

	@Source("org/kablink/teaming/gwt/public/images/delete_10.png")
	public ImageResource delete10();

	@Source("org/kablink/teaming/gwt/public/images/delete_16.gif")
	public ImageResource delete16();

	@Source("org/kablink/teaming/gwt/public/images/lpe_custom_jsp.gif")
	public ImageResource landingPageEditorCustomJsp();

	@Source("org/kablink/teaming/gwt/public/images/lpe_entry16.png")
	public ImageResource landingPageEditorEntry();

	@Source("org/kablink/teaming/gwt/public/images/lpe_folder.gif")
	public ImageResource landingPageEditorFolder();

	@Source("org/kablink/teaming/gwt/public/images/lpe_graphic16.png")
	public ImageResource landingPageEditorGraphic();

	@Source("org/kablink/teaming/gwt/public/images/lpe_link_entry.gif")
	public ImageResource landingPageEditorLinkEntry();

	@Source("org/kablink/teaming/gwt/public/images/lpe_link_folder.gif")
	public ImageResource landingPageEditorLinkFolder();

	@Source("org/kablink/teaming/gwt/public/images/lpe_link_url.gif")
	public ImageResource landingPageEditorLinkUrl();

	@Source("org/kablink/teaming/gwt/public/images/lpe_list16b.png")
	public ImageResource landingPageEditorList();

	@Source("org/kablink/teaming/gwt/public/images/lpe_table_16.png")
	public ImageResource landingPageEditorTable();

	@Source("org/kablink/teaming/gwt/public/images/lpe_utility_element.gif")
	public ImageResource landingPageEditorUtilityElement();
	
	@Source("org/kablink/teaming/gwt/public/images/mast_head_novell_graphic.png")
	public ImageResource mastHeadNovellGraphic();

	@Source("org/kablink/teaming/gwt/public/images/mast_head_novell_logo.png")
	public ImageResource mastHeadNovellLogo();

	@Source("org/kablink/teaming/gwt/public/images/next_16.gif")
	public ImageResource next16();

	@Source("org/kablink/teaming/gwt/public/images/next_disabled_16.gif")
	public ImageResource nextDisabled16();

	@Source("org/kablink/teaming/gwt/public/images/previous_16.gif")
	public ImageResource previous16();

	@Source("org/kablink/teaming/gwt/public/images/previous_disabled_16.gif")
	public ImageResource previousDisabled16();

	@Source("org/kablink/teaming/gwt/public/images/slide_down.png")
	public ImageResource slideDown();

	@Source("org/kablink/teaming/gwt/public/images/slide_left.png")
	public ImageResource slideLeft();

	@Source("org/kablink/teaming/gwt/public/images/slide_right.png")
	public ImageResource slideRight();

	@Source("org/kablink/teaming/gwt/public/images/slide_up.png")
	public ImageResource slideUp();

	@Source("org/kablink/teaming/gwt/public/images/spinner16x16.gif")
	public ImageResource spinner16();

	@Source("org/kablink/teaming/gwt/public/images/warn_icon16.gif")
	public ImageResource warningIcon16();
}// end GwtTeamingImageBundle
