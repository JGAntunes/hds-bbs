package bbs.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class Message {
  private String body;
  private byte[] signature;
  private String userId;

  @JsonIgnore
  public boolean isValid(User user) {
    try {
      // The attributes must be set
      if (this.body == null || this.signature == null || this.userId == null) {
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

  public String getUserId(String userId) {
    return this.userId;
  }
}
