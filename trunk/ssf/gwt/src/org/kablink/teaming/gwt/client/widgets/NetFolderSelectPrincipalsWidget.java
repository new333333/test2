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

import org.kablink.teaming.gwt.client.GwtPrincipal;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ShareForwardRights;
import org.kablink.teaming.gwt.client.widgets.SelectPrincipalsWidget.PrincipalNameWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class NetFolderSelectPrincipalsWidget extends SelectPrincipalsWidget
{
	/**
	 * 
	 */
	private NetFolderSelectPrincipalsWidget()
	{
		super();
	}

	/**
	 * 
	 */
	@Override
	public String getColName( int col )
	{
		if ( col == 0 )
			return GwtTeaming.getMessages().selectPrincipalsWidget_NameCol();
	
		if ( col == 1 )
			return GwtTeaming.getMessages().selectPrincipalsWidget_RightsCol();
		
		if ( col == 2 )
			return GwtTeaming.getMessages().selectPrincipalsWidget_TypeCol();
		
		return "Unknown";
	}
	
	/**
	 * 
	 */
	@Override
	public String getColWidth( int col )
	{
		if ( col == 0 )
			return "55%";
	
		if ( col == 1 )
			return "20%";
		
		if ( col == 2 )
			return "20%";
		
		return "10%";
	}
	
	/**
	 * 
	 */
	@Override
	public int getNumCols()
	{
		return 3;
	}
	
	/**
	 * 
	 */
	@Override
	protected Widget getWidgetForCol( int col, GwtPrincipal principal )
	{
		if ( principal == null )
			return null;
		
		if ( col == 0 )
		{
			// Add the principal name
			return new PrincipalNameWidget( principal );
		}
		
		if ( col == 1 )
		{
			if ( principal.getAdditionalData() instanceof ShareForwardRights )
			{
				ShareForwardRights rights;
				
				rights = (ShareForwardRights) principal.getAdditionalData();
				return new InlineLabel( rights.getShareRightsAsString() );
			}
			
			return new InlineLabel( "Could not get ShareForwardRights" );
		}
		
		if ( col == 2 )
		{
			// Add the recipient type
			return new InlineLabel( principal.getTypeAsString() );
		}
		
		return null;
	}
	
	/**
	 * This method gets called when the user adds a principal to the list.  We will add a
	 * ShareRights object to the principal
	 */
	@Override
	protected void principalAdded( GwtPrincipal principal )
	{
		if ( principal != null )
		{
			principal.setAdditionalData( new ShareForwardRights() );
		}
	}
	
	
	
	/**
	 * Callback interface to interact with the "NetFolderSelectPrincipalsWidget"
	 * asynchronously after it loads. 
	 */
	public interface NetFolderSelectPrincipalsWidgetClient
	{
		void onSuccess( NetFolderSelectPrincipalsWidget widget );
		void onUnavailable();
	}
	
	/**
	 * Loads the NetFolderSelectPrincipalsWidget split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
		final NetFolderSelectPrincipalsWidgetClient client )
	{
		GWT.runAsync( NetFolderSelectPrincipalsWidget.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_NetFolderSelectPrincipalsWidget() );
				if ( client != null )
				{
					client.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				NetFolderSelectPrincipalsWidget widget;
				
				widget= new NetFolderSelectPrincipalsWidget();
				client.onSuccess( widget );
			}
		});
	}
}

