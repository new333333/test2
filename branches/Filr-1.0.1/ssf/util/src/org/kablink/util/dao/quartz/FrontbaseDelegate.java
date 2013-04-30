/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.util.dao.quartz;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;
import org.slf4j.Logger;
/**
 * Override methods using setBytes for blobs
 * @author Janet McCann
 *
 */
public class FrontbaseDelegate extends StdJDBCDelegate {
   public FrontbaseDelegate(Logger logger, String tablePrefix, String instanceId) {
        super(logger, tablePrefix, instanceId);
   }
   public FrontbaseDelegate(Logger logger, String tablePrefix, String instanceId,
           Boolean useProperties) {
	   super(logger, tablePrefix, instanceId, useProperties);
   }
   /**
    * <p>
    * Insert the job detail record.
    * </p>
    * 
    * @param conn
    *          the DB Connection
    * @param job
    *          the job to insert
    * @return number of rows inserted
    * @throws IOException
    *           if there were problems serializing the JobDataMap
    */
   public int insertJobDetail(Connection conn, JobDetail job)
           throws IOException, SQLException {
       ByteArrayOutputStream baos = serializeJobData(job.getJobDataMap());

       PreparedStatement ps = null;

       int insertResult = 0;

       try {
           ps = conn.prepareStatement(rtp(INSERT_JOB_DETAIL));
           ps.setString(1, job.getName());
           ps.setString(2, job.getGroup());
           ps.setString(3, job.getDescription());
           ps.setString(4, job.getJobClass().getName());
           ps.setBoolean(5, job.isDurable());
           ps.setBoolean(6, job.isVolatile());
           ps.setBoolean(7, job.isStateful());
           ps.setBoolean(8, job.requestsRecovery());
           ps.setObject(9, new ByteArrayInputStream(baos.toByteArray()), Types.BLOB);

           insertResult = ps.executeUpdate();
       } finally {
           if (null != ps) {
               try {
                   ps.close();
               } catch (SQLException ignore) {
               }
           }
       }

       if (insertResult > 0) {
           String[] jobListeners = job.getJobListenerNames();
           for (int i = 0; jobListeners != null && i < jobListeners.length; i++)
               insertJobListener(conn, job, jobListeners[i]);
       }

       return insertResult;
   }

   /**
    * <p>
    * Update the job detail record.
    * </p>
    * 
    * @param conn
    *          the DB Connection
    * @param job
    *          the job to update
    * @return number of rows updated
    * @throws IOException
    *           if there were problems serializing the JobDataMap
    */
   public int updateJobDetail(Connection conn, JobDetail job)
           throws IOException, SQLException {
       ByteArrayOutputStream baos = serializeJobData(job.getJobDataMap());

       PreparedStatement ps = null;

       int insertResult = 0;

       try {
           ps = conn.prepareStatement(rtp(UPDATE_JOB_DETAIL));
           ps.setString(1, job.getDescription());
           ps.setString(2, job.getJobClass().getName());
           ps.setBoolean(3, job.isDurable());
           ps.setBoolean(4, job.isVolatile());
           ps.setBoolean(5, job.isStateful());
           ps.setBoolean(6, job.requestsRecovery());
           ps.setObject(7, new ByteArrayInputStream(baos.toByteArray()), Types.BLOB);
           ps.setString(8, job.getName());
           ps.setString(9, job.getGroup());

           insertResult = ps.executeUpdate();
       } finally {
           if (null != ps) {
               try {
                   ps.close();
               } catch (SQLException ignore) {
               }
           }
       }

       if (insertResult > 0) {
           deleteJobListeners(conn, job.getName(), job.getGroup());

           String[] jobListeners = job.getJobListenerNames();
           for (int i = 0; jobListeners != null && i < jobListeners.length; i++)
               insertJobListener(conn, job, jobListeners[i]);
       }

       return insertResult;
   }
   /**
    * <p>
    * Update the job data map for the given job.
    * </p>
    * 
    * @param conn
    *          the DB Connection
    * @param job
    *          the job to update
    * @return the number of rows updated
    */
   public int updateJobData(Connection conn, JobDetail job)
           throws IOException, SQLException {
       ByteArrayOutputStream baos = serializeJobData(job.getJobDataMap());

       PreparedStatement ps = null;

       try {
           ps = conn.prepareStatement(rtp(UPDATE_JOB_DATA));
           ps.setObject(1, new ByteArrayInputStream(baos.toByteArray()), Types.BLOB);
           ps.setString(2, job.getName());
           ps.setString(3, job.getGroup());

           return ps.executeUpdate();
       } finally {
           if (null != ps) {
               try {
                   ps.close();
               } catch (SQLException ignore) {
               }
           }
       }
   }
   /**
    * <p>
    * Insert a new calendar.
    * </p>
    * 
    * @param conn
    *          the DB Connection
    * @param calendarName
    *          the name for the new calendar
    * @param calendar
    *          the calendar
    * @return the number of rows inserted
    * @throws IOException
    *           if there were problems serializing the calendar
    */
   public int insertCalendar(Connection conn, String calendarName,
           Calendar calendar) throws IOException, SQLException {
       ByteArrayOutputStream baos = serializeObject(calendar);

       PreparedStatement ps = null;

       try {
           ps = conn.prepareStatement(rtp(INSERT_CALENDAR));
           ps.setString(1, calendarName);
           ps.setObject(2, new ByteArrayInputStream(baos.toByteArray()), Types.BLOB);

           return ps.executeUpdate();
       } finally {
           if (null != ps) {
               try {
                   ps.close();
               } catch (SQLException ignore) {
               }
           }
       }
   }

   /**
    * <p>
    * Update a calendar.
    * </p>
    * 
    * @param conn
    *          the DB Connection
    * @param calendarName
    *          the name for the new calendar
    * @param calendar
    *          the calendar
    * @return the number of rows updated
    * @throws IOException
    *           if there were problems serializing the calendar
    */
   public int updateCalendar(Connection conn, String calendarName,
           Calendar calendar) throws IOException, SQLException {
       ByteArrayOutputStream baos = serializeObject(calendar);

       PreparedStatement ps = null;

       try {
           ps = conn.prepareStatement(rtp(UPDATE_CALENDAR));
           ps.setObject(1, new ByteArrayInputStream(baos.toByteArray()), Types.BLOB);
           ps.setString(2, calendarName);

           return ps.executeUpdate();
       } finally {
           if (null != ps) {
               try {
                   ps.close();
               } catch (SQLException ignore) {
               }
           }
       }
   }
   
}
