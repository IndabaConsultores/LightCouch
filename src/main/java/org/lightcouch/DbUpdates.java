package org.lightcouch;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DbUpdates {

	private List<DbUpdatesResult> results;

	@SerializedName("last_seq")
	private String lastSeq;

	public List<DbUpdatesResult> getResults() {
		return results;
	}

	public void setResults(List<DbUpdatesResult> results) {
		this.results = results;
	}

	public String getLastSeq() {
		return lastSeq;
	}

	public void setLastSeq(String lastSeq) {
		this.lastSeq = lastSeq;
	}
}