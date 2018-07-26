package bbs.core;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.ZonedDateTime;
import java.util.Base64;

public class User extends TimeStampedPayload {
  private RSAPublicKey pubKey;
  private byte[] id;
  private ZonedDateTime creationDate;

  @Override
  @JsonIgnore
  public boolean isValid() {
    try {
      if (!super.isValid()) {
        return false;
      }
      // The attributes must be set
      if (this.id == null || this.pubKey == null || this.creationDate == null) {
        return false;
      }
      // Id should be based on the sha256 digest of the key
      // Compare digests
      return MessageDigest.isEqual(this.id, Utils.getKeyDigest(this.getPubKey()));

      // Catch everything and return false otherwise
    } catch (Exception e) {
      return false;
    }
  }

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
        "Timestamp: " + this.getTimestamp() + "\n" +
        "CreationDate: " + this.getCreationDate() + "\n" +
        "PubKey: " + this.getPubKeyString();
  }
}
