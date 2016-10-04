/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.event.InvokeEditNetFolderRightsDlgEvent;
import org.kablink.teaming.gwt.client.util.PerEntityShareRightsInfo;
import org.kablink.teaming.gwt.client.util.PerUserRightsInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ?
 * 
 * @author jwootton@novell.com 
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
	 * Add additional information to the principal object.
	 */
	@Override
	protected void addAdditionalPrincipalInfo( GwtPrincipal principal )
	{
		if ( principal != null )
		{
			PerUserRightsInfo rightsInfo;
			
			rightsInfo = new PerUserRightsInfo( new PerEntityShareRightsInfo(), false );
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
	 * Return the list of roles (rights) the user has given out.
	 */
	public ArrayList<GwtRole> getRoles()
	{
		GwtRole viewRole;
		GwtRole shareExternalRole;
		GwtRole shareInternalRole;
		GwtRole sharePublicRole;
		GwtRole sharePublicLinkRole;
		GwtRole reshareRole;
		GwtRole reshareFolderRole;
		GwtRole shareFolderInternalRole;
		GwtRole shareFolderExternalRole;
		GwtRole shareFolderPublicRole;
		ArrayList<GwtRole> roles;
		ArrayList<GwtPrincipal> principals;
		
		roles = new ArrayList<GwtRole>();

		// Create the necessary role objects
		{
			viewRole = new GwtRole();
			viewRole.setType( GwtRoleType.AllowAccess );
			roles.add( viewRole );

			shareExternalRole = new GwtRole();
			shareExternalRole.setType( GwtRoleType.ShareExternal );
			roles.add( shareExternalRole );
			
			shareFolderExternalRole = new GwtRole();
			shareFolderExternalRole.setType( GwtRoleType.ShareFolderExternal );
			roles.add( shareFolderExternalRole );

			reshareRole = new GwtRole();
			reshareRole.setType( GwtRoleType.ShareForward );
			roles.add( reshareRole );
			
			reshareFolderRole = new GwtRole();
			reshareFolderRole.setType( GwtRoleType.ShareFolderForward );
			roles.add( reshareFolderRole );			

			shareInternalRole = new GwtRole();
			shareInternalRole.setType( GwtRoleType.ShareInternal );
			roles.add( shareInternalRole );
			
			shareFolderInternalRole = new GwtRole();
			shareFolderInternalRole.setType( GwtRoleType.ShareFolderInternal );
			roles.add( shareFolderInternalRole );
			
			sharePublicRole = new GwtRole();
			sharePublicRole.setType( GwtRoleType.SharePublic );
			roles.add( sharePublicRole );
			
			shareFolderPublicRole = new GwtRole();
			shareFolderPublicRole.setType( GwtRoleType.ShareFolderPublic );
			roles.add( shareFolderPublicRole );
			
			sharePublicLinkRole = new GwtRole();
			sharePublicLinkRole.setType( GwtRoleType.SharePublicLinks );
			roles.add( sharePublicLinkRole );
		}
		
		// Get the list of principals
		principals = getListOfSelectedPrincipals();
		if ( principals != null && principals.size() > 0 )
		{
			for ( GwtPrincipal nextPrincipal : principals )
			{
				Object obj;
				
				obj = nextPrincipal.getAdditionalData();
				if ( obj != null && obj instanceof PerUserRightsInfo )
				{
					PerUserRightsInfo rightsInfo;
					
					rightsInfo = (PerUserRightsInfo) obj;
					
					if ( rightsInfo.canAccess() )
						viewRole.addMember( nextPrincipal );
					
					if ( rightsInfo.canReshare() )
						reshareRole.addMember( nextPrincipal );
					
					if ( rightsInfo.canReshareFolders() )
						reshareFolderRole.addMember( nextPrincipal );
						
					if ( rightsInfo.canShareExternal() )
						shareExternalRole.addMember( nextPrincipal );
					
					if ( rightsInfo.canShareFolderExternal() )
						shareFolderExternalRole.addMember( nextPrincipal );
					
					if ( rightsInfo.canShareInternal() )
						shareInternalRole.addMember( nextPrincipal );
					
					if ( rightsInfo.canShareFolderInternal() )
						shareFolderInternalRole.addMember( nextPrincipal );
					
					if ( rightsInfo.canSharePublic() )
						sharePublicRole.addMember( nextPrincipal );
					
					if ( rightsInfo.canShareFolderPublic() )
						shareFolderPublicRole.addMember( nextPrincipal );					
					
					if ( rightsInfo.canSharePublicLink() )
						sharePublicLinkRole.addMember( nextPrincipal );
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
			if ( principal.getAdditionalData() instanceof PerUserRightsInfo )
			{
				NetFolderRightsWidget widget;
				PerUserRightsInfo rightsInfo;
				
				rightsInfo = (PerUserRightsInfo) principal.getAdditionalData();
				widget = new NetFolderRightsWidget( rightsInfo, principal.getIdLong() );
				return widget;
			}
			
			return new InlineLabel( "Could not get PerUserRightsInfo" );
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
						principal.setAdditionalData( new PerUserRightsInfo() );
					}
					
					// Initialize the rights this user has
					obj = principal.getAdditionalData();
					if ( obj != null && obj instanceof PerUserRightsInfo )
					{
						PerUserRightsInfo rightsInfo;

						rightsInfo = (PerUserRightsInfo) obj;
						
						switch ( nextRole.getType() )
						{
						case ShareExternal:
							rightsInfo.setCanShareExternal( true );
							break;
							
						case ShareFolderExternal:
							rightsInfo.setCanShareFolderExternal( true );
							break;
							
						case ShareForward:
							rightsInfo.setCanReshare( true );
							break;	
							
						case ShareFolderForward:
							rightsInfo.setCanReshareFolders( true );
							break;								
							
						case ShareInternal:
							rightsInfo.setCanShareInternal( true );
							break;
							
						case ShareFolderInternal:
							rightsInfo.setCanShareFolderInternal( true );
							break;
							
						case SharePublic:
							rightsInfo.setCanSharePublic( true );
							break;
							
						case ShareFolderPublic:
							rightsInfo.setCanShareFolderPublic( true );
							break;
						
						case SharePublicLinks:
							rightsInfo.setCanSharePublicLink( true );
							break;
							
						case AllowAccess:
							rightsInfo.setCanAccess( true );
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
	 * PerUserRightsInfo object to the principal
	 */
	@Override
	protected void principalAdded( GwtPrincipal principal )
	{
		if ( principal != null )
		{
			InvokeEditNetFolderRightsDlgEvent event;
			
			// Fire an event to invoke the "edit net folder rights" dialog.
			event = new InvokeEditNetFolderRightsDlgEvent( principal.getIdLong() );
			GwtTeaming.fireEvent( event );
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
