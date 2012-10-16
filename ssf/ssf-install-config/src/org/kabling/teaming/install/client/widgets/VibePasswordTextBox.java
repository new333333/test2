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
package org.kabling.teaming.install.client.widgets;

import com.google.gwt.user.client.ui.TextBoxBase;

/**
 * The Class GwTextBox.
 */
public class VibePasswordTextBox extends VibeBasePasswordTextBox<TextBoxBase>
{

	/**
	 * Instantiates a new gw text box.
	 */
	public VibePasswordTextBox()
	{
		super();
	}

	/**
	 * Instantiates a new gw text box.
	 * 
	 * @param text
	 *            the text
	 */
	public VibePasswordTextBox(String text)
	{
		super(text);
	}

	
	@Override
	public void setValidator(VibeValidator<TextBoxBase> validator)
	{
		this.validator = validator;
	}
}
