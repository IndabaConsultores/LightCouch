package org.lightcouch;

import com.google.gson.annotations.SerializedName;

public class DbUpdatesResult {

	@SerializedName("db_name")
	private String dbName;

	private String type;

	private String seq;

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}
}
