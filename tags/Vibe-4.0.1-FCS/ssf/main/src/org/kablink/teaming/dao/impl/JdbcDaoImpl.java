/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.kablink.teaming.dao.JdbcDao;
import org.kablink.teaming.util.encrypt.HibernateEncryptor;
import org.kablink.util.encrypt.ExtendedPBEStringEncryptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * JDBC-based DAO module.
 * 
 * IMPORTANT: This module is reserved for select internal use only, and application layer MUST refrain
 *            from using this module directly in almost all use cases. If data exchange takes place through
 *            this interface, it will bypass the cluster-wide caching layer completely, which in many
 *            cases can cause serious inconsistency among cluster nodes and can result in unrecoverable data
 *            integrity issues in the persistent data layer.
 * 
 * @author jong
 *
 */
public class JdbcDaoImpl extends JdbcDaoSupport implements JdbcDao {

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private LobHandler lobHandler;
	// We permit access to transaction template from inside this DAO class,
	// which is a notable departure from the previous approach we took with
	// Hibernate-based DAO classes. 
	// Unlike with Hibernate-based DAO classes that typically exchange
	// domain objects across the interface boundary in response to short-lived
	// user interactions, this JDBC-based DAO class tends to be used for 
	// targeted batch-oriented operations performed by the system itself.
	// For that reason, it is beneficial (and sometimes necessary) for this
	// DAO class to be able to internally control and manage the transaction
	// boundaries as opposed to the caller doing it.
	private TransactionTemplate transactionTemplate;
	
	@Override
	protected JdbcTemplate createJdbcTemplate(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		return (JdbcTemplate) namedParameterJdbcTemplate.getJdbcOperations();
	}

	protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}
	
	public void setLobHandler(LobHandler lobHandler) {
		this.lobHandler = lobHandler;
	}
	
	protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	@Override
	public Map<String,Integer> migrateHibernateEncryptedItems(final HibernateEncryptor hibernateEncryptorWithOldKey, final HibernateEncryptor hibernateEncryptorWithNewKey) {
		Map<String,Integer> result = new HashMap<String,Integer>();
		int count;
		
		// LdapConnectionConfig (SS_LdapConnectionConfig)
		count = getTransactionTemplate().execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				int count = 0;
				List<EncryptedRow<String>> encryptedRows = JdbcDaoImpl.this.getJdbcTemplate().query("select id, credentials from SS_LdapConnectionConfig where credentials is not null", new EncryptedStringIdRowMapper());
				for(EncryptedRow<String> row:encryptedRows) {
					try {
						// Decrypt using old key
						String decryptedData = hibernateEncryptorWithOldKey.decrypt(row.getEncryptedData());
						// Encrypt using new key
						String newlyEncryptedData = hibernateEncryptorWithNewKey.encrypt(decryptedData);
						JdbcDaoImpl.this.getJdbcTemplate().update("update SS_LdapConnectionConfig set credentials = ? where id = ?", newlyEncryptedData, row.getId());
						count++;
					}
					catch(Exception e) {
						logger.error("Error migrating credentials column value for row with id=" + row.getId() + " in SS_LdapConnectionConfig table", e);
						continue; // Continue to the next row
					}
				}
				return count;
			}
		});
		result.put("SS_LdapConnectionConfig", Integer.valueOf(count));
		
		// PostingDef (SS_Postings)
		count = getTransactionTemplate().execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				int count = 0;
				List<EncryptedRow<String>>encryptedRows = JdbcDaoImpl.this.getJdbcTemplate().query("select id, credentials from SS_Postings where credentials is not null", new EncryptedStringIdRowMapper());
				for(EncryptedRow<String> row:encryptedRows) {
					try {
						String decryptedData = hibernateEncryptorWithOldKey.decrypt(row.getEncryptedData());
						String newlyEncryptedData = hibernateEncryptorWithNewKey.encrypt(decryptedData);
						JdbcDaoImpl.this.getJdbcTemplate().update("update SS_Postings set credentials = ? where id = ?", newlyEncryptedData, row.getId());
						count++;
					}
					catch(Exception e) {
						logger.error("Error migrating credentials column value for row with id=" + row.getId() + " in SS_Postings table", e);
						continue; // Continue to the next row
					}
				}
				return count;
			}
		});
		result.put("SS_Postings", Integer.valueOf(count));
		
		// ResourceDriverConfig (SS_ResourceDriver)
		count = getTransactionTemplate().execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				int count = 0;
				List<EncryptedRow<Long>> encryptedRowsWithLongId = JdbcDaoImpl.this.getJdbcTemplate().query("select id, password from SS_ResourceDriver where password is not null", new EncryptedLongIdRowMapper());
				for(EncryptedRow<Long> row:encryptedRowsWithLongId) {
					try {
						String decryptedData = hibernateEncryptorWithOldKey.decrypt(row.getEncryptedData());
						String newlyEncryptedData = hibernateEncryptorWithNewKey.encrypt(decryptedData);
						JdbcDaoImpl.this.getJdbcTemplate().update("update SS_ResourceDriver set password = ? where id = ?", newlyEncryptedData, row.getId());
						count++;
					}
					catch(Exception e) {
						logger.error("Error migrating password column value for row with id=" + row.getId() + " in SS_ResourceDriver table", e);
						continue; // Continue to the next row
					}
				}
				return count;
			}
		});
		result.put("SS_ResourceDriver", Integer.valueOf(count));
		
		return result;
	}
	
	@Override
	public Map<String,Integer> migrateUserPasswords(ExtendedPBEStringEncryptor oldkey_encryptor_first_gen,
			ExtendedPBEStringEncryptor oldkey_encryptor_second_gen,
			ExtendedPBEStringEncryptor newkey_encryptor_first_gen,
			ExtendedPBEStringEncryptor newkey_encryptor_second_gen) {
		Map<String,Integer> result = new HashMap<String,Integer>();
		int count;
		
		// Migrate those passwords that were encrypted using first generation algorithm.
		count = migrateUserPasswords(oldkey_encryptor_first_gen, newkey_encryptor_first_gen, "PBEWithMD5AndDES");
		result.put("withFirstGen", Integer.valueOf(count));
	
		// Migrate those passwords that were encrypted using second generation algorithm.
		count = migrateUserPasswords(oldkey_encryptor_second_gen, newkey_encryptor_second_gen, "PBEWITHSHA256AND128BITAES-CBC-BC");
		result.put("withSecondGen", Integer.valueOf(count));
		
		return result;
	}
	
	private int migrateUserPasswords(final ExtendedPBEStringEncryptor oldkey_encryptor,
			final ExtendedPBEStringEncryptor newkey_encryptor,
			String algorithm) {
		// Get a list of user passwords encrypted using first generation algorithm and old key.
		List<EncryptedRow<Long>> encryptedPasswords = this.getJdbcTemplate().query
				("select id, password from SS_Principals where password is not null and pwdenc = ?", new EncryptedLongIdRowMapper(), algorithm);
		
		int successCountTotal = 0;
		int successCountOneBatch;

		int batchSize = 100; // transaction batch size
		
		int offset = 0;
		int size;
		
		while(offset < encryptedPasswords.size()) {
			size = Math.min(encryptedPasswords.size()-offset, batchSize);
			final List<EncryptedRow<Long>> sublist = encryptedPasswords.subList(offset, offset+size);
			
			successCountOneBatch = getTransactionTemplate().execute(new TransactionCallback<Integer>() {
				@Override
				public Integer doInTransaction(TransactionStatus status) {
					int count = 0;
					for(EncryptedRow<Long> row : sublist) {
						try {
							// Decrypt the password using old key
							String decryptedPassword = oldkey_encryptor.decrypt(row.getEncryptedData());
							// Encrypt the password using new key. The algorithm remains the same (that is,
							// we only upgrade key value not the algorithm during this processing).
							String newlyEncryptedPassword = newkey_encryptor.encrypt(decryptedPassword);
							JdbcDaoImpl.this.getJdbcTemplate().update("update SS_Principals set password = ? where id = ?", newlyEncryptedPassword, row.getId());	
							count++;
						}
						catch(Exception e) {
							logger.error("Error migrating password column value for row with id=" + row.getId() + " in SS_Principals table", e);
							continue; // Continue to the next row
						}
					}
					return count;
				}
			});
			// This log is just to inform user that the system is moving along...
			logger.info("Migrated " + successCountOneBatch + " user passwords...");
			successCountTotal += successCountOneBatch;
			
			offset += size;
		}
		
		return successCountTotal;
	}
	
	private static final class EncryptedRow<T> {
		private T id;
		private String encryptedData;
		public T getId() {
			return id;
		}
		public void setId(T id) {
			this.id = id;
		}
		public String getEncryptedData() {
			return encryptedData;
		}
		public void setEncryptedData(String encryptedData) {
			this.encryptedData = encryptedData;
		}
	}
	
	private static final class EncryptedLongIdRowMapper implements RowMapper<EncryptedRow<Long>> {

		@Override
		public EncryptedRow<Long> mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			Object id = rs.getObject(1);
			if(id instanceof Long) { 
			}
			else if(id instanceof Number) {
				id = Long.valueOf(((Number)id).longValue());
			}
			else {
				throw new IllegalArgumentException("An unexpected type [" + id.getClass().getName() + "] for ID value [" + id + "] when Long is expected.");
			}
			EncryptedRow<Long> row = new EncryptedRow<Long>();
			row.setId((Long)id);
			row.setEncryptedData(rs.getString(2));
			return row;
		}
		
	}
	
	private static final class EncryptedStringIdRowMapper implements RowMapper<EncryptedRow<String>> {

		@Override
		public EncryptedRow<String> mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			EncryptedRow<String> row = new EncryptedRow<String>();
			row.setId(rs.getString(1));
			row.setEncryptedData(rs.getString(2));
			return row;
		}		
	}
	
}
