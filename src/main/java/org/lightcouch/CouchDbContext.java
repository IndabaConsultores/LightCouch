/*
 * Copyright (C) lightcouch.org
 * Copyright (C) 2018 indaba.es
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lightcouch;

import static org.lightcouch.CouchDbUtil.*;
import static org.lightcouch.URIBuilder.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.ClassicHttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * Contains database server specific APIs.
 * 
 * @see CouchDbClient#context() 
 * @since 0.0.2
 * @author Ahmed Yehia
 */
public class CouchDbContext {

	private static final Log log = LogFactory.getLog(CouchDbClient.class);

	private CouchDbClientBase dbc;

	CouchDbContext(CouchDbClientBase dbc, CouchDbProperties props) {
		this.dbc = dbc;
		if (props.isCreateDbIfNotExist()) {
			createDB(props.getDbName());
		} else {
			serverVersion(); // pre warm up client
		}
	}

	/**
	 * Requests CouchDB deletes a database.
	 * @param dbName The database name
	 * @param confirm A confirmation string with the value: <tt>delete database</tt>
	 */
	public void deleteDB(String dbName, String confirm) {
		assertNotEmpty(dbName, "dbName");
		if(!"delete database".equals(confirm))
			throw new IllegalArgumentException("Invalid confirm!");
		dbc.delete(buildUri(dbc.getBaseUri()).path(dbName).build());
	}

	/**
	 * Requests CouchDB creates a new database; if one doesn't exist.
	 * @param dbName The Database name
	 */
	public void createDB(String dbName) {
		this.createDB(dbName, 0);
	}
	
	/**
	 * Requests CouchDB creates a new database; if one doesn't exist.
	 * @param dbName The Database name
	 * @param shards The number of range partitions (> 0)
	 */
	public void createDB(String dbName, int shards) {
		assertNotEmpty(dbName, "dbName");
		InputStream getresp = null;
		ClassicHttpResponse putresp = null;
		URIBuilder builder = buildUri(dbc.getBaseUri()).path(dbName);
		if(shards > 0) {
			builder = builder.query("q", shards);
		}
		final URI uri = builder.build();
		try {
			
			getresp = dbc.get(uri);
		} catch (NoDocumentException e) { // db doesn't exist
			final HttpPut put = new HttpPut(uri);
			putresp = dbc.executeRequest(put);
			log.info(String.format("Created Database: '%s'", dbName));
		} finally {
			close(getresp);
			close(putresp);
		}
	}

	/**
	 * @return All Server databases.
	 */
	public List<String> getAllDbs() {
		InputStream instream = null;
		try {
			Type typeOfList = new TypeToken<List<String>>() {}.getType();
			instream = dbc.get(buildUri(dbc.getBaseUri()).path("_all_dbs").build());
			Reader reader = new InputStreamReader(instream, StandardCharsets.UTF_8);
			return dbc.getGson().fromJson(reader, typeOfList);
		} finally {
			close(instream);
		}
	}

	/**
	 * @return {@link CouchDbInfo} Containing the DB server info.
	 */
	public CouchDbInfo info() {
		return dbc.get(buildUri(dbc.getDBUri()).build(), CouchDbInfo.class);
	}

	/**
	 * @return DB Server version.
	 */
	public String serverVersion() {
		InputStream instream = null;
		try {
			instream = dbc.get(buildUri(dbc.getBaseUri()).build());
			Reader reader = new InputStreamReader(instream, StandardCharsets.UTF_8);
			return getAsString(JsonParser.parseReader(reader).getAsJsonObject(), "version");
		} finally {
			close(instream);
		}
	}

	/**
	 * Triggers a database <i>compact</i> request.
	 */
	public void compact() {
		ClassicHttpResponse response = null;
		try {
			response = dbc.post(buildUri(dbc.getDBUri()).path("_compact").build(), "");
		} finally {
			close(response);
		}
	}

	/**
	 * Requests the database commits any recent changes to disk.
	 */
	public void ensureFullCommit() {
		ClassicHttpResponse response = null;
		try {
			response = dbc.post(buildUri(dbc.getDBUri()).path("_ensure_full_commit").build(), "");
		} finally {
			close(response);
		}
	}
	
	/**
	 * Request a database sends a list of UUIDs.
	 * @param count The count of UUIDs.
	 * @return a list of UUIDs
	 */
	public List<String> uuids(long count) {
		final String uri = String.format("%s_uuids?count=%d", dbc.getBaseUri(), count);
		final JsonObject json = dbc.findAny(JsonObject.class, uri);
		return dbc.getGson().fromJson(json.get("uuids").toString(), new TypeToken<List<String>>(){}.getType());
	}
	
	/**
	 * Request all database update events in the CouchDB instance.
	 * @param since
	 * @return a list of all database events in the CouchDB instance
	 */
	public DbUpdates dbUpdates(String since) {
		InputStream instream = null;
		try {
			URIBuilder builder = buildUri(dbc.getBaseUri()).path("_db_updates");
			if(since != null && !"".equals(since)) {
				builder.query("since", since);
			}
			instream = dbc.get(builder.build());
			Reader reader = new InputStreamReader(instream, StandardCharsets.UTF_8);
			return dbc.getGson().fromJson(reader, DbUpdates.class);
		} finally {
			close(instream);
		}
	}
}
