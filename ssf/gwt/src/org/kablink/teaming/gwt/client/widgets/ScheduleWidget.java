/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtSchedule;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtSchedule.DayFrequency;
import org.kablink.teaming.gwt.client.GwtSchedule.TimeFrequency;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This widget is used to define a schedule of when something happens.
 * 
 * @author jwootton@novell.com
 */
public class ScheduleWidget extends Composite
{
	private CheckBox m_enableScheduleCkbox;
	private RadioButton m_dailyRb;
	private RadioButton m_onSelectedDaysRb;
	private CheckBox m_monCkbox;
	private CheckBox m_tueCkbox;
	private CheckBox m_wedCkbox;
	private CheckBox m_thursCkbox;
	private CheckBox m_friCkbox;
	private CheckBox m_satCkbox;
	private CheckBox m_sunCkbox;
	private RadioButton m_atTimeRb;
	private RadioButton m_repeatEveryRb;
	private ListBox m_atHoursListbox;
	private ListBox m_atMinutesListbox;
	private ListBox m_repeatEveryListbox;

	private List<HandlerRegistration> m_registeredEventHandlers;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
	};
	
	
	/**
	 * 
	 */
	public ScheduleWidget( String enableCheckBoxLabel )
	{
		FlowPanel mainPanel;
		FlowPanel tmpPanel;
		GwtTeamingMessages messages;

		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "scheduleWidget_MainPanel" );
		
		m_enableScheduleCkbox = new CheckBox( enableCheckBoxLabel );
		mainPanel.add( m_enableScheduleCkbox );

		// Add some space
		tmpPanel = new FlowPanel();
		tmpPanel.getElement().getStyle().setMarginTop( 10, Unit.PX );
		mainPanel.add( tmpPanel );

		tmpPanel = new FlowPanel();
		m_dailyRb = new RadioButton( "dayFrequency", messages.scheduleWidget_EveryDayLabel() );
		tmpPanel.add( m_dailyRb );
		mainPanel.add( tmpPanel );
		
		tmpPanel = new FlowPanel();
		m_onSelectedDaysRb = new RadioButton( "dayFrequency", messages.scheduleWidget_OnSelectedDaysLabel() );
		tmpPanel.add( m_onSelectedDaysRb );
		mainPanel.add( tmpPanel );
		
		// Create a checkbox for each day of the week
		{
			FlowPanel daysPanel;
			FlowPanel dayPanel;
			Label dayLabel;
			FlexTable daysTable;
			int col;
			
			daysPanel = new FlowPanel();
			daysPanel.addStyleName( "scheduleWidget_OnSelectedDaysPanel" );
			
			daysTable = new FlexTable();
			daysPanel.add( daysTable );
			col = 0;
			
			dayPanel = new FlowPanel();
			dayPanel.addStyleName( "scheduleWidget_DayPanel" );
			m_sunCkbox = new CheckBox();
			dayPanel.add( m_sunCkbox );
			dayLabel = new Label( messages.scheduleWidget_SundayLabel() );
			dayPanel.add( dayLabel );
			daysTable.setWidget( 0, col, dayPanel );
			++col;
			
			dayPanel = new FlowPanel();
			dayPanel.addStyleName( "scheduleWidget_DayPanel" );
			m_monCkbox = new CheckBox();
			dayPanel.add( m_monCkbox );
			dayLabel = new Label( messages.scheduleWidget_MondayLabel() );
			dayPanel.add( dayLabel );
			daysTable.setWidget( 0, col, dayPanel );
			++col;
			
			dayPanel = new FlowPanel();
			dayPanel.addStyleName( "scheduleWidget_DayPanel" );
			m_tueCkbox = new CheckBox();
			dayPanel.add( m_tueCkbox );
			dayLabel = new Label( messages.scheduleWidget_TuesdayLabel() );
			dayPanel.add( dayLabel );
			daysTable.setWidget( 0, col, dayPanel );
			++col;
			
			dayPanel = new FlowPanel();
			dayPanel.addStyleName( "scheduleWidget_DayPanel" );
			m_wedCkbox = new CheckBox();
			dayPanel.add( m_wedCkbox );
			dayLabel = new Label( messages.scheduleWidget_WednesdayLabel() );
			dayPanel.add( dayLabel );
			daysTable.setWidget( 0, col, dayPanel );
			++col;
			
			dayPanel = new FlowPanel();
			dayPanel.addStyleName( "scheduleWidget_DayPanel" );
			m_thursCkbox = new CheckBox();
			dayPanel.add( m_thursCkbox );
			dayLabel = new Label( messages.scheduleWidget_ThursdayLabel() );
			dayPanel.add( dayLabel );
			daysTable.setWidget( 0, col, dayPanel );
			++col;
			
			dayPanel = new FlowPanel();
			dayPanel.addStyleName( "scheduleWidget_DayPanel" );
			m_friCkbox = new CheckBox();
			dayPanel.add( m_friCkbox );
			dayLabel = new Label( messages.scheduleWidget_FridayLabel() );
			dayPanel.add( dayLabel );
			daysTable.setWidget( 0, col, dayPanel );
			++col;
			
			dayPanel = new FlowPanel();
			dayPanel.addStyleName( "scheduleWidget_DayPanel" );
			m_satCkbox = new CheckBox();
			dayPanel.add( m_satCkbox );
			dayLabel = new Label( messages.scheduleWidget_SaturdayLabel() );
			dayPanel.add( dayLabel );
			daysTable.setWidget( 0, col, dayPanel );
			++col;
			
			mainPanel.add( daysPanel );
		}

		// Add some space
		tmpPanel = new FlowPanel();
		tmpPanel.getElement().getStyle().setMarginTop( 16, Unit.PX );
		mainPanel.add( tmpPanel );

		// Create the controls for selecting the time to do the operation
		{
			// Add the controls for specifying the hour and minute the operation should occur
			{
				FlexTable table;
				HTMLTable.RowFormatter rowFormatter;
				String tzNameAbrev;
				int tzOffsetHour;
				int i;
				
				// Get the offsetHour from the user's time zone
				tzOffsetHour = GwtTeaming.m_requestInfo.getTimeZoneOffsetHour();
				tzNameAbrev = GwtTeaming.m_requestInfo.getTimeZoneIdAbrev();

				table = new FlexTable();
				rowFormatter = table.getRowFormatter();
				rowFormatter.setVerticalAlign( 0, HasVerticalAlignment.ALIGN_MIDDLE );

				m_atTimeRb = new RadioButton( "timeFrequency", messages.scheduleWidget_AtTimeLabel() );
				table.setWidget( 0, 0, m_atTimeRb );
				
				m_atHoursListbox = new ListBox( false );
				m_atHoursListbox.setVisibleItemCount( 1 );
				for (i = 0; i < 24; ++i)
				{
					String hourStr;
					int hour;
					
					hour = i - tzOffsetHour;
					if ( hour < 0 )
						hour = 24 + hour;
					hour = (hour % 24);
					hourStr = String.valueOf( hour );
					m_atHoursListbox.addItem( String.valueOf( i ), hourStr );
				}
				table.setWidget( 0, 1, m_atHoursListbox );
				
				table.setText( 0, 2, ":" );
				
				m_atMinutesListbox = new ListBox( false );
				m_atMinutesListbox.setVisibleItemCount( 1 );
				m_atMinutesListbox.addItem( "00", "00" );
				m_atMinutesListbox.addItem( "05", "05" );
				m_atMinutesListbox.addItem( "10", "10" );
				m_atMinutesListbox.addItem( "15", "15" );
				m_atMinutesListbox.addItem( "20", "20" );
				m_atMinutesListbox.addItem( "25", "25" );
				m_atMinutesListbox.addItem( "30", "30" );
				m_atMinutesListbox.addItem( "35", "35" );
				m_atMinutesListbox.addItem( "40", "40" );
				m_atMinutesListbox.addItem( "45", "45" );
				m_atMinutesListbox.addItem( "50", "50" );
				m_atMinutesListbox.addItem( "55", "55" );
				table.setWidget( 0, 3, m_atMinutesListbox );
				
				table.setText( 0, 4, tzNameAbrev );
				
				mainPanel.add( table );
			}
			
			// Add the controls for specifying how often the operation should happen
			{
				FlexTable table;
				HTMLTable.RowFormatter rowFormatter;
				
				table = new FlexTable();
				rowFormatter = table.getRowFormatter();
				rowFormatter.setVerticalAlign( 0, HasVerticalAlignment.ALIGN_MIDDLE );
				
				m_repeatEveryRb = new RadioButton( "timeFrequency", messages.scheduleWidget_RepeatEveryLabel() );
				table.setWidget( 0, 0, m_repeatEveryRb );
				
				String ds = GwtClientHelper.getRequestInfo().getDecimalSeparator();
				m_repeatEveryListbox = new ListBox( false );
				m_repeatEveryListbox.setVisibleItemCount( 1 );
				m_repeatEveryListbox.addItem( ("0" + ds + "25"), "15" );
				m_repeatEveryListbox.addItem( ("0" + ds +  "5"), "30" );
				m_repeatEveryListbox.addItem( ("0" + ds + "75"), "45" );
				m_repeatEveryListbox.addItem(  "1",               "1" );
				m_repeatEveryListbox.addItem(  "2",               "2" );
				m_repeatEveryListbox.addItem(  "3",               "3" );
				m_repeatEveryListbox.addItem(  "4",               "4" );
				m_repeatEveryListbox.addItem(  "6",               "6" );
				m_repeatEveryListbox.addItem(  "8",               "8" );
				m_repeatEveryListbox.addItem( "12",              "12" );
				
				table.setWidget( 0, 1, m_repeatEveryListbox );
				table.setText( 0, 2, messages.scheduleWidget_HoursLabel() );
				
				mainPanel.add( table );
			}
		}

		initWidget( mainPanel );
	}

	/**
	 * Return the "at hours"
	 */
	private int getAtHours()
	{
		int selectedIndex;
		int hours;

		hours = 0;
		selectedIndex = m_atHoursListbox.getSelectedIndex();
		if ( selectedIndex >= 0 )
			hours = Integer.valueOf( m_atHoursListbox.getValue( selectedIndex ) );
		
		return hours;
	}
	
	/**
	 * Return the "at minutes"
	 */
	private int getAtMinutes()
	{
		int selectedIndex;
		int minutes;

		minutes = 0;
		selectedIndex = m_atMinutesListbox.getSelectedIndex();
		if ( selectedIndex >= 0 )
			minutes = Integer.valueOf( m_atMinutesListbox.getValue( selectedIndex ) );
		
		return minutes;
	}
	
	/**
	 * Return the value for how often to repeat the schedule
	 */
	private int getRepeatValue()
	{
		int selectedIndex;
		int value;

		value = 0;
		selectedIndex = m_repeatEveryListbox.getSelectedIndex();
		if ( selectedIndex >= 0 )
			value = Integer.valueOf( m_repeatEveryListbox.getValue( selectedIndex ) );
		
		return value;
	}
	
	/**
	 * Get the schedule selected in this widget and return a GwtSchedule object.
	 */
	public GwtSchedule getSchedule()
	{
		GwtSchedule schedule;
		
		schedule = new GwtSchedule();
		
		schedule.setEnabled( m_enableScheduleCkbox.getValue() );
		if ( m_dailyRb.getValue() == true )
			schedule.setDayFrequency( DayFrequency.EVERY_DAY );
		else
		{
			schedule.setDayFrequency( DayFrequency.ON_SELECTED_DAYS );
			schedule.setOnSunday( m_sunCkbox.getValue() );
			schedule.setOnMonday( m_monCkbox.getValue() );
			schedule.setOnTuesday( m_tueCkbox.getValue() );
			schedule.setOnWednesday( m_wedCkbox.getValue() );
			schedule.setOnThursday( m_thursCkbox.getValue() );
			schedule.setOnFriday( m_friCkbox.getValue() );
			schedule.setOnSaturday( m_satCkbox.getValue() );
		}
		
		if ( m_atTimeRb.getValue() == true )
		{
			schedule.setTimeFrequency( TimeFrequency.AT_SPECIFIC_TIME );
			schedule.setAtHours( getAtHours() );
			schedule.setAtMinutes( getAtMinutes() );
		}
		else
		{
			schedule.setTimeFrequency( getTimeFrequency() );
			schedule.setRepeatEveryValue( getRepeatValue() );
		}

		return schedule;
	}
	
	/**
	 * Return how often the schedule repeats, every hour or every minute
	 */
	private TimeFrequency getTimeFrequency()
	{
		int repeatValue;
		
		repeatValue = getRepeatValue();
		if ( repeatValue == 15 || repeatValue == 30 || repeatValue == 45 )
			return TimeFrequency.REPEAT_EVERY_MINUTE;
		
		return TimeFrequency.REPEAT_EVERY_HOUR;
	}
	
	/**
	 * Initialize the controls with the values found in the given GwtSchedule object.
	 */
	public void init( GwtSchedule schedule )
	{
		// Clear all controls
		{
			m_enableScheduleCkbox.setValue( false );
			m_dailyRb.setValue( true );
			m_onSelectedDaysRb.setValue( false );
			m_monCkbox.setValue( false );
			m_tueCkbox.setValue( false );
			m_wedCkbox.setValue( false );
			m_thursCkbox.setValue( false );
			m_friCkbox.setValue( false );
			m_satCkbox.setValue( false );
			m_sunCkbox.setValue( false );
			m_atTimeRb.setValue( true );
			m_atHoursListbox.setSelectedIndex( 0 );
			m_atMinutesListbox.setSelectedIndex( 0 );
			m_repeatEveryRb.setValue( false );
			m_repeatEveryListbox.setSelectedIndex( m_repeatEveryListbox.getItemCount()-1 );
		}
		
		// Do we have a schedule?
		if ( schedule != null )
		{
			// Yes
			m_enableScheduleCkbox.setValue( schedule.getEnabled() );
			
			if ( schedule.getDayFrequency() == DayFrequency.EVERY_DAY )
			{
				m_dailyRb.setValue( true );
				m_onSelectedDaysRb.setValue( false );
			}
			else
			{
				m_dailyRb.setValue( false );
				m_onSelectedDaysRb.setValue( true );
			}
			
			m_monCkbox.setValue( schedule.getOnMonday() );
			m_tueCkbox.setValue( schedule.getOnTuesdy() );
			m_wedCkbox.setValue( schedule.getOnWednesday() );
			m_thursCkbox.setValue( schedule.getOnThursday() );
			m_friCkbox.setValue( schedule.getOnFriday() );
			m_satCkbox.setValue( schedule.getOnSaturday() );
			m_sunCkbox.setValue( schedule.getOnSunday() );
			
			if ( schedule.getTimeFrequency() == TimeFrequency.AT_SPECIFIC_TIME )
			{
				int minutes;
				int hour;
				String minutesStr;
				String hoursStr;
				
				m_atTimeRb.setValue( true );
				m_repeatEveryRb.setValue( false );

				hour = schedule.getAtHours();
				hoursStr = String.valueOf( hour );
				GwtClientHelper.selectListboxItemByValue(
												m_atHoursListbox,
												hoursStr );

				minutes = schedule.getAtMinutes();
				minutesStr = String.valueOf( minutes );
				if ( minutesStr.equalsIgnoreCase( "0" ) )
					minutesStr = "00";
				else if ( minutesStr.equalsIgnoreCase( "5" ) )
					minutesStr = "05";
				GwtClientHelper.selectListboxItemByValue(
												m_atMinutesListbox,
												minutesStr );
			}
			else
			{
				int repeatEveryValue;

				m_atTimeRb.setValue( false );
				m_repeatEveryRb.setValue( true );
				
				repeatEveryValue = schedule.getRepeatEveryValue();
				if ( repeatEveryValue != 0 )
				{
					GwtClientHelper.selectListboxItemByValue(
														m_repeatEveryListbox,
														String.valueOf( schedule.getRepeatEveryValue() ) );
				}
			}
		}
	}
	
	/**
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach()
	{
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when this widget is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we having allocated a list to track events we've
		// registered yet...
		if ( null == m_registeredEventHandlers )
		{
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if ( m_registeredEventHandlers.isEmpty() )
		{
			// ...register the events.
			EventHelper.registerEventHandlers(
										GwtTeaming.getEventBus(),
										REGISTERED_EVENTS,
										this,
										m_registeredEventHandlers );
		}
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	public void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( ( null != m_registeredEventHandlers ) && ( ! ( m_registeredEventHandlers.isEmpty() ) ) )
		{
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
}
