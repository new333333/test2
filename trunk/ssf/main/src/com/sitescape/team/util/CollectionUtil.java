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

package com.sitescape.team.util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/**
 * @author Janet McCann
 *
 */
public class CollectionUtil {

	/**
	 * Merge 2 collections so that duplicates do not get added. This is 
	 * necessary when we are using Lists but want Set semantics.  This occurs in
	 * our hibernate mappings for associations.  We don't use sets, cause it forces
	 * the lazy-loading of assoctions when we add new members.  The hibernate proxies do this
	 * to ensure duplicates are not added.  
	 * 
	 */
	public static List mergeAsSet(List oColl, Collection nColl) {
      	if (oColl == null) {
       		if (nColl instanceof List)
       			oColl = (List)nColl;
       		else
       			oColl = new ArrayList(nColl);
       		return oColl;
       	}
       	if ((nColl == null) || nColl.isEmpty()) {
    		oColl.clear();
    		return oColl;
    	}
       	Set coll = differences(oColl,nColl);
       	oColl.removeAll(coll);
       	coll = differences(nColl, oColl);
       	oColl.addAll(coll);
       	
   	return oColl;
	}
	//return list of members in 1 but not in 2
	public static Set differences(Collection coll1, Collection coll2) {
	   	Object o;
	   	if (coll1 == null) return new HashSet();
	   	if (coll2 == null) 	return new HashSet(coll1);
	   	Set coll = new HashSet();
	   	
    	for (Iterator iter=coll1.iterator(); iter.hasNext();) {
    		o=iter.next();
			if (!coll2.contains(o)) {
				coll.add(o);
    		}
    	}
    	return coll;
		
	}
	
	/**
	 * An interface specifying a function which simply returns a value. This is
	 * especially useful for defining "lazy" computations, as the body of
	 * {@link #apply()} is not evaluated until explicitly called.
	 * 
	 * @param <E> -
	 *            the type of the returned value
	 */
	public interface Func0<E> {
		E apply();
	}
	
	/**
	 * 
	 * An interface specifying a function to take an <code>S</code> to a
	 * <code>T</code>
	 * 
	 * @param <S> -
	 *            the source type
	 * @param <T> -
	 *            the target type
	 */
	public interface Func1<S, T> {
		<Source extends S> T apply(Source x);
	}
	
	public static class Id<S> implements Func1<S, S> {
		public <Source extends S> S apply(Source x) {
			return x;
		}
	}
	
	/**
	 * An interface specifying a {@link Func1} which maps <code>T</code>s to
	 * {@link Boolean}s.
	 * 
	 * @param <T>
	 */
	public interface Predicate<T> extends Func1<T, Boolean> {}
	
	/**
	 * An interface specifying a method which takes two arguments.
	 *
	 * @param <A> - the base type of the first argument
	 * @param <B> - the base type of the second argument
	 * @param <C> - the type of the result
	 */
	public interface Func2<A, B, C> {
		<First extends A, Second extends B> C apply(First a, Second b);
	}
	
	/**
	 * Applies the specified {@link Func1} to all elements of the
	 * {@link Collection} <code>xs</code>, returning the new list formed by
	 * the results.
	 * 
	 * @param <S> -
	 *            the source <code>Collection</code>'s element type
	 * @param <T> -
	 *            the target <code>Collection</code>'s element type
	 * @param t -
	 *            the <code>Transform</code> to be applied
	 * @param xs -
	 *            the <code>Collection</code> to transform
	 * @return the new
	 *         <code>Collection<code> formed by the application of the <code>
	 * Transform<code> to all elements of the source.
	 */
	public static <F extends Func1<S, T>, S, T> List<T> map(F t, Collection<S> xs) {
		ArrayList<T> result = new ArrayList<T>(xs.size());
		for (S x : xs) {
			result.add(t.apply(x));
		}
		return result;
	}
	
	/**
	 * Applies <code>f</code> successively to all elements of the
	 * {@link Collection} and accumulates the result, starting with the seed
	 * value <code>init</code>
	 * 
	 * @param <A> -
	 *            the type to be returned
	 * @param <B> -
	 *            the type of the elements of <code>xs</code>
	 * @param f -
	 *            the accumulation {@link Func2}
	 * @param init -
	 *            the seed value
	 * @param xs -
	 *            the <code>Collection</code> to be accumulated over
	 * @return the <code>T</code> value of accumulating all elements of
	 *         <code>xs</code> with <code>init</code>
	 */
	public static <A, B> A foldl(Func2<A, B, A> f, A init, Collection<B> xs) {
		A result = init;
		for (B x : xs) {
			result = f.apply(result, x);
		}
		return result;
	}
	
	/**
	 * Returns the list of <code>E</code>s which are true under the
	 * {@link Predicate} <code>p</code>
	 * 
	 * @param <E> -
	 *            the type of elements to be filtered
	 * @param p -
	 *            the result membership functions
	 * @param xs -
	 *            the {@link Collection} to be filtered
	 * @return - all elements of <code>xs</code> for which p.apply(x) is
	 *         <code>true</code>
	 */
	public static <E> List<E> filter(final Predicate<E> p, final List<E> xs) {
		List<E> result = new ArrayList<E>(xs.size());
		return foldl(new Func2<List<E>, E, List<E>>() {
			public <First extends List<E>, Second extends E> List<E> apply(
					First a, Second b) {
				if (p.apply(b)) {
					a.add(b);
				}
				return a;
			}}, result, xs);
	}

}