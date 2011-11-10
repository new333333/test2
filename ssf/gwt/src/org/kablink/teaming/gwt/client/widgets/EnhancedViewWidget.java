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

package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.lpe.EnhancedViewConfig;
import org.kablink.teaming.gwt.client.lpe.EnhancedViewProperties;
import org.kablink.teaming.gwt.client.lpe.EntryProperties;
import org.kablink.teaming.gwt.client.lpe.FolderProperties;

import com.google.gwt.user.client.ui.Label;



/**
 * 
 * @author jwootton
 *
 */
public class EnhancedViewWidget extends VibeWidget
{
	/**
	 * Create the appropriate widget based on what type of enhanced view we are dealing with
	 */
	public static VibeWidget createWidget( EnhancedViewConfig config )
	{
		EnhancedViewProperties properties;
		String landingPageStyle;
		VibeWidget widget;
		
		landingPageStyle = config.getLandingPageStyle();
		properties = config.getProperties();
		switch ( properties.getViewType() )
		{
		case DISPLAY_ENTRY:
		{
			EntryProperties entryProperties;
			
			entryProperties = new EntryProperties();
			entryProperties.setEntryId( properties.getEntryId() );
			entryProperties.setShowAuthor( true );
			entryProperties.setShowDate( true );
			entryProperties.setShowTitle( true );
			entryProperties.setNumRepliesToShow( 10 );
			
			widget = new EntryWidget( entryProperties, landingPageStyle );
			break;
		}

		case DISPLAY_RECENT_ENTRIES:
		{
			FolderProperties folderProperties;
			
			folderProperties = new FolderProperties();
			folderProperties.setFolderId( properties.getFolderId() );
			folderProperties.setShowTitle( true );
			folderProperties.setShowDescValue( false );
			folderProperties.setShowEntriesOpenedValue( true );
			folderProperties.setShowEntryAuthor( true );
			folderProperties.setShowEntryDate( true );
			folderProperties.setNumEntriesToBeShownValue( properties.getNumEntriesToBeShownValue() );
			folderProperties.setNumRepliesToShow( 10 );
			
			widget = new FolderWidget( folderProperties, landingPageStyle );
			break;
		}

		case DISPLAY_LIST_OF_RECENT_ENTRIES:
		{
			FolderProperties folderProperties;
			
			folderProperties = new FolderProperties();
			folderProperties.setFolderId( properties.getFolderId() );
			folderProperties.setShowTitle( true );
			folderProperties.setShowDescValue( true );
			folderProperties.setShowEntriesOpenedValue( false );
			folderProperties.setShowEntryAuthor( true );
			folderProperties.setShowEntryDate( true );
			folderProperties.setNumEntriesToBeShownValue( properties.getNumEntriesToBeShownValue() );
			folderProperties.setNumRepliesToShow( 0 );
			
			widget = new FolderWidget( folderProperties, landingPageStyle );
			break;
		}
		
		case DISPLAY_CALENDAR:
		case DISPLAY_FULL_ENTRY:
		case DISPLAY_SORTED_LIST_FILES:
		case DISPLAY_SORTED_LIST_RECENT_ENTRIES:
		case DISPLAY_SURVEY:
		case DISPLAY_TASK_FOLDER:
		case UNKNOWN:
		default:
			widget = new EnhancedViewWidget( config );
			break;
		}
		
		return widget;
	}
	
	
	/**
	 * This widget simply displays the name of the jsp file that is associated with the view type. 
	 */
	private EnhancedViewWidget( EnhancedViewConfig config )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( config.getProperties(), config.getLandingPageStyle() );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	/**
	 * 
	 */
	private VibeFlowPanel init( EnhancedViewProperties properties, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + landingPageStyle );
		mainPanel.addStyleName( "entryWidgetMainPanel" + landingPageStyle );

		Label jspName;
		jspName = new Label( properties.getJspName() );
		mainPanel.add( jspName );
		
		mainPanel.addStyleName( "landingPageWidgetShowBorder" );

		return mainPanel;
	}
}

