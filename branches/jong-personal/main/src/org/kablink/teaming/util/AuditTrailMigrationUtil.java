/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.util;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.dao.StatelessSessionTemplate;
import org.kablink.teaming.domain.AuditTrail;
import org.kablink.teaming.domain.DeletedBinder;

/**
 * @author Jong
 *
 */
public class AuditTrailMigrationUtil {

	public enum MigrationStatus {
		/*
		 * Minimum required migration has been completed
		 */
		minimumRequiredCompleted,
		/*
		 * All migration has been completed
		 */
		allCompleted,
		/*
		 * Neither of above
		 */
		none
	}
	
	private static Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	public static void migrateMinimumForZone(final Long zoneId, final Long now) {
		getStatelessSessionTemplate().execute(new StatelessSessionTemplate.Callback<Void>() {
			@Override
			public Void doInHibernate(final StatelessSession session)
					throws HibernateException, SQLException {
				int numberOfDays = SPropsUtil.getInt("binder.changes.allowed.days", 7) + 1;
				Date sinceDate = new Date(now - numberOfDays*1000L*60L*60L*24L);
				migrateSince(session, zoneId, sinceDate);
				return null;
			}
		});
	}
	
	public static void migrateAll() {
		getStatelessSessionTemplate().execute(new StatelessSessionTemplate.Callback<Void>() {
			@Override
			public Void doInHibernate(final StatelessSession session)
					throws HibernateException, SQLException {
				migrateSince(session, null, null);
				return null;
			}
		});
	}
	
	public static boolean isAuditTrailTableEmpty() {
		// If SS_AuditTrail table is empty, migration is considered complete (although, technically speaking, 
		// it may be because the table was empty and there was nothing to migrate in the first place).
		AuditTrail auditTrail = getStatelessSessionTemplate().execute(new StatelessSessionTemplate.Callback<AuditTrail>() {
			@Override
			public AuditTrail doInHibernate(final StatelessSession session)
					throws HibernateException, SQLException {
				return (AuditTrail) session.createCriteria(AuditTrail.class)
						.setMaxResults(1)
						.setCacheable(false)
						.uniqueResult();
			}
		});
		return auditTrail == null;
	}
	
	public static MigrationStatus getMigrationStatus() {
		try {
			MigrationStatus result = MigrationStatus.none;
			
			String filePath = SPropsUtil.getDirPath("data.root.dir") + "conf" + File.separator + "atms.txt"; // atms = Audit Trail Migration Status
			Path file = Paths.get(filePath);
			if(Files.exists(file)) {
				List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
				if(lines.size() > 0) {
					String value = lines.get(0).trim();
					if(value.equals(MigrationStatus.minimumRequiredCompleted.name()))
						return MigrationStatus.minimumRequiredCompleted;
					else if(value.equals(MigrationStatus.allCompleted.name()))
						return MigrationStatus.allCompleted;
				}
			}
			return result;
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public static void setMigrationStatus(MigrationStatus newStatus) {
		try {
			String filePath = SPropsUtil.getDirPath("data.root.dir") + "conf" + File.separator + "atms.txt"; // atms = Audit Trail Migration Status
			Path file = Paths.get(filePath);
			List<String> lines = new ArrayList<String>();
			lines.add(newStatus.name());
			Files.write(file, lines, StandardCharsets.UTF_8);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
		
	private static void migrateSince(StatelessSession session, Long zoneId, Date sinceDate) {
		if(zoneId != null) {
			if(sinceDate == null)
				throw new IllegalArgumentException("Date must also be specified when using zone as a criteria");
		}

		Criteria critTotalSize = session
				.createCriteria(AuditTrail.class)
				.setProjection(Projections.rowCount());
		if(zoneId != null) {
			critTotalSize
			.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
			.add(Restrictions.ge("startDate", sinceDate));
		}
		long totalSizeToMigrate = (Long) critTotalSize.uniqueResult();
		
		if(totalSizeToMigrate == 0) {
			if(zoneId != null)
				logger.info("Migrating minimum required audit trail records created on '" + sinceDate + "' or later for zone '" + zoneId + "' - No record to process.");
			else
				logger.info("Migrating all remaining audit trail records across all zones - No record to process.");
			return;
		}

		int batchMaxSize = SPropsUtil.getInt("audittrail.migration.batch.max.size", 1000);
		boolean discardUnprocessableRecord = SPropsUtil.getBoolean("audittrail.migration.discard.unprocessable.record", false);
		
		if(zoneId != null) {
			logger.info("Migrating minimum required audit trail records created on '" + sinceDate + "' or later for zone '" + zoneId + "'");
			logger.info("There are " + totalSizeToMigrate + " audit trail records to process for this zone...");
		}
		else {
			logger.info("Migrating all remaining audit trail records asynchronously across all zones - This may take significant time depending on the amount of data to migrate.");
			logger.info("There are total of " + totalSizeToMigrate + " audit trail records to process across all zones...");
		}
		
		long totalProcessed = 0;
		String auditTrailRecordLastId = null;
		
		while(true) {	
			Criteria critBatch = session.createCriteria(AuditTrail.class)
					.setMaxResults(batchMaxSize)
					.setCacheable(false);
			if(zoneId != null) {
				critBatch
				.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
				.add(Restrictions.ge("startDate", sinceDate));
			}
			
			final List<AuditTrail> auditTrailRecords = critBatch.list();
					
			if(auditTrailRecords.size() > 0) {
				// Use Hibernate transaction and manage it directly. 
				// The Spring TransactionTemplate is of no use here, because it only works with regular Session and not StatelessSession.
				Transaction trans = session.beginTransaction();
				try {
					List<Object> newRecords;
					for(AuditTrail auditTrailRecord:auditTrailRecords) {
						auditTrailRecordLastId = auditTrailRecord.getId();
						if(logger.isDebugEnabled())
							logger.debug("Processing audit trail record with id=" + auditTrailRecord.getId() + 
									", startDate=" + auditTrailRecord.getStartDate() + 
									", transactionType=" + ((auditTrailRecord.getAuditType() != null)? auditTrailRecord.getAuditType().name() : "null"));
						newRecords = auditTrailRecord.toNewAuditObjects();
						// Migrate the old record by inserting new records that it maps to.
						for(Object newRecord:newRecords) {
							try {
								session.insert(newRecord);
							}
							catch(Exception e) {
								if((e instanceof ConstraintViolationException) && (newRecord instanceof DeletedBinder)) {
									// In some rare cases, it is possible to see duplicate records in the SS_AuditTrail table describing
									// deletion of the same binder object. Since the new SS_DeletedBinder uses binderId as primary key,
									// duplicate entries are not allowed with the new table. When that ever happens, we should simply
									// keep a single record for the binder. For the sake of simplicity, we will keep the previous one
									// and toss out the later one (probably it doesn't matter which one we keep).
									logger.warn("Failed to insert DeletedBinder " + newRecord.toString() + " for audit trail record [" + auditTrailRecordLastId + "] due to duplicate entry - Discarding it and continuing");
									if(logger.isDebugEnabled())
										logger.debug("The details of the exception", e);
								}
								else {
									// This record is not processable.
									if(discardUnprocessableRecord) {
										// Discard the record and continue.
										logger.warn("Unable to process the audit trail record [" + auditTrailRecordLastId + "]. Discarding it and continuing", e);
									}
									else {
										// Rethrow the exception so as to abort the transaction and stop the migration process.
										throw e;
									}
								}
							}
						}
						// Purge the old record.
						session.delete(auditTrailRecord);
					}
					trans.commit();
					totalProcessed += auditTrailRecords.size();
					logger.info("So far " + totalProcessed + " records have been successfully processed out of expected " + totalSizeToMigrate + "...");
				}
				catch(Exception e) {
					logger.error("Failed to process the audit trail record [" + auditTrailRecordLastId + "]", e);
					trans.rollback();
					logger.error("Transaction rolled back");
					throw e; // Rethrow so that migration would fail fast
				}
			}
			else {
				// No more record to process
				break;
			}
		}
		
		if(zoneId != null)
			logger.info("Migration of minimum required audit trail records has been completed for zone '" + zoneId + "'.");
		else
			logger.info("Migration of all remaining audit trail records across all zones has been completed.");
	}
	
	private static StatelessSessionTemplate getStatelessSessionTemplate() {
		return (StatelessSessionTemplate)SpringContextUtil.getBean("statelessSessionTemplate");
	}
	
}
