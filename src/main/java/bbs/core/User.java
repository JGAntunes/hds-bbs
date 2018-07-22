package bbs.core;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class User {
  private RSAPublicKey key;
  private String id;

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
    // Set the id based on the sha256 digest of the key
    MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = sha256Digest.digest(decoded);
    // Base64 encode it and set it
    this.id = new String(Base64.getEncoder().encode(hash));
  }

  public void setKey(RSAPublicKey key) {
    this.key = key;
  }

  public String getId() {
    return this.id;
  }

  public Boolean verifySignature (Message message) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
    Signature signature = Signature.getInstance("SHA256withRSA");
    signature.initVerify(this.key);
    signature.update(message.getBody().getBytes());
    return signature.verify(message.getSignature().getBytes());
  }
}
