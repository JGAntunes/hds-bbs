package bbs.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class User {
  private RSAPublicKey key;
  private byte[] id;

  @JsonIgnore
  public boolean isValid() {
    try {
      // The attributes must be set
      if (this.id == null || this.key == null) {
        return false;
      }
      // Id should be based on the sha256 digest of the key
      MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = sha256Digest.digest(this.key.getEncoded());
      // Compare digests
      return MessageDigest.isEqual(this.id, hash);
      // Catch everything and return false otherwise
    } catch (Exception e) {
      return false;
    }
  }

  public String getKey() {
    return new String(Base64.getEncoder().encode(key.getEncoded()));
  }

  public void setKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
    // Strip down the key delimiters
    // Remove all the new lines
    String cleanKey = key
        .replace("-----BEGIN PUBLIC KEY-----\n", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replace("\n", "");
    // Base64 decode it
    byte[] decoded = Base64.getDecoder().decode(cleanKey.getBytes());
    KeyFactory kf = KeyFactory.getInstance("RSA");
    this.key = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(decoded));
  }

  public void setKey(RSAPublicKey key) {
    this.key = key;
  }

  // Get the id in hex string format
  public String getId() {
    return DatatypeConverter.printHexBinary(this.id).toLowerCase();
  }

  // Set the id in byte array from hex string format
  public void setId(String id) {
    this.id = DatatypeConverter.parseHexBinary(id);
  }

  public Boolean verifySignature (Message message) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
    Signature signature = Signature.getInstance("SHA256withRSA");
    signature.initVerify(this.key);
    signature.update(message.getBody().getBytes(StandardCharsets.UTF_8));
    return signature.verify(message.getSignature().getBytes());
  }

  @Override
  public String toString() {
    return "User\nid: " + this.getId() + "\nkey: " + this.getKey() ;
  }
}
