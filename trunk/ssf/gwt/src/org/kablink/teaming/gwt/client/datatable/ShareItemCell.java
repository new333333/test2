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
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.GwtShareItem;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;


/**
 * A cell used to render a share item
 */
public class ShareItemCell extends AbstractCell<GwtShareItem>
{
	private static String m_imgHtml;

	/**
	 * 
	 */
	public ShareItemCell()
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
	public void onBrowserEvent(
		Context context,
		Element parent,
		GwtShareItem value,
		NativeEvent event,
		ValueUpdater<GwtShareItem> valueUpdater )
	{
		// Let AbstractCell handle the keydown event.
		super.onBrowserEvent( context, parent, value, event, valueUpdater );
		
		// Handle the click event.
		if ( "click".equals( event.getType() ) )
		{
			valueUpdater.update( value );
		}
	}

	/**
	 * 
	 */
	@Override
	public void render( Context context, GwtShareItem value, SafeHtmlBuilder sb )
	{
		if ( value == null )
		{
			GwtClientHelper.renderEmptyHtml( sb );
			return;
		}
		
		{
			SafeHtml safeValue;

			// Wrap everything in a <div>
			sb.appendHtmlConstant( "<div class=\"shareItem_InfoPanel\">" );
			
			// Add the group's title
			sb.appendHtmlConstant( "<span class=\"shareItem_Name\">" );
			safeValue = SafeHtmlUtils.fromString( value.getRecipientName() );
			sb.append( safeValue );
			sb.appendHtmlConstant( "</span>" );
			
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
		GwtShareItem value,
		NativeEvent event,
		ValueUpdater<GwtShareItem> valueUpdater )
	{
		if ( valueUpdater != null )
		{
			valueUpdater.update( value );
		}
	}
}
