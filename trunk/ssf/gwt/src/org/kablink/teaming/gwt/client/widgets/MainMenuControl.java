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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
public class MainMenuControl extends Composite {
	/*
	 * Inner classed used to track clicks on the GWT UI button.
	 */
	private static class GwtUISelector implements ClickHandler {
		/**
		 * Called when the button is clicked.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
			rpcService.getUserWorkspacePermalink(new AsyncCallback<String>() {
				public void onFailure(Throwable t) {}
				public void onSuccess(String userWorkspaceURL)  {
					jsToggleGwtUI();
					jsLoadUserWorkspaceURL(userWorkspaceURL + "&captive=false");
				}
				
				private native void jsToggleGwtUI() /*-{
					// Toggle the GWT UI state.
					window.top.ss_toggleGwtUI(false);
				}-*/;

				private native void jsLoadUserWorkspaceURL(String userWorkspaceURL) /*-{
					// Give the GWT UI state toggling 1/2
					// second to complete and reload the user
					// workspace.
					window.setTimeout(
						function() {
							window.top.location.href = userWorkspaceURL;
						},
						500);
				}-*/;
			});
		}
	}
	
	/*
	 * Inner class used to handle mouse events for menu buttons.  
	 */
	private static class MenuButtonHover implements MouseOverHandler, MouseOutHandler {
		private Widget m_buttonWidget;
		
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
	 * Inner class that implements clicking on button on the menu.
	 */
	private static class MenuButtonSelector implements ClickHandler {
		private String m_action;

		/**
		 * Class constructor.
		 * 
		 * @param action
		 */
		MenuButtonSelector(String action) {
			// Simply store the parameters.
			m_action = action;
		}
		
		/**
		 * Called when the menu button is clicked.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// Fire the action.
			Window.alert("MainMenuControl.MenuButtonSelector.onClick( 'NOT IMPLEMENTED' ):  " + m_action);
		}
	}


	/*
	 * Inner class that implements clicking on toggle on the menu.
	 */
	private static class MenuToggleSelector implements ClickHandler {
		private boolean m_isBase;
		private Anchor m_menuAnchor;
		private Image m_altImg;
		private Image m_baseImg;
		private String m_altAction;
		private String m_baseAction;

		/**
		 * Class constructor.
		 * 
		 * @param menuAnchor
		 * @param baseImg
		 * @param altImg
		 * @param baseAction
		 * @param altAction
		 */
		MenuToggleSelector(Anchor menuAnchor, Image baseImg, Image altImg, String baseAction, String altAction) {
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
		 * Called when the menu button is clicked.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// Fire the action...
			String action = (m_isBase ? m_baseAction : m_altAction);
			Window.alert("MainMenuControl.MenuButtonToggle.onClick( 'NOT IMPLEMENTED' ):  " + action);
			
			// ...and toggle the state of the image.
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
		// Create the menu's main FlowPanel...
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("mainMenuControl");
		
		// ...add the slide-left/right toggle...
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("mainMenuButton mainMenuButton_LeftRight subhead-control-bg1 roundcornerSM");
		createMenuToggle(panel, GwtTeaming.getImageBundle().slideLeft(),  GwtTeaming.getImageBundle().slideRight(), "slideLeft", "slideRight");
		mainPanel.add(panel);

		// ...add the slide-up/down toggle...
		panel = new FlowPanel();
		panel.addStyleName("mainMenuButton mainMenuButton_UpDown subhead-control-bg1 roundcornerSM");
		createMenuToggle(panel, GwtTeaming.getImageBundle().slideUp(),   GwtTeaming.getImageBundle().slideDown(), "slideUp", "slideDown");
		mainPanel.add(panel);

		// ...add the browse hierarchy button...
		panel = new FlowPanel();
		panel.addStyleName("mainMenuButton mainMenuButton_BrowseHierarchy subhead-control-bg1 roundcornerSM");
		createMenuButton(panel, GwtTeaming.getImageBundle().browseHierarchy(), new MenuButtonSelector("browseHierarchy"));
		mainPanel.add(panel);
		
		// ...and add the GWT UI button.
		panel = new FlowPanel();
		panel.addStyleName("mainMenuButton mainMenuButton_GwtUI subhead-control-bg1 roundcornerSM");
		createMenuButton(panel, GwtTeaming.getImageBundle().gwtUI(), new GwtUISelector());
		mainPanel.add(panel);
		
		// Finally, all composites must call initWidget() in their
		// constructors.
		initWidget(mainPanel);
	}

	/*
	 * Creates an image based button on the menu bar.
	 */
	private static Anchor createMenuButton(FlowPanel panel, ImageResource imgRes, ClickHandler ch) {
		Image img = new Image(imgRes);
		img.addStyleName("mainMenuButton_WidgetImage");
		Anchor a = new Anchor();
		a.addStyleName("mainMenuButton_WidgetAnchor");
		a.getElement().appendChild(img.getElement());
		a.addClickHandler(ch);
		MenuButtonHover hover = new MenuButtonHover(panel);
		a.addMouseOverHandler(hover);
		a.addMouseOutHandler( hover);
		panel.add(a);
		return a;
	}
	
	/*
	 * Creates an image based toggle on the menu bar.
	 */
	private static Anchor createMenuToggle(FlowPanel panel, ImageResource baseImgRes, ImageResource altImgRes, String baseAction, String altAction) {
		Image altImg = new Image(altImgRes);
		altImg.addStyleName("mainMenuButton_WidgetImage");
		Image baseImg = new Image(baseImgRes);
		baseImg.addStyleName("mainMenuButton_WidgetImage");
		Anchor a = new Anchor();
		a.addStyleName("mainMenuButton_WidgetAnchor");
		a.addClickHandler(new MenuToggleSelector(a, baseImg, altImg, baseAction, altAction));
		MenuButtonHover hover = new MenuButtonHover(panel);
		a.addMouseOverHandler(hover);
		a.addMouseOutHandler( hover);
		panel.add(a);
		return a;
	}
}
