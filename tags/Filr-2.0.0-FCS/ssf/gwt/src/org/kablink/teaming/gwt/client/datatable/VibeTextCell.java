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

import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

/**
 * A {@link Cell} used to render text.
 * 
 * @author drfoster@novell.com
 */
public class VibeTextCell extends AbstractSafeHtmlCell<String> {

	/**
	 * Constructs a VibeTextCell that uses a
	 * {@link SimpleSafeHtmlRenderer} to render its text.
	 */
	public VibeTextCell() {
		super(SimpleSafeHtmlRenderer.getInstance());
	}

	/**
	 * Constructs a VibeTextCell that uses the provided
	 * {@link SafeHtmlRenderer} to render its text.
	 * 
	 * @param renderer	A {@link SafeHtmlRenderer SafeHtmlRenderer<String>}
	 *					instance.
	 */
	public VibeTextCell(SafeHtmlRenderer<String> renderer) {
		super(renderer);
	}

	/**
	 * Renders the text in this cell.
	 * 
	 * @param context
	 * @param value
	 * @param sb
	 */
	@Override
	public void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
		if ((null == value) || (!(GwtClientHelper.hasString(value.asString())))) {
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}
		sb.append(value);
	}
}
