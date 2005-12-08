package com.sitescape.ef.security.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sitescape.ef.security.function.Function;

/**
 *
 * @author Jong Kim
 */
public class SecurityDaoImpl extends HibernateDaoSupport implements SecurityDao {

    private static final String COMPANY_ID = "zoneId"; 
    private static final String WORK_AREA_ID = "workAreaId";
    private static final String WORK_AREA_TYPE = "workAreaType";
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

    public List findFunctions(final String zoneId) {
        return (List)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        return session.getNamedQuery("find-Functions-ByCompany")
                        	.setString(COMPANY_ID, zoneId)
                        	.setCacheable(true)
                        	.list();
                    }
                }
            );
    }

    public List findWorkAreaFunctionMemberships(final String zoneId, 
            final Long workAreaId, final String workAreaType) {
        return (List)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        return session.getNamedQuery("find-FunctionMemberships-ByCompanyAndWorkArea")
                       		.setString(COMPANY_ID, zoneId)
                           	.setLong(WORK_AREA_ID, workAreaId.longValue())
                        	.setString(WORK_AREA_TYPE, workAreaType)
                        	.setCacheable(true)
                        	.list();
                    }
                }
            );
    }
    
    private Criterion functionCriterion(List functions) {
        Disjunction disjunction = Expression.disjunction();
        for(Iterator i = functions.iterator(); i.hasNext();) {
            Function function = (Function) i.next();
            disjunction.add(Expression.eq("functionId", function.getId()));
        }
        return disjunction;
    }

    public boolean checkWorkAreaFunctionMembership(final String zoneId,
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
                       		.setString(COMPANY_ID, zoneId)
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
