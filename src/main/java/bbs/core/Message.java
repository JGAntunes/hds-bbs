package bbs.core;

public class Message {
  private String body;
  private String signature;
  private String userId;

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
