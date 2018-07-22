package bbs.core;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.NoSuchElementException;

public class Message {
  private String body;
  private String signature;
  private String userId;

  public boolean isValid(User user) {
    try {
      // The attributes must be set
      if (this.body == null || this.signature == null || this.userId == null) {
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

  public String getSignature() {
    return this.signature;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getBody() {
    return this.body;
  }

  public void setUserId(String userId) {

  }

  public String getUserId(String userId) {
    return this.userId;
  }

  private void sign() {

  }
}
