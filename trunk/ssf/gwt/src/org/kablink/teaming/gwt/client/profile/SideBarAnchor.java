package org.kablink.teaming.gwt.client.profile;

import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Class used for a Profile Side bar Anchor item.  
 * 
 * @author nbjensen@novell.com
 *
 */
public class SideBarAnchor extends FlowPanel 
{

	/**
	 * Class constructor.
	 * 
	 * @param id
	 * @param displayText
	 * @param altText
	 * @param ch
	 */
	public SideBarAnchor(String id, String displayText, String altText, ClickHandler ch) {
		// Initialize the super class...
		super();
		addStyleName("profileSideBar_ItemPanel");
	
		// ...create a FlowPanel to hold the Label...
		FlowPanel mpaLabelPanel = new FlowPanel();
		mpaLabelPanel.getElement().setId(id);
		mpaLabelPanel.addStyleName("profileSideBar_Item");
		
		Label mpaLabel = new Label(displayText);
		mpaLabel.addStyleName("profileSideBar_ItemText");
	
		// ...create the Anchor...
		Anchor mpA = new Anchor();
		//mpA.setWidth("100%");
		mpA.addStyleName("profileSideBar_ItemA");
		if (GwtClientHelper.hasString(altText)) {
			mpA.setTitle(altText);
		}
		
		mpA.addClickHandler(ch);
		HoverByID mpaHover = new HoverByID(id, "workspaceTreeControlRowHover");
		mpA.addMouseOverHandler(mpaHover);
		mpA.addMouseOutHandler( mpaHover);
	
		// ...and connect everything together.
		mpA.getElement().appendChild(mpaLabel.getElement());
		mpaLabelPanel.add(mpA);
		add(mpaLabelPanel);
	}
	
	private class HoverByID implements MouseOverHandler, MouseOutHandler {
		private String m_hoverStyle;	// The style to use with the hover.
		private String m_hoverId;		// The ID of the widget to apply the hover style to.
		
		/**
		 * Class constructor.
		 * 
		 * @param hoverId
		 * @param hoverString;
		 */
		HoverByID(String hoverId, String hoverStyle) {
			// Simply store the parameters.
			m_hoverId = hoverId;
			m_hoverStyle = hoverStyle;
		}
		
		/**
		 * Called when the mouse leaves a menu item.
		 * 
		 * @param me
		 */
		public void onMouseOut(MouseOutEvent me) {
			// Simply remove the hover style.
			Element selectorPanel_New = Document.get().getElementById(m_hoverId);
			selectorPanel_New.removeClassName(m_hoverStyle);
		}
		
		/**
		 * Called when the mouse enters a menu item.
		 * 
		 * @param me
		 */
		public void onMouseOver(MouseOverEvent me) {
			// Simply add the hover style.
			Element selectorPanel_New = Document.get().getElementById(m_hoverId);
			selectorPanel_New.addClassName(m_hoverStyle);
		}
	}
}
