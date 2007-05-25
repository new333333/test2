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
	Double rating=null; 
	Long count=null;
	public AverageRating() {
		
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Double getAverage() {
		return rating;
	}
	public void setAverage(Double rating) {
		this.rating = rating;
	}
	public void setAverage(double rating) {
		this.rating = Double.valueOf(rating);
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public void setCount(long count) {
		this.count = Long.valueOf(count);
	}

}
