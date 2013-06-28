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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.ArrayList;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'get calendar
 * appointments' RPC command.
 * 
 * @author drfoster@novell.com
 */
public class CalendarAppointmentsRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private ArrayList<Appointment>	m_appointments;	//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public CalendarAppointmentsRpcResponseData() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 *
	 * @param appointments
	 */
	public CalendarAppointmentsRpcResponseData(ArrayList<Appointment> appointments) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setAppointments(appointments);
	}
	
	/**
	 * Constructor method.
	 *
	 * @param appointment
	 */
	public CalendarAppointmentsRpcResponseData(Appointment appointment) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		addAppointment(appointment);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public int                    getAppointmentCount() {validateAppointments(); return m_appointments.size();}
	public ArrayList<Appointment> getAppointments()     {validateAppointments(); return m_appointments;       }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAppointments(ArrayList<Appointment> appointments) {m_appointments = appointments;}

	/**
	 * Adds an appointment to the appointment list.
	 * 
	 * @param appointment
	 */
	public void addAppointment(Appointment appointment) {
		validateAppointments();
		m_appointments.add(appointment);
	}
	
	/*
	 * Validates that there's an appointments list defined.  If there's
	 * not, one is defined.
	 */
	private void validateAppointments() {
		if (null == m_appointments) {
			m_appointments = new ArrayList<Appointment>();
		}
	}
}
