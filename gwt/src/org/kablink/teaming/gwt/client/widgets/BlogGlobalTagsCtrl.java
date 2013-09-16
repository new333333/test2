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

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.BlogGlobalTagSelectedEvent;
import org.kablink.teaming.gwt.client.util.TagInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;


/**
 * This control is used to display the global tags that have been applied to blog entries
 * in the given blog folder. 
 * @author jwootton
 *
 */
public class BlogGlobalTagsCtrl extends VibeWidget
{
	private FlexTable m_table;
	private GlobalTagInlineLabel m_selectedTagLabel;
	private ClickHandler m_tagClickHandler;
	
	
	/**
	 * Callback interface to interact with the blog global tags control asynchronously after it loads. 
	 */
	public interface BlogGlobalTagsCtrlClient
	{
		void onSuccess( BlogGlobalTagsCtrl baCtrl );
		void onUnavailable();
	}
	
	
	/**
	 * 
	 */
	private class GlobalTagInlineLabel extends InlineLabel
	{
		private TagInfo m_tagInfo;
		
		/**
		 * 
		 */
		public GlobalTagInlineLabel( TagInfo tagInfo )
		{
			super( tagInfo.getTagName() );
			
			m_tagInfo = tagInfo;
		}
		
		/**
		 * 
		 */
		public TagInfo getTagInfo()
		{
			return m_tagInfo;
		}
	}
	
	/**
	 * 
	 */
	private BlogGlobalTagsCtrl()
	{
		VibeFlowPanel mainPanel;
		InlineLabel label;
		
		m_selectedTagLabel = null;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "blogGlobalTagsCtrlMainPanel" );
		
		m_table = new FlexTable();
		
		// Add the "Global Tags" title
		label = new InlineLabel( GwtTeaming.getMessages().blogGlobalTagsTitle() );
		label.addStyleName( "blogGlobalTagsCtrlTitle" );
		m_table.setWidget( 0, 0, label );
		
		mainPanel.add( m_table );
		
		// Create a click handler that will be used with every tag.
		m_tagClickHandler = new ClickHandler()
		{
			/**
			 * 
			 */
			@Override
			public void onClick( ClickEvent event )
			{
				Object src;
				
				// Get the tag the user clicked on.
				src = event.getSource();
				if ( src != null && src instanceof GlobalTagInlineLabel )
				{
					Scheduler.ScheduledCommand cmd;
					GlobalTagInlineLabel label;
					final TagInfo tagInfo;
					
					// Is a tag currently selected?
					if ( m_selectedTagLabel != null )
					{
						// Yes, remove the "selected" style from the label.
						clearSelectedTags();
					}
					
					label = (GlobalTagInlineLabel) src;
					m_selectedTagLabel = label;
					m_selectedTagLabel.addStyleName( "blogGlobalTagsCtrlSelected" );
					tagInfo = label.getTagInfo();
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							handleClickOnTag( tagInfo );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};
		
		initWidget( mainPanel );
	}

	/**
	 * Add the given tag to the list of of tags.
	 */
	private void addGlobalTag( TagInfo tagInfo )
	{
		if ( tagInfo != null  )
		{
			GlobalTagInlineLabel label;
			int row;
			
			row = m_table.getRowCount();
			
			// Add the tag to the list
			label = new GlobalTagInlineLabel( tagInfo );
			label.addStyleName( "blogGlobalTagsCtrlTagLabel" );
			label.addClickHandler( m_tagClickHandler );
			m_table.setWidget( row, 0, label );
		}
	}
	
	/**
	 * Unselect any selected tags.
	 */
	public void clearSelectedTags()
	{
		if ( m_selectedTagLabel != null )
		{
			m_selectedTagLabel.removeStyleName( "blogGlobalTagsCtrlSelected" );
			m_selectedTagLabel = null;
		}
	}

	
	/**
	 * Loads the BlogGlobalTagsCtrl split point and returns an instance of it via the callback.
	 */
	public static void createAsync( final BlogGlobalTagsCtrlClient bgtCtrlClient )
	{
		GWT.runAsync( BlogGlobalTagsCtrl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				BlogGlobalTagsCtrl bgtCtrl;

				bgtCtrl = new BlogGlobalTagsCtrl();
				bgtCtrlClient.onSuccess( bgtCtrl );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_BlogGlobalTagsCtrl() );
				bgtCtrlClient.onUnavailable();
			}
		} );
	}
	
	/**
	 * 
	 */
	private void handleClickOnTag( TagInfo tagInfo )
	{
		if ( tagInfo != null )
		{
			// Fire the BlogGlobalTagSelectedEvent so interested parties will know
			// that this tag was selected.
			{
				BlogGlobalTagSelectedEvent event;
				
				event = new BlogGlobalTagSelectedEvent( tagInfo );
				GwtTeaming.fireEvent( event );
			}
		}
	}
	
	/**
	 * Initialize this control for the given blog folder
	 */
	public void init( ArrayList<TagInfo> listOfGlobalTags )
	{
		if ( listOfGlobalTags != null )
		{
			for (TagInfo nextTagInfo : listOfGlobalTags)
			{
				addGlobalTag( nextTagInfo );
			}
		}
	}
}
