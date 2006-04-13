package com.sitescape.ef.security.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.FetchMode;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.security.function.WorkAreaFunctionMembership;

/**
 *
 * @author Jong Kim
 */
public class SecurityDaoImpl extends HibernateDaoSupport implements SecurityDao {

    private static final String ZONE_ID = "zoneName"; 
    private static final String WORK_AREA_ID = "workAreaId";
    private static final String WORK_AREA_TYPE = "workAreaType";
    private static final String FUNCTION_ID = "functionId";
    private static final String WORK_AREA_OPERATION_NAME = "operationName";
    private static final String PRINCIPAL_IDS = "principalIds";
    
    public void save(Object obj) {
        getHibernateTemplate().save(obj);
    }

    public void update(Object obj) {
        getHibernateTemplate().update(obj);
    }

    public void delete(Object obj) {
        getHibernateTemplate().delete(obj);
    }
    public Function loadFunction(Long id) {
        return (Function)getHibernateTemplate().get(Function.class, id);
    }
    public WorkAreaFunctionMembership loadWorkAreaFunctionMembership(Long id) {
        return (WorkAreaFunctionMembership)getHibernateTemplate().get(WorkAreaFunctionMembership.class, id);
           	
    }
    public List findFunctions(final String zoneName) {
        return (List)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        List results = session.createCriteria(Function.class)
                        	.add(Expression.eq(ZONE_ID, zoneName))
                        	.setFetchMode("operationNames", FetchMode.JOIN)
                        	.setCacheable(true)
                        	.list();
                    	//since we eagerly fetch, results are not unique
                    	return new ArrayList(new HashSet(results));
                    }
                }
            );
    }


	public WorkAreaFunctionMembership getWorkAreaFunctionMembership(final String zoneName, 
			final Long workAreaId, final String workAreaType, final Long functionId) {
        return (WorkAreaFunctionMembership) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                    	List results = session.createCriteria(WorkAreaFunctionMembership.class)
                                .add(Expression.conjunction() 
                               			.add(Expression.eq(ZONE_ID, zoneName))
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
	
	public List findWorkAreaFunctionMemberships(final String zoneName, 
            final Long workAreaId, final String workAreaType) {
		
	       return (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                    	List results = session.createCriteria(WorkAreaFunctionMembership.class)
	                                .add(Expression.conjunction() 
	                               			.add(Expression.eq(ZONE_ID, zoneName))
	                               			.add(Expression.eq(WORK_AREA_ID, workAreaId))
	                               			.add(Expression.eq(WORK_AREA_TYPE, workAreaType))
	                               		)
	                               	.setFetchMode("memberIds", FetchMode.JOIN)
	                               	.list();
	                    	//since we eagerly fetch, results are not unique
	                    	return new ArrayList(new HashSet(results));
	                    	}
	                	}
	            );
	 	}
    public void deleteWorkAreaFunctionMemberships(final String zoneName, final Long workAreaId, final String workAreaType) {
    	List members = findWorkAreaFunctionMemberships(zoneName, workAreaId, workAreaType);
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

    public boolean checkWorkAreaFunctionMembership(final String zoneName,
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
                       		.setString(ZONE_ID, zoneName)
                            .setLong(WORK_AREA_ID, workAreaId.longValue())
                        	.setString(WORK_AREA_TYPE, workAreaType)
                        	.setString(WORK_AREA_OPERATION_NAME, workAreaOperationName)
                        	.setParameterList(PRINCIPAL_IDS, membersToLookup)
                        	.setMaxResults(1) // Fetching the first matching row is enough for us
                        	.list();
                    }
                }
            );
        
        if(matches.size() > 0)
            return true;
        else
            return false;
    }

    
}
