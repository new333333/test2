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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * 
 */
public class ActivityStreamTopEntry extends ActivityStreamEntry
{
	private ArrayList<ActivityStreamComment> m_comments;
	private InlineLabel m_binderName;	// Name of the binder this entry comes from.
	
	/**
	 * 
	 */
	public ActivityStreamTopEntry( ActionHandler actionHandler )
	{
		super( actionHandler );
		
		{
			ActivityStreamComment comment1;
			ActivityStreamComment comment2;
			
			comment1 = new ActivityStreamComment( actionHandler );
			comment1.setTitle( "This is the title for comment #1" );
			comment1.setAuthor( "Peter Hurley" );
			comment1.setDate( "September 1, 2010" );
			comment1.setDesc( "The quick brown fox jumped over something.  Rhoncus in neque neque tortor quia elit, amet neque quis primis sapien, mauris vestibulum adipiscing varius quis suscipit ligula. Hendrerit vitae, donec sem auctor porta habitasse commodo etiam, dolor aliquet in. Mauris ornare neque dictum erat vestibulum volutpat, mi vel nam, ultricies rhoncus, auctor sapien mauris" );
			comment1.setAvatarUrl( "http://jwootton2.provo.novell.com:8080/ssf/i/iccg/pics/group_icon.gif" );
			addComment( comment1 );

			comment2 = new ActivityStreamComment( actionHandler );
			comment2.setTitle( "This is the title for comment #2" );
			comment2.setAuthor( "Peter Hurley" );
			comment2.setDate( "September 2, 2010" );
			comment2.setDesc( "Rhoncus in neque neque tortor quia elit, amet neque quis primis sapien, mauris vestibulum adipiscing varius quis suscipit ligula. Hendrerit vitae, donec sem auctor porta habitasse commodo etiam, dolor aliquet in. Mauris ornare neque dictum erat vestibulum volutpat, mi vel nam, ultricies rhoncus, auctor sapien mauris" );
			comment2.setAvatarUrl( "http://jwootton2.provo.novell.com:8080/ssf/i/iccg/pics/group_icon.gif" );
			addComment( comment2 );
		}
	}

	
	/**
	 * Add the name of the binder this entry comes from.
	 */
	public void addAdditionalHeaderUI( FlowPanel headerPanel )
	{
		ImageResource imageResource;
		Image img;
		
		imageResource = GwtTeaming.getImageBundle().breadSpace();
		img = new Image( imageResource );
		headerPanel.add( img );
		
		// Create a label that holds the name of the binder this entry comes from.
		m_binderName = new InlineLabel();
		m_binderName.addStyleName( "activityStreamTopEntryBinderName" );
		headerPanel.add( m_binderName );
		
		// Add mouse over handler.
		{
			MouseOverHandler mouseOverHandler;
			
			mouseOverHandler = new MouseOverHandler()
			{
				/**
				 * 
				 */
				public void onMouseOver( MouseOverEvent event )
				{
					m_binderName.addStyleName( "activityStreamHover" );
				}
				
			};
			m_binderName.addMouseOverHandler( mouseOverHandler );
		}
		
		// Add mouse out handler.
		{
			MouseOutHandler mouseOutHandler;
			
			mouseOutHandler = new MouseOutHandler()
			{
				/**
				 * 
				 */
				public void onMouseOut( MouseOutEvent event )
				{
					m_binderName.removeStyleName( "activityStreamHover" );
				}
			};
			m_binderName.addMouseOutHandler( mouseOutHandler );
		}
		
		// Add a click handler.
		{
			ClickHandler clickHandler;
			
			clickHandler = new ClickHandler()
			{
				/**
				 * 
				 */
				public void onClick( ClickEvent event )
				{
					Window.alert( "Not implemented yet - Binder Name" );
				}
				
			};
			m_binderName.addClickHandler( clickHandler );
		}
	}

	
	/**
	 * 
	 */
	private void addComment( ActivityStreamComment comment )
	{
		FlowPanel mainPanel;
		FlowPanel panel;
		
		// Create a <div> for the comment to live in.
		panel = new FlowPanel();
		panel.addStyleName( "level1Comment" );
		panel.add( comment );
		
		mainPanel = getMainPanel();
		mainPanel.add( panel );
	}
	
	
	/**
	 * 
	 */
	public void clearEntrySpecificInfo()
	{
		m_binderName.setText( "" );
	}
	
	
	/**
	 * 
	 */
	public String getAvatarImageStyleName()
	{
		return "activityStreamTopEntryAvatarImg";
	}

	
	/**
	 * Return the name of the style used with the content panel.
	 */
	public String getContentPanelStyleName()
	{
		return "activityStreamTopEntryContentPanel";
	}

	
	/**
	 * 
	 */
	public String getEntryHeaderStyleName()
	{
		return "activityStreamTopEntryHeader";
	}

	
	/**
	 * 
	 */
	public String getTitlePanelStyleName()
	{
		return "activityStreamTopEntryTitlePanel";
	}

	
	/**
	 * 
	 */
	public String getTitleStyleName()
	{
		return "activityStreamTopEntryTitle";
	}

	
	/**
	 * 
	 */
	public void setBinderName( String binderName )
	{
		m_binderName.setText( binderName );
	}
}
