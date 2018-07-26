package bbs.core;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.ZonedDateTime;
import java.util.Base64;
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
      if (!user.getStringId().equals(this.userId)) {
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

  @JsonProperty("signature")
  // Get the signature in hex string format
  public String getStringSignature() {
    return Base64.getEncoder().encodeToString(this.signature);
  }

  @JsonIgnore
  // Get the signature in hex string format
  public byte[] getByteSignature() {
    return this.signature;
  }


  @JsonProperty("signature")
  // Set the signature to byte array from hex string
  public void setSignature(String signature) {
    this.signature = Base64.getDecoder().decode(signature.getBytes(StandardCharsets.UTF_8));
  }

  @JsonIgnore
  public void setSignature(byte[] signature) {
    this.signature = signature;
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

  @JsonProperty("creationDate")
  public String getCreationDate() {
    return this.creationDate.toString();
  }

  @JsonProperty("creationDate")
  public void setCreationDate(String creationDate) {
    ZonedDateTime date = ZonedDateTime.parse(creationDate);
    this.creationDate = date;
  }

  public void setCreationDate(ZonedDateTime creationDate) {
    this.creationDate = creationDate;
  }

  @JsonIgnore
  // Utility method to output a byte[] with info from the message to sign
  public byte[] getContentToSign() {
    // For now we look at the body and the message creation date
    String toSign = this.getCreationDate() + this.getBody();
    return toSign.getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public String toString() {
    return "\n== Message ==\n"+
        "Signature: " + this.getStringSignature() + "\n" +
        "UserId: " + this.getUserId() + "\n" +
        "CreationDate: " + this.getCreationDate() + "\n" +
        "Body: " + this.getBody();
  }
}
