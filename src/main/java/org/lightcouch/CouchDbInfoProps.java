package org.lightcouch;

public class CouchDbInfoProps {

  private boolean partitioned;

  public boolean isPartitioned() {
    return partitioned;
  }

  public void setPartitioned(boolean partitioned) {
    this.partitioned = partitioned;
  }

  @Override
  public String toString() {
    return String.format("CouchDbInfoProps [partitioned=%s]", partitioned);
  }
}
