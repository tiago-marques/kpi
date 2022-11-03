package org.ws.to;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SeriesMetaTO {

  private String type;
  private String iso;
  private Long minTimestamp;

  private Long maxTimestamp;

  public SeriesMetaTO(String type, String iso, Long minTimestamp, Long maxTimestamp) {
    this.type = type;
    this.iso = iso;
    this.minTimestamp = minTimestamp;
    this.maxTimestamp = maxTimestamp;
  }

  public Long getMinTimestamp() {
    return minTimestamp;
  }

  public void setMinTimestamp(Long minTimestamp) {
    this.minTimestamp = minTimestamp;
  }

  public Long getMaxTimestamp() {
    return maxTimestamp;
  }

  public void setMaxTimestamp(Long maxTimestamp) {
    this.maxTimestamp = maxTimestamp;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getIso() {
    return iso;
  }

  public void setIso(String iso) {
    this.iso = iso;
  }
}

