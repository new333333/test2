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


import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.ui.Anchor;

/**
 * A {@link Cell} used to render the Anchor.
 */
public class AnchorCell extends AbstractSafeHtmlCell<String>
{
	/**
	 * Construct a new AnchorCell that will use a {@link SimpleSafeHtmlRenderer}.
	 */
	public AnchorCell()
	{
		this( SimpleSafeHtmlRenderer.getInstance() );
	}

	/**
	 * Construct a new AnchorCell that will use a given {@link SafeHtmlRenderer}.
	 * 
	 * @param renderer
	 *            a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
	 */
	public AnchorCell( SafeHtmlRenderer<String> renderer )
	{
		// We care about click and keydown action
		super( renderer, "click", "keydown" );
	}

	/**
	 * 
	 */
	@Override
	public void onBrowserEvent( Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater )
	{
		EventTarget eventTarget = event.getEventTarget();
		if ( Element.is( eventTarget ) )
		{
			Element target = eventTarget.cast();
			
			// We only want to handle the click of the anchor, not the cell
			if ( target.getTagName().equalsIgnoreCase( "A" ) )
			{
				super.onBrowserEvent( context, parent, value, event, valueUpdater );

				if ( "click".equals( event.getType() ) )
				{
					onEnterKeyDown( context, parent, value, event, valueUpdater );
				}
			}
		}
	}

	/**
	 * 
	 */
	@Override
	protected void render( com.google.gwt.cell.client.Cell.Context arg0, SafeHtml arg1, SafeHtmlBuilder sb )
	{
		Anchor anchor = new Anchor( arg1 );
		
		anchor.addStyleName( "noTextDecoration" );
		
		// Commenting href as it its causing problems
		// anchor.setHref("#");
		sb.appendHtmlConstant( anchor.toString() );
	}

	/**
	 * 
	 */
	@Override
	protected void onEnterKeyDown( Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater )
	{
		if ( valueUpdater != null )
		{
			valueUpdater.update( value );
		}
	}
}
