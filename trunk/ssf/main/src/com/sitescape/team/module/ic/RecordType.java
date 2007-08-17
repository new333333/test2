package com.sitescape.team.module.ic;

public enum RecordType {
	
	record(-1),	flash(0), audio(1), share(2), chat(3), summary(4);
	
	private int number;
	
	RecordType(int i) {
		this.number = i;
	}
	
	public static RecordType getByNumber(int recordType) {
		RecordType[] all = RecordType.values();
		for (int i = 0; i < all.length; i++) {
			if (all[i].number == recordType) {
				return all[i];
			}
		}
		return RecordType.record;
	}
}
