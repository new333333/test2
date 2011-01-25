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
package org.kablink.teaming.security.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.dao.KablinkDao;
import org.kablink.teaming.domain.ZoneMismatchException;
import org.kablink.teaming.security.accesstoken.impl.TokenInfo;
import org.kablink.teaming.security.accesstoken.impl.TokenInfoApplication;
import org.kablink.teaming.security.accesstoken.impl.TokenInfoRequest;
import org.kablink.teaming.security.accesstoken.impl.TokenInfoSession;
import org.kablink.teaming.security.dao.SecurityDao;
import org.kablink.teaming.security.function.Condition;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author Jong Kim
 */
public class SecurityDaoImpl extends KablinkDao implements SecurityDao {

    private static final String ZONE_ID = "zoneId"; 
    private static final String WORK_AREA_ID = "workAreaId";
    private static final String WORK_AREA_TYPE = "workAreaType";
    private static final String FUNCTION_ID = "functionId";
    private static final String WORK_AREA_OPERATION_NAME = "operationName";
    private static final String PRINCIPAL_IDS = "principalIds";
    private static final String RESERVED_ID = "internalId";
    public void save(Object obj) {
        getHibernateTemplate().save(obj);
    }

    public void update(Object obj) {
        getHibernateTemplate().update(obj);
    }

    public void delete(Object obj) {
        getHibernateTemplate().delete(obj);
    }
    public Function loadFunction(Long zoneId, Long id)  throws NoObjectByTheIdException {
		long begin = System.currentTimeMillis();
		try {
	        Function f = (Function)getHibernateTemplate().get(Function.class, id);
	        if (f != null && zoneId.equals(f.getZoneId())) return f;
	        throw new NoObjectByTheIdException("errorcode.no.role.by.the.id", id);
    	}
    	finally {
    		end(begin, "loadFunction(Long,Long)");
    	}	        
    }

    public List findFunctions(final Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
	        return (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                        List<Function> results = session.createCriteria(Function.class)
	                        	.add(Expression.eq(ZONE_ID, zoneId))
	                        	.setFetchMode("operationNames", FetchMode.JOIN)
	                        	.setCacheable(true)
	                        	.addOrder(Order.asc("name"))
	                        	.list();
	                    	//since we eagerly fetch, results are not unique
	                    	List sorted = new ArrayList();
	                    	for (Function f:results) {
	                    		//keep orderd
	                    		if (!sorted.contains(f)) sorted.add(f);                    		
	                    	}
	                    	return sorted;
	                    }
	                }
	            );
    	}
    	finally {
    		end(begin, "findFunctions(Long)");
    	}
    }

    public Condition loadFunctionCondition(Long zoneId, Long functionConditionId) throws NoObjectByTheIdException {
		long begin = System.currentTimeMillis();
		try {
	        Condition c = (Condition)getHibernateTemplate().get(Condition.class, functionConditionId);
	        if (c != null && zoneId.equals(c.getZoneId())) 
	        	return c;
	        else 
	        	throw new NoObjectByTheIdException("errorcode.no.condition.by.the.id", functionConditionId);
    	}
    	finally {
    		end(begin, "loadFunctionCondition(Long,String)");
    	}	        
    }
    
    public List<Condition> findFunctionConditions(final Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
	        return (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                        return session.createCriteria(Condition.class)
	                        	.add(Expression.eq(ZONE_ID, zoneId))
	                        	//.setCacheable(true)
	                        	.addOrder(Order.asc("title"))
	                        	.list();
	                    }
	                }
	            );
    	}
    	finally {
    		end(begin, "findFunctionConditions(Long)");
    	}	
    }

	public WorkAreaFunctionMembership getWorkAreaFunctionMembership(final Long zoneId, 
			final Long workAreaId, final String workAreaType, final Long functionId) {
		long begin = System.currentTimeMillis();
		try {
	        return (WorkAreaFunctionMembership) getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                    	List results = session.createCriteria(WorkAreaFunctionMembership.class)
	                                .add(Expression.conjunction() 
	                               			.add(Expression.eq(ZONE_ID, zoneId))
	                               			.add(Expression.eq(WORK_AREA_ID, workAreaId))
	                               			.add(Expression.eq(WORK_AREA_TYPE, workAreaType))
	                               			.add(Expression.eq(FUNCTION_ID, functionId))
	                               		)
	                               	.setFetchMode("memberIds", FetchMode.JOIN)
	                               	.list();
	                    	//since we eagerly fetch, results are not unique
	                    	if(results == null || results.size() == 0)
	                    		return null;
	                    	else
	                    		return (WorkAreaFunctionMembership) results.get(0);
	                   	}
	                }
	            );	    	}
    	finally {
    		end(begin, "getWorkAreaFunctionMembership(Long,Long,String,Long)");
    	}

	}

	public List findWorkAreaFunctionMemberships(final Long zoneId, final Long functionId) {
		long begin = System.currentTimeMillis();
		try {
	        return (List) getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                    	return session.createCriteria(WorkAreaFunctionMembership.class)
	                                .add(Expression.conjunction() 
	                               			.add(Expression.eq(ZONE_ID, zoneId))
	                               			.add(Expression.eq(FUNCTION_ID, functionId))
	                               		)
	                               	.list();
	                    }
	                }
	                	
	            );		
    	}
    	finally {
    		end(begin, "findWorkAreaFunctionMemberships(Long,Long)");
    	}
	}
	public List findWorkAreaFunctionMemberships(final Long zoneId,
            final Long workAreaId, final String workAreaType) {
		long begin = System.currentTimeMillis();
		try {
	       return (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                    	List results = session.createCriteria(WorkAreaFunctionMembership.class)
	                                .add(Expression.conjunction() 
	                               			.add(Expression.eq(ZONE_ID, zoneId))
	                               			.add(Expression.eq(WORK_AREA_ID, workAreaId))
	                               			.add(Expression.eq(WORK_AREA_TYPE, workAreaType))
	                               		)
	                               	.setFetchMode("memberIds", FetchMode.JOIN)
	                               	.setCacheable(true) //cache for use during indexing, each entry will make this call
	                               	.list();
	                    	//since we eagerly fetch, results are not unique
	                    	return new ArrayList(new HashSet(results));
	                    	}
	                	}
	            );
    	}
    	finally {
    		end(begin, "findWorkAreaFunctionMemberships(Long,Long,String)");
    	}
	 }
	
    public void deleteWorkAreaFunctionMemberships(final Long zoneId, final Long workAreaId, final String workAreaType) {
		long begin = System.currentTimeMillis();
		try {
	    	List members = findWorkAreaFunctionMemberships(zoneId, workAreaId, workAreaType);
	    	for (int i=0; i<members.size(); ++i) {
	    		WorkAreaFunctionMembership m = (WorkAreaFunctionMembership)members.get(i);
	    		//hibernate will delete memberids
	    		delete(m);
	    	}
    	}
    	finally {
    		end(begin, "deleteWorkAreaFunctionMemberships(Long,Long,String)");
    	}
     	
    }
   
    private Criterion functionCriterion(List functions) {
        Disjunction disjunction = Expression.disjunction();
        for(Iterator i = functions.iterator(); i.hasNext();) {
            Function function = (Function) i.next();
            disjunction.add(Expression.eq("functionId", function.getId()));
        }
        return disjunction;
    }

    public List<Long> checkWorkAreaFunctionMembership(final Long zoneId,
            final Long workAreaId, final String workAreaType, 
            final String workAreaOperationName, final Set membersToLookup) {
		long begin = System.currentTimeMillis();
		try {
	    	return (List) getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                        // The following query performs 4 table joins in a single SQL query.
	                        // To increase performance, it only asks for the first matching 
	                        // record. In addition, it actually fetches only the ID (ie, primary
	                        // key) field of the record, which eliminates the need for another 
	                        // SELECT statement that would have been normally required otherwise. 
	                        // So, in summary, this query is as efficient as it can get. 
	                        return session.getNamedQuery("check-WorkAreaFunctionMembership")
	                       		.setLong(ZONE_ID, zoneId)
	                            .setLong(WORK_AREA_ID, workAreaId.longValue())
	                        	.setString(WORK_AREA_TYPE, workAreaType)
	                        	.setString(WORK_AREA_OPERATION_NAME, workAreaOperationName)
	                        	.setParameterList(PRINCIPAL_IDS, membersToLookup)
	                        	// 1/22/2011 - We need to get back the ids of ALL functions that matched the query.
	                        	//.setMaxResults(1) // Fetching the first matching row is enough for us
	                         	.setCacheable(true)
	                         	.list();
	                    }
	                }
	            );
    	}
    	finally {
    		end(begin, "checkWorkAreaFunctionMembership(Long,Long,String,String,Set)");
    	}
    }
    public List findWorkAreaFunctionMembershipsByOperation(final Long zoneId,
            final Long workAreaId, final String workAreaType, 
            final String workAreaOperationName) {
		long begin = System.currentTimeMillis();
		
		int retryCount = 0;
		//The loop will retry up to 5 times if it fails the first time
		while(retryCount < 5) {
			try {
		    	List matches = (List) getHibernateTemplate().execute(
		                new HibernateCallback() {
		                    public Object doInHibernate(Session session) throws HibernateException {
		                        // The following query performs 4 table joins in a single SQL query.
		                        return session.getNamedQuery("get-WorkAreaFunctionMembershipByOperation")
		                       		.setLong(ZONE_ID, zoneId)
		                            .setLong(WORK_AREA_ID, workAreaId.longValue())
		                        	.setString(WORK_AREA_TYPE, workAreaType)
		                        	.setString(WORK_AREA_OPERATION_NAME, workAreaOperationName)
		                         	.setCacheable(true)
		                         	.list();
		                    }
		                }
		            );
		    	return matches;
	    	} catch (org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException ol) {
				++retryCount;
				if (retryCount >= 5) {
					throw ol;
				}
				try {
					//wait a couple .2 ms
					Thread.sleep(300);
				} catch (InterruptedException e) {}
			} catch (StaleObjectStateException os) {
				++retryCount;
				if (retryCount >= 5) {
					throw os;
				}
				try {
					//wait a couple .2 ms
					Thread.sleep(300);
				} catch (InterruptedException e) {}
			}
	    	finally {
	    		end(begin, "findWorkAreaFunctionMembershipsByOperation(Long,Long,String,String)");
	    	}
		}
		return null;
    }
    
    public List findWorkAreaByOperation(final Long zoneId,
                      final String workAreaOperationName, final Set membersToLookup) {
		long begin = System.currentTimeMillis();
		try {
	    	List matches = (List) getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                        // The following query performs 4 table joins in a single SQL query.
	                        // To increase performance, it only asks for the first matching 
	                        // record. In addition, it actually fetches only the ID (ie, primary
	                        // key) field of the record, which eliminates the need for another 
	                        // SELECT statement that would have been normally required otherwise. 
	                        // So, in summary, this query is as efficient as it can get. 
	                        return session.getNamedQuery("get-FunctionMembershipByOperation")
	                       		.setLong(ZONE_ID, zoneId)
	                         	.setString(WORK_AREA_OPERATION_NAME, workAreaOperationName)
	                         	.setParameterList(PRINCIPAL_IDS, membersToLookup)
	                        	.list();
	                    }
	                }
	            );
	    	return matches;
    	}
    	finally {
    		end(begin, "findWorkAreaByOperation(Long,String,Set)");
    	}
    }

	public void deleteAll(final Class clazz) {
		long begin = System.currentTimeMillis();
		try {
			getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException {
					session.createQuery("DELETE " + clazz.getName())
							.executeUpdate();
					return null;
				}
			});
    	}
    	finally {
    		end(begin, "deleteAll(Class)");
    	}
	}

	public TokenInfoRequest loadTokenInfoRequest(Long zoneId, String infoId) {
		long begin = System.currentTimeMillis();
		try {
			TokenInfoRequest info = (TokenInfoRequest) getHibernateTemplate().get
			(TokenInfoRequest.class, infoId);
			
			if(info != null) {
				if(!zoneId.equals(info.getZoneId()))
					throw new ZoneMismatchException(info.getZoneId(), zoneId);
			}
	
			return info;
    	}
    	finally {
    		end(begin, "loadTokenInfoRequest(Long,String)");
    	}
	}

	public TokenInfoApplication loadTokenInfoApplication(Long zoneId, String infoId) {
		long begin = System.currentTimeMillis();
		try {
			TokenInfoApplication info = (TokenInfoApplication) getHibernateTemplate().get
			(TokenInfoApplication.class, infoId);
			
			if(info != null) {
				if(!zoneId.equals(info.getZoneId()))
					throw new ZoneMismatchException(info.getZoneId(), zoneId);
			}
	
			return info;
    	}
    	finally {
    		end(begin, "loadTokenInfoApplication(Long,String)");
    	}
	}

	public TokenInfoSession loadTokenInfoSession(Long zoneId, String infoId) {
		long begin = System.currentTimeMillis();
		try {
			TokenInfoSession info = (TokenInfoSession) getHibernateTemplate().get
			(TokenInfoSession.class, infoId);
			
			if(info != null) {
				if(!zoneId.equals(info.getZoneId()))
					throw new ZoneMismatchException(info.getZoneId(), zoneId);
			}
	
			return info;
    	}
    	finally {
    		end(begin, "loadTokenInfoSession(Long,String)");
    	}
	}

	public void deleteUserTokenInfoSession(final Long userId) {
		long begin = System.currentTimeMillis();
		try {
		   	getHibernateTemplate().execute(
		    	   	new HibernateCallback() {
		    	   		public Object doInHibernate(Session session) throws HibernateException {
			     	   		session.createQuery("Delete org.kablink.teaming.security.accesstoken.impl.TokenInfoSession where userId=:userId")
			   				.setLong("userId", userId)
		     	   			.executeUpdate();
		       	   		return null;
		       	   		}
		       	   	}
		    	 );    	
    	}
    	finally {
    		end(begin, "deleteUserTokenInfoSession(Long)");
    	}
	}

    public void deleteTokenInfoOlderThan(final Date thisDate) {
		long begin = System.currentTimeMillis();
		try {
			getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException {
					session.createQuery("DELETE " + TokenInfo.class.getName() + " where lastAccessTime<:lastAccessTime")
							.setDate("lastAccessTime", thisDate)
							.executeUpdate();
					return null;
				}
			});
    	}
    	finally {
    		end(begin, "deleteTokenInfoOlderThan()");
    	}    	
    }
 }
