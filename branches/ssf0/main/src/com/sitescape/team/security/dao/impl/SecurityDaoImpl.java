/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.security.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.domain.ZoneMismatchException;
import com.sitescape.team.security.accesstoken.impl.TokenInfoRequest;
import com.sitescape.team.security.accesstoken.impl.TokenInfoSession;
import com.sitescape.team.security.dao.SecurityDao;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
/**
 *
 * @author Jong Kim
 */
public class SecurityDaoImpl extends HibernateDaoSupport implements SecurityDao {

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
        Function f = (Function)getHibernateTemplate().get(Function.class, id);
        if (zoneId.equals(f.getZoneId())) return f;
        throw new NoObjectByTheIdException("errorcode.no.role.by.the.id", id);
    }

    @SuppressWarnings("unchecked")
    public List<Function> findFunctions(final Long zoneId) {
        return (List<Function>)getHibernateTemplate().execute(
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


	public WorkAreaFunctionMembership getWorkAreaFunctionMembership(final Long zoneId, 
			final Long workAreaId, final String workAreaType, final Long functionId) {
        return (WorkAreaFunctionMembership) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                    	@SuppressWarnings("unchecked")
                    	List<WorkAreaFunctionMembership> results = session.createCriteria(WorkAreaFunctionMembership.class)
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
            );	
	}

	public List findWorkAreaFunctionMemberships(final Long zoneId, final Long functionId) {
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
	public List findWorkAreaFunctionMemberships(final Long zoneId,
            final Long workAreaId, final String workAreaType) {
		
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
    public void deleteWorkAreaFunctionMemberships(final Long zoneId, final Long workAreaId, final String workAreaType) {
    	List members = findWorkAreaFunctionMemberships(zoneId, workAreaId, workAreaType);
    	for (int i=0; i<members.size(); ++i) {
    		WorkAreaFunctionMembership m = (WorkAreaFunctionMembership)members.get(i);
    		//hibernate will delete memberids
    		delete(m);
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

    public boolean checkWorkAreaFunctionMembership(final Long zoneId,
            final Long workAreaId, final String workAreaType, 
            final String workAreaOperationName, final Set membersToLookup) {
    	List matches = (List) getHibernateTemplate().execute(
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
                        	.setMaxResults(1) // Fetching the first matching row is enough for us
                         	.setCacheable(true)
                         	.list();
                    }
                }
            );
        
        if(matches.size() > 0)
            return true;
        else
            return false;
    }
    public List findWorkAreaFunctionMembershipsByOperation(final Long zoneId,
            final Long workAreaId, final String workAreaType, 
            final String workAreaOperationName) {
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
    }
    
    public List findWorkAreaByOperation(final Long zoneId,
                      final String workAreaOperationName, final Set membersToLookup) {
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

	public void deleteAll(final Class clazz) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				session.createQuery("DELETE " + clazz.getName())
						.executeUpdate();
				return null;
			}
		});
	}

	public TokenInfoRequest loadTokenInfoRequest(Long zoneId, String infoId) {
		TokenInfoRequest info = (TokenInfoRequest) getHibernateTemplate().get
		(TokenInfoRequest.class, infoId);
		
		if(info != null) {
			if(!zoneId.equals(info.getZoneId()))
				throw new ZoneMismatchException(info.getZoneId(), zoneId);
		}

		return info;
	}

	public TokenInfoSession loadTokenInfoSession(Long zoneId, String infoId) {
		TokenInfoSession info = (TokenInfoSession) getHibernateTemplate().get
		(TokenInfoSession.class, infoId);
		
		if(info != null) {
			if(!zoneId.equals(info.getZoneId()))
				throw new ZoneMismatchException(info.getZoneId(), zoneId);
		}

		return info;
	}

	public void deleteUserTokenInfoSession(final Long userId) {
	   	getHibernateTemplate().execute(
	    	   	new HibernateCallback() {
	    	   		public Object doInHibernate(Session session) throws HibernateException {
		     	   		session.createQuery("Delete com.sitescape.team.security.accesstoken.impl.TokenInfoSession where userId=:userId")
		   				.setLong("userId", userId)
	     	   			.executeUpdate();
	       	   		return null;
	       	   		}
	       	   	}
	    	 );    	
	}

 }
