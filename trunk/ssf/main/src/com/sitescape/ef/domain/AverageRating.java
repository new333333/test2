package com.sitescape.ef.domain;
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
