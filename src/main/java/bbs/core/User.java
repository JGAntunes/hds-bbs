package bbs.core;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public abstract class User {
  private RSAPublicKey pubKey;
  private byte[] id;

  @JsonIgnore
  public abstract boolean isValid();

  @JsonProperty("pubKey")
  public String getPubKeyString() {
    return Base64.getEncoder().encodeToString(this.pubKey.getEncoded());
  }

  @JsonIgnore
  public RSAPublicKey getPubKey() {
    return this.pubKey;
  }

  @JsonProperty("pubKey")
  public void setPubKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
    KeyFactory kf = KeyFactory.getInstance("RSA");
    this.pubKey = (RSAPublicKey) kf.generatePublic(Utils.loadPublicKeyFromPEMString(key));
  }

  public void setPubKey(RSAPublicKey key) {
    this.pubKey = key;
  }

  @JsonProperty("id")
  // Get the id in hex string format
  public String getStringId() {
    return DatatypeConverter.printHexBinary(this.id).toLowerCase();
  }

  @JsonIgnore
  // Get the id in hex string format
  public byte[] getByteId() {
    return this.id;
  }

  // Set the id in byte array from hex string format
  public void setId(String id) {
    this.id = DatatypeConverter.parseHexBinary(id);
  }

  public Boolean verifySignature (Message message) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
    Signature signature = Signature.getInstance("SHA256withRSA");
    signature.initVerify(this.pubKey);
    signature.update(message.getContentToSign());
    return signature.verify(message.getByteSignature());
  }

  @Override
  public String toString() {
    return "\n== User ==\n"+
        "Id: " + this.getStringId() + "\n" +
        "PubKey: " + this.getPubKeyString();
  }
}
