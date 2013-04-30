package org.kabling.teaming.install.client;

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

import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.widgets.VibeValidator;

import com.google.gwt.user.client.ui.TextBoxBase;

/**
 * The Class ValueRequiredValidator.
 */
public class LocalHostNotAllowedValidator extends VibeValidator<TextBoxBase>
{

	/** The d bundle. */
	private static AppResource dBundle = AppUtil.getAppResource();

	/**
	 * Instantiates a new name validator.
	 * 
	 * @param txtBox
	 *            the txt box
	 */
	public LocalHostNotAllowedValidator(TextBoxBase txtBox)
	{
		super(txtBox);
	}

	@Override
	public String validate()
	{
		String name = widget.getText().trim();

		//Make sure there is some data, if no data, return the error message
		if (name.isEmpty())
		{
			return dBundle.requiredField();
		}
		
		if (name.equalsIgnoreCase("localhost"))
		{
			return dBundle.localHostNotValid();
		}

		return null;
	}
}
