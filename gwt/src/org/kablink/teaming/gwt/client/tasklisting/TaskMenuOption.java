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

package org.kablink.teaming.gwt.client.tasklisting;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

/**
 * Class used to model the contents of various task menu <SELECT>
 * widgets.
 * 
 * @author drfoster@novell.com
 */
public class TaskMenuOption  {
	private boolean			m_menuChecked;	//
	private boolean			m_separator;	//
	private ImageResource	m_menuImageRes;	//
	private String			m_menu;			//
	private String			m_menuAlt;		//
	
	/**
	 * Class constructor.
	 * 
	 * @param menu
	 * @param menuImageRes
	 * @param menuAlt
	 */
	public TaskMenuOption(String menu, ImageResource menuImageRes, String menuAlt, boolean menuChecked) {
		m_menu         = menu;
		m_menuImageRes = menuImageRes;
		m_menuAlt      = menuAlt;
		m_menuChecked  = menuChecked;
	}
	
	public TaskMenuOption(String menu, ImageResource menuImageRes, String menuAlt) {
		// Always use the initial form of the constructor.
		this(menu, menuImageRes, menuAlt, false);
	}
	
	public TaskMenuOption(String menu, String menuAlt, boolean menuChecked) {
		// Always use the initial form of the constructor.
		this(menu, null, menuAlt, menuChecked);
	}
	
	public TaskMenuOption(String menu, String menuAlt) {
		// Always use the initial form of the constructor.
		this(menu, null, menuAlt, false);
	}
	
	public TaskMenuOption() {
		// Always use the initial form of the constructor.
		this(null, null, null, false);
		m_separator = true;
	}

	/**
	 * Returns an Image Widget for the TaskMenuOption.
	 * 
	 * @return
	 */
	public Image buildImage() {
		Image reply;
		ImageResource ir = getMenuImageRes();
		if (null == ir) {
			reply = null;
		}
		else {
			reply = new Image(ir);
			reply.addStyleName("margin-right-5");
			reply.getElement().setAttribute("align", "absmiddle");
		}
		return reply;
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean       isMenuChecked()   {return m_menuChecked; }
	public boolean       isSeparator()     {return m_separator;   }
	public ImageResource getMenuImageRes() {return m_menuImageRes;}
	public String        getMenu()         {return m_menu;        }
	public String        getMenuAlt()      {return m_menuAlt;     }

	/**
	 * Searches a List<TaskMenuOption> for the one with the specified
	 * menu string.
	 * 
	 * @param menu
	 * @param tmoList
	 * 
	 * @return
	 */
	public static TaskMenuOption getTMOFromList(String menu, List<TaskMenuOption> tmoList) {
		TaskMenuOption reply = null;
		for (TaskMenuOption tmo:  tmoList) {
			if (menu.equals(tmo.getMenu())) {
				reply = tmo;
				break;
			}
		}
		return reply;
	}
}
