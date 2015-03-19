/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.datatable;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.NetFolder;
import org.kablink.teaming.gwt.client.NetFolder.NetFolderSyncStatus;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;


/**
 * A cell used to render a Net Folder sync status
 */
public class NetFolderSyncStatusCell extends AbstractCell<NetFolder>
{
	private static String m_waitingForSyncImgHtml = null;
	private static String m_syncInProgressImgHtml = null;
	private static String m_syncCanceledImgHtml = null;
	private static String m_syncStoppedImgHtml = null;
	private static String m_syncCompletedImgHtml = null;
	private static String m_syncNeverRunImgHtml = null;
	private static String m_syncStatusUnknownImgHtml = null;

	/**
	 * 
	 */
	public NetFolderSyncStatusCell()
	{
		// We care about click and keydown action
		super( "click", "keydown" );

		if ( m_waitingForSyncImgHtml == null )
		{
			ImageResource imgResource;
			Image img;
			
			imgResource = GwtTeaming.getImageBundle().netFolderSyncStatusWaitingToBeSyncd();
			img = GwtClientHelper.buildImage( imgResource );
			img.setTitle( GwtTeaming.getMessages().netFolderSyncStatusWaitingToBeSyncd() );
			img.addStyleName( "netFolder_syncStatusImg" );
			m_waitingForSyncImgHtml = img.toString();
		}

		if ( m_syncInProgressImgHtml == null )
		{
			ImageResource imgResource;
			Image img;
			
			imgResource = GwtTeaming.getImageBundle().netFolderSyncStatusInProgress();
			img = GwtClientHelper.buildImage( imgResource );
			img.setTitle( GwtTeaming.getMessages().netFolderSyncStatusInProgress() );
			img.addStyleName( "netFolder_syncStatusImg" );
			m_syncInProgressImgHtml = img.toString();
		}

		if ( m_syncStoppedImgHtml == null )
		{
			ImageResource imgResource;
			Image img;
			
			imgResource = GwtTeaming.getImageBundle().netFolderSyncStatusStopped();
			img = GwtClientHelper.buildImage( imgResource );
			img.setTitle( GwtTeaming.getMessages().netFolderSyncStatusStopped() );
			img.addStyleName( "netFolder_syncStatusImg" );
			m_syncStoppedImgHtml = img.toString();
		}

		if ( m_syncCanceledImgHtml == null )
		{
			ImageResource imgResource;
			Image img;
			
			imgResource = GwtTeaming.getImageBundle().netFolderSyncStatusCanceled();
			img = GwtClientHelper.buildImage( imgResource );
			img.setTitle( GwtTeaming.getMessages().netFolderSyncStatusCanceled() );
			img.addStyleName( "netFolder_syncStatusImg" );
			m_syncCanceledImgHtml = img.toString();
		}

		if ( m_syncCompletedImgHtml == null )
		{
			ImageResource imgResource;
			Image img;
			
			imgResource = GwtTeaming.getImageBundle().netFolderSyncStatusCompleted();
			img = GwtClientHelper.buildImage( imgResource );
			img.setTitle( GwtTeaming.getMessages().netFolderSyncStatusCompleted() );
			img.addStyleName( "netFolder_syncStatusImg" );
			m_syncCompletedImgHtml = img.toString();
		}

		if ( m_syncNeverRunImgHtml == null )
		{
			ImageResource imgResource;
			Image img;
			
			imgResource = GwtTeaming.getImageBundle().netFolderSyncStatusNeverRun();
			img = GwtClientHelper.buildImage( imgResource );
			img.setTitle( GwtTeaming.getMessages().netFolderSyncStatusNeverRun() );
			img.addStyleName( "netFolder_syncStatusImg" );
			m_syncNeverRunImgHtml = img.toString();
		}


		if ( m_syncStatusUnknownImgHtml == null )
		{
			ImageResource imgResource;
			Image img;
			
			imgResource = GwtTeaming.getImageBundle().netFolderSyncStatusUnknown();
			img = GwtClientHelper.buildImage( imgResource );
			img.setTitle( GwtTeaming.getMessages().netFolderSyncStatusUnknown() );
			img.addStyleName( "netFolder_syncStatusImg" );
			m_syncStatusUnknownImgHtml = img.toString();
		}
	}

	/**
	 * 
	 */
	@Override
	public void onBrowserEvent(
		Context context,
		Element parent,
		NetFolder value,
		NativeEvent event,
		ValueUpdater<NetFolder> valueUpdater )
	{
		// Let AbstractCell handle the keydown event.
		super.onBrowserEvent( context, parent, value, event, valueUpdater );
		
		// Handle the click event.
		if ( "click".equals( event.getType() ) )
		{
			EventTarget eventTarget;
			
			// Only want to handle clicks on the status image
			eventTarget = event.getEventTarget();
			if ( parent.getFirstChildElement().isOrHasChild( Element.as( eventTarget ) ) )
			{
				valueUpdater.update( value );
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public void render( Context context, NetFolder value, SafeHtmlBuilder sb )
	{
		if ( value == null )
		{
			GwtClientHelper.renderEmptyHtml( sb );
			return;
		}
		
		{
			NetFolderSyncStatus status;

			// Wrap everything in a <div>
			sb.appendHtmlConstant( "<div class=\"netFolder_SyncStatusPanel\">" );
			
			status = value.getStatus();
			switch ( status )
			{
			case SYNC_CANCELED:
				sb.appendHtmlConstant( m_syncCanceledImgHtml );
				break;
			
			case SYNC_COMPLETED:
				sb.appendHtmlConstant( m_syncCompletedImgHtml );
				break;
			
			case SYNC_IN_PROGRESS:
				sb.appendHtmlConstant( m_syncInProgressImgHtml );
				break;
				
			case SYNC_NEVER_RUN:
				sb.appendHtmlConstant( m_syncNeverRunImgHtml );
				break;
				
			case SYNC_STOPPED:
				sb.appendHtmlConstant( m_syncStoppedImgHtml );
				break;
				
			case WAITING_TO_BE_SYNCD:
				sb.appendHtmlConstant( m_waitingForSyncImgHtml );
				break;
				
			case DELETE_FAILED:
				// Add a status message
				sb.appendHtmlConstant( "<span class=\"netFolderStatus\">" );
				sb.appendEscaped( GwtTeaming.getMessages().manageNetFoldersDlg_DeleteFailed() );
				sb.appendHtmlConstant( "</span>" );
				break;
				
			case DELETE_IN_PROGRESS:
				// Add a status message
				sb.appendHtmlConstant( "<span class=\"netFolderStatus\">" );
				sb.appendEscaped( GwtTeaming.getMessages().manageNetFoldersDlg_DeleteInProgress() );
				sb.appendHtmlConstant( "</span>" );
				break;
				
			default:
				sb.appendHtmlConstant( m_syncStatusUnknownImgHtml );
			}
			
			// Close the <div>
			sb.appendHtmlConstant( "</div>" );
		}
	}

	/**
	 * 
	 */
	@Override
	protected void onEnterKeyDown(
		Context context,
		Element parent,
		NetFolder value,
		NativeEvent event,
		ValueUpdater<NetFolder> valueUpdater )
	{
		if ( valueUpdater != null )
		{
			valueUpdater.update( value );
		}
	}
}
