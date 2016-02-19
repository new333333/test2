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
import java.util.HashMap;

import org.kablink.teaming.gwt.client.GwtPrincipal;
import org.kablink.teaming.gwt.client.GwtRole;
import org.kablink.teaming.gwt.client.GwtRole.GwtRoleType;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.InvokeEditUserZoneShareRightsDlgEvent;
import org.kablink.teaming.gwt.client.util.PerUserZoneShareRightsInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget is used in the "Edit Zone Share Rights" dialog to select users/groups and define
 * the share rights they have.
 */
public class ZoneShareRightsSelectPrincipalsWidget extends SelectPrincipalsWidget
{
	/**
	 * 
	 */
	private ZoneShareRightsSelectPrincipalsWidget()
	{
		super();
	}

	/**
	 * Add additional information to the principal object.
	 */
	@Override
	protected void addAdditionalPrincipalInfo( GwtPrincipal principal )
	{
		if ( principal != null )
		{
			PerUserZoneShareRightsInfo rightsInfo;
			
			rightsInfo = new PerUserZoneShareRightsInfo();
			principal.setAdditionalData( rightsInfo );
		}
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
	 * Override this method to provide a different fixed height
	 */
	@Override
	protected int getPrincipalsTableFixedHeight()
	{
		return 400;
	}
	
	/**
	 * Override this method to provide a different style name
	 */
	@Override
	protected String getPrincipalsTablePanelStyle()
	{
		return "zoneShareRightsSelectPrincipalsWidget_PrincipalsTablePanelHeight";
	}
	

	/**
	 * Return the list of roles (rights) the user has given out.
	 */
	public ArrayList<GwtRole> getRoles()
	{
		GwtRole shareWithAllInternalRole;
		GwtRole shareWithAllExternalRole;
		GwtRole shareExternalRole;
		GwtRole shareInternalRole;
		GwtRole sharePublicRole;
		GwtRole reshareRole;
		GwtRole shareLinkRole;
		ArrayList<GwtRole> roles;
		ArrayList<GwtPrincipal> principals;
		
		roles = new ArrayList<GwtRole>();

		// Create the necessary role objects
		{
			shareWithAllInternalRole = new GwtRole();
			shareWithAllInternalRole.setType( GwtRoleType.EnableShareWithAllInternal );
			roles.add( shareWithAllInternalRole );

			shareWithAllExternalRole = new GwtRole();
			shareWithAllExternalRole.setType( GwtRoleType.EnableShareWithAllExternal );
			roles.add( shareWithAllExternalRole );

			shareExternalRole = new GwtRole();
			shareExternalRole.setType( GwtRoleType.EnableShareExternal );
			roles.add( shareExternalRole );

			reshareRole = new GwtRole();
			reshareRole.setType( GwtRoleType.EnableShareForward );
			roles.add( reshareRole );

			shareInternalRole = new GwtRole();
			shareInternalRole.setType( GwtRoleType.EnableShareInternal );
			roles.add( shareInternalRole );
			
			sharePublicRole = new GwtRole();
			sharePublicRole.setType( GwtRoleType.EnableSharePublic );
			roles.add( sharePublicRole );
			
			shareLinkRole = new GwtRole();
			shareLinkRole.setType( GwtRoleType.EnableShareLink );
			roles.add( shareLinkRole );
		}
		
		// Get the list of principals
		principals = getListOfSelectedPrincipals();
		if ( principals != null && principals.size() > 0 )
		{
			for ( GwtPrincipal nextPrincipal : principals )
			{
				Object obj;
				
				obj = nextPrincipal.getAdditionalData();
				if ( obj != null && obj instanceof PerUserZoneShareRightsInfo )
				{
					PerUserZoneShareRightsInfo rightsInfo;
					
					rightsInfo = (PerUserZoneShareRightsInfo) obj;
					
					if ( rightsInfo.getIsEnableShareForwarding() )
						reshareRole.addMember( nextPrincipal );
						
					if ( rightsInfo.getIsEnableShareExternal() )
						shareExternalRole.addMember( nextPrincipal );
					
					if ( rightsInfo.getIsEnableShareInternal() )
						shareInternalRole.addMember( nextPrincipal );
					
					if ( rightsInfo.getIsEnableSharePublic() )
						sharePublicRole.addMember( nextPrincipal );
					
					if ( rightsInfo.getIsEnableShareWithAllExternal() )
						shareWithAllExternalRole.addMember( nextPrincipal );
					
					if ( rightsInfo.getIsEnableShareWithAllInternal() )
						shareWithAllInternalRole.addMember( nextPrincipal );
					
					if ( rightsInfo.getIsEnableShareWithAllInternal() )
						shareWithAllInternalRole.addMember( nextPrincipal );
					
					if ( rightsInfo.getIsEnableShareLink() )
						shareLinkRole.addMember( nextPrincipal );
				}
			}
		}
		
		return roles;
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
			if ( principal.getAdditionalData() instanceof PerUserZoneShareRightsInfo )
			{
				ZoneShareRightsWidget widget;
				PerUserZoneShareRightsInfo rightsInfo;
				
				rightsInfo = (PerUserZoneShareRightsInfo) principal.getAdditionalData();
				widget = new ZoneShareRightsWidget( rightsInfo, principal.getIdLong() );
				return widget;
			}
			
			return new InlineLabel( "Could not get PerUserZoneShareRightsInfo" );
		}
		
		if ( col == 2 )
		{
			// Add the recipient type
			return new InlineLabel( principal.getTypeAsString() );
		}
		
		return null;
	}

	/**
	 * 
	 */
	public void initWidget( ArrayList<GwtRole> listOfRoles )
	{
		HashMap<Long, GwtPrincipal> listOfPrincipals;
		
		listOfPrincipals = new HashMap<Long, GwtPrincipal>();
		
		if ( listOfRoles != null )
		{
			// Go through each role and add each member of the role to our list of principals.
			// We only want a member to appear once in the list.
			for ( GwtRole nextRole : listOfRoles )
			{
				ArrayList<GwtPrincipal> listOfMembers;
				
				listOfMembers = nextRole.getListOfMembers();
				if ( listOfMembers == null )
					continue;
				
				for ( GwtPrincipal nextMember : listOfMembers )
				{
					GwtPrincipal principal;
					Object obj;
					
					// Is this principal already in our list.
					principal = listOfPrincipals.get( nextMember.getIdLong() );
					if ( principal == null )
					{
						// No, add them.
						listOfPrincipals.put( nextMember.getIdLong(), nextMember );
						principal = nextMember;
						principal.setAdditionalData( new PerUserZoneShareRightsInfo() );
					}
					
					// Initialize the rights this user has
					obj = principal.getAdditionalData();
					if ( obj != null && obj instanceof PerUserZoneShareRightsInfo )
					{
						PerUserZoneShareRightsInfo rightsInfo;

						rightsInfo = (PerUserZoneShareRightsInfo) obj;
						
						switch ( nextRole.getType() )
						{
						case EnableShareExternal:
							rightsInfo.setEnableShareExternal( true );
							break;
						
						case EnableShareForward:
							rightsInfo.setEnableShareForwarding( true );
							break;
							
						case EnableShareInternal:
							rightsInfo.setEnableShareInternal( true );
							break;
							
						case EnableSharePublic:
							rightsInfo.setEnableSharePublic( true );
							break;
						
						case EnableShareWithAllExternal:
							rightsInfo.setEnableShareWithAllExternal( true );
							break;
							
						case EnableShareWithAllInternal:
							rightsInfo.setEnableShareWithAllInternal( true );
							break;
							
						case EnableShareLink:
							rightsInfo.setEnableShareLink( true );
							break;
						
						default:
							break;
						}
					}
				}
			}
		}

		init( new ArrayList<GwtPrincipal>( listOfPrincipals.values() ) );
	}

	/**
	 * This method gets called when the user adds a principal to the list.  We will add a
	 * PerUserEnableShareRightsInfo object to the principal
	 */
	@Override
	protected void principalAdded( GwtPrincipal principal )
	{
		if ( principal != null )
		{
			InvokeEditUserZoneShareRightsDlgEvent event;
			
			// Fire an event to invoke the "edit share rights" dialog.
			event = new InvokeEditUserZoneShareRightsDlgEvent( principal.getIdLong() );
			GwtTeaming.fireEvent( event );
		}
	}
	
	
	
	/**
	 * Callback interface to interact with the "ZoneShareRightsSelectPrincipalsWidget"
	 * asynchronously after it loads. 
	 */
	public interface ZoneShareRightsSelectPrincipalsWidgetClient
	{
		void onSuccess( ZoneShareRightsSelectPrincipalsWidget widget );
		void onUnavailable();
	}
	
	/**
	 * Loads the ZoneShareRightsSelectPrincipalsWidget split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
		final ZoneShareRightsSelectPrincipalsWidgetClient client )
	{
		GWT.runAsync( ZoneShareRightsSelectPrincipalsWidget.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ZoneShareRightsSelectPrincipalsWidget() );
				if ( client != null )
				{
					client.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ZoneShareRightsSelectPrincipalsWidget widget;
				
				widget= new ZoneShareRightsSelectPrincipalsWidget();
				client.onSuccess( widget );
			}
		});
	}
}
