/*
 * ========================================================================
 *
 * Copyright (c) 2012 Unpublished Work of Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS AN UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL,
 * PROPRIETARY AND TRADE SECRET INFORMATION OF NOVELL, INC. ACCESS TO
 * THIS WORK IS RESTRICTED TO (I) NOVELL, INC. EMPLOYEES WHO HAVE A NEED
 * TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE OF THEIR ASSIGNMENTS AND
 * (II) ENTITIES OTHER THAN NOVELL, INC. WHO HAVE ENTERED INTO
 * APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE USED,
 * PRACTICED, PERFORMED, COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED,
 * LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT OF NOVELL, INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT
 * AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL
 * LIABILITY.
 *
 * ========================================================================
 */

package org.kabling.teaming.install.client.images;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;

/**
 * Resources that match the GWT standard style theme.
 */
public interface CellTableResource extends CellTable.Resources
{

	@Override
	@Source("busyanim32b.gif")
	ImageResource cellTableLoading();

	@Override
	@Source("sortAZ.gif")
	ImageResource cellTableSortAscending();

	@Override
	@Source("sortZA.gif")
	ImageResource cellTableSortDescending();

	@Source("gray_blend8_28.png")
	ImageResource headerBackground();

	@Override
	@Source(Style.DEFAULT_CSS)
	Style cellTableStyle();

	/**
	 * The Interface Style.
	 */
	interface Style extends CellTable.Style
	{
		String DEFAULT_CSS = "../widgets/VibeCellTable.css";
	}
}
