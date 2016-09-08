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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Comment box with an arrow.
 * 
 * @author Rajesh
 * 
 */
public class ArrowCommentBox extends PopupPanel
{

	/**
	 * The Enum Arrow.
	 */
	enum Arrow
	{

		/** The LEFT. */
		LEFT,
		/** The RIGHT. */
		RIGHT,
		/** The UP. */
		UP,
		/** The DOWN. */
		DOWN;
	}

	/** The label. */
	private Label label;

	/**
	 * Create the comment box.
	 * 
	 * @param text
	 *            - message to be displayed
	 * @param arrow
	 *            - direction of the arrow
	 */
	public ArrowCommentBox(String text, Arrow arrow)
	{
		super(true, false);
		setStyleName("commentBoxDlg");

		FlowPanel content = new FlowPanel();
		content.addStyleName("content");

		// Styling for the arrows are done part of the css
		if (arrow.equals(Arrow.LEFT))
		{
			content.addStyleName("leftArrow");
		}
		else if (arrow.equals(Arrow.RIGHT))
		{
			content.addStyleName("rightArrow");
		}
		else if (arrow.equals(Arrow.UP))
		{
			content.addStyleName("upArrow");
		}
		else if (arrow.equals(Arrow.DOWN))
		{
			content.addStyleName("downArrow");
		}

		// Message
		label = new Label(text);
		content.add(label);
		setWidget(content);
	}

	/**
	 * Set the message to be displayed for the dialog.
	 * 
	 * @param msg
	 *            the new message
	 */
	public void setMessage(String msg)
	{
		label.setText(msg);
	}
}
