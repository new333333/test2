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

	@Override
	public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater)
	{

		EventTarget eventTarget = event.getEventTarget();
		if (Element.is(eventTarget))
		{
			Element target = eventTarget.cast();

			// We only want to handle the click of the anchor, not the cell
			if (target.getTagName().equalsIgnoreCase("A"))
			{
				super.onBrowserEvent(context, parent, value, event, valueUpdater);

  				if ("click".equals(event.getType()))
				{
					onEnterKeyDown(context, parent, value, event, valueUpdater);
				}
			}
		}
	}

	/**
	 * Construct a new ButtonCell that will use a {@link SimpleSafeHtmlRenderer}.
	 */
	public AnchorCell()
	{
		this(SimpleSafeHtmlRenderer.getInstance());
	}

	/**
	 * Construct a new ButtonCell that will use a given {@link SafeHtmlRenderer}.
	 * 
	 * @param renderer
	 *            a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
	 */
	public AnchorCell(SafeHtmlRenderer<String> renderer)
	{
		// We care about click and keydown action
		super(renderer, "click", "keydown");
	}

	@Override
	protected void render(com.google.gwt.cell.client.Cell.Context arg0, SafeHtml arg1, SafeHtmlBuilder sb)
	{
		Anchor anchor = new Anchor(arg1);

		// Commenting href as it its causing problems
		// anchor.setHref("#");
		sb.appendHtmlConstant(anchor.toString());
	}

	@Override
	protected void onEnterKeyDown(Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater)
	{
		if (valueUpdater != null)
		{
			valueUpdater.update(value);
		}
	}

}
