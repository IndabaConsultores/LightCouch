package org.lightcouch;

public class CouchDbInfoCluster {

  private long n;
  private long q;
  private long r;
  private long w;

  public long getN() {
    return n;
  }

  public void setN(long n) {
    this.n = n;
  }

  public long getQ() {
    return q;
  }

  public void setQ(long q) {
    this.q = q;
  }

  public long getR() {
    return r;
  }

  public void setR(long r) {
    this.r = r;
  }

  public long getW() {
    return w;
  }

  public void setW(long w) {
    this.w = w;
  }

  @Override
  public String toString() {
    return String.format("CouchDbInfoCluster [n=%s, q=%s, r=%s, w=%s]", n, q, r, w);
  }

}
