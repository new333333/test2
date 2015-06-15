/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.SearchTagEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTag;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderTagsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryTagsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTagRightsForBinderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTagRightsForEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTagRightsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetTagSortOrderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTagsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SaveTagSortOrderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UpdateBinderTagsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UpdateEntryTagsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TagSortOrder;
import org.kablink.teaming.gwt.client.util.TagType;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements a dialog for managing folder and workspace tags.
 *  
 * @author jwootton@novell.com
 */
public class TagThisDlg extends DlgBox
	implements EditSuccessfulHandler, EditCanceledHandler, KeyUpHandler,
	// Event handlers implemented by this class.
		SearchFindResultsEvent.Handler
{
	private final static int	MAX_TAG_LENGTH		= 60;				// As per ObjectKeys.MAX_TAG_LENGTH.
	private final static int	VISIBLE_TAG_LENGTH	= 20;				// Any better guesses?

	private EditSuccessfulHandler m_onEditSuccessfulHandler;
	private String m_binderId;						// Id of the binder we are working with.  This can be null if m_entryId is not null.
	private BinderType m_binderType;				// Type of binder we are workith with.  This can be null
	private String m_entryId;						// Id of the entry we are working with.  This can be null if m_binderId is not null.
	private boolean m_canManagePersonalTags;
	private boolean m_canManageGlobalTags;
	private GwtTeamingMainMenuImageBundle m_images;	// Access to the GWT main menu images.
	private GwtTeamingMessages m_messages;			// Access to the GWT UI messages.
	private AsyncCallback<VibeRpcResponse> m_rightsCallback = null;
	private AsyncCallback<VibeRpcResponse> m_readTagsCallback = null;
	private AsyncCallback<VibeRpcResponse> m_saveTagsCallback = null;
	private AsyncCallback<VibeRpcResponse> m_saveSortOrderCallback = null;
	private ArrayList<TagInfo> m_currentListOfPersonalTags;
	private ArrayList<TagInfo> m_currentListOfGlobalTags;
	private ArrayList<TagInfo> m_toBeAdded;			// List of tags to be added.
	private ArrayList<TagInfo> m_toBeDeleted;		// List of tags to be deleted.
	private Label m_titleLabel;
	private RadioButton m_personalRB;
	private RadioButton m_communityRB;
	private FindCtrl m_findCtrl;
	private FlexTable m_table;						// Holds a list of tags applied to the given binder/entry.
	private FlowPanel m_tagTablePanel;
	private FlexTable.FlexCellFormatter m_cellFormatter;
	private ImageResource m_deleteImgR;
	private TagSortOrder m_sortOrder;
	private FlowPanel m_errorPanel;
	private Label m_errorLabel;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		// Search events.
		TeamingEvents.SEARCH_FIND_RESULTS,
	};
	

	/**
	 * This widget is used to delete a tag.
	 */
	private class DeleteTagWidget extends Composite
		implements ClickHandler
	{
		private TagInfo m_tagInfo;
		
		/**
		 * 
		 */
		public DeleteTagWidget( TagInfo tagInfo )
		{
			FlowPanel panel;
			Image delImg;
			
			m_tagInfo = tagInfo;
			
			panel = new FlowPanel();
			
			delImg = new Image( m_deleteImgR );
			delImg.addStyleName( "cursorPointer" );
			delImg.getElement().setAttribute( "title", GwtTeaming.getMessages().deleteTagHint() );
			delImg.addClickHandler( this );
			
			panel.add( delImg );
			
			// All composites must call initWidget() in their constructors.
			initWidget( panel );
		}
		
		/**
		 * 
		 */
		public TagInfo getTagInfo()
		{
			return m_tagInfo;
		}

		/**
		 * This gets called when the user clicks on delete tag image.
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			deleteTag( m_tagInfo );
		}
	}
	
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private TagThisDlg(
		boolean autoHide,
		boolean modal,
		EditSuccessfulHandler editSuccessfulHandler,
		int left,
		int top,
		String dlgCaption )
	{
		// Initialize the superclass...
		super(autoHide, modal, left, top);
		
		// ...register the events to be handled by this class...
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this);
		
		// ...initialize everything else...
		m_onEditSuccessfulHandler = editSuccessfulHandler;
		m_messages = GwtTeaming.getMessages();
		m_images = GwtTeaming.getMainMenuImageBundle();
		m_toBeAdded = new ArrayList<TagInfo>();
		m_toBeDeleted = new ArrayList<TagInfo>();
		m_currentListOfPersonalTags = new ArrayList<TagInfo>();
		m_currentListOfGlobalTags = new ArrayList<TagInfo>();
		
		// Read the tag sort order from the user's properties.
		getSortOrderAsync();
		
		// ...and create the dialog's content.
		createAllDlgContent(
			dlgCaption,
			this,	// The dialog's EditSuccessfulHandler.
			this,	// The dialog's EditCanceledHandler.
			null );	// Data accessed via global data members. 

	}

	/**
	 * Add the "There are no tags associated with this entry" text to the table
	 * that holds the list of tags.
	 */
	private void addNoTagsMessage()
	{
		int row;
		String msg = "";
		
		row = 1;
		m_cellFormatter.setColSpan( row, 0, 3 );
		m_cellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_cellFormatter.addStyleName( row, 0, "oltBorderRight" );
		m_cellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 0, "oltLastRowBorderBottom" );

		// Get the appropriate message depending on whether we are working with an entry/folder/workspace.
		if ( m_binderType != null )
		{
			if ( m_binderType == BinderType.FOLDER )
				msg = m_messages.noTagsForFolder();
			else if ( m_binderType == BinderType.WORKSPACE )
				msg = m_messages.noTagsForWorkspace();
			else
				msg = "unknown binder type";
		}
		else if ( m_entryId != null )
			msg = m_messages.noTagsForEntry();

		m_table.setText( row, 0, msg );
	}
	
	
	/**
	 * Add the given tag to the end of the table that holds the list of tags.
	 */
	private void addTagToTable( TagInfo tagInfo )
	{
		String type;
		int row;
		DeleteTagWidget delWidget;
		boolean canDelete;
		
		row = m_table.getRowCount();
		
		// Do we have any tags in the table?
		if ( row == 2 )
		{
			String text;
			
			// Maybe
			// The first row might be the message, "there are no tags..."
			// Get the text from the first row.
			text = m_table.getText( 1, 0 );
			
			// Does the first row contain a message?
			if ( text != null )
			{
				if ( text.equalsIgnoreCase( m_messages.noTagsForEntry() ) ||
					 text.equalsIgnoreCase( m_messages.noTagsForFolder() ) ||
					 text.equalsIgnoreCase( m_messages.noTagsForWorkspace() ) )
				{
					// Yes
					m_table.removeRow( 1 );
					--row;
				}
			}
		}
		
		// Add the tag as the first tag in the table.
		row = 1;
		m_table.insertRow( row );
		
		// Add the tag name in the first column.  Allow the user to click on the tag name.
		// This will invoke the "tag search" page.
		{
			InlineLabel label;
			ClickHandler clickHandler;
			
			m_cellFormatter.setColSpan( row, 0, 1 );
			
			label = new InlineLabel( tagInfo.getTagName() );
			label.addStyleName( "tagThisDlg_TagName" );
			clickHandler = new ClickHandler()
			{
				/**
				 * This gets called when the user clicks on the tag name.
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					Object source;
					
					source = event.getSource();
					if ( source != null && source instanceof InlineLabel )
					{
						final String tagName = ((InlineLabel) source).getText();
						ScheduledCommand cmd = new ScheduledCommand() {
							@Override
							public void execute() {
								handleClickOnTag( tagName );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				}
			};
			label.addClickHandler( clickHandler );
			
			m_table.setWidget( row, 0, label );
		}

		// Add the tag type in the second column.
		if ( tagInfo.getTagType() == TagType.PERSONAL )
			type = GwtTeaming.getMessages().personal();
		else if ( tagInfo.getTagType() == TagType.COMMUNITY )
			type = GwtTeaming.getMessages().community();
		else
			type = GwtTeaming.getMessages().unknownTagType();
		m_table.setText( row, 1, type );

		// If this is a community tag and the user can't manage public tags then don't
		// add the delete widget.
		canDelete = true;
		if ( tagInfo.getTagType() == TagType.COMMUNITY && m_canManageGlobalTags == false )
		{
			canDelete = false;
		}
		else if ( tagInfo.getTagType() == TagType.PERSONAL && m_canManagePersonalTags == false )
		{
			canDelete = false;
		}
		
		
		if ( canDelete )
		{
			// Add the delete image to the 3rd column.
			delWidget = new DeleteTagWidget( tagInfo );
			m_table.setWidget( row, 2, delWidget );
		}

		// Add the necessary styles to the cells in the row.
		m_cellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_cellFormatter.addStyleName( row, 2, "oltBorderRight" );
		m_cellFormatter.addStyleName( row, 0, "oltContentBorderBottom" );
		m_cellFormatter.addStyleName( row, 1, "oltContentBorderBottom" );
		m_cellFormatter.addStyleName( row, 2, "oltContentBorderBottom" );
		m_cellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 1, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 2, "oltContentPadding" );
		
		// Add the tag to our current list of tags.
		if ( tagInfo.getTagType() == TagType.PERSONAL )
			m_currentListOfPersonalTags.add( tagInfo );
		else
			m_currentListOfGlobalTags.add( tagInfo );
		
		// Limit the height of the table that holds the tags to 300 pixels.
		adjustTagTablePanelHeight();
	}
	
	
	/**
	 * 
	 */
	private void adjustTagTablePanelHeight()
	{
		ScheduledCommand cmd = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				int height;
				
				// Get the height of the table that holds the list of tags.
				height = m_table.getOffsetHeight();
				
				// If the height is greater than 300 pixels put an overflow auto on the panel
				// and give the panel a fixed height of 150 pixels.
				if ( height >= 300 )
					m_tagTablePanel.addStyleName( "tagThisTagTablePanelHeight" );
				else
					m_tagTablePanel.removeStyleName( "tagThisTagTablePanelHeight" );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}

	/*
	 * Returns true if a string contains punctuation characters and
	 * false otherwise.
	 * 
	 * Implementation logic is based on that in the ss_tagModify()
	 * method in ss_tags.js.
	 */
	private native boolean containsPunctuation(String s) /*-{
		var pattern = new RegExp("[!\"#$%&'()*+,./:;<=>?@[\\\\\\]^`{|}~-]");
		if (pattern.test(s)) {
			return true;
		}
		return false;
	}-*/;
	
	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param ignored
	 * 
	 * @return
	 */
	@Override
	public Panel createContent( Object callbackData )
	{
		FlowPanel mainPanel;
		ImageResource imageResource;
		Image addImg;
		ClickHandler clickHandler;
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		mainPanel.addStyleName( "dlgContent" );
		
		// Add a label where the entry/folder/workspace title will be displayed.
		// The title will be filled in in init()
		m_titleLabel = new Label();
		m_titleLabel.addStyleName( "tagThisDlg_TitleLabel" );
		mainPanel.add( m_titleLabel );
		
		// Add a "personal" radio button.
		{
			m_personalRB = new RadioButton( "tag-type", GwtTeaming.getMessages().personal() );
			m_personalRB.setValue( Boolean.TRUE );
			mainPanel.add( m_personalRB );
		
			// Add a click handler for the personal rb
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent clickEvent )
				{
					ScheduledCommand cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Set the filter of the Find Control to only search for personal tags.
							m_findCtrl.setSearchType( SearchType.PERSONAL_TAGS );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			m_personalRB.addClickHandler( clickHandler );
		}
		
		// Add a "community" radio button
		{
			m_communityRB = new RadioButton( "tag-type", GwtTeaming.getMessages().community() );
			m_communityRB.addStyleName( "paddingLeft1em" );
			mainPanel.add( m_communityRB );

			// Add a click handler for the personal rb
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent clickEvent )
				{
					ScheduledCommand cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Set the filter of the Find Control to only search for community tags.
							m_findCtrl.setSearchType( SearchType.COMMUNITY_TAGS );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			m_communityRB.addClickHandler( clickHandler );
		}

		// Add the find control.
		{
			HTMLTable.RowFormatter rowFormatter;

			final FlexTable table = new FlexTable();
			rowFormatter = table.getRowFormatter();
			rowFormatter.setVerticalAlign( 0, HasVerticalAlignment.ALIGN_TOP );
			mainPanel.add( table );
			
			final KeyUpHandler kuh = this;
			FindCtrl.createAsync(
					this,
					GwtSearchCriteria.SearchType.PERSONAL_TAGS,
					new FindCtrlClient() {				
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}// end onUnavailable()
				
				@Override
				public void onSuccess( FindCtrl findCtrl )
				{
					m_findCtrl = findCtrl;
					m_findCtrl.addKeyUpHandler( kuh );
					table.setWidget( 0, 0, m_findCtrl );
				}// end onSuccess()
			} );
	
			// Add an "add tag" image.
			{
				CellFormatter cellFormatter;
				
				imageResource = GwtTeaming.getImageBundle().add_btn();
				addImg = new Image( imageResource );
				addImg.addStyleName( "cursorPointer" );
				addImg.getElement().setAttribute( "title", GwtTeaming.getMessages().addTag() );
				cellFormatter = table.getCellFormatter();
				cellFormatter.addStyleName( 0, 1, "paddingTop5px" );
				table.setWidget( 0, 1, addImg );
		
				// Add a click handler to the "add tag" image.
				clickHandler = new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent clickEvent )
					{
						ScheduledCommand cmd = new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Try to add a new tag.
								handleClickOnAddTag();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
				addImg.addClickHandler( clickHandler );
			}
		}
		
		// Add a panel where errors dealing with tag names will be displayed.
		{
			m_errorPanel = new FlowPanel();
			m_errorPanel.addStyleName( "dlgErrorPanel" );
			m_errorPanel.setVisible( false );

			m_errorLabel = new Label();
			m_errorLabel.addStyleName( "dlgErrorLabel" );
			m_errorPanel.add( m_errorLabel );
			
			mainPanel.add( m_errorPanel );
		}
		
		// Create a table to hold the list of tags that are associated with the given binder/entry.
		{
			HTMLTable.RowFormatter rowFormatter;
			
			m_tagTablePanel = new FlowPanel();
			m_tagTablePanel.addStyleName( "tagThisTagTablePanel" );

			m_table = new FlexTable();
			m_table.addStyleName( "paddingTop8px" );
			m_table.setCellSpacing( 0 );
			
			m_tagTablePanel.add( m_table );

			// Add the column headers.
			{
				InlineLabel label;
				
				label = new InlineLabel( GwtTeaming.getMessages().tagName() );
				//~JW:  label.addStyleName( "cursorPointer" );
				clickHandler = new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent clickEvent )
					{
						ScheduledCommand cmd = new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Change the sort order to sort by name
								handleClickOnNameHeader();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
				label.addClickHandler( clickHandler );
				m_table.setWidget( 0, 0, label );
				
				label = new InlineLabel( GwtTeaming.getMessages().tagType() );
				//~JW:  label.addStyleName( "cursorPointer" );
				clickHandler = new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent clickEvent )
					{
						ScheduledCommand cmd = new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Change the sort order to sort by type
								handleClickOnTypeHeader();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
				label.addClickHandler( clickHandler );
				m_table.setWidget( 0, 1, label );
				
				m_table.setHTML( 0, 2, "&nbsp;" );	// The delete image will go in this column.
				
				rowFormatter = m_table.getRowFormatter();
				rowFormatter.addStyleName( 0, "oltHeader" );

				m_cellFormatter = m_table.getFlexCellFormatter();
				// On IE calling m_cellFormatter.setWidth( 0, 2, "*" ); throws an exception.
				// That is why we are calling DOM.setElementAttribute(...) instead.
				//~JW:  m_cellFormatter.setWidth( 0, 2, "*" );
				DOM.setElementAttribute( m_cellFormatter.getElement( 0, 2 ), "width", "*" );
				
				m_cellFormatter.addStyleName( 0, 0, "oltBorderLeft" );
				m_cellFormatter.addStyleName( 0, 0, "oltHeaderBorderTop" );
				m_cellFormatter.addStyleName( 0, 0, "oltHeaderBorderBottom" );
				m_cellFormatter.addStyleName( 0, 0, "oltHeaderPadding" );
				m_cellFormatter.addStyleName( 0, 1, "oltHeaderBorderTop" );
				m_cellFormatter.addStyleName( 0, 1, "oltHeaderBorderBottom" );
				m_cellFormatter.addStyleName( 0, 1, "oltHeaderPadding" );
				m_cellFormatter.addStyleName( 0, 2, "oltBorderRight" );
				m_cellFormatter.addStyleName( 0, 2, "oltHeaderBorderTop" );
				m_cellFormatter.addStyleName( 0, 2, "oltHeaderBorderBottom" );
				m_cellFormatter.addStyleName( 0, 2, "oltHeaderPadding" );
			}
			
			mainPanel.add( m_tagTablePanel );
		}

		// Create an image resource for the delete image.
		m_deleteImgR = GwtTeaming.getImageBundle().delete();

		return mainPanel;
	}
	
	
	/**
	 * Remove the given tag from the table and add the tag to the "to be deleted" list.
	 */
	public void deleteTag( TagInfo tagInfo )
	{
		int row;
		
		// Find the row this tag lives in.
		row = findTagInTable( tagInfo );
		
		// Did we find the tag in the table?
		if ( row > 0 )
		{
			String tagId;
			
			// Yes
			// Remove the tag from our list of tags.
			if ( tagInfo.getTagType() == TagType.PERSONAL )
				removeTagFromListOfTags( m_currentListOfPersonalTags, tagInfo );
			else
				removeTagFromListOfTags( m_currentListOfGlobalTags, tagInfo );
			
			// Remove the tag from the table.
			m_table.removeRow( row );
			
			// Did we remove the last tag from the table?
			if ( m_table.getRowCount() == 1 )
			{
				// Yes
				// Add the "no tags..." message to the table.
				addNoTagsMessage();
			}
			
			// Does this tag already exist in the db?
			tagId = tagInfo.getTagId();
			if ( GwtClientHelper.hasString( tagId ) )
			{
				// Yes, Remember we need to delete this tag.
				m_toBeDeleted.add( tagInfo );
			}
			else
			{
				// No, remove this tag from the list of tags to be added.
				removeTagFromListOfTags( m_toBeAdded, tagInfo );
			}
			
			// Adjust the height of the table that holds the tags.
			adjustTagTablePanelHeight();
		}
	}
	
	
	/**
	 * This method gets called when user user presses the Cancel push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() interface
	 * method.
	 * 
	 * @return
	 */
	@Override
	public boolean editCanceled()
	{
		// Simply return true to allow the dialog to close.
		return true;
	}

	
	/**
	 * This method gets called when user user presses the OK push
	 * button.  We will issue an ajax request to save the list of tags.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful( Object callbackData )
	{
		String tmp;
		
		// Is there any text in the control used to enter a tag name?
		tmp = m_findCtrl.getText();
		if ( tmp != null && tmp.length() > 0 )
		{
			// Yes
			// Add the text in the edit field as a tag.
			if ( handleClickOnAddTag() == false )
			{
				// There was an error adding the tag.
				return false;
			}
		}
		
		// Issue an ajax request to save the tags.
		saveTags();
		
		if ( m_onEditSuccessfulHandler != null )
		{
			ArrayList<ArrayList<TagInfo>> data;
			
			// Gather up the list of personal and global tags and pass them back to the handler.
			data = new ArrayList<ArrayList<TagInfo>>();
			data.add( m_currentListOfPersonalTags );
			data.add( m_currentListOfGlobalTags );
			m_onEditSuccessfulHandler.editSuccessful( data );
		}

		return true;
	}

	
	/**
	 * Find the given tag in the table that holds the tags.
	 */
	private int findTagInTable( TagInfo tagInfo )
	{
		int i;
		String name;
		TagType type;
		
		name = tagInfo.getTagName();
		type = tagInfo.getTagType();
		
		// Look through the table for the given tag.
		// Tags start in row 1.
		for (i = 1; i < m_table.getRowCount(); ++i)
		{
			Widget widget;
			
			// Get the DeleteTagWidget from the 3 column.
			widget = m_table.getWidget( i, 2 );
			if ( widget != null && widget instanceof DeleteTagWidget )
			{
				TagInfo nextTagInfo;
				
				nextTagInfo = ((DeleteTagWidget) widget).getTagInfo();
				if ( nextTagInfo != null )
				{
					if ( type == nextTagInfo.getTagType() )
					{
						if ( name != null && name.equalsIgnoreCase( nextTagInfo.getTagName() ) )
						{
							// We found the tag.
							return i;
						}
					}
				}
			}
		}
		
		// If we get here we did not find the tag.
		return -1;
	}
	
	
	/**
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Return something so that the editSuccessful() method gets
		// called.  Doesn't matter what since we're passing our data
		// around via global data members.
		return Boolean.TRUE;
	}

	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		if ( m_findCtrl != null )
			return m_findCtrl.getFocusWidget();
		
		return null;
	}
	
	/*
	 * Issue an ajax request to get the tag sort order from the user's properties.
	 */
	private void getSortOrderAsync() {
		ScheduledCommand getSortOrder = new ScheduledCommand() {
			@Override
			public void execute()
			{
				getSortOrderNow();
			}// end execute()
		};
		Scheduler.get().scheduleDeferred( getSortOrder );
	}
	
	private void getSortOrderNow()
	{
		GetTagSortOrderCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetTagSortOrder() );
			}

			/**
			 * 
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				m_sortOrder = (TagSortOrder) response.getResponseData();
			}
			
		};
		
		// Issue an ajax request to get the tag sort order from the user's properties.
		cmd = new GetTagSortOrderCmd();
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * This method gets called when the user clicks on the "add tag" image.  We will take the name
	 * of the tag, validate it and try to add it.
	 */
	private boolean handleClickOnAddTag()
	{
		String tagName;
		TagType tagType;
		TagInfo tagInfo;
		
		tagName = m_findCtrl.getText();
		
		// Get the type of tag we are dealing with.
		tagType = getSelectedTagType();
		
		// Is the tag name valid?
		if ( isTagNameValid( tagName ) == false )
		{
			// No, isTagNameValid() will have told the user about the problem.
			return false;
			
		}
		// Is the tag a duplicate?
		if ( isTagADuplicate( tagName, tagType ) )
		{
			// Yes!  isTagADuplicate() will have told the user
			// about any problems.  Simply bail.
			return false;
		}
		
		// Clear what the user has typed.
		m_findCtrl.clearText();
		
		// If we get here the tag is valid.
		// Create a TagInfo object and initialize it.
		tagInfo = new TagInfo();
		tagInfo.setTagName( tagName );
		tagInfo.setTagType( tagType );
		
		// Add the tag to the table that holds the list of tags.
		addTagToTable( tagInfo );
		
		// Add this tag to our "to be added" list.
		m_toBeAdded.add( tagInfo );
		
		return true;
	}
	
	/**
	 * Return the type of tag, personal or community, the user has selected.
	 */
	private TagType getSelectedTagType()
	{
		if ( m_personalRB.isVisible() && m_personalRB.getValue() == Boolean.TRUE )
			return TagType.PERSONAL;
		
		if ( m_communityRB.isVisible() && m_communityRB.getValue() == Boolean.TRUE )
			return TagType.COMMUNITY;
		
		return TagType.UNKNOWN;
	}
	
	/**
	 * This gets called when the user clicks on the name header.  We will change the sort
	 * order to sort by tag name.
	 */
	private void handleClickOnNameHeader()
	{
		// Are we currently sorting by name ascending?
		if ( m_sortOrder == TagSortOrder.SORT_BY_NAME_ASCENDING )
		{
			// Yes, change to sort by name descending
			m_sortOrder = TagSortOrder.SORT_BY_NAME_DESCENDING;
		}
		// Are we currently sorting by name descending?
		else if ( m_sortOrder == TagSortOrder.SORT_BY_NAME_DESCENDING )
		{
			// Yes, change to sort by name ascending.
			m_sortOrder = TagSortOrder.SORT_BY_NAME_ASCENDING;
		}
		// Are we currently sorting by type ascending?
		else if ( m_sortOrder == TagSortOrder.SORT_BY_TYPE_ASCENDING )
		{
			// Yes, change to sort by name ascending.
			m_sortOrder = TagSortOrder.SORT_BY_NAME_ASCENDING;
		}
		// Are we currently sorting by type descending?
		else if ( m_sortOrder == TagSortOrder.SORT_BY_TYPE_DESCENDING )
		{
			// Yes, change to sort by name descending
			m_sortOrder = TagSortOrder.SORT_BY_TYPE_DESCENDING;
		}
		
		// Resort the tags with the new sort order.
		sortTags();
		
		// Save the new sort order in the user's properties.
		saveSortOrder();
	}
	
	
	/**
	 * This gets called when the user clicks on the name of a tag in the list of tags.
	 * If the user has made changes to the list of tags we will ask them if they want
	 * to save their changes.  We will then execute a tag search using the name of
	 * the selected tag.
	 */
	private void handleClickOnTag( String tagName )
	{
		// Has the user added/deleted any tags?
		if ( (m_toBeAdded != null && m_toBeAdded.size() > 0) || (m_toBeDeleted != null && m_toBeDeleted.size() > 0) )
		{
			String question;
			
			// Yes, ask the user if they want to save their changes?
			question = m_messages.promptSaveBeforeTagSearch( tagName );
			if ( Window.confirm( question ) == true )
				saveTags();
		}
		
		// Execute a tag search using the given tag name.
		GwtTeaming.fireEvent(new SearchTagEvent( tagName ));
		
		// Close this dialog.
		hide();
	}
	
	/**
	 * This gets called when the user clicks on the tag type header.  We will change the sort
	 * order to sort by tag type.
	 */
	private void handleClickOnTypeHeader()
	{
		// Are we currently sorting by name ascending?
		if ( m_sortOrder == TagSortOrder.SORT_BY_NAME_ASCENDING )
		{
			// Yes, change to sort by type ascending
			m_sortOrder = TagSortOrder.SORT_BY_TYPE_ASCENDING;
		}
		// Are we currently sorting by name descending?
		else if ( m_sortOrder == TagSortOrder.SORT_BY_NAME_DESCENDING )
		{
			// Yes, change to sort by type descending.
			m_sortOrder = TagSortOrder.SORT_BY_TYPE_DESCENDING;
		}
		// Are we currently sorting by type ascending?
		else if ( m_sortOrder == TagSortOrder.SORT_BY_TYPE_ASCENDING )
		{
			// Yes, change to sort by type descending.
			m_sortOrder = TagSortOrder.SORT_BY_TYPE_DESCENDING;
		}
		// Are we currently sorting by type descending?
		else if ( m_sortOrder == TagSortOrder.SORT_BY_TYPE_DESCENDING )
		{
			// Yes, change to sort by type ascending
			m_sortOrder = TagSortOrder.SORT_BY_TYPE_ASCENDING;
		}
		
		// Resort the tags with the new sort order.
		sortTags();
		
		// Save the new sort order in the user's properties.
		saveSortOrder();
	}
	
	
	/**
	 * Hide any error message that may be visible.
	 */
	private void hideError()
	{
		m_errorPanel.setVisible( false );
	}
	
	/**
	 * Initialize the dialog for the given binderId.
	 */
	public void init( String binderId, String binderTitle, BinderType binderType )
	{
		m_binderId = binderId;
		m_binderType = binderType;
		m_entryId = null;
		
		init( binderTitle );
	}
	
	/**
	 * Initialize the dialog for the given entryId
	 */
	public void init( String entryId, String entryTitle )
	{
		m_binderId = null;
		m_entryId = entryId;
		
		init( entryTitle );
	}
	
	/**
	 * Initialize the dialog. m_binderId or m_entryId must be set before calling this method.
	 */
	private void init( String title )
	{
		if ( title != null )
			m_titleLabel.setText( title );
		
		m_canManageGlobalTags = false;
		m_canManagePersonalTags = false;
		m_findCtrl.setInitialSearchString( "" );

		adjustTagTablePanelHeight();
		
		if ( m_readTagsCallback == null )
		{
			// Create a callback that will be used when we read the tags for a binder or entry.
			m_readTagsCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					String entityId;
					
					if ( m_binderId != null && m_binderId.length() > 0 )
						entityId = m_binderId;
					else
						entityId = m_entryId;
					
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetTags(),
						entityId );
				}
				
				/**
				 * 
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					ArrayList<TagInfo> tags;
					GetTagsRpcResponseData responseData;
					
					responseData = (GetTagsRpcResponseData) response.getResponseData();
					tags = responseData.getTags();
					
					// Update the dialog with the list of tags.
					updateDlg( tags );
				}
			};
		}
		
		if ( m_rightsCallback == null )
		{
			// Create a callback that will be used when we issue an ajax request to see if
			// the user has rights to manage personal or global tags.
			m_rightsCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					String entityId;
					
					if ( m_binderId != null && m_binderId.length() > 0 )
						entityId = m_binderId;
					else
						entityId = m_entryId;
					
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetTagRights(),
						entityId );
				}
				
				/**
				 * 
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					ArrayList<Boolean> tagRights;
					GetTagRightsRpcResponseData responseData;
					
					responseData = (GetTagRightsRpcResponseData) response.getResponseData();
					tagRights = responseData.getRights();
					
					// If the user can't manage personal tags then hide the "Personal tag" radio button.
					m_canManagePersonalTags = tagRights.get( 0 ).booleanValue();
					m_personalRB.setVisible( m_canManagePersonalTags );

					// If the user can't manage public tags then hide the "Community tag" radio button.
					m_canManageGlobalTags = tagRights.get( 1 ).booleanValue();
					m_communityRB.setVisible( m_canManageGlobalTags );
					
					ScheduledCommand cmd = new ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
						public void execute()
						{
							// Does the user have rights to do anything?
							if ( m_canManagePersonalTags == false && m_canManageGlobalTags == false )
							{
								FlowPanel errorPanel;
								Label label;
								
								// No
								// Get the panel that holds the errors.
								errorPanel = getErrorPanel();
								errorPanel.clear();

								// Display a message telling the user they don't have rights to do anything.
								label = new Label( GwtTeaming.getMessages().noTagRights() );
								label.addStyleName( "dlgErrorLabel" );
								errorPanel.add( label );
								showErrors();
							}
							else
							{
								// Yes
								if ( m_canManagePersonalTags )
								{
									m_personalRB.setValue( Boolean.TRUE );
									m_findCtrl.setSearchType( SearchType.PERSONAL_TAGS );
								}
								else
								{
									m_communityRB.setValue( Boolean.TRUE );
									m_findCtrl.setSearchType( SearchType.COMMUNITY_TAGS );
								}
								
								// Are we working with a binder?
								if ( m_binderId != null && m_binderId.length() > 0 )
								{
									GetBinderTagsCmd cmd;
									
									// Yes, Issue a request to get the tags associated with the given binder.
									cmd = new GetBinderTagsCmd( m_binderId );
									GwtClientHelper.executeCommand( cmd, m_readTagsCallback );
								}
								else if ( m_entryId != null && m_entryId.length() > 0 )
								{
									GetEntryTagsCmd cmd;
									
									// We are working with an entry.
									// Issue a request to get the tags associated with the given entry.
									cmd = new GetEntryTagsCmd( m_entryId );
									GwtClientHelper.executeCommand( cmd, m_readTagsCallback );
								}
							}
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
		}

		// Are we working with a binder?
		if ( m_binderId != null && m_binderId.length() > 0 )
		{
			GetTagRightsForBinderCmd cmd;
			
			// Yes
			// Issue a request to see what rights the user has regarding tags on a binder.
			// The onSuccess() method will issue the call to read the tags.
			cmd = new GetTagRightsForBinderCmd( m_binderId );
			GwtClientHelper.executeCommand( cmd, m_rightsCallback );
		}
		else if ( m_entryId != null && m_entryId.length() > 0 )
		{
			GetTagRightsForEntryCmd cmd;
			
			// We are working with an entry.
			// Issue a request to see what rights the user has regarding tags on a binder.
			// The onSuccess() method will issue a request to get the tags associated with the given entry.
			cmd = new GetTagRightsForEntryCmd( m_entryId );
			GwtClientHelper.executeCommand( cmd, m_rightsCallback );
		}
		else
		{
			Window.alert( "binderId and entryId are both empty.  This doesn't work!!!" );
			return;
		}
	}


	/*
	 * Checks to see if the given tag name already exists in the list of tags.
	 */
	private boolean isTagADuplicate( String tagName, TagType tagType )
	{
		boolean alreadyInList;
		
		// Do we have a tag name?
		if ( GwtClientHelper.hasString( tagName ) == false )
			return false;
	
		if ( tagType == TagType.PERSONAL )
			alreadyInList = isTagInList( m_currentListOfPersonalTags, tagName );
		else
			alreadyInList = isTagInList( m_currentListOfGlobalTags, tagName );
		
		// Is the tag already in the list?
		if ( alreadyInList )
		{
			// Yes
			// ...tell the user that's not valid and bail.
			showError( m_messages.mainMenuTagThisDlgErrorDuplicateTag() );
		}
		
		return alreadyInList;
	}

	/**
	 * Is the given tag name in the given list.
	 */
	private boolean isTagInList( ArrayList<TagInfo> listOfTags, String tagName )
	{
		if ( tagName == null )
			return false;
		
		for ( TagInfo nextTag : listOfTags )
		{
			String nextTagName;
				
			nextTagName = nextTag.getTagName();
			if ( tagName.equalsIgnoreCase( nextTagName ) )
				return true;
		}
		
		// If we get here the given tag name is not in the given list.
		return false;
	}
	
	
	/**
	 * Check to see if the tag name is valid.  A tag name is invalid if it contains spaces,
	 * underscores, or punctuation characters.
	 */
	private boolean isTagNameValid( String tagName )
	{
		if ( GwtClientHelper.hasString( tagName ) == false )
			return false;
		
		// If the tag contains spaces...
		if ( 0 <= tagName.indexOf(" ") )
		{
			// ...tell the user that's not valid and bail.
			showError( m_messages.mainMenuTagThisDlgErrorTagHasSpaces() );
			return false;
		}

		// If the tag contains underscores...
		if ( 0 <= tagName.indexOf("_") )
		{
			// ...tell the user that's not valid and bail.
			showError( m_messages.mainMenuTagThisDlgErrorTagHasUnderscores() );
			return false;
		}

		// If the tag is too long...
		if ( MAX_TAG_LENGTH < tagName.length() )
		{
			// ...tell the user and truncate it.
			showError( m_messages.mainMenuTagThisDlgWarningTagTruncated() );
			return false;
		}

		// If the tag contains punctuation characters...
		if ( containsPunctuation( tagName ) )
		{
			// ...tell the user that's not valid and bail.
			showError( m_messages.mainMenuTagThisDlgErrorTagHasPunctuation() );
			return false;
		}

		// If we get here, the tag name is valid.
		return true;
	}
	
	
	/**
	 * Handles the KeyUpEvent
	 */
	@Override
	public void onKeyUp( KeyUpEvent event )
	{
        final int keyCode;

        // Get the key the user pressed
        keyCode = event.getNativeEvent().getKeyCode();

        // Did the user press Enter?
        if ( keyCode == KeyCodes.KEY_ENTER )
        {
			// Yes, kill the keystroke.
        	event.stopPropagation();
        	event.preventDefault();
        }

        ScheduledCommand cmd = new ScheduledCommand()
        {
			@Override
			public void execute()
			{
		        // Did the user press Enter?
		        if ( keyCode == KeyCodes.KEY_ENTER )
		        {
					// Yes, try to add a new tag.
					handleClickOnAddTag();
		        }
		        else
		        {
		        	String tagName;
		        	
		    		tagName = m_findCtrl.getText();

		    		// Check if the tag name entered so far is valid.  If it is not, isTagNameValid()
		        	// will display an error message.
		        	if ( GwtClientHelper.hasString( tagName ) == false || isTagNameValid( tagName ) )
		        	{
		        		// The tag name is valid, hide any error messages that were previously visible.
		        		hideError();
		        	}
		        }
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	
	/**
	 * Find the given tag in the given list of tags and remove it. 
	 */
	private void removeTagFromListOfTags( ArrayList<TagInfo> listOfTags, TagInfo tagInfo )
	{
		int i;
		TagType tagType;
		String tagName;
		
		tagType = tagInfo.getTagType();
		tagName = tagInfo.getTagName();
		i = 0;
		for ( TagInfo nextTag : listOfTags )
		{
			if ( tagType == nextTag.getTagType() && tagName.equalsIgnoreCase( nextTag.getTagName() ) )
			{
				listOfTags.remove( i );
				return;
			}

			++i;
		}
	}
	
	
	/**
	 * Issue an ajax request to save the sort order.
	 */
	private void saveSortOrder()
	{
		SaveTagSortOrderCmd cmd;
		
		// Issue an ajax request to save the sort order.
		if ( m_saveSortOrderCallback == null )
		{
			m_saveSortOrderCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_SaveTagSortOrder() );
				}
				
				/**
				 * 
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					// Nothing to do.
				}
			};
		}
		
		// Issue an ajax request to save the sort order
		cmd = new SaveTagSortOrderCmd( m_sortOrder );
		GwtClientHelper.executeCommand( cmd, m_saveSortOrderCallback );
	}

	
	/**
	 * We will issue an ajax request to save the list of tags.
	 */
	private void saveTags()
	{
		// Issue an ajax request to save the subscription data.
		if ( m_saveTagsCallback == null )
		{
			m_saveTagsCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_SaveTags() );
				}
				
				/**
				 * 
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					// Nothing to do.
				}
			};
		}
		
		// Issue an ajax request to save the tags for the binder/entry we are working with.
		// Are we working with a binder?
		if ( m_binderId != null && m_binderId.length() > 0 )
		{
			UpdateBinderTagsCmd cmd;
			
			// Yes, Issue a request to update the tags associated with the given binder.
			cmd = new UpdateBinderTagsCmd( m_binderId, m_toBeDeleted, m_toBeAdded );
			GwtClientHelper.executeCommand( cmd, m_saveTagsCallback );
		}
		else if ( m_entryId != null && m_entryId.length() > 0 )
		{
			UpdateEntryTagsCmd cmd;
			
			// We are working with an entry.
			// Issue a request to update the tags associated with the given entry.
			cmd = new UpdateEntryTagsCmd( m_entryId, m_toBeDeleted, m_toBeAdded );
			GwtClientHelper.executeCommand( cmd, m_saveTagsCallback );
		}
		else
		{
			Window.alert( "binderId and entryId are both empty.  This doesn't work!!!" );
		}
	}
	
	
	/**
	 * 
	 */
	public void showDlg()
	{
		showDlg( false, -1, -1 );
	}
	
	
	/**
	 * 
	 */
	public void showDlg( final boolean setPosition, final int right, final int top )
	{
		PopupPanel.PositionCallback posCallback;
		
		hideError();
		hideErrorPanel();
		showContentPanel();
		createFooterButtons( DlgBox.DlgButtonMode.OkCancel );

		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			@Override
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				int width;
				int x;
				
				// Set the width of the table that holds the tags.
				width = m_findCtrl.getOffsetWidth();
				width += (width/2);
				m_table.setWidth( String.valueOf( width ) + "px" );
				
				if ( setPosition )
				{
					int y;
					
					if ( width > offsetWidth )
						x = right - width;
					else
						x = right - offsetWidth;

					y = top;
					if ( y > Window.getClientHeight() )
						y = Window.getClientHeight();
					
					if ( (y + offsetHeight) > Window.getClientHeight() )
					{
						y -= (offsetHeight + 10);
					}
					
					setPopupPosition( x, y );
				}
				
				else
				{
					center();
				}
			}
		};
		setPopupPositionAndShow( posCallback );
	}
	
	/**
	 * Display the given error to the user.
	 */
	private void showError( String errMsg )
	{
		// Make the error panel the same width as the find control.
		m_errorPanel.setWidth( String.valueOf( m_findCtrl.getOffsetWidth() ) + "px" );

		m_findCtrl.hideSearchResults();

		m_errorLabel.setText( errMsg );
		m_errorPanel.setVisible( true );
	}
	
	
	/**
	 * Sort the list of tags based on the sort order found in m_sortOrder
	 */
	private void sortTags()
	{
		//~JW:  Finish
	}
	
	
	/**
	 * Update the dialog to reflect the given list of tags associated with the binder/entry. 
	 */
	private void updateDlg( ArrayList<TagInfo> tags )
	{
		int i;
		
		m_toBeDeleted.clear();
		m_toBeAdded.clear();
		m_currentListOfPersonalTags.clear();
		m_currentListOfGlobalTags.clear();
		
		// Remove all of the rows from the table.
		// We start at row 1 so we don't delete the header.
		while ( m_table.getRowCount() > 1 )
		{
			// Remove the 1st row that holds tag information.
			m_table.removeRow( 1 );
		}
		
		// Add a row for each tag.
		for ( TagInfo tagInfo : tags )
		{
			addTagToTable( tagInfo );
		}

		// Do we have any tags?
		if ( tags.size() == 0 )
		{
			// No, add a message to the table telling the user there are no tags.
			addNoTagsMessage();
		}
		
		// Add the appropriate border to the last row.
		if ( m_table.getRowCount() > 1 )
		{
			int row;
			
			row = m_table.getRowCount() - 1;
			for (i = 0; i < m_table.getCellCount( row ); ++i)
			{
				m_cellFormatter.addStyleName( row, i, "oltLastRowBorderBottom" );
			}
		}
	}
	
	/**
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults(SearchFindResultsEvent event) {
		// If the find results aren't for this widget...
		if (!(((Widget) event.getSource()).equals(this))) {
			// ...ignore the event.
			return;
		}
		
		// Make sure we are dealing with a tag.
		GwtTeamingItem selectedObj = event.getSearchResults();
		if ( selectedObj instanceof GwtTag )
		{
			TagInfo tagInfo;
			TagType tagType;
			String tagName;
			
			// Get the name of the selected tag.
			tagName = ((GwtTag) selectedObj).getTagName();
			
			// Get the type of tag we are dealing with.
			tagType = getSelectedTagType();
			
			// Is this tag already in our list of tags?
			if ( isTagADuplicate( tagName, tagType ) )
			{
				// Yes,  isTagADuplicate() will have told the user
				// about the problem.  Simply bail.
				
				// Hide the search-results widget.
				m_findCtrl.hideSearchResults();

				return;
			}
			
			// Hide any error message that may be visible.
			hideError();
			
			// If we get here the tag is valid.
			// Create a TagInfo object and initialize it.
			tagInfo = new TagInfo();
			tagInfo.setTagName( tagName );
			tagInfo.setTagType( tagType );
			
			// Add the tag to the table that holds the list of tags.
			addTagToTable( tagInfo );
			
			// Add this tag to our "to be added" list.
			m_toBeAdded.add( tagInfo );
			
			// Hide the search-results widget.
			m_findCtrl.hideSearchResults();

			// Clear what the user has typed.
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					/**
					 * 
					 */
					@Override
					public void execute()
					{
						m_findCtrl.clearText();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		}
	}
	
	/**
	 * Callback interface to interact with the dialog asynchronously
	 * after it loads. 
	 */
	public interface TagThisDlgClient {
		void onSuccess(TagThisDlg dlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the TagThisDialog and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
			final boolean prefetch,
			final TagThisDlgClient dlgClient,
			
			// Creation parameters.
			final boolean autoHide,
			final boolean modal,
			final EditSuccessfulHandler editSuccessfulHandler,
			final int left,
			final int top,
			final String dlgCaption,

			// First initAndShow variation parameters.
			final TagThisDlg first_initAndShowDlg,
			final String first_binderId,
			final String first_binderTitle,
			final BinderType first_binderType,
	
			// Second initAndShow variation parameters.
			final TagThisDlg second_initAndShowDlg,
			final String second_entryId,
			final String second_entryTitle,
			final int second_x,
			final int second_y) {
		loadControl1(
			// Prefetch parameters.
			prefetch,
			dlgClient,
			
			// Creation parameters.
			autoHide,
			modal,
			editSuccessfulHandler,
			left,
			top,
			dlgCaption,

			// First initAndShow variation parameters.
			first_initAndShowDlg,
			first_binderId,
			first_binderTitle,
			first_binderType,
	
			// Second initAndShow variation parameters.
			second_initAndShowDlg,
			second_entryId,
			second_entryTitle,
			second_x,
			second_y);
	}
	
	/*
	 * Various control loaders used to load the split points containing
	 * the code for the controls by the TagThisDlg object.
	 * 
	 * Loads the split point for the FindCtrl.
	 */
	private static void loadControl1(
			// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
			final boolean prefetch,
			final TagThisDlgClient dlgClient,
			
			// Creation parameters.
			final boolean autoHide,
			final boolean modal,
			final EditSuccessfulHandler editSuccessfulHandler,
			final int left,
			final int top,
			final String dlgCaption,
	
			// First initAndShow variation parameters.
			final TagThisDlg first_initAndShowDlg,
			final String first_binderId,
			final String first_binderTitle,
			final BinderType first_binderType,
	
			// Second initAndShow variation parameters.
			final TagThisDlg second_initAndShowDlg,
			final String second_entryId,
			final String second_entryTitle,
			final int second_x,
			final int second_y) {		
		// The TagThisDlg is dependent on the FindCtrl.  Make sure
		// it has been fetched before trying to use it.
		FindCtrl.prefetch(new FindCtrlClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
				dlgClient.onUnavailable();
			}
			
			@Override
			public void onSuccess( FindCtrl findCtrl )
			{
				ScheduledCommand loadNextControl = new ScheduledCommand() {
					@Override
					public void execute() {
						loadControl2(
							// Prefetch parameters.
							prefetch,
							dlgClient,
							
							// Creation parameters.
							autoHide,
							modal,
							editSuccessfulHandler,
							left,
							top,
							dlgCaption,
	
							// First initAndShow variation parameters.
							first_initAndShowDlg,
							first_binderId,
							first_binderTitle,
							first_binderType,
					
							// Second initAndShow variation parameters.
							second_initAndShowDlg,
							second_entryId,
							second_entryTitle,
							second_x,
							second_y);
					}
				};
				Scheduler.get().scheduleDeferred(loadNextControl);
			}
		} );
	}

	/*
	 * Loads the split point for the TagThisDlg.
	 */
	private static void loadControl2(
			// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
			final boolean prefetch,
			final TagThisDlgClient dlgClient,
			
			// Creation parameters.
			final boolean autoHide,
			final boolean modal,
			final EditSuccessfulHandler editSuccessfulHandler,
			final int left,
			final int top,
			final String dlgCaption,
	
			// First initAndShow variation parameters.
			final TagThisDlg first_initAndShowDlg,
			final String first_binderId,
			final String first_binderTitle,
			final BinderType first_binderType,
	
			// Second initAndShow variation parameters.
			final TagThisDlg second_initAndShowDlg,
			final String second_entryId,
			final String second_entryTitle,
			final int second_x,
			final int second_y) {		
		GWT.runAsync(TagThisDlg.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				initTagThisDlg_Finish(
					// Prefetch parameters.
					prefetch,
					dlgClient,
					
					// Creation parameters.
					autoHide,
					modal,
					editSuccessfulHandler,
					left,
					top,
					dlgCaption,

					// First initAndShow variation parameters.
					first_initAndShowDlg,
					first_binderId,
					first_binderTitle,
					first_binderType,
			
					// Second initAndShow variation parameters.
					second_initAndShowDlg,
					second_entryId,
					second_entryTitle,
					second_x,
					second_y);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_TagThisDlg());
				dlgClient.onUnavailable();
			}
		});
	}
	
	/*
	 * Finishes the initialization of the TagThisDlg object.
	 */
	private static void initTagThisDlg_Finish(
			// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
			final boolean prefetch,
			final TagThisDlgClient dlgClient,
			
			// Creation parameters.
			final boolean autoHide,
			final boolean modal,
			final EditSuccessfulHandler editSuccessfulHandler,
			final int left,
			final int top,
			final String dlgCaption,
	
			// First initAndShow variation parameters.
			final TagThisDlg first_initAndShowDlg,
			final String first_binderId,
			final String first_binderTitle,
			final BinderType first_binderType,
	
			// Second initAndShow variation parameters.
			final TagThisDlg second_initAndShowDlg,
			final String second_entryId,
			final String second_entryTitle,
			final int second_x,
			final int second_y) {		
		if (prefetch) {
			dlgClient.onSuccess(null);
		}
		
		else if ((null == first_initAndShowDlg) && (null == second_initAndShowDlg)) {
			TagThisDlg dlg = new TagThisDlg(
				autoHide,
				modal,
				editSuccessfulHandler,
				left,
				top,
				dlgCaption );
			
			dlgClient.onSuccess(dlg);
		}
		
		else if (null != first_initAndShowDlg) {
			first_initAndShowDlg.init(first_binderId, first_binderTitle, first_binderType);
			first_initAndShowDlg.showDlg();
		}
		
		else if (null != second_initAndShowDlg){
			second_initAndShowDlg.init( second_entryId, second_entryTitle );
			second_initAndShowDlg.showDlg( true, second_x, second_y );
		}
	}	
		
	/**
	 * Loads the TagThisDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param editSuccessfulHandler
	 * @param left
	 * @param top
	 * @param dlgCaption
	 * @param dlgClient
	 */
	public static void createAsync(
			final boolean autoHide,
			final boolean modal,
			final EditSuccessfulHandler editSuccessfulHandler,
			final int left,
			final int top,
			final String dlgCaption,
			final TagThisDlgClient dlgClient) {
		doAsyncOperation(
			// Prefetch parameters.  false -> Not a prefetch.
			false,
			dlgClient,
			
			// Required creation parameters.
			autoHide,
			modal,
			editSuccessfulHandler,
			left,
			top,
			dlgCaption,
			
			// First initAndShow variation parameters ignored.
			null,
			null,
			null,
			null,
			
			// Second initAndShow variation parameters ignored.
			null,
			null,
			null,
			-1,
			-1);
	}

	/**
	 * Initialize and show the dialog (first variation.)
	 * 
	 * @param tagThisDlg
	 * @param binderId
	 * @param binderTitle
	 * @param binderType
	 */
	public static void initAndShow(
			final TagThisDlg tagThisDlg,
			final String binderId,
			final String binderTitle,
			final BinderType binderType) {
		doAsyncOperation(
			// Prefetch parameters.  false -> Not a prefetch.
			false,
			null,
			
			// Ignore creation parameters.
			false,
			false,
			null,
			-1,
			-1,
			null,
			
			// Required First initAndShow variation parameters.
			tagThisDlg,
			binderId,
			binderTitle,
			binderType,

			// Second initAndShow variation parameters ignored.
			null,
			null,
			null,
			-1,
			-1);
	}

	/**
	 * Initialize and show the dialog (second variation.)
	 * 
	 * @param tagThisDlg
	 * @param entryId
	 * @param entryTitle
	 * @param x
	 * @param y
	 */
	public static void initAndShow(
			final TagThisDlg tagThisDlg,
			final String entryId,
			final String entryTitle,
			final int x,
			final int y) {
		doAsyncOperation(
			// Prefetch parameters.  false -> Not a prefetch.
			false,
			null,
			
			// Creation parameters ignored.
			false,
			false,
			null,
			-1,
			-1,
			null,
			
			// First initAndShow variation parameters ignored.
			null,
			null,
			null,
			null,
			
			// Required second initAndShow variation parameters.
			tagThisDlg,
			entryId,
			entryTitle,
			x,
			y);
	}
	
	/**
	 * Causes the split point for the TagThisDlg to be fetched.
	 * 
	 * @param dlgClient
	 */
	public static void prefetch(TagThisDlgClient dlgClient) {
		// If we weren't give a TagThisDlgClient...
		if (null == dlgClient) {
			// ...create a dummy one...
			dlgClient = new TagThisDlgClient() {				
				@Override
				public void onUnavailable() {
					// Unused.
				}
				
				@Override
				public void onSuccess(TagThisDlg dlg) {
					// Unused.
				}
			};
		}

		// ...and perform the prefetch.
		doAsyncOperation(
			// Prefetch parameters.  true -> Prefetch only.
			true,
			dlgClient,
			
			// Creation parameters ignored.
			false,
			false,
			null,
			-1,
			-1,
			null,
			
			// First initAndShow variation parameters ignored.
			null,
			null,
			null,
			null,
			
			// Second initAndShow variation parameters ignored.
			null,
			null,
			null,
			-1,
			-1);
	}
	
	public static void prefetch() {
		prefetch(null);
	}
}
