/*
 * Copyright (C) 2011 lightcouch.org
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

import static org.lightcouch.CouchDbUtil.assertNotEmpty;
import static org.lightcouch.CouchDbUtil.close;
import static org.lightcouch.CouchDbUtil.getStream;
import static org.lightcouch.URIBuilder.buildUri;

import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.lightcouch.ReplicationResult.ReplicationHistory;

import com.google.gson.JsonObject;

/**
 * This class provides access to the database replication API; a replication request 
 * is sent via HTTP POST to <code>_replicate</code> URI.
 * 
 * <h3>Usage Example:</h3>
 * <pre>
 * ReplicationResult replication = dbClient.replication()
 * 	.source("source-db")
 * 	.target("target-db")
 * 	.createTarget(true)
 *	.filter("example/filter1")
 * 	.trigger();
 * 
 * {@code
 * List<ReplicationHistory> histories = replication.getHistories();
 * }
 * </pre>
 * 
 * @see CouchDbClientBase#replication()
 * @see ReplicationResult
 * @see ReplicationHistory
 * @see Replicator
 * @since 0.0.2
 * @author Ahmed Yehia
 *
 */
public class Replication {

	static final Log log = LogFactory.getLog(Replication.class);
	
	private String source;
	private String target;
	private Boolean cancel;
	private Boolean continuous;
	private String filter;
	private JsonObject queryParams; 
	private String[] docIds;      
	private String proxy;       
	private Boolean createTarget;
	private String sinceSeq;
	
	// OAuth
	private JsonObject targetOauth;
	private String consumerSecret;
	private String consumerKey; 
	private String tokenSecret; 
	private String token;

	private CouchDbClientBase dbc;
			
	public Replication(CouchDbClientBase dbc) {
		this.dbc = dbc;
	}

	/**
	 * Triggers a replication request. 
	 * @return {@link ReplicationResult}
	 */
	public ReplicationResult trigger() {
		assertNotEmpty(source, "Source");
		assertNotEmpty(target, "Target");
		ClassicHttpResponse response = null;
		try {
			JsonObject json = createJson();
			if(log.isDebugEnabled()) {
				log.debug(json);
			}
			final URI uri = buildUri(dbc.getBaseUri()).path("_replicate").build();
			response = dbc.post(uri, json.toString());
			final InputStreamReader reader = new InputStreamReader(getStream(response), StandardCharsets.UTF_8);
			return dbc.getGson().fromJson(reader, ReplicationResult.class);
		} finally {
			close(response);
		}
	}

	// fields

	public Replication source(String source) {
		this.source = source;
		return this;
	}

	public Replication target(String target) {
		this.target = target;
		return this;
	}

	public Replication continuous(Boolean continuous) {
		this.continuous = continuous;
		return this;
	}

	public Replication filter(String filter) {
		this.filter = filter;
		return this;
	}

	public Replication queryParams(String queryParams) {
		this.queryParams = dbc.getGson().fromJson(queryParams, JsonObject.class);
		return this;
	}
	
	public Replication queryParams(Map<String, Object> queryParams) {
		this.queryParams = dbc.getGson().toJsonTree(queryParams).getAsJsonObject();
		return this;
	}

	public Replication docIds(String... docIds) {
		this.docIds = docIds;
		return this;
	}

	public Replication proxy(String proxy) {
		this.proxy = proxy;
		return this;
	}

	public Replication cancel(Boolean cancel) {
		this.cancel = cancel;
		return this;
	}

	public Replication createTarget(Boolean createTarget) {
		this.createTarget = createTarget;
		return this;
	}
	
	/**
	 * Starts a replication since an update sequence.  
	 * @param sinceSeq The update sequence
	 * @return {@link Replication}
	 */
	public Replication sinceSeq(String sinceSeq) {
		this.sinceSeq = sinceSeq;
		return this;
	}
	
	public Replication targetOauth(String consumerSecret, String consumerKey, String tokenSecret, String token) {
		targetOauth = new JsonObject();
		this.consumerSecret = consumerKey;
		this.consumerKey = consumerKey;
		this.tokenSecret = tokenSecret;
		this.token = token;
		return this;
	}
	
	// helper
	
	private JsonObject createJson() {
		JsonObject json = new JsonObject();
		addProperty(json, "source", source);
		addProperty(json, "cancel", cancel);
		addProperty(json, "continuous", continuous);
		addProperty(json, "filter", filter);
		
		if(queryParams != null) 
			json.add("query_params", queryParams);
		if(docIds != null) 
			json.add("doc_ids", dbc.getGson().toJsonTree(docIds, String[].class));
		
		addProperty(json, "proxy", proxy);
		addProperty(json, "since_seq", sinceSeq);
		addProperty(json, "create_target", createTarget);
		
		if(targetOauth != null) {
			JsonObject auth = new JsonObject();
			JsonObject oauth = new JsonObject();
			addProperty(oauth, "consumer_secret", consumerSecret);
			addProperty(oauth, "consumer_key", consumerKey);
			addProperty(oauth, "token_secret", tokenSecret);
			addProperty(oauth, "token", token);
			
			addProperty(targetOauth, "url", target);
			auth.add("oauth", oauth);
			
			targetOauth.add("auth", auth);
			json.add("target", targetOauth);
		} else {
			addProperty(json, "target", target);
		}
		return json;
	}

	private void addProperty(JsonObject json, String name, Object value) {
		if(value != null) {
			if(value instanceof Boolean)
				json.addProperty(name, (Boolean)value);
			else if (value instanceof String)
				json.addProperty(name, (String)value);
			else if (value instanceof Integer)
				json.addProperty(name, (Integer)value);
		}
	}
}
