package bbs.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class TimeStampedPayload {
  private ZonedDateTime timestamp;
  // Two minutes of offset allowed
  private final int MAXIMUM_SECOND_OFFSET = 120;

  @JsonIgnore
  public boolean isValid() {
    // Timestamp must be set
    if (timestamp == null) {
      return false;
    }
    return Math.abs(ChronoUnit.SECONDS.between(timestamp, ZonedDateTime.now())) < MAXIMUM_SECOND_OFFSET;
  }

  public ZonedDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(ZonedDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @JsonProperty("timestamp")
  public String getStringTimestamp() {
    return this.timestamp.toString();
  }

  @JsonProperty("timestamp")
  public void setTimestamp(String timestamp) {
    ZonedDateTime date = ZonedDateTime.parse(timestamp);
    this.timestamp = date;
  }

  public void generateTimestamp() {
    this.timestamp = ZonedDateTime.now();
  }
}
