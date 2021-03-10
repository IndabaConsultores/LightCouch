package org.lightcouch;

public class CouchDbInfoSizes {

  private long active;
  private long external;
  private long file;

  public long getActive() {
    return active;
  }

  public void setActive(long active) {
    this.active = active;
  }

  public long getExternal() {
    return external;
  }

  public void setExternal(long external) {
    this.external = external;
  }

  public long getFile() {
    return file;
  }

  public void setFile(long file) {
    this.file = file;
  }

  @Override
  public String toString() {
    return String.format("CouchDbInfoSizes [active=%s, external=%s, file=%s]", active, external, file);
  }

}
