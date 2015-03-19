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

import org.kabling.teaming.install.client.widgets.ArrowCommentBox.Arrow;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class GwBaseTextBox.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class VibeBasePasswordTextBox<T extends Widget> extends PasswordTextBox implements FocusHandler, BlurHandler, KeyUpHandler, ClickHandler
{

	/** The validator. */
	protected VibeValidator<T> validator;

	/** The dlg. */
	private ArrowCommentBox dlg;

	private boolean initialFocus = true;
	private String watermark;

	/**
	 * Sets the validator.
	 * 
	 * @param validator
	 *            the new validator
	 */
	protected abstract void setValidator(VibeValidator<T> validator);

	/**
	 * Instantiates a new gw base text box.
	 */
	public VibeBasePasswordTextBox()
	{
		this(null);
	}

	/**
	 * Instantiates a new gw base text box.
	 * 
	 * @param text
	 *            the text
	 */
	public VibeBasePasswordTextBox(String text)
	{
		setText(text);
		addStyleName("gw-TextBox");
		addFocusHandler(this);
		addBlurHandler(this);
		addKeyUpHandler(this);
		addClickHandler(this);
	}

	@Override
	public void onClick(ClickEvent event)
	{
		validateUI(false);
	}

	/**
	 * If there is a validator set, make sure the values inside the textbox is valid.
	 * 
	 * @return - true if valid
	 */
	public boolean isValid()
	{
		if (validator == null)
		{
			return true;
		}
		if (validator.validate() != null)
		{
			validateUI(true);
			return false;
		}
		return true;
	}

	@Override
	public void onBlur(BlurEvent event)
	{
		if (dlg != null)
		{
			dlg.hide();
		}
		validateUI(true);

		if (watermark != null && getText().equals(""))
		{
			setText(watermark);
			this.addStyleName("gwt-TextBox-watermark");
		}
	}

	@Override
	public void onFocus(FocusEvent event)
	{
		if (initialFocus && !isShowsError())
		{
			initialFocus = false;

			if (watermark != null && getText().equals(watermark))
			{
				setText("");
				this.removeStyleName("gwt-TextBox-watermark");
			}
			return;
		}

		validateUI(false);
		clearError();

		if (watermark != null && getText().equals(watermark))
		{
			setText("");
			this.removeStyleName("gwt-TextBox-watermark");
		}
	}

	public void showError()
	{
		addStyleName("textError");
	}

	/**
	 * Clear the error.
	 */
	public void clearError()
	{
		if (validator != null)
		{
			Widget widget = validator.getWidget();
			widget.removeStyleName("textError");
		}
		else
		{
			removeStyleName("textError");
		}

	}

	@Override
	public void onKeyUp(KeyUpEvent event)
	{
		validateUI(false);
		clearError();
	}

	/**
	 * Validate the text inside textbox using the validator.
	 * 
	 * @param hideUI
	 *            - if true, show the error without showing the error dialog box
	 */
	private void validateUI(boolean hideUI)
	{
		if (validator == null)
		{
			return;
		}

		String message = validator.validate();
		Widget widget = validator.getWidget();

		// We have an error, show it to the user
		if (message != null)
		{
			if (dlg == null)
			{
				dlg = new ArrowCommentBox(message, Arrow.LEFT);
			}
			dlg.setMessage(message);

			if (dlg.isAttached())
			{
				return;
			}

			if (!hideUI)
			{
				int left = widget.getAbsoluteLeft() + widget.getOffsetWidth() + 7;
				int top = widget.getAbsoluteTop() - 15;
				dlg.setPopupPosition(left, top);
				dlg.show();
			}
			else
			{
				dlg.hide();
			}

			if (!widget.getStyleName().contains("textError"))
			{
				widget.addStyleName("textError");
			}
		}
		// No error, lets remove the error text and hide the popup
		else
		{
			// When they blur out, we will validate
			// How should we style this and how to display the error
			widget.removeStyleName("textError");

			if (dlg != null && dlg.isVisible())
			{
				dlg.hide();
			}
		}
	}

	private boolean isShowsError()
	{
		if (getStyleName().contains("textError"))
			return true;

		return false;
	}

	public void setWatermark(String text)
	{
		this.watermark = text;

		this.setText(text);
		this.addStyleName("gwt-TextBox-watermark");
	}

	@Override
	public void setText(String text)
	{
		super.setText(text);
		this.removeStyleName("gwt-TextBox-watermark");
	}
	
	
}
