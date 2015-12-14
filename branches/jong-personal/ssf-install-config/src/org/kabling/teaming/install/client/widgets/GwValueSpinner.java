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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Wrapper around the ValueSpinner so that we can style.
 * 
 * @author Rajesh
 */
public class GwValueSpinner extends Composite
{

	/** The val spinner. */
	private ValueSpinner valSpinner;

	private InlineLabel valSpinnerLabel;

	/**
	 * Instantiates a new gw value spinner.
	 * 
	 * @param initialValue
	 *            the initial value
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param description
	 *            the description
	 */
	public GwValueSpinner(int initialValue, int min, int max, String description)
	{
		this(initialValue, min, max, ValueSpinner.MIN_STEP, ValueSpinner.MAX_STEP, true, description);
	}

	/**
	 * Instantiates a new gw value spinner.
	 * 
	 * @param initialValue
	 *            the initial value
	 * @param min
	 *            Minimum value.
	 * @param max
	 *            Maximum value.
	 * @param minStep
	 *            Minimum value for stepping.
	 * @param maxStep
	 *            Maximum value for stepping.
	 * @param constrained
	 *            If set to false minimum and maximum values will not have any effect.
	 */
	public GwValueSpinner(long initialValue, int min, int max, int minStep, int maxStep, boolean constrained)
	{
		this(initialValue, min, max, minStep, maxStep, constrained, null);
	}

	/**
	 * Instantiates a new gw value spinner.
	 * 
	 * @param value
	 *            Initial value.
	 * @param min
	 *            Minimum value.
	 * @param max
	 *            Maximum value.
	 * @param minStep
	 *            Minimum value for stepping.
	 * @param maxStep
	 *            Maximum value for stepping.
	 * @param constrained
	 *            Describing the constraints, shows up to the right of the value spinner widget
	 * @param description
	 *            the description
	 */
	public GwValueSpinner(long value, int min, int max, int minStep, int maxStep, boolean constrained, String description)
	{
		FlowPanel content = new FlowPanel();
		content.addStyleName("gwValueSpinner");
		valSpinner = new ValueSpinner(value, min, max, minStep, maxStep, constrained);
		content.add(valSpinner);

		if (description != null)
		{
			valSpinnerLabel = new InlineLabel(description);
			valSpinnerLabel.addStyleName("gwValueSpinner-Label");
			content.add(valSpinnerLabel);
		}

		getValueSpinner().getSpinner().addSpinnerListener(new SpinnerListener()
		{
			
			@Override
			public void onSpinning(double value)
			{
				getValueSpinner().getTextBox().setValue(String.valueOf((long)value));
			}
		});
		valSpinner.setValue(value);
		initWidget(content);
	}

	/**
	 * Gets the value spinner.
	 * 
	 * @return the value spinner
	 */
	public ValueSpinner getValueSpinner()
	{
		return valSpinner;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public long getValue()
	{
		return (long)valSpinner.getValue();
	}

	public int getValueAsInt()
	{
		return (int)valSpinner.getValue();
	}
	
	public void setValue(long value)
	{
		valSpinner.setValue(value);
	}

	public void setEnabled(boolean enabled)
	{
		getValueSpinner().setEnabled(enabled);

		if (!enabled && valSpinnerLabel != null)
		{
			valSpinnerLabel.addStyleName("disabled");
		}
		else
		{
			if (valSpinnerLabel != null)
			{
				valSpinnerLabel.removeStyleName("disabled");
			}
		}
	}
	
	public InlineLabel getValSpinnerLabel()
	{
		return valSpinnerLabel;
	}
}
