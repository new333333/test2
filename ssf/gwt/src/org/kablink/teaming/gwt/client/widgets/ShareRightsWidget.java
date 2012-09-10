/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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


import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.ShareRights.AccessRights;
import org.kablink.teaming.gwt.client.widgets.EditShareRightsDlg.EditShareRightsDlgClient;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * This widget is used to display the rights for a given share and allow the user
 * to change the rights.
 */
public class ShareRightsWidget extends Composite
	implements ClickHandler
{
	private GwtShareItem m_shareInfo;
	private AccessRights m_highestRightsPossible;
	private InlineLabel m_rightsLabel;
	private Image m_rightsImg;
	private EditSuccessfulHandler m_editShareRightsHandler;

	private static EditShareRightsDlg m_editShareRightsDlg;
	
	
	/**
	 * 
	 */
	public ShareRightsWidget(
		GwtShareItem shareInfo,
		AccessRights highestRightsPossible )
	{
		ImageResource imageResource;
		
		m_shareInfo = shareInfo;
		m_highestRightsPossible = highestRightsPossible;

		m_rightsLabel = new InlineLabel( shareInfo.getShareRightsAsString() );
		m_rightsLabel.addStyleName( "shareThisDlg_RightsLabel" );
		m_rightsLabel.addClickHandler( this );
		
		imageResource = GwtTeaming.getImageBundle().activityStreamActions1();
		m_rightsImg = new Image( imageResource );
		m_rightsImg.getElement().setAttribute( "align", "absmiddle" );
		m_rightsLabel.getElement().appendChild( m_rightsImg.getElement() );

		initWidget( m_rightsLabel );
	}

	/**
	 * This method gets called when the user clicks on the current rights a user has.
	 * We will pop up a dialog to let the user change the rights.
	 */
	private void invokeEditShareRightsDlg()
	{
		if ( m_editShareRightsDlg != null )
		{
			if ( m_editShareRightsHandler == null )
			{
				m_editShareRightsHandler = new EditSuccessfulHandler()
				{
					@Override
					public boolean editSuccessful( Object obj )
					{
						if ( obj instanceof Boolean )
						{
							Boolean retValue;
							
							// Did the "Edit Share Rights" dialog successfully update
							// our GwtShareItem.
							retValue = (Boolean) obj;
							if ( retValue == true )
								updateRightsLabel();
						}

						return true;
					}
				};
			}
			
			// Invoke the "edit share rights" dialog.
			m_editShareRightsDlg.init( m_shareInfo, m_highestRightsPossible, m_editShareRightsHandler );
			m_editShareRightsDlg.showRelativeToTarget( m_rightsLabel );
		}
		else
		{
			EditShareRightsDlg.createAsync( true, true, new EditShareRightsDlgClient()
			{
				@Override
				public void onUnavailable() 
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( EditShareRightsDlg esrDlg )
				{
					m_editShareRightsDlg = esrDlg;
					invokeEditShareRightsDlg();
				}
			} );
			
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onClick( ClickEvent event )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				invokeEditShareRightsDlg();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}

	/**
	 * 
	 */
	private void updateRightsLabel()
	{
		m_rightsLabel.setText( m_shareInfo.getShareRightsAsString() );
		m_rightsLabel.getElement().appendChild( m_rightsImg.getElement() );
	}
}

