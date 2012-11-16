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
import org.kablink.teaming.gwt.client.util.PerUserRightsInfo;
import org.kablink.teaming.gwt.client.widgets.EditNetFolderRightsDlg.EditNetFolderRightsDlgClient;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * This widget is used to display the rights a user has to a net folder and allow the user
 * to change the rights.
 */
public class NetFolderRightsWidget extends Composite
	implements ClickHandler
{
	private PerUserRightsInfo m_rightsInfo;
	private InlineLabel m_rightsLabel;
	private Image m_rightsImg;
	private EditSuccessfulHandler m_editRightsHandler;

	private static EditNetFolderRightsDlg m_editNetFolderRightsDlg;
	
	
	/**
	 * 
	 */
	public NetFolderRightsWidget( PerUserRightsInfo rightsInfo )
	{
		ImageResource imageResource;
		
		m_rightsInfo = rightsInfo;

		m_rightsLabel = new InlineLabel( m_rightsInfo.getRightsAsString() );
		m_rightsLabel.addStyleName( "netFolderRightsWidget_RightsLabel" );
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
	private void invokeEditRightsDlg()
	{
		if ( m_editNetFolderRightsDlg != null )
		{
			if ( m_editRightsHandler == null )
			{
				m_editRightsHandler = new EditSuccessfulHandler()
				{
					@Override
					public boolean editSuccessful( Object obj )
					{
						if ( obj instanceof Boolean )
						{
							Boolean retValue;
							
							// Did the "Edit Net Folder Rights" dialog successfully update
							// our PerUserRightsInfo object?
							retValue = (Boolean) obj;
							if ( retValue == true )
								updateRightsLabel();
						}

						return true;
					}
				};
			}
			
			// Invoke the "edit net folder rights" dialog.
			m_editNetFolderRightsDlg.init( m_rightsInfo, m_editRightsHandler );
			m_editNetFolderRightsDlg.showRelativeToTarget( m_rightsLabel );
		}
		else
		{
			EditNetFolderRightsDlg.createAsync( true, true, new EditNetFolderRightsDlgClient()
			{
				@Override
				public void onUnavailable() 
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( EditNetFolderRightsDlg enfrDlg )
				{
					m_editNetFolderRightsDlg = enfrDlg;
					invokeEditRightsDlg();
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
				invokeEditRightsDlg();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}

	/**
	 * 
	 */
	private void updateRightsLabel()
	{
		m_rightsLabel.setText( m_rightsInfo.getRightsAsString() );
		m_rightsLabel.getElement().appendChild( m_rightsImg.getElement() );
	}
}

