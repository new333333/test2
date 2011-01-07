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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTag;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TagType;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;


/**
 * Implements a dialog for managing folder and workspace tags.
 *  
 * @author drfoster@novell.com
 */
public class TagThisDlg extends DlgBox
	implements ActionHandler, EditSuccessfulHandler, EditCanceledHandler
{
	private final static int	MAX_TAG_LENGTH		= 60;				// As per ObjectKeys.MAX_TAG_LENGTH.
	private final static int	VISIBLE_TAG_LENGTH	= 20;				// Any better guesses?

	private ActionTrigger m_actionTrigger;			// Interface to use to trigger teaming actions.
	private String m_binderId;						// Id of the binder we are working with.  This can be null if m_entryId is not null.
	private String m_entryId;						// Id of the entry we are working with.  This can be null if m_binderId is not null.
	private boolean m_isPublicTagManager;			// true -> The user can manage public tags on the binder.  false -> They can't.
	private GwtTeamingMainMenuImageBundle m_images;	// Access to the GWT main menu images.
	private GwtTeamingMessages m_messages;			// Access to the GWT UI messages.
	private AsyncCallback<ArrayList<TagInfo>> m_readTagsCallback = null;
	private ArrayList<TagInfo> m_currentListOfTags;
	private ArrayList<TagInfo> m_toBeAdded;			// List of tags to be added.
	private ArrayList<TagInfo> m_toBeDeleted;		// List of tags to be deleted.
	private RadioButton m_personalRB;
	private RadioButton m_communityRB;
	private FindCtrl m_findCtrl;
	private FlexTable m_table;						// Holds a list of tags applied to the given binder/entry.
	private FlexTable.FlexCellFormatter m_cellFormatter;
	private ImageResource m_deleteImgR;

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
		public void onClick( ClickEvent event )
		{
			deleteTag( m_tagInfo );
		}
	}
	
	
	/**
	 * Class constructor.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param currentBinder
	 * @param binderTags
	 * @param isPublicTagManager
	 * @param dlgCaption
	 */
	public TagThisDlg(
		boolean autoHide,
		boolean modal,
		ActionTrigger actionTrigger,
		int left,
		int top,
		String dlgCaption )
	{
		// Initialize the superclass...
		super(autoHide, modal, left, top );

		// ...initialize everything else...
		m_actionTrigger = actionTrigger;
		m_messages = GwtTeaming.getMessages();
		m_images = GwtTeaming.getMainMenuImageBundle();
		m_toBeAdded = new ArrayList<TagInfo>();
		m_toBeDeleted = new ArrayList<TagInfo>();
	
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
		
		row = 1;
		m_cellFormatter.setColSpan( row, 0, 3 );
		m_cellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_cellFormatter.addStyleName( row, 0, "oltBorderRight" );
		m_cellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 0, "oltLastRowBorderBottom" );

		m_table.setText( row, 0, GwtTeaming.getMessages().noTags() );
	}
	
	
	/**
	 * Add the given tag to the end of the table that holds the list of tags.
	 */
	private void addTagToTable( TagInfo tagInfo )
	{
		String type;
		int row;
		DeleteTagWidget delWidget;
		
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
			if ( text != null && text.equalsIgnoreCase( GwtTeaming.getMessages().noTags() ) )
			{
				// Yes
				m_table.removeRow( 1 );
				--row;
			}
		}
		
		// Add the tag name in the first column.
		m_cellFormatter.setColSpan( row, 0, 1 );
		m_table.setText( row, 0, tagInfo.getTagName() );

		// Add the tag type in the second column.
		if ( tagInfo.getTagType() == TagType.PERSONAL )
			type = GwtTeaming.getMessages().personal();
		else if ( tagInfo.getTagType() == TagType.COMMUNITY )
			type = GwtTeaming.getMessages().community();
		else
			type = GwtTeaming.getMessages().unknownTagType();
		m_table.setText( row, 1, type );

		// Add the delete image to the 3rd column.
		delWidget = new DeleteTagWidget( tagInfo );
		m_table.setWidget( row, 2, delWidget );

		// Add the necessary styles to the cells in the row.
		m_cellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_cellFormatter.addStyleName( row, 2, "oltBorderRight" );
		m_cellFormatter.addStyleName( row, 0, "oltContentBorderBottom" );
		m_cellFormatter.addStyleName( row, 1, "oltContentBorderBottom" );
		m_cellFormatter.addStyleName( row, 2, "oltContentBorderBottom" );
		m_cellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 1, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 2, "oltContentPadding" );
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
		
		// Add a "personal" radio button.
		{
			m_personalRB = new RadioButton( "tag-type", GwtTeaming.getMessages().personal() );
			m_personalRB.setValue( Boolean.TRUE );
			mainPanel.add( m_personalRB );
		
			// Add a click handler for the personal rb
			clickHandler = new ClickHandler()
			{
				public void onClick( ClickEvent clickEvent )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
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
				public void onClick( ClickEvent clickEvent )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
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
			FlexTable table;
			HTMLTable.RowFormatter rowFormatter;

			table = new FlexTable();
			rowFormatter = table.getRowFormatter();
			rowFormatter.setVerticalAlign( 0, HasVerticalAlignment.ALIGN_TOP );
			mainPanel.add( table );
			
			m_findCtrl = new FindCtrl( this, GwtSearchCriteria.SearchType.PERSONAL_TAGS );
			table.setWidget( 0, 0, m_findCtrl );
	
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
					public void onClick( ClickEvent clickEvent )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
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
		
		// Create a table to hold the list of tags that are associated with the given binder/entry.
		{
			HTMLTable.RowFormatter rowFormatter;
			
			m_table = new FlexTable();
			m_table.addStyleName( "paddingTop8px" );
			m_table.setCellSpacing( 0 );
			m_table.setWidth( "100%" );

			// Add the column headers.
			{
				m_table.setText( 0, 0, GwtTeaming.getMessages().tagName() );
				m_table.setText( 0, 1, GwtTeaming.getMessages().tagType() );
				m_table.setText( 0, 2, "" );	// The delete image will go in this column.
				
				rowFormatter = m_table.getRowFormatter();
				rowFormatter.addStyleName( 0, "oltHeader" );

				// Set the width of each column to 50%
				m_cellFormatter = m_table.getFlexCellFormatter();
				m_cellFormatter.setWidth( 0, 0, "50%" );
				m_cellFormatter.setWidth( 0, 1, "50%" );
				m_cellFormatter.setWidth( 0, 2, "*" );
				
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
			
			mainPanel.add( m_table );
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
			// Yes
			// Remove the tag from the table.
			m_table.removeRow( row );
			
			// Did we remove the last tag from the table?
			if ( m_table.getRowCount() == 1 )
			{
				// Yes
				// Add the "no tags..." message to the table.
				addNoTagsMessage();
			}
			
			// Remember we need to delete this tag.
			m_toBeDeleted.add( tagInfo );
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
	public boolean editCanceled()
	{
		// Simply return true to allow the dialog to close.
		return true;
	}

	
	/**
	 * This method gets called when user user presses the OK push
	 * button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	public boolean editSuccessful( Object callbackData )
	{
		// Nothing to do.  Return true to close the dialog.
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
		return m_findCtrl.getFocusWidget();
	}
	
	/**
	 * This method gets called when the user selects an item from the search results in the "find" control.
	 */
	public void handleAction( TeamingAction ta, Object selectedObj )
	{
		if ( TeamingAction.SELECTION_CHANGED == ta )
		{
			// Make sure we are dealing with a tag.
			if ( selectedObj instanceof GwtTag )
			{
				TagInfo tagInfo;
				
				// Does this tag already exist in the list?
				//!!! Finish
				
				// Create a TagInfo object and initialize it.
				tagInfo = new TagInfo();
				tagInfo.setTagName( ((GwtTag) selectedObj).getTagName() );
				tagInfo.setTagType( getSelectedTagType() );
				
				// Add the tag to the table that holds the list of tags.
				addTagToTable( tagInfo );
				
				// Hide the search-results widget.
				m_findCtrl.hideSearchResults();
			}
		}
	}
	
	/**
	 * This method gets called when the user clicks on the "add tag" image.  We will take the name
	 * of the tag, validate it and try to add it.
	 */
	private void handleClickOnAddTag()
	{
		
	}
	
	/**
	 * Return the type of tag, personal or community, the user has selected.
	 */
	private TagType getSelectedTagType()
	{
		if ( m_personalRB.getValue() == Boolean.TRUE )
			return TagType.PERSONAL;
		
		if ( m_communityRB.isVisible() && m_communityRB.getValue() == Boolean.TRUE )
			return TagType.COMMUNITY;
		
		return TagType.UNKNOWN;
	}
	
	/**
	 * Initialize the dialog for the given binderId or entryId.
	 *
	 * @param binderId - Id of the binder we are working with.  Can be null if entryId is not null.
	 * @param entryId - Id of the entry we are working with.  Can be null if binderId is not null.
	 */
	public void init( String binderId, String entryId )
	{
		m_binderId = binderId;
		m_entryId = entryId;
		m_isPublicTagManager = false;

		if ( m_readTagsCallback == null )
		{
			// Create a callback that will be used when we read the tags for a binder or entry.
			m_readTagsCallback = new AsyncCallback<ArrayList<TagInfo>>()
			{
				/**
				 * 
				 */
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
				public void onSuccess( ArrayList<TagInfo> tags )
				{
					// Update the dialog with the list of tags.
					updateDlg( tags );
				}
			};
		}
		
		// Are we working with a binder?
		if ( m_binderId != null && m_binderId.length() > 0 )
		{
			// Yes, Issue a request to get the tags associated with the given binder.
			GwtTeaming.getRpcService().getBinderTags( HttpRequestInfo.createHttpRequestInfo(), m_binderId, m_readTagsCallback );
		}
		else if ( m_entryId != null && m_entryId.length() > 0 )
		{
			// We are working with an entry.
			// Issue a request to get the tags associated with the given entry.
			GwtTeaming.getRpcService().getEntryTags( HttpRequestInfo.createHttpRequestInfo(), m_entryId, m_readTagsCallback );
		}
		else
		{
			Window.alert( "binderId and entryId are both empty.  This doesn't work!!!" );
			return;
		}
	}


	/**
	 * Update the dialog to reflect the given list of tags associated with the binder/entry. 
	 */
	private void updateDlg( ArrayList<TagInfo> tags )
	{
		int i;
		
		m_toBeDeleted.clear();
		m_toBeAdded.clear();
		m_currentListOfTags = tags;
		
		// Remove all of the rows from the table.
		// We start at row 1 so we don't delete the header.
		for (i = 1; i < m_table.getRowCount(); ++i)
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
	
	
	/*
	 * Does what's needed to validate tagName.  The user is informed
	 * if any errors are detected.
	 * 
	 * If the string needs to be modified to be validated, the modified
	 * string is returned.
	 * 
	 * Implementation logic is based on that in the ss_tagModify()
	 * method in ss_tags.js.
	 */
	private String validateTagName(String tagName, TagType tagType)
	{
		// Do we have a tag name to validate?
		String reply = ((null == tagName) ? tagName : tagName.trim());
	
		if (GwtClientHelper.hasString(reply)) {
			// Yes!  If the tag contains spaces...
			if (0 <= reply.indexOf(" ")) {
				// ...tell the user that's not valid and bail.
				Window.alert(m_messages.mainMenuTagThisDlgErrorTagHasSpaces());
				return "";
			}

			// If the tag contains underscores...
			if (0 <= reply.indexOf("_")) {
				// ...tell the user that's not valid and bail.
				Window.alert(m_messages.mainMenuTagThisDlgErrorTagHasUnderscores());
				return "";
			}

			// If the tag is too long...
			if (MAX_TAG_LENGTH < reply.length()) {
				// ...tell the user and truncate it.
				Window.alert(m_messages.mainMenuTagThisDlgWarningTagTruncated());
				reply = reply.substring(0, (MAX_TAG_LENGTH - 1));
			}

			// If the tag contains punctuation characters...
			if (containsPunctuation(reply)) {
				// ...tell the user that's not valid and bail.
				Window.alert(m_messages.mainMenuTagThisDlgErrorTagHasPunctuation());
				return "";
			}

			// If this tag is a duplicate...
		//!!! Finish
		/*
			List<TagInfo> tagsList = ((TagType.COMMUNITY == tagType) ? m_communityTags : m_personalTags);
			for (Iterator<TagInfo> tagsIT = tagsList.iterator(); tagsIT.hasNext(); ) {
				TagInfo tag = tagsIT.next();
				if (tag.getTagName().equalsIgnoreCase(reply)) {
					// ...tell the user that's not valid and bail.
					Window.alert(m_messages.mainMenuTagThisDlgErrorDuplicateTag());
					return "";
				}
			}
		*/
		}
		
		// If we get here, reply refers to the validated string or an
		// empty string if the tag was not valid.  Return it.
		return reply;
	}
}
