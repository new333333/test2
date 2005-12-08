/**
 * Copyright (c) 2000-2005 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sitescape.util.dao.hibernate;

import com.sitescape.util.GetterUtil;
import com.sitescape.util.dao.DataAccess;
import com.sitescape.util.dao.DriverInfo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Enumeration;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.GenericDialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.function.SQLFunction;

import com.sitescape.util.dao.hibernate.FrontBase4Dialect;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.id.TableHiLoGenerator;
import org.hibernate.sql.CaseFragment;
import org.hibernate.sql.JoinFragment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <a href="DynamicDialect.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Brian Wing Shun Chan
 * @version $Revision: 1.12 $
 *
 */
public class DynamicDialect extends Dialect {

	public DynamicDialect() {

		// Instantiate the proper dialect

		/*
		String datasource = GetterUtil.getString(
			Environment.getProperties().getProperty(Environment.DATASOURCE,
			"jdbc/LiferayPool")); */
		
		String datasource = "jdbc/SiteScapePool";

		try {
			Connection con = DataAccess.getConnection(datasource);

			String url = con.getMetaData().getURL();

			Class dialectClass = null;

			if (url.startsWith(DriverInfo.DB2_URL)) {
				dialectClass = DB2Dialect.class;
			}
			else if (url.startsWith(DriverInfo.HYPERSONIC_URL)) {
				dialectClass = HSQLDialect.class;
			}
			else if (url.startsWith(DriverInfo.MYSQL_URL)) {
				dialectClass = MySQLDialect.class;
			}
			else if (url.startsWith(DriverInfo.ORACLE_URL)) {
				dialectClass = Oracle9Dialect.class;
			}
			else if (url.startsWith(DriverInfo.FRONTBASE_URL)) {
				dialectClass = FrontBase4Dialect.class;
			}			
			else if (url.startsWith(DriverInfo.POSTGRESQL_URL)) {
				dialectClass = PostgreSQLDialect.class;
			}
			else if (url.startsWith(DriverInfo.SQLSERVER_URL) || url.startsWith(DriverInfo.SQLSERVER2_URL)) {
				dialectClass = SQLServerDialect.class;
			}

			if (dialectClass != null) {
				_log.debug("Class implementation " + dialectClass.getName());
			}
			else {
				_log.debug("Class implementation is null");
			}

			if (dialectClass != null) {
				_dialect = (Dialect)dialectClass.newInstance();
			}

			DataAccess.cleanUp(con);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		if (_dialect == null) {
			_dialect = new GenericDialect();
		}

		// Synchorize default properties

		getDefaultProperties().clear();

		Enumeration enu = _dialect.getDefaultProperties().propertyNames();

		while (enu.hasMoreElements()) {
        	String key = (String)enu.nextElement();
        	String value = _dialect.getDefaultProperties().getProperty(key);

			getDefaultProperties().setProperty(key, value);
		}
	}
	public String getTypeName(int code) throws HibernateException {
		return _dialect.getTypeName(code);
	}

	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException {
		return _dialect.getTypeName(code, length, precision, scale);
	}
	public boolean hasAlterTable() {
		return _dialect.hasAlterTable();
	}

	public boolean dropConstraints() {
		return _dialect.dropConstraints();
	}
	public boolean qualifyIndexName() {
		return _dialect.qualifyIndexName();
	}
	public boolean forUpdateOfColumns() {
		return _dialect.forUpdateOfColumns();
	}
	public String getForUpdateString(String aliases) {
		return _dialect.getForUpdateString(aliases);
	}
	public String getForUpdateNowaitString(String aliases) {
		return _dialect.getForUpdateNowaitString(aliases);
	}

	public String getForUpdateString() {
		return _dialect.getForUpdateString();
	}
	public String getForUpdateNowaitString() {
		return _dialect.getForUpdateNowaitString();
	}
	public boolean supportsUnique() {
		return _dialect.supportsUnique();
	}
	public boolean supportsUniqueConstraintInCreateAlterTable() {
	    return _dialect.supportsUniqueConstraintInCreateAlterTable();
	}
	public String getAddColumnString() {
		return _dialect.getAddColumnString();
	}

	public String getDropForeignKeyString() {
		return _dialect.getDropForeignKeyString();
	}

	public String getTableTypeString() {
		return _dialect.getTableTypeString();
	}
	public String getAddForeignKeyConstraintString(
			String constraintName, String[] foreignKey, String referencedTable,
			String[] primaryKey) {
		return _dialect.getAddForeignKeyConstraintString(
			constraintName, foreignKey, referencedTable, primaryKey);
	}

	public String getAddPrimaryKeyConstraintString(String constraintName) {
		return _dialect.getAddPrimaryKeyConstraintString(constraintName);
	}

	public String getNullColumnString() {
		return _dialect.getNullColumnString();
	}

	public boolean supportsIdentityColumns() {
		return _dialect.supportsIdentityColumns();
	}
	
	public boolean supportsSequences() {
		return _dialect.supportsSequences();
	}

	public boolean supportsInsertSelectIdentity() {
		return _dialect.supportsInsertSelectIdentity();
	}
	public String appendIdentitySelectToInsert(String insertSQL) {
		return _dialect.appendIdentitySelectToInsert(insertSQL);
	}


	public String getIdentitySelectString(String table, String column, int type)
	throws MappingException {
		return _dialect.getIdentitySelectString(table, column, type);
	}
	
	public String getIdentityColumnString(int type) throws MappingException {
		return _dialect.getIdentityColumnString(type);
	}

	public String getIdentityInsertString() {
		return _dialect.getIdentityInsertString();
	}

	public String getNoColumnsInsertString() {
		return _dialect.getNoColumnsInsertString();
	}

	public String getSequenceNextValString(String sequenceName)
	throws MappingException {

		return _dialect.getSequenceNextValString(sequenceName);
	}

	public String[] getCreateSequenceStrings(String sequenceName) throws MappingException {
		return _dialect.getCreateSequenceStrings(sequenceName);
	}

	public String[] getDropSequenceStrings(String sequenceName) throws MappingException {
		return _dialect.getDropSequenceStrings(sequenceName);
	}
	
	public String getQuerySequencesString() {
		return _dialect.getQuerySequencesString();
	}

	public String getCascadeConstraintsString() {
		return _dialect.getCascadeConstraintsString();
	}

	public JoinFragment createOuterJoinFragment() {
		return _dialect.createOuterJoinFragment();
	}

	public CaseFragment createCaseFragment() {
		return _dialect.createCaseFragment();
	}

	public String getLowercaseFunction() {
		return _dialect.getLowercaseFunction();
	}

	public boolean supportsLimit() {
		return _dialect.supportsLimit();
	}

	public boolean supportsLimitOffset() {
		return _dialect.supportsLimitOffset();
	}

	public String getLimitString(String querySelect, boolean hasOffset) {
		return _dialect.getLimitString(querySelect, hasOffset);
	}

	public String getLimitString(
		String querySelect, int offset, int limit) {
		return _dialect.getLimitString(querySelect, offset, limit);
	}
	
	public boolean supportsVariableLimit() {
		return _dialect.supportsVariableLimit();
	}

	public boolean bindLimitParametersInReverseOrder() {
		return _dialect.bindLimitParametersInReverseOrder();
	}
	
	public boolean bindLimitParametersFirst() {
		return _dialect.bindLimitParametersFirst();
	}

	public boolean useMaxForLimit() {
		return _dialect.useMaxForLimit();
	}
	
	public char openQuote() {
		return _dialect.openQuote();
	}

	public char closeQuote() {
		return _dialect.closeQuote();
	}
	
	public boolean supportsIfExistsBeforeTableName()  {
		return _dialect.supportsIfExistsBeforeTableName();
	}

	public boolean supportsIfExistsAfterTableName() {
		return _dialect.supportsIfExistsAfterTableName();
	}

	public char getSchemaSeparator() {
		return _dialect.getSchemaSeparator();
	}

	public boolean supportsColumnCheck() {
		return _dialect.supportsColumnCheck();
	}
	
	public boolean supportsTableCheck() {
		return _dialect.supportsTableCheck();
	}

	public boolean hasDataTypeInIdentityColumn() {
		return _dialect.hasDataTypeInIdentityColumn();
	}

	public boolean supportsCascadeDelete() {
		return _dialect.supportsCascadeDelete();
	}

	public String appendLockHint(LockMode mode, String tableName) {
		return _dialect.appendLockHint(mode, tableName);
	}

	public Class getNativeIdentifierGeneratorClass() {
		return _dialect.getNativeIdentifierGeneratorClass();
	}

	public String getSelectGUIDString() {
		return _dialect.getSelectGUIDString();
	}

	public boolean supportsOuterJoinForUpdate() {
		return _dialect.supportsOuterJoinForUpdate();
	}

	public String getSelectClauseNullString(int sqlType) {
		return _dialect.getSelectClauseNullString(sqlType);
	}
	
	public boolean supportsNotNullUnique() {
		return _dialect.supportsNotNullUnique();
	}
	
	public SQLExceptionConverter buildSQLExceptionConverter() {
		return _dialect.buildSQLExceptionConverter();
	}

	public ViolatedConstraintNameExtracter
	getViolatedConstraintNameExtracter() {
		return _dialect.getViolatedConstraintNameExtracter();
	}
	
	public boolean hasSelfReferentialForeignKeyBug() {
		return _dialect.hasSelfReferentialForeignKeyBug();
	}	

	public boolean useInputStreamToInsertBlob() {
		return _dialect.useInputStreamToInsertBlob();
	}
	
	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
		return _dialect.registerResultSetOutParameter(statement, col);
	}

	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
		return _dialect.getResultSet(ps);
	}
	
	public boolean supportsUnionAll() {
		return _dialect.supportsUnionAll();
	}
	
	public boolean supportsCommentOn() {
		return _dialect.supportsCommentOn();
	}
	
	public String getTableComment(String comment) {
		return _dialect.getTableComment(comment);
	}

	public String getColumnComment(String comment) {
		return _dialect.getColumnComment(comment);
	}

	public String toString() {
		if (_dialect != null) {
			return _dialect.toString();
		}
		else {
			return null;
		}
	}

	private static final Log _log = LogFactory.getLog(DynamicDialect.class);

	private Dialect _dialect;

}