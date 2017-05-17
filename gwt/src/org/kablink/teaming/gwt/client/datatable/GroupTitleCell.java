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
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.ManageGroupsDlg.GroupInfoPlus;
import org.kablink.teaming.gwt.client.widgets.ManageGroupsDlg.GroupModificationStatus;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

/**
 * A cell used to render a group title
 */
public class GroupTitleCell extends AbstractCell<GroupInfoPlus>
{
	private static String m_imgHtml;
	
	/**
	 * 
	 */
	public GroupTitleCell()
	{
		// We care about click and keydown action
		super( "click", "keydown" );

		if ( m_imgHtml == null )
		{
			ImageResource imgResource;
			Image img;
			
			imgResource = GwtTeaming.getImageBundle().spinner16();
			img = GwtClientHelper.buildImage( imgResource );
			m_imgHtml = img.toString();
		}
	}

	/**
	 * 
	 */
	@Override
	public void onBrowserEvent( Context context, Element parent, GroupInfoPlus value, NativeEvent event, ValueUpdater<GroupInfoPlus> valueUpdater )
	{
		// Let AbstractCell handle the keydown event.
		super.onBrowserEvent( context, parent, value, event, valueUpdater );
		
		// Handle the click event.
		if ( "click".equals( event.getType() ) )
		{
			EventTarget eventTarget;
			
			// Only want to handle clicks on the group's title
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
	public void render( Context context, GroupInfoPlus value, SafeHtmlBuilder sb )
	{
		GroupInfo groupInfo;
		
		if ( value == null )
		{
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}
		
		groupInfo = value.getGroupInfo();
		if ( groupInfo != null )
		{
			SafeHtml safeValue;
			String secondaryText;
			GroupModificationStatus status;

			// Wrap everything in a <div>
			safeValue = SafeHtmlUtils.fromTrustedString( "<div class=\"groupTitlePanel\" title=\"" );
			sb.append( safeValue );
			secondaryText = groupInfo.getSecondaryDisplayText();
			if ( secondaryText == null )
				secondaryText = "";
			safeValue = SafeHtmlUtils.fromString( secondaryText );
			sb.append( safeValue );
			safeValue = SafeHtmlUtils.fromTrustedString( "\" >" );
			sb.append( safeValue );
			
			// Add the group's title
			sb.appendHtmlConstant( "<span class=\"groupTitle\">" );
			safeValue = SafeHtmlUtils.fromString( groupInfo.getTitle() );
			sb.append( safeValue );
			sb.appendHtmlConstant( "</span>" );
			
			status = value.getStatus();
			if ( status != GroupModificationStatus.READY )
			{
				String statusMsg;
				
				// Add the spinner
				sb.appendHtmlConstant( m_imgHtml );
				
				// Get the appropriate status message.
				if ( status == GroupModificationStatus.GROUP_CREATION_IN_PROGRESS )
					statusMsg = GwtTeaming.getMessages().manageGroupsDlgCreatingGroup();
				else if ( status == GroupModificationStatus.GROUP_DELETION_IN_PROGRESS )
					statusMsg = GwtTeaming.getMessages().manageGroupsDlgDeletingGroup();
				else if ( status == GroupModificationStatus.GROUP_MODIFICATION_IN_PROGRESS )
					statusMsg = GwtTeaming.getMessages().manageGroupsDlgModifyingGroup();
				else if ( status == GroupModificationStatus.GROUP_MEMBERSHIP_MODIFICATION_IN_PROGRESS )
					statusMsg = GwtTeaming.getMessages().manageGroupsDlgUpdatingMembership();
				else
					statusMsg = GwtTeaming.getMessages().manageGroupsDlgUnknownStatus();
					
				// Add a status message
				sb.appendHtmlConstant( "<span class=\"groupStatus\">" );
				sb.appendEscaped( statusMsg );
				sb.appendHtmlConstant( "</span>" );
			}
			
			// Close the <div>
			sb.appendHtmlConstant( "</div>" );
		}
	}

	/**
	 * 
	 */
	@Override
	protected void onEnterKeyDown( Context context, Element parent, GroupInfoPlus value, NativeEvent event, ValueUpdater<GroupInfoPlus> valueUpdater )
	{
		if ( valueUpdater != null )
		{
			valueUpdater.update( value );
		}
	}
}
