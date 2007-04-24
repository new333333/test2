/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.domain;
/**
 * Component class
 */
public class AverageRating {
	Float rating=null; 
	Integer count=null;
	public AverageRating() {
		
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Float getAverage() {
		return rating;
	}
	public void setAverage(Float rating) {
		this.rating = rating;
	}
	public void setAverage(float rating) {
		this.rating = Float.valueOf(rating);
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public void setCount(int count) {
		this.count = Integer.valueOf(count);
	}

}
