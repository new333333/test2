/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kabling.teaming.install.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

import org.kabling.teaming.install.client.images.SpinnerResources;

/**
 * The {@link Spinner} provide two arrows for in- and decreasing values. A linked {@link SpinnerListener}
 */
public class Spinner
{
	
	/** The Constant INITIAL_SPEED. */
	private static final int INITIAL_SPEED = 7;

	/** The images. */
	private SpinnerResources images;

	/** The decrement arrow. */
	private final Image decrementArrow = new Image();

	/** The increment arrow. */
	private final Image incrementArrow = new Image();

	/** The spinner listeners. */
	private List<SpinnerListener> spinnerListeners = new ArrayList<SpinnerListener>();

	/** The initial speed. */
	private int step, minStep, maxStep, initialSpeed = 7;

	/** The max. */
	private long value, min, max;

	/** The increment. */
	private boolean increment;

	/** The constrained. */
	private boolean constrained;

	/** The enabled. */
	private boolean enabled = true;

	/** The timer. */
	private final Timer timer = new Timer()
	{
		private int counter = 0;
		private int speed = 7;

		@Override
		public void cancel()
		{
			super.cancel();
			speed = initialSpeed;
			counter = 0;
		}

		@Override
		public void run()
		{
			counter++;
			if (speed <= 0 || counter % speed == 0)
			{
				speed--;
				counter = 0;
				if (increment)
				{
					increase();
				}
				else
				{
					decrease();
				}
			}
			if (speed < 0 && step < maxStep)
			{
				step += 1;
			}
		}
	};

	/** The mouse down handler. */
	private MouseDownHandler mouseDownHandler = new MouseDownHandler()
	{

		@Override
		public void onMouseDown(MouseDownEvent event)
		{
			if (enabled)
			{
				Image sender = (Image) event.getSource();
				if (sender == incrementArrow)
				{
					sender.setResource(images.arrowUpPressed());
					increment = true;
					increase();
				}
				else
				{
					sender.setResource(images.arrowDownPressed());
					increment = false;
					decrease();
				}
				timer.scheduleRepeating(30);
			}
		}
	};

	/** The mouse over handler. */
	private MouseOverHandler mouseOverHandler = new MouseOverHandler()
	{
		@Override
		public void onMouseOver(MouseOverEvent event)
		{
			if (enabled)
			{
				Image sender = (Image) event.getSource();
				if (sender == incrementArrow)
				{
					sender.setResource(images.arrowUpHover());
				}
				else
				{
					sender.setResource(images.arrowDownHover());
				}
			}
		}
	};

	/** The mouse out handler. */
	private MouseOutHandler mouseOutHandler = new MouseOutHandler()
	{
		@Override
		public void onMouseOut(MouseOutEvent event)
		{
			if (enabled)
			{
				cancelTimer((Widget) event.getSource());
			}
		}
	};

	/** The mouse up handler. */
	private MouseUpHandler mouseUpHandler = new MouseUpHandler()
	{
		@Override
		public void onMouseUp(MouseUpEvent event)
		{
			if (enabled)
			{
				cancelTimer((Widget) event.getSource());
			}
		}
	};

	/**
	 * Instantiates a new spinner.
	 * 
	 * @param spinner
	 *            the widget listening to the arrows
	 * @param value
	 *            initial value
	 */
	public Spinner(SpinnerListener spinner, long value)
	{
		this(spinner, value, 0, 0, ValueSpinner.MIN_STEP, ValueSpinner.MAX_STEP, false);
	}

	/**
	 * Instantiates a new spinner.
	 * 
	 * @param spinner
	 *            the widget listening to the arrows
	 * @param value
	 *            initial value
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 */
	public Spinner(SpinnerListener spinner, long value, long min, long max)
	{
		this(spinner, value, min, max, ValueSpinner.MIN_STEP, ValueSpinner.MAX_STEP, true);
	}

	/**
	 * Instantiates a new spinner.
	 * 
	 * @param spinner
	 *            the widget listening to the arrows
	 * @param value
	 *            initial value
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 * @param minStep
	 *            min value for stepping
	 * @param maxStep
	 *            max value for stepping
	 */
	public Spinner(SpinnerListener spinner, long value, long min, long max, int minStep, int maxStep)
	{
		this(spinner, value, min, max, minStep, maxStep, true);
	}

	/**
	 * Instantiates a new spinner.
	 * 
	 * @param spinner
	 *            the widget listening to the arrows
	 * @param value
	 *            initial value
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 * @param minStep
	 *            min value for stepping
	 * @param maxStep
	 *            max value for stepping
	 * @param constrained
	 *            determines if min and max value will take effect
	 */
	public Spinner(SpinnerListener spinner, long value, long min, long max, int minStep, int maxStep, boolean constrained)
	{
		this(spinner, value, min, max, minStep, maxStep, constrained, (SpinnerResources) GWT.create(SpinnerResources.class));
	}

	/**
	 * Instantiates a new spinner.
	 * 
	 * @param spinner
	 *            the widget listening to the arrows
	 * @param value
	 *            initial value
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 * @param minStep
	 *            min value for stepping
	 * @param maxStep
	 *            max value for stepping
	 * @param constrained
	 *            determines if min and max value will take effect
	 * @param images
	 *            the arrows images
	 */
	public Spinner(SpinnerListener spinner, long value, long min, long max, int minStep, int maxStep, boolean constrained,
			SpinnerResources images)
	{
		super();
		spinnerListeners.add(spinner);
		this.images = images;
		this.value = value;
		this.constrained = constrained;
		this.step = minStep;
		this.minStep = minStep;
		this.maxStep = maxStep;
		this.min = min;
		this.max = max;
		this.initialSpeed = INITIAL_SPEED;
		incrementArrow.addMouseUpHandler(mouseUpHandler);
		incrementArrow.addMouseDownHandler(mouseDownHandler);
		incrementArrow.addMouseOverHandler(mouseOverHandler);
		incrementArrow.addMouseOutHandler(mouseOutHandler);
		incrementArrow.setResource(images.arrowUp());
		decrementArrow.addMouseUpHandler(mouseUpHandler);
		decrementArrow.addMouseDownHandler(mouseDownHandler);
		decrementArrow.addMouseOverHandler(mouseOverHandler);
		decrementArrow.addMouseOutHandler(mouseOutHandler);
		decrementArrow.setResource(images.arrowDown());
		fireOnValueChanged();
	}

	/**
	 * Adds the spinner listener.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addSpinnerListener(SpinnerListener listener)
	{
		spinnerListeners.add(listener);
	}

	/**
	 * Gets the decrement arrow.
	 * 
	 * @return the image representing the decreating arrow
	 */
	public Image getDecrementArrow()
	{
		return decrementArrow;
	}

	/**
	 * Gets the increment arrow.
	 * 
	 * @return the image representing the increasing arrow
	 */
	public Image getIncrementArrow()
	{
		return incrementArrow;
	}

	/**
	 * Gets the max.
	 * 
	 * @return the maximum value
	 */
	public long getMax()
	{
		return max;
	}

	/**
	 * Gets the max step.
	 * 
	 * @return the maximum spinner step
	 */
	public int getMaxStep()
	{
		return maxStep;
	}

	/**
	 * Gets the min.
	 * 
	 * @return the minimum value
	 */
	public long getMin()
	{
		return min;
	}

	/**
	 * Gets the min step.
	 * 
	 * @return the minimum spinner step
	 */
	public int getMinStep()
	{
		return minStep;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the current value
	 */
	public long getValue()
	{
		return value;
	}

	/**
	 * Checks if is constrained.
	 * 
	 * @return true is min and max values are active, false if not
	 */
	public boolean isConstrained()
	{
		return constrained;
	}

	/**
	 * Checks if is enabled.
	 * 
	 * @return Gets whether this widget is enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Removes the spinner listener.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeSpinnerListener(SpinnerListener listener)
	{
		spinnerListeners.remove(listener);
	}

	/**
	 * Sets whether this widget is enabled.
	 * 
	 * @param enabled
	 *            true to enable the widget, false to disable it
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		if (enabled)
		{
			incrementArrow.setResource(images.arrowUp());
			decrementArrow.setResource(images.arrowDown());
		}
		else
		{
			incrementArrow.setResource(images.arrowUpDisabled());
			decrementArrow.setResource(images.arrowDownDisabled());
		}
		if (!enabled)
		{
			timer.cancel();
		}
	}

	/**
	 * Sets the initial speed.
	 * 
	 * @param initialSpeed
	 *            the initial speed of the spinner. Higher values mean lower speed, default value is 7
	 */
	public void setInitialSpeed(int initialSpeed)
	{
		this.initialSpeed = initialSpeed;
	}

	/**
	 * Sets the max.
	 * 
	 * @param max
	 *            the maximum value. Will not have any effect if constrained is set to false
	 */
	public void setMax(long max)
	{
		this.max = max;
	}

	/**
	 * Sets the max step.
	 * 
	 * @param maxStep
	 *            the maximum step for this spinner
	 */
	public void setMaxStep(int maxStep)
	{
		this.maxStep = maxStep;
	}

	/**
	 * Sets the min.
	 * 
	 * @param min
	 *            the minimum value. Will not have any effect if constrained is set to false
	 */
	public void setMin(long min)
	{
		this.min = min;
	}

	/**
	 * Sets the min step.
	 * 
	 * @param minStep
	 *            the minimum step for this spinner
	 */
	public void setMinStep(int minStep)
	{
		this.minStep = minStep;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            sets the current value of this spinner
	 * @param fireEvent
	 *            fires value changed event if set to true
	 */
	public void setValue(long value, boolean fireEvent)
	{
		this.value = value;
		if (fireEvent)
		{
			fireOnValueChanged();
		}
	}

	/**
	 * Decreases the current value of the spinner by subtracting current step.
	 */
	protected void decrease()
	{
		value -= step;
		if (constrained && value < min)
		{
			value = min;
			timer.cancel();
		}
		fireOnValueChanged();
	}

	/**
	 * Increases the current value of the spinner by adding current step.
	 */
	protected void increase()
	{
		value += step;
		if (constrained && value > max)
		{
			value = max;
			timer.cancel();
		}
		fireOnValueChanged();
	}

	/**
	 * Cancel timer.
	 * 
	 * @param sender
	 *            the sender
	 */
	private void cancelTimer(Widget sender)
	{
		step = minStep;

		if (sender instanceof Image)
		{
			if (sender == incrementArrow)
			{
				((Image) sender).setResource(images.arrowUp());
			}
			else
			{
				((Image) sender).setResource(images.arrowDown());
			}
		}
		timer.cancel();
	}

	/**
	 * Fire on value changed.
	 */
	private void fireOnValueChanged()
	{
		for (SpinnerListener listener : spinnerListeners)
		{
			listener.onSpinning(value);
		}
	}
}
