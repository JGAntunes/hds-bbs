package bbs.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class Message {
  private String body;
  private byte[] signature;
  private String userId;
  private ZonedDateTime creationDate;

  @JsonIgnore
  public boolean isValid(User user) {
    try {
      // The attributes must be set
      if (this.body == null || this.signature == null || this.userId == null || this.creationDate == null) {
        return false;
      }
      // The userId assigned is the same as the given user
      if (!user.getId().equals(this.userId)) {
        return false;
      }
      return user.verifySignature(this);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return false;
    } catch (SignatureException e) {
      e.printStackTrace();
      return false;
    } catch (InvalidKeyException e) {
      e.printStackTrace();
      return false;
    }
  }

  // Get the signature in hex string format
  public String getSignature() {
    return DatatypeConverter.printHexBinary(this.signature).toLowerCase();
  }

  // Set the signature to byte array from hex string
  public void setSignature(String signature) {
    this.signature = DatatypeConverter.parseHexBinary(signature);
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getBody() {
    return this.body;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return this.userId;
  }

  public String getCreationDate() {
    return this.creationDate.toString();
  }

  public void setCreationDate(String creationDate) {
    ZonedDateTime date = ZonedDateTime.parse(creationDate);
    this.creationDate = date;
  }

  @Override
  public String toString() {
    return "Message\nsign: " + this.getSignature() + "\nuserId: " + this.getUserId() + "\nbody: " + this.getBody();
  }
}
