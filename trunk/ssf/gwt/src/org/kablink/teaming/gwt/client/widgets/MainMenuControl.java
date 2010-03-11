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

package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.ActionHandler;
import org.kablink.teaming.gwt.client.ActionRequestor;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.TeamingAction;
import org.kablink.teaming.gwt.client.util.OnBrowseHierarchyInfo;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


/**
 * This widget will display Teaming's main menu control.
 * 
 * @author drfoster@novell.com
 */
public class MainMenuControl extends Composite implements ActionRequestor {
	private List<ActionHandler> m_actionHandlers = new ArrayList<ActionHandler>();
	
	/*
	 * Inner class used to handle mouse events for menu buttons.  
	 */
	private static class MenuButtonHover implements MouseOverHandler, MouseOutHandler {
		private Widget m_buttonWidget;	// The Widget the hover is for.
		
		/**
		 * Class constructor.
		 * 
		 * @param buttonAnchor
		 */
		MenuButtonHover(Widget buttonAnchor) {
			// Simply store the parameters.
			m_buttonWidget = buttonAnchor;
		}
		
		/**
		 * Called when the mouse leaves a menu button.
		 * 
		 * @param me
		 */
		public void onMouseOut(MouseOutEvent me) {
			// Simply remove the hover style.
			m_buttonWidget.removeStyleName("subhead-control-bg2");
		}
		
		/**
		 * Called when the mouse enters a menu button.
		 * 
		 * @param me
		 */
		public void onMouseOver(MouseOverEvent me) {
			// Simply add the hover style.
			m_buttonWidget.addStyleName("subhead-control-bg2");
		}
	}
	
	/*
	 * Inner class that implements clicking on buttons on the menu.
	 */
	private class MenuButtonSelector implements ClickHandler {
		private FlowPanel m_panel;		// The FlowPanel containing the menu button.
		private TeamingAction m_action;	// The TeamingAction to fire when this button is clicked.

		/**
		 * Class constructor.
		 * 
		 * @param action
		 * @param actionObject
		 */
		MenuButtonSelector(TeamingAction action, FlowPanel panel) {
			// Simply store the parameters.
			m_action = action;
			m_panel = panel;
		}
		
		/**
		 * Called when the button is clicked.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// Fire the action.
			Object actionObject;
			if (TeamingAction.BROWSE_HIERARCHY == m_action) {
				int left = m_panel.getAbsoluteLeft();
				int top  = m_panel.getParent().getElement().getAbsoluteBottom();
				actionObject = new OnBrowseHierarchyInfo(top, left);
			}
			else {
				actionObject = null;
			}
			triggerAction(m_action, actionObject);
		}
	}


	/*
	 * Inner class that implements clicking on toggles on the menu.
	 */
	private class MenuToggleSelector implements ClickHandler {
		private Anchor m_menuAnchor;		// The toggle's Anchor widget.
		private boolean m_isBase;			// true -> Toggle is set to the base action.  false -> It's set to the alternate action.
		private Image m_altImg;				// The Anchor's Image for the alternate action.
		private Image m_baseImg;			// The Anchor's Image for the base      action.
		private TeamingAction m_altAction;	// The alternate TeamingAction.
		private TeamingAction m_baseAction;	// The base      TeamingAction.

		/**
		 * Class constructor.
		 * 
		 * @param menuAnchor
		 * @param baseImg
		 * @param baseAction
		 * @param altImg
		 * @param altAction
		 */
		MenuToggleSelector(Anchor menuAnchor, Image baseImg, TeamingAction baseAction, Image altImg, TeamingAction altAction) {
			// Store the parameters...
			m_menuAnchor = menuAnchor;
			m_altImg = altImg;
			m_baseImg = baseImg;
			m_altAction = altAction;
			m_baseAction = baseAction;

			// ...and initialize everything else.
			m_isBase = true;
			m_menuAnchor.getElement().appendChild(m_baseImg.getElement());
		}
		
		/**
		 * Called when the toggle is clicked.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// Fire the action...
			TeamingAction action = (m_isBase ? m_baseAction : m_altAction);
			triggerAction(action);
			
			// ...and toggle the state of the Anchor.
			Image addImg;
			Image removeImg;
			if (m_isBase) {
				addImg    = m_altImg;
				removeImg = m_baseImg;
			}
			else {
				addImg    = m_baseImg;
				removeImg = m_altImg;
			}
			m_isBase = (!m_isBase);
			m_menuAnchor.getElement().replaceChild(addImg.getElement(), removeImg.getElement());
		}
	}

	/**
	 * Constructor method.
	 */
	public MainMenuControl() {
		GwtTeamingImageBundle images   = GwtTeaming.getImageBundle();
		GwtTeamingMessages    messages = GwtTeaming.getMessages();
		
		// Create the menu's main FlowPanel...
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("mainMenuControl");
		
		// ...add the slide-left/right toggle...
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("mainMenuButton mainMenuButton_LeftRight subhead-control-bg1 roundcornerSM");
		createMenuToggle(panel, images.slideLeft(), messages.mainMenuAltLeftNavHideShow(), TeamingAction.HIDE_LEFT_NAVIGATION, images.slideRight(), messages.mainMenuAltLeftNavHideShow(), TeamingAction.SHOW_LEFT_NAVIGATION);
		mainPanel.add(panel);

		// ...add the slide-up/down toggle...
		panel = new FlowPanel();
		panel.addStyleName("mainMenuButton mainMenuButton_UpDown subhead-control-bg1 roundcornerSM");
		createMenuToggle(panel, images.slideUp(), messages.mainMenuAltMastHeadHideShow(), TeamingAction.HIDE_MASTHEAD, images.slideDown(), messages.mainMenuAltMastHeadHideShow(), TeamingAction.SHOW_MASTHEAD);
		mainPanel.add(panel);

		// ...add the browse hierarchy button...
		panel = new FlowPanel();
		panel.addStyleName("mainMenuButton mainMenuButton_BrowseHierarchy subhead-control-bg1 roundcornerSM");
		createMenuButton(panel, images.browseHierarchy(), messages.mainMenuAltBrowseHierarchy(), new MenuButtonSelector(TeamingAction.BROWSE_HIERARCHY, panel));
		mainPanel.add(panel);
		
		// ...and add the GWT UI button.
		panel = new FlowPanel();
		panel.addStyleName("mainMenuButton mainMenuButton_GwtUI subhead-control-bg1 roundcornerSM");
		createMenuButton(panel, images.gwtUI(), messages.mainMenuAltGwtUI(), new MenuButtonSelector(TeamingAction.TOGGLE_GWT_UI, panel));
		mainPanel.add(panel);
		
		// Finally, all composites must call initWidget() in their
		// constructors.
		initWidget(mainPanel);
	}

	/**
	 * Called to add an ActionHandler to this MainMenuControl.
	 * 
	 * Implements the ActionRequestor.addActionHandler() method.
	 * 
	 * @param actionHandler
	 */
	public void addActionHandler(ActionHandler actionHandler) {
		m_actionHandlers.add( actionHandler );
	}
	
	
	/*
	 * Creates an image based button on the menu bar.
	 */
	private Anchor createMenuButton(FlowPanel panel, ImageResource imgRes, String imgTitle, ClickHandler ch) {
		// Create the Image...
		Image img = new Image(imgRes);
		img.setTitle(imgTitle);
		img.addStyleName("mainMenuButton_WidgetImage");
		
		// ...create the Anchor...
		Anchor a = new Anchor();
		a.addStyleName("mainMenuButton_WidgetAnchor");
		
		// ...tie things together...
		a.getElement().appendChild(img.getElement());
		a.addClickHandler(ch);
		
		// ...add mouse over handling...
		MenuButtonHover hover = new MenuButtonHover(panel);
		a.addMouseOverHandler(hover);
		a.addMouseOutHandler( hover);
		
		// ...and add the Anchor to the panel and return it.
		panel.add(a);
		return a;
	}
	
	/*
	 * Creates an image based toggle on the menu bar.
	 */
	private Anchor createMenuToggle(FlowPanel panel, ImageResource baseImgRes, String baseTitle, TeamingAction baseAction, ImageResource altImgRes, String altTitle, TeamingAction altAction) {
		// Create the alternate Image...
		Image altImg = new Image(altImgRes);
		altImg.setTitle(altTitle);
		altImg.addStyleName("mainMenuButton_WidgetImage");
		
		// ...create the base Image...
		Image baseImg = new Image(baseImgRes);
		baseImg.setTitle(baseTitle);
		baseImg.addStyleName("mainMenuButton_WidgetImage");
		
		// ...create the Anchor...
		Anchor a = new Anchor();
		a.addStyleName("mainMenuButton_WidgetAnchor");
		
		// ...tie things together...
		a.addClickHandler(new MenuToggleSelector(a, baseImg, baseAction, altImg, altAction));
		
		// ...add mouse hover handling...
		MenuButtonHover hover = new MenuButtonHover(panel);
		a.addMouseOverHandler(hover);
		a.addMouseOutHandler( hover);
		
		// ...and add the Anchor to the panel and return it.
		panel.add(a);
		return a;
	}

	/*
	 * Fires a TeamingAction at the registered ActionHandler's.
	 */
	private void triggerAction(TeamingAction action) {
		// Always use the final form of the method.
		triggerAction(action, null);
	}
	
	private void triggerAction(TeamingAction action, Object obj) {
		// Scan the ActionHandler's that have been registered...
		for (Iterator<ActionHandler> ahIT = m_actionHandlers.iterator(); ahIT.hasNext(); ) {
			// ...firing the action at each.
			ahIT.next().handleAction(action, obj);
		}
	}
}
