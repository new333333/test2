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

package org.kablink.teaming.gwt.client;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;


/**
 * Images used by GWT Teaming.
 * 
 * @author jwootton
 */
public interface GwtTeamingImageBundle extends ImageBundle
{
	@Resource("org/kablink/teaming/gwt/public/images/browse_hierarchy.png")
	public AbstractImagePrototype browseHierarchy();

	@Resource("org/kablink/teaming/gwt/public/images/edit_10.png")
	public AbstractImagePrototype edit10();

	@Resource("org/kablink/teaming/gwt/public/images/edit_16.gif")
	public AbstractImagePrototype edit16();

	@Resource("org/kablink/teaming/gwt/public/images/delete_10.png")
	public AbstractImagePrototype delete10();

	@Resource("org/kablink/teaming/gwt/public/images/delete_16.gif")
	public AbstractImagePrototype delete16();

	@Resource("org/kablink/teaming/gwt/public/images/lpe_custom_jsp.gif")
	public AbstractImagePrototype landingPageEditorCustomJsp();

	@Resource("org/kablink/teaming/gwt/public/images/lpe_entry16.png")
	public AbstractImagePrototype landingPageEditorEntry();

	@Resource("org/kablink/teaming/gwt/public/images/lpe_folder.gif")
	public AbstractImagePrototype landingPageEditorFolder();

	@Resource("org/kablink/teaming/gwt/public/images/lpe_graphic16.png")
	public AbstractImagePrototype landingPageEditorGraphic();

	@Resource("org/kablink/teaming/gwt/public/images/lpe_link_entry.gif")
	public AbstractImagePrototype landingPageEditorLinkEntry();

	@Resource("org/kablink/teaming/gwt/public/images/lpe_link_folder.gif")
	public AbstractImagePrototype landingPageEditorLinkFolder();

	@Resource("org/kablink/teaming/gwt/public/images/lpe_link_url.gif")
	public AbstractImagePrototype landingPageEditorLinkUrl();

	@Resource("org/kablink/teaming/gwt/public/images/lpe_list16b.png")
	public AbstractImagePrototype landingPageEditorList();

	@Resource("org/kablink/teaming/gwt/public/images/lpe_table_16.png")
	public AbstractImagePrototype landingPageEditorTable();

	@Resource("org/kablink/teaming/gwt/public/images/lpe_utility_element.gif")
	public AbstractImagePrototype landingPageEditorUtilityElement();
	
	@Resource("org/kablink/teaming/gwt/public/images/mast_head_novell_graphic.png")
	public AbstractImagePrototype mastHeadNovellGraphic();

	@Resource("org/kablink/teaming/gwt/public/images/mast_head_novell_logo.png")
	public AbstractImagePrototype mastHeadNovellLogo();

	@Resource("org/kablink/teaming/gwt/public/images/next_16.gif")
	public AbstractImagePrototype next16();

	@Resource("org/kablink/teaming/gwt/public/images/next_disabled_16.gif")
	public AbstractImagePrototype nextDisabled16();

	@Resource("org/kablink/teaming/gwt/public/images/previous_16.gif")
	public AbstractImagePrototype previous16();

	@Resource("org/kablink/teaming/gwt/public/images/previous_disabled_16.gif")
	public AbstractImagePrototype previousDisabled16();

	@Resource("org/kablink/teaming/gwt/public/images/slide_down.png")
	public AbstractImagePrototype slideDown();

	@Resource("org/kablink/teaming/gwt/public/images/slide_left.png")
	public AbstractImagePrototype slideLeft();

	@Resource("org/kablink/teaming/gwt/public/images/slide_right.png")
	public AbstractImagePrototype slideRight();

	@Resource("org/kablink/teaming/gwt/public/images/slide_up.png")
	public AbstractImagePrototype slideUp();

	@Resource("org/kablink/teaming/gwt/public/images/spinner16x16.gif")
	public AbstractImagePrototype spinner16();

	@Resource("org/kablink/teaming/gwt/public/images/warn_icon16.gif")
	public AbstractImagePrototype warningIcon16();
}// end GwtTeamingImageBundle
