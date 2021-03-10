/*
 * Copyright (C) 2021 indaba.es
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

import com.google.gson.annotations.SerializedName;

/**
 * Holds information about a CouchDB database instance.
 * 
 * @author Ahmed Yehia
 */
public class CouchDbInfo {

  @SerializedName("db_name")
  private String dbName;
  @SerializedName("doc_count")
  private long docCount;
  @SerializedName("doc_del_count")
  private String docDelCount;
  @SerializedName("update_seq")
  private String updateSeq;
  @SerializedName("purge_seq")
  private String purgeSeq;
  @SerializedName("compact_running")
  private boolean compactRunning;
  @SerializedName("disk_size")
  private long diskSize;
  @SerializedName("instance_start_time")
  private long instanceStartTime;
  @SerializedName("disk_format_version")
  private int diskFormatVersion;

  private CouchDbInfoCluster cluster;
  private CouchDbInfoSizes sizes;
  private CouchDbInfoProps props;

  public String getDbName() {
    return dbName;
  }

  public long getDocCount() {
    return docCount;
  }

  public String getDocDelCount() {
    return docDelCount;
  }

  public String getUpdateSeq() {
    return updateSeq;
  }

  public String getPurgeSeq() {
    return purgeSeq;
  }

  public boolean isCompactRunning() {
    return compactRunning;
  }

  public long getDiskSize() {
    return diskSize;
  }

  public long getInstanceStartTime() {
    return instanceStartTime;
  }

  public int getDiskFormatVersion() {
    return diskFormatVersion;
  }

  public CouchDbInfoCluster getCluster() {
    return cluster;
  }

  public void setCluster(CouchDbInfoCluster cluster) {
    this.cluster = cluster;
  }

  public CouchDbInfoSizes getSizes() {
    return sizes;
  }

  public void setSizes(CouchDbInfoSizes sizes) {
    this.sizes = sizes;
  }

  public CouchDbInfoProps getProps() {
    return props;
  }

  public void setProps(CouchDbInfoProps props) {
    this.props = props;
  }
  
  @Override
  public String toString() {
    
    String dbinfo = String.format(
        "CouchDbInfo [dbName=%s, docCount=%s, docDelCount=%s, updateSeq=%s, purgeSeq=%s, compactRunning=%s, diskSize=%s, instanceStartTime=%s, diskFormatVersion=%s]",
        dbName, docCount, docDelCount, updateSeq, purgeSeq, compactRunning, diskSize, instanceStartTime,
        diskFormatVersion);
      
        if (cluster != null) dbinfo += "\n" + cluster.toString();
        if (sizes != null) dbinfo += "\n" + sizes.toString();
        if (props != null) dbinfo += "\n" + props.toString();
    
        return dbinfo;
  }

}
