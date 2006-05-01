package com.sitescape.util;

public class Pair<H,T> {

	private H head;
	private T tail;
	
	public Pair(H head, T tail) {
		this.head = head;
		this.tail = tail;
	}
	
	public H getHead() {
		return head;
	}
	
	public T getTail() {
		return tail;
	}
}
